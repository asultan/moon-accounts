FROM adoptopenjdk/openjdk11:ubi
ARG JAR_FILE=target/accounts-1.0.0.jar
COPY ${JAR_FILE} accounts.jar
ENTRYPOINT ["java","-jar","/accounts.jar"]