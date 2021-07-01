#FROM maven:3.6.3-jdk-8-slim as builder
#FROM maven:3.6.3-adoptopenjdk-15 as builder
FROM adoptopenjdk:8-jdk-hotspot as builder
ENV MAVEN_OPTS="-XX:+TieredCompilation -XX:TieredStopAtLevel=1"
WORKDIR /src
RUN apt update && apt install -y libpcap-dev libatomic1 maven
COPY pom.xml /src
RUN mvn dependency:go-offline
COPY . /src
RUN mvn -T8 -Dmaven.test.skip=true -DskipTests package