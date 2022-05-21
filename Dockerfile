FROM maven:3.8.5-openjdk-11
MAINTAINER "Gabor Bata"

ENV WAIT_VERSION 2.7.2
ADD https://github.com/ufoscout/docker-compose-wait/releases/download/$WAIT_VERSION/wait /wait
RUN chmod +x /wait

ADD . /app
WORKDIR /app
RUN mvn clean package
CMD /wait && mvn spring-boot:run
