FROM eclipse-temurin:17-jdk-alpine
EXPOSE 8080
ADD target/customer-onboaring-application.jar customer-onboaring-application.jar
LABEL authors="cdere"

ENTRYPOINT ["java","-jar","customer-onboaring-application.jar"]