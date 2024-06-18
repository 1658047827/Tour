FROM maven:3.8.1-openjdk-17
WORKDIR /usr/src/backend
COPY . .
COPY settings.xml /usr/share/maven/conf/settings.xml
RUN mvn install
RUN mvn package
CMD ["java", "-jar", "target/backend-1.0-SNAPSHOT.jar"]