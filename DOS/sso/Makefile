GOCMD=go
GOBUILD=$(GOCMD) build -ldflags="-w -s" 

SSO_EXE=/sso

all: build

build: fmt
	CGO_ENABLED=0 \
	GOOS=linux \
	GOARCH=amd64 \
	$(GOBUILD) -o $(SSO_EXE) cmd/http/run.go

fmt:
	$(GOCMD) fmt ./...

clean:
	rm -f $(SSO_EXE)
