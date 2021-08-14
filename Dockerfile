FROM openjdk:11-jre-slim
COPY target/filestorage-1.0.0.jar /filestorage-1.0.0.jar
ENTRYPOINT ["java","-jar","/filestorage-1.0.0.jar"]