FROM openjdk:17
RUN mkdir /myapp 
WORKDIR /myapp
COPY . .  
ENTRYPOINT ["java", "-jar" ,"target/demo-0.0.1-SNAPSHOT.jar"]
EXPOSE 8080  