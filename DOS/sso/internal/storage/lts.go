package storage

import (
	"database/sql"
	"fmt"

	"github.com/kelseyhightower/envconfig"
	_ "github.com/lib/pq"
)

type (
	User struct {
		UID         string
		Username    string
		IsActive    bool
		CreatedAt   int64
		Authorities map[string]interface{}
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
		var uid, passwordPlain, retUsername string
		var enabled bool

		err := db.QueryRow("SELECT id::character varying as uid, username, password, enabled FROM users WHERE username = $1",
			username).Scan(&uid, &retUsername, &passwordPlain, &enabled)

		if err != nil {

			return nil, err
		}

		if passwordPlain != password {

			return nil, fmt.Errorf("Verify your credentials")
		}

		pseudoSet, err := pullUserAuths(retUsername, db)

		if err != nil {

			return nil, err
		}

		usr = &User{
			UID:         uid,
			Username:    retUsername,
			IsActive:    enabled,
			CreatedAt:   12123123,
			Authorities: pseudoSet,
		}
	}

	return usr, nil
}

func pullUserAuths(username string, db *sql.DB) (map[string]interface{}, error) {

	rows, err := db.Query("select authority from authorities where username = $1;", username)
	if err != nil {
		return nil, err
	}

	pseudoSet := make(map[string]interface{})
	var authority string

	for rows.Next() {
		if err := rows.Scan(&authority); err != nil {
			return nil, err
		}
		pseudoSet[authority] = new(struct{})
	}

	return pseudoSet, nil
}
