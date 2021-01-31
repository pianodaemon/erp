FROM python:3.6-slim-buster
LABEL maintainer="eplauchu@agnux.com"

RUN apt-get update
RUN apt-get install -y build-essential libxml2-dev libxslt1-dev lib32z1-dev libpq-dev xsltproc git

COPY service /service
WORKDIR /service

RUN python -m pip install --upgrade pip
RUN python -m pip install -r requirements.txt

EXPOSE 10080

CMD [ "python", "./run.py" ]
