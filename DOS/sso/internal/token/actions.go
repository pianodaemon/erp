package token

import (
	"crypto/rsa"
	"fmt"
	"net/http"
	"time"

	jwt "github.com/dgrijalva/jwt-go"
	request "github.com/dgrijalva/jwt-go/request"
)

func RemainingValidity(token *jwt.Token) int {

	const (
		expireOffset = 3600
	)

	var timestamp interface{} = token.Claims.(jwt.MapClaims)["exp"]

	if validity, ok := timestamp.(float64); ok {

		tm := time.Unix(int64(validity), 0)
		remainer := tm.Sub(time.Now())

		if remainer > 0 {
			return int(remainer.Seconds() + expireOffset)
		}
	}

	return expireOffset
}

// Extracts token from a http resquest by involving public key
func ExtractFromReq(publicKey *rsa.PublicKey, req *http.Request, checkSignMethod bool) (*jwt.Token, error) {

	tokenRequest, err := request.ParseFromRequest(
		req,
		request.OAuth2Extractor,
		func(token *jwt.Token) (interface{}, error) {

			if checkSignMethod {

				if _, ok := token.Method.(*jwt.SigningMethodRSA); !ok {

					return nil, fmt.Errorf("Unexpected signing method: %v", token.Header["alg"])
				}
			}

			return publicKey, nil
		})

	if err != nil {
		return nil, err
	}

	return tokenRequest, err
}

// Generates a token along with its claims by signing with private key
func Generate(privateKey *rsa.PrivateKey, expirationDelta int, userUID string, userAuths string) (string, error) {

	token := jwt.New(jwt.SigningMethodRS512)

	token.Claims = jwt.MapClaims{
		"exp":         time.Now().Add(time.Hour * time.Duration(expirationDelta)).Unix(),
		"iat":         time.Now().Unix(),
		"sub":         userUID,
		"authorities": userAuths,
	}

	tokenString, err := token.SignedString(privateKey)

	if err != nil {

		return "", err
	}

	return tokenString, nil
}
