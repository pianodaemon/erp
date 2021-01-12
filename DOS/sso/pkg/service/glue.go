package service

import (
	"crypto/rsa"
	"fmt"
	"net/http"

	"github.com/codegangsta/negroni"
	"github.com/gorilla/mux"
	"github.com/kelseyhightower/envconfig"
	"github.com/sirupsen/logrus"

	co "immortalcrab.com/sso/internal/controllers"
	"immortalcrab.com/sso/internal/rsapi"
	dal "immortalcrab.com/sso/internal/storage"
	ton "immortalcrab.com/sso/internal/token"
	aut "immortalcrab.com/sso/pkg/authentication"
)

var apiSettings rsapi.RestAPISettings

func init() {

	envconfig.Process("rsapi", &apiSettings)
}

func getExpDelta() int {

	ref := struct {
		Delta int `default:"72"`
	}{0}

	/* It stands for
	   TOKEN_CLERK_EXP_DELTA */
	envconfig.Process("token_clerk_exp", &ref)

	return ref.Delta
}

func getKeys() (*rsa.PrivateKey, *rsa.PublicKey, error) {

	ref := struct {
		Private string `default:"/pem/private_key"`
		Public  string `default:"/pem/public_key.pub"`
	}{"", ""}

	/* It stands for
	   TOKEN_CLERK_RSA_PRIVATE and  TOKEN_CLERK_RSA_PUBLIC */
	envconfig.Process("token_clerk_rsa", &ref)

	return ton.GetPrivateKey(ref.Private), ton.GetPublicKey(ref.Public), nil
}

// Engages the RESTful API
func Engage(logger *logrus.Logger) (merr error) {

	defer func() {

		if r := recover(); r != nil {
			merr = r.(error)
		}
	}()

	priv, pub, err := getKeys()

	if err != nil {

		goto culminate
	}

	{
		// Authentication middleware
		requireTokenAut := func(rw http.ResponseWriter, req *http.Request, next http.HandlerFunc) {

			isNotBlackListed := func() bool {

				tokenStr := req.Header.Get("Authorization")

				answer, err := dal.IsInBlackList(tokenStr)

				if err != nil {

					logger.Println("Issue detected at data abstraction layer: %s", err.Error())
					logger.Println("Perhaps token ( %s ) is not blacklisted", tokenStr)
					return true
				}

				return !answer
			}

			tokenReq, err := ton.ExtractFromReq(pub, req, true)

			if err == nil && tokenReq.Valid && isNotBlackListed() {
				next(rw, req)
			} else {
				rw.WriteHeader(http.StatusUnauthorized)
			}
		}

		tcSettings := &aut.TokenClerkSettings{priv, pub, getExpDelta()}
		clerk := aut.NewTokenClerk(logger, tcSettings)

		/* The connection of both components occurs through
		   the router glue and its adaptive functions */
		glue := func(api *rsapi.RestAPI) *mux.Router {

			router := mux.NewRouter()

			v1 := router.PathPrefix("/v1").Subrouter()

			mgmt := v1.PathPrefix("/sso").Subrouter()

			mgmt.HandleFunc("/token-auth", co.SignOn(clerk.IssueToken)).Methods("POST")

			mgmt.Handle("/logout", negroni.New(
				negroni.HandlerFunc(requireTokenAut),
				negroni.HandlerFunc(
					func(w http.ResponseWriter, r *http.Request, next http.HandlerFunc) {

						co.SignOff(clerk.CeaseToken)(w, r)
					},
				),
			)).Methods("GET")

			{
				const userIDMask string = "[[:alnum:]\\-]+"

				mgmt.Handle(fmt.Sprintf("/{user_id:%s}/refresh-token-auth", userIDMask), negroni.New(
					negroni.HandlerFunc(requireTokenAut),
					negroni.HandlerFunc(
						func(w http.ResponseWriter, r *http.Request, next http.HandlerFunc) {

							co.Revive(clerk.RefreshToken)(w, r)
						},
					),
				)).Methods("POST")
			}

			return router
		}

		api := rsapi.NewRestAPI(logger, &apiSettings, glue)

		api.PowerOn()
	}

culminate:

	return err
}
