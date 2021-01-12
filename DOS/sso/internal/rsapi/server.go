package rsapi

import (
	"context"
	"encoding/json"
	"fmt"
	"net/http"
	"os"
	"os/signal"
	"strconv"
	"sync/atomic"
	"syscall"
	"time"

	"github.com/gorilla/mux"
	"github.com/meatballhat/negroni-logrus"
	"github.com/sirupsen/logrus"
	"github.com/urfave/negroni"
)

// Configuration variables required to spin up
// the Restful API's server
type RestAPISettings struct {
	Sport int   `default:"10090"`
	Mmu   int64 `default:"41943040"`
}

// Represents a runnable RESTful API
type RestAPI struct {
	config  *RestAPISettings
	logger  *logrus.Logger
	server  *http.Server
	Healthy int64
	done    chan bool
	quit    chan os.Signal
}

func healthCheck(healthy *int64) func(w http.ResponseWriter, r *http.Request) {

	return func(w http.ResponseWriter, r *http.Request) {

		if h := atomic.LoadInt64(healthy); h > 0 {

			if r.Method == "HEAD" {
				w.WriteHeader(http.StatusNoContent)
			} else {
				type Response struct {
					Uptime string `json:"uptime"`
				}

				w.Header().Set("Content-Type", "application/json")
				w.Header().Set("X-Content-Type-Options", "nosniff")
				json.NewEncoder(w).Encode(Response{
					Uptime: fmt.Sprintf("%s", time.Since(time.Unix(0, h))),
				})
			}

			return
		}
		w.WriteHeader(http.StatusServiceUnavailable)
	}
}

// Creates an instance of a RestAPI type
func NewRestAPI(logger *logrus.Logger, config *RestAPISettings,
	initRoutes func(*RestAPI) *mux.Router) *RestAPI {

	api := &RestAPI{
		config: config,
		logger: logger,
	}

	n := negroni.New()
	n.Use(negronilogrus.NewMiddlewareFromLogger(logger, "web"))

	router := initRoutes(api)

	// Health check is strictly embedded within this mechanism
	router.HandleFunc("/health-check", healthCheck(&api.Healthy)).Methods("HEAD", "GET")

	n.UseHandler(router)

	var listenAddr string = fmt.Sprintf(":%s", strconv.Itoa(api.config.Sport))

	api.server = &http.Server{
		Addr:         listenAddr,
		Handler:      n,
		ReadTimeout:  5 * time.Second,
		WriteTimeout: 60 * time.Second,
		IdleTimeout:  15 * time.Second,
	}

	return api
}

// Wrapper to ask for The Maximum Memory for Upload
func (api *RestAPI) GetMemMapSizeUpload() int64 {
	return api.config.Mmu
}

// Starts the RESTful API mechanism
func (api *RestAPI) PowerOn() {
	api.done = make(chan bool)
	api.quit = make(chan os.Signal, 1)
	signal.Notify(api.quit, syscall.SIGINT, syscall.SIGTERM)

	go api.shutdown()

	api.logger.Println("Server is ready to handle requests at", api.server.Addr)
	atomic.StoreInt64(&api.Healthy, time.Now().UnixNano())
	if err := api.server.ListenAndServe(); err != nil && err != http.ErrServerClosed {
		api.logger.Fatalf("Could not listen on %s: %v\n", api.server.Addr, err)
	}

	<-api.done
	api.logger.Println("Server stopped")
}

// Shutdowns the RESTful API mechanism gracefully
func (api *RestAPI) shutdown() {
	<-api.quit
	api.logger.Println("Server is shutting down...")
	atomic.StoreInt64(&api.Healthy, 0)

	ctx, cancel := context.WithTimeout(context.Background(), 30*time.Second)
	defer cancel()

	api.server.SetKeepAlivesEnabled(false)
	if err := api.server.Shutdown(ctx); err != nil {
		api.logger.Fatalf("Could not gracefully shutdown the server: %v\n", err)
	}
	close(api.done)
}
