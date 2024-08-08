# Stage 1: Build the application
FROM maven:3.8.5-openjdk-17-slim AS build

# Install necessary libraries, vim, and bash completion
RUN apt-get update && \
    apt-get install -y libxext6 libxrender1 libxtst6 libxi6 libx11-dev libfreetype6 fontconfig fonts-dejavu-core vim bash bash-completion && \
    apt-get clean

# Set the working directory inside the container
WORKDIR /app

# Copy the pom.xml file and the source code into the container
COPY pom.xml .
COPY src ./src

# Clean and build the application with detailed logging
RUN mvn clean package -X

# Stage 2: Create the final image
FROM openjdk:17-jdk-slim

# Install necessary libraries, vim, and bash completion
RUN apt-get update && \
    apt-get install -y libxext6 libxrender1 libxtst6 libxi6 libx11-dev libfreetype6 fontconfig fonts-dejavu-core vim bash bash-completion && \
    apt-get clean

# Set the working directory in the container
WORKDIR /app

# Copy the executable JAR file from the build stage
COPY --from=build /app/target/servent-1.0-SNAPSHOT-shaded.jar /app/servent.jar

# Set bash as the default shell
SHELL ["/bin/bash", "-c"]

# Enable bash completion by sourcing it in the bashrc
RUN echo "source /usr/share/bash-completion/bash_completion" >> ~/.bashrc

# Run the application
CMD ["java", "-jar", "servent.jar"]

