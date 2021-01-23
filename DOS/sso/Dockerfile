# Building stage
FROM golang:1.15-alpine as builder

LABEL MAINTAINER="Edward Nygma <j4nusx@yahoo.com>"

WORKDIR /go/src/immortalcrab.com/sso

COPY Makefile go.sum go.mod ./

RUN go mod download

COPY pkg pkg

COPY internal internal

COPY cmd cmd

RUN apk add make
RUN make clean build

# Final image
FROM ubuntu:bionic
LABEL MAINTAINER="Edward Nygma <j4nusx@yahoo.com>"

# getting last software catalog
RUN apt-get update --fix-missing

# Minimal deb packages required
RUN apt-get install -y --no-install-recommends nginx

# Remove downloaded .debs from cache
RUN apt-get clean

# no longer needed list of packages
RUN rm -rf /var/lib/apt/lists/*

# replacement for default site enabled on distro
RUN rm /etc/nginx/sites-enabled/default
COPY ./nginx_site /etc/nginx/sites-enabled/default

ENV APP_DIR=/
COPY --from=builder /sso $APP_DIR
COPY scripts/run_service.sh $APP_DIR
WORKDIR $APP_DIR

EXPOSE 10080

CMD ["/run_service.sh"]
