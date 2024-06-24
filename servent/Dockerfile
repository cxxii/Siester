# Use an official Maven image to build the project
FROM maven:3.8.5-openjdk-17 AS build

# Set the working directory inside the container
WORKDIR /app

# Copy the pom.xml file and the source code into the container
COPY pom.xml .
COPY src ./src

# Clean and build the application with detailed logging
RUN mvn clean package -X

# Inspect the contents of the target directory
RUN ls -l /app/target

# Start with a base image that has Java installed
FROM openjdk:17-jdk-alpine

# Set the working directory in the container
WORKDIR /app

# Copy the executable JAR file
COPY --from=build /app/target/servent-1.0-SNAPSHOT-shaded.jar /app/servent.jar

# Run the application
CMD ["java", "-jar", "servent.jar"]
