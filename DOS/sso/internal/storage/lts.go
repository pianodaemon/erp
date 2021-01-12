package storage

import (
	"crypto/sha256"
	"database/sql"
	"fmt"

	"github.com/kelseyhightower/envconfig"
	_ "github.com/lib/pq"
)

type (
	User struct {
		UID       string `json:"uid"`
		Username  string `json:"username"`
		IsActive  bool   `json:"is_active"`
		CreatedAt int64  `json:"created_at"`
	}

	PgSqlSettings struct {
		Host     string `default:"rdbms_obs"`
		Db       string `default:"soa"`
		User     string `default:"postgres"`
		Password string `default:"postgres"`
		Port     int    `default:"5432"`
	}
)

var pgSettings PgSqlSettings

func init() {

	envconfig.Process("postgres", &pgSettings)
}

func shapeConnStr() string {

	// SSL mode disable to use in containers
	return fmt.Sprintf("user=%s password=%s host=%s port=%d dbname=%s sslmode=disable",
		pgSettings.User,
		pgSettings.Password,
		pgSettings.Host,
		pgSettings.Port,
		pgSettings.Db)
}

func Authenticate(username, password string) (*User, error) {

	var usr *User

	dbinfo := shapeConnStr()

	db, err := sql.Open("postgres", dbinfo)

	if err != nil {

		return nil, fmt.Errorf("Issues when connecting to the long term storage")
	}

	defer db.Close()

	{
		var uid, passwordHash, retUsername string
		var disabled bool

		err := db.QueryRow("SELECT id::character varying as uid, username, passwd, disabled FROM users WHERE username = $1",
			username).Scan(&uid, &retUsername, &passwordHash, &disabled)

		if err != nil {

			return nil, err
		}

		h := sha256.New()
		h.Write([]byte(password))
		hashed := fmt.Sprintf("%x", h.Sum(nil))

		if passwordHash != hashed {

			return nil, fmt.Errorf("Verify your credentials")
		}

		usr = &User{
			UID:       uid,
			Username:  retUsername,
			IsActive:  !disabled,
			CreatedAt: 12123123,
		}
	}

	return usr, nil
}

// GetUserAuthorities retrieves a string representing all authorities for a user
func GetUserAuthorities(userID string) (string, error) {

	var auth, app, lastApp, appAuths, userAuths string
	var firstRow = true
	var authWeightPerApp = 0

	dbinfo := shapeConnStr()
	db, err := sql.Open("postgres", dbinfo)
	if err != nil {

		return "", fmt.Errorf("Issues when connecting to the long term storage")
	}

	defer db.Close()

	q := `SELECT apps.nombre_app, auths.title
			FROM user_authority AS usr_auth
			JOIN authorities AS auths ON usr_auth.authority_id = auths.id
			JOIN apps ON auths.app_id = apps.id
			WHERE usr_auth.user_id = $1
			ORDER BY apps.nombre_app, auths.title`

	rows, err := db.Query(q, userID)
	if err != nil {

		return "", err
	}

	for rows.Next() {

		if err := rows.Scan(&app, &auth); err != nil {

			return "", err
		}

		if app != lastApp && !firstRow {

			setUserAuths(&userAuths, authWeightPerApp, lastApp, appAuths)

			authWeightPerApp = 0
			appAuths = ""
		}

		switch auth {
		case "F":
			authWeightPerApp += 4

		case "C", "R", "U", "D":
			authWeightPerApp++

			if appAuths == "" {
				appAuths += auth
			} else {
				appAuths += "," + auth
			}
		}

		firstRow = false
		lastApp = app
	}

	if !firstRow {

		setUserAuths(&userAuths, authWeightPerApp, lastApp, appAuths)
	}

	return userAuths, nil
}

func setUserAuths(userAuths *string, authWeightPerApp int, lastApp, appAuths string) {

	if authWeightPerApp >= 4 {

		if *userAuths == "" {
			*userAuths += lastApp + "=F"
		} else {
			*userAuths += "|" + lastApp + "=F"
		}
	} else {

		if *userAuths == "" {
			*userAuths += lastApp + "=" + appAuths
		} else {
			*userAuths += "|" + lastApp + "=" + appAuths
		}
	}
}
