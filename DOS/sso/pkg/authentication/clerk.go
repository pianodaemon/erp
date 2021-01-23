package authentication

import (
	"crypto/rsa"
	"encoding/json"
	"net/http"

	dal "immortalcrab.com/sso/internal/storage"
	ton "immortalcrab.com/sso/internal/token"

	"github.com/sirupsen/logrus"
)

type (

	// Settings for the clerk in charge of the tokens
	TokenClerkSettings struct {
		PrivateKey      *rsa.PrivateKey
		PublicKey       *rsa.PublicKey
		ExpirationDelta int
	}

	// Represents a clerk in charge of the tokens
	TokenClerk struct {
		config *TokenClerkSettings
		logger *logrus.Logger
	}

	TokenAuthentication struct {
		Token string `json:"token" form:"token"`
	}
)

// Spawns an newer instance of the clerk in charge of the tokens
func NewTokenClerk(logger *logrus.Logger,
	config *TokenClerkSettings) *TokenClerk {

	return &TokenClerk{
		config: config,
		logger: logger,
	}
}

// Ceases a token that has been embedded within the request
func (self *TokenClerk) CeaseToken(req *http.Request) error {

	tokenReq, err := ton.ExtractFromReq(self.config.PublicKey, req, false)

	if err != nil {

		return err
	}

	err = dal.Expire(req.Header.Get("Authorization"), tokenReq)

	if err != nil {

		return err
	}

	return nil
}

// Issues a newer token once the authentication ran smoothly
func (self *TokenClerk) IssueToken(username, password string) ([]byte, error) {

	user, err := dal.Authenticate(username, password)

	if err != nil {

		return nil, err
	}

	token, err := ton.Generate(self.config.PrivateKey, self.config.ExpirationDelta, user.UID)

	if err != nil {

		return nil, err
	}

	response, _ := json.Marshal(TokenAuthentication{Token: token})

	return response, nil
}

// Allow the application to ask sso to issue a newer
// access token without having to re-authenticate the user
func (self *TokenClerk) RefreshToken(userID string) ([]byte, error) {

	token, err := ton.Generate(self.config.PrivateKey, self.config.ExpirationDelta, userID)

	if err != nil {

		return nil, err
	}

	response, err := json.Marshal(TokenAuthentication{Token: token})

	if err != nil {
		return []byte{}, err
	}

	return response, nil
}
