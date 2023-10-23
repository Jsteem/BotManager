FROM eclipse-temurin:17-jdk-jammy

RUN apt-get update \
    && apt-get upgrade -y \
    && apt-get install -yqq --no-install-recommends libxext6 libxrender1 libxtst6 libxi6 \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/* /tmp/* /var/tmp/*



# Copy the OSRSBot.jar file to the container
COPY OSRSBot/OSRSBot.jar /app/OSRSBot.jar

# Create the directory path inside the container
RUN mkdir -p /root/.config/OsrsBot/Scripts/Sources

# Copy the files from the host directory to the container directory
COPY Scripts /root/.config/OsrsBot/Scripts/Sources
COPY Cache /root/.config/OsrsBot/Cache 

# Set the working directory
WORKDIR /app

# Run the command
CMD java -jar OSRSBot.jar --bot-runelite --developer-mode -debug