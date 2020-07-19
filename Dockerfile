FROM openjdk:11-jdk-slim

EXPOSE 9041

VOLUME /tmp

ADD /target/AlgorithmMasterDB-1.0-SNAPSHOT.jar algorithm-master-db-1.0.jar

ENTRYPOINT ["java","-jar","algorithm-master-db-1.0.jar"]