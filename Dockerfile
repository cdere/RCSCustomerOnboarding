FROM openjdk:17-oracle
EXPOSE 8080
ADD target/customer-onboaring-application.jar customer-onboaring-application.jar
LABEL authors="cdere"

ENTRYPOINT ["java","-jar","customer-onboaring-application.jar"]