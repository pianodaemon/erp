FROM dockette/jessie AS legacy-builder
MAINTAINER enygma@immortalcrabsystems.com

RUN apt-get update

# JDK 7 and more to build war
RUN apt-get install -y wget openjdk-7-jdk default-jdk \
    && rm -rf /var/lib/apt/lists/*

ENV JAVA_HOME="/usr/lib/jvm/default-java"


WORKDIR /yield/r1

RUN wget -O maven.tar.gz https://downloads.apache.org/maven/maven-3/3.6.3/binaries/apache-maven-3.6.3-bin.tar.gz
RUN mkdir /maven
RUN tar -C /maven -xzf maven.tar.gz
RUN rm maven.tar.gz

CMD ["mvn"]
