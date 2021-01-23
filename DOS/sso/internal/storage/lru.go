package storage

import (
	"context"
	"os"
	"fmt"
	"time"
	"strings"

	ton "immortalcrab.com/sso/internal/token"

	jwt "github.com/dgrijalva/jwt-go"
	"github.com/go-redis/redis/v8"
)

// Fetches trivially an enviroment variable
func getEnv(key, fallback string) string {

	if value, ok := os.LookupEnv(key); ok {

		return value
	}
	return fallback
}

// Expires a token by placement of at cache (latter resouce usage)
func Expire(tokenStr string, token *jwt.Token) error {

	remValidity := time.Second * time.Duration(ton.RemainingValidity(token))

	var ctx = context.Background()
	var cli *redis.Client

	if err := setRedisClientUp(&cli); err != nil {

		return err
	}

	defer cli.Close()

	if err := cli.Set(ctx, tokenStr, tokenStr, remValidity).Err(); err != nil {

		return err
	}

	return nil
}

// Inquiries cache blacklist whether token exist or not
func IsInBlackList(tokenStr string) (bool, error) {

	var ctx = context.Background()
	var cli *redis.Client

	if err := setRedisClientUp(&cli); err != nil {
		return false, err
	}

	defer cli.Close()

	_, err := cli.Get(ctx, tokenStr).Result()

	if err == redis.Nil {
		/* token is not in */
		return false, nil
	} else if err != nil {
		/* Oops something went bad */
		return false, err
	}

	/* Sadness.. token is in */
	return true, nil
}

// Sets up a steady redis connection
func setRedisClientUp(rcli **redis.Client) error {

	var err error = nil
	var cli *redis.Client
	var ctx = context.Background()

	host := getEnv("REDIS_HOST", "localhost")
	port := getEnv("REDIS_PORT", "6379")

	cli = redis.NewClient(&redis.Options{
		Addr:     (host + ":" + port),
		Password: "", // no password set
		DB:       0,  // use default DB
	})

	_, err = cli.Ping(ctx).Result()

	if err == nil {
		*rcli = cli
	}

	return err
}

// Endorses the roles to a set identifier
func EndorseAuthorites(asetID string, authorities map[string]interface{}) error {

	anumber := len(authorities)

	if anumber == 0 {

		return fmt.Errorf("no authorites feed to endorse")
	}

	if len(asetID) == 0 {

		return fmt.Errorf("an empty string can not be endorsed")
	}

	var ctx = context.Background()
	var cli *redis.Client

	if err := setRedisClientUp(&cli); err != nil {

		return err
	}

	defer cli.Close()

	roles := make([]string, 0, len(authorities))
	for role := range authorities {

		roles = append(roles, role)
	}

	// It blows the previous set of the authorities
	if err := cli.Do(ctx, "del", asetID).Err(); err != nil {

		return err
	}

	// The authorities are placed into the set
	if err := cli.Do(ctx, "sadd", asetID, strings.Join(roles, " ")).Err(); err != nil {

		return err
	}

	return nil
}

