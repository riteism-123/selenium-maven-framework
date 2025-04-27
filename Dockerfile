# Base image with Maven and Java
FROM maven:3.9.5-eclipse-temurin-17

# Install Chrome
RUN apt-get update && apt-get install -y wget gnupg unzip && \
    wget -q -O - https://dl-ssl.google.com/linux/linux_signing_key.pub | apt-key add - && \
    sh -c 'echo "deb [arch=amd64] http://dl.google.com/linux/chrome/deb/ stable main" >> /etc/apt/sources.list.d/google-chrome.list' && \
    apt-get update && apt-get install -y google-chrome-stable

# Workdir
WORKDIR /app

# Preload dependencies
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy code
COPY . .

# Run tests with headless mode forced
CMD ["mvn", "clean", "test", "-Dheadless=true"]
