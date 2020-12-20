FROM debian:bullseye
LABEL maintainer="edward@nygma.com"

# getting last software catalog
RUN apt-get update --fix-missing

# Minimal deb packages required
RUN apt-get install -y --no-install-recommends build-essential \
    python3-wheel python3-setuptools python3-pip python3-dev python3-venv\
    postgresql-client libpq-dev

# Remove downloaded .debs from cache
RUN apt-get clean

# no longer needed list of packages
RUN rm -rf /var/lib/apt/lists/*

ENV APP_DIR=/app
COPY service $APP_DIR
COPY protos /protos
WORKDIR $APP_DIR

RUN pip3 install -r requirements.txt

# this is an initial approach
# (a better one may be read a matrix within a txt)
RUN python3 -m grpc_tools.protoc \
    -I../protos \
    --python_out=. \
    --grpc_python_out=. \
    ../protos/sales.proto

RUN chmod 755 /app/docker-entrypoint.sh

EXPOSE 10082 10082

CMD ["/app/docker-entrypoint.sh"]
