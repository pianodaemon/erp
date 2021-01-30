FROM dockette/stretch
MAINTAINER eplauchu@agnux.com

RUN apt-get update
RUN apt-get install -y python3-pip \
    python3-psycopg2 python3-zeep python3-pip python3-lxml \
    python3-reportlab python3-unidecode python3-qrcode \
    xsltproc git

COPY service /service
WORKDIR /service

RUN pip3 install -r requirements.txt

EXPOSE 10080

CMD [ "python3", "./run.py" ]
