FROM ubuntu:bionic AS warehouse-api
MAINTAINER eplauchu@immortalcrab.com

RUN apt-get update

RUN apt-get install -y wget openjdk-11-jdk default-jdk \
    && rm -rf /var/lib/apt/lists/*

ENV JAVA_HOME="/usr/lib/jvm/default-java"

COPY service /service
WORKDIR /service

RUN wget -O maven.tar.gz https://downloads.apache.org/maven/maven-3/3.6.3/binaries/apache-maven-3.6.3-bin.tar.gz
RUN mkdir /maven
RUN tar -C /maven -xzf maven.tar.gz
RUN rm maven.tar.gz

RUN /maven/apache-maven-3.6.3/bin/mvn package

CMD ["/maven/apache-maven-3.6.3/bin/mvn", "exec:java"]
