FROM ubuntu:22.04

# Config
ENV TZ=Europe/Warsaw
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone && \
    apt-get update && apt-get upgrade && apt-get install -y wget unzip nano && \
    echo '# Created by docker:' >> /root/.bashrc

# Python 3.8
RUN apt-get install -y software-properties-common && \
    add-apt-repository -y ppa:deadsnakes/ppa && \
    apt-get update && apt-get install -y python3.8 && \
    echo 'alias python3=python3.8' >> /root/.bashrc && alias python3=python3.8

# Java 8
RUN apt-get install -y openjdk-8-jdk

# Kotlin 1.4.0
RUN wget https://github.com/JetBrains/kotlin/releases/download/v1.4.0/kotlin-compiler-1.4.0.zip && \
    unzip -d /opt kotlin-compiler-1.4.0.zip && rm kotlin-compiler-1.4.0.zip && \
    echo 'export PATH=$PATH:/opt/kotlinc/bin' >> /root/.bashrc
ENV PATH=$PATH:/opt/kotlinc/bin

# Gradle 7.5.1
RUN wget https://services.gradle.org/distributions/gradle-7.5.1-bin.zip && \
    unzip -d /opt gradle-7.5.1-bin.zip && rm gradle-7.5.1-bin.zip && \
    echo 'export PATH=$PATH:/opt/gradle-7.5.1/bin' >> /root/.bashrc
ENV PATH=$PATH:/opt/gradle-7.5.1/bin

# Hello World - CMD
COPY hello-world-cmd/ /root/hello-world-cmd/
RUN kotlinc /root/hello-world-cmd/HelloWorld.kt -include-runtime -d /root/hello-world-cmd/HelloWorld.jar && \
    java -jar /root/hello-world-cmd/HelloWorld.jar

# Hello World - Gradle
COPY hello-world-gradle/ /root/hello-world-gradle/
RUN cd /root/hello-world-gradle && gradle build && gradle run
