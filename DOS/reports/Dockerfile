FROM ubuntu:bionic AS reports-builder
MAINTAINER eplauchu@agnux.com

RUN apt-get update

RUN apt-get install -y wget openjdk-11-jdk default-jdk \
    && rm -rf /var/lib/apt/lists/*

ENV JAVA_HOME="/usr/lib/jvm/default-java"


WORKDIR /yield/godinez

RUN wget -O maven.tar.gz https://downloads.apache.org/maven/maven-3/3.6.3/binaries/apache-maven-3.6.3-bin.tar.gz
RUN mkdir /maven
RUN tar -C /maven -xzf maven.tar.gz
RUN rm maven.tar.gz

CMD ["mvn"]
