FROM amazoncorretto:11
COPY target/*.jar app.jar
#COPY common/target/shareit-common-0.0.1-SNAPSHOT.jar /shareit-gateway/libs/
#ENV CLASSPATH="/shareit-gateway/libs/shareit-common-0.0.1-SNAPSHOT.jar"
#ENTRYPOINT ["java","-jar","/app.jar", "$CLASSPATH"]
ENTRYPOINT ["java","-jar","/app.jar"]