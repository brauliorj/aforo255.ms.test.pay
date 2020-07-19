FROM openjdk:13
VOLUME /tmp
EXPOSE 7006
ADD ./target/aforo255.ms.test.pay-0.0.1-SNAPSHOT.jar af255-test-app-pay.jar
ENTRYPOINT ["java","-jar","-Dspring.profiles.active=docker","-Dspring.cloud.config.uri=http://af255-test-server-config:7777","/af255-test-app-pay.jar"]