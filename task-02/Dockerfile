FROM pawelduraj/android-t1:1.0.0

# Java 17
RUN apt-get remove -y openjdk-8-jdk && \
    apt-get install -y openjdk-17-jdk

# Kotlin 1.7.20
RUN rm -r /opt/kotlinc && \
    wget https://github.com/JetBrains/kotlin/releases/download/v1.7.20/kotlin-compiler-1.7.20.zip && \
    unzip -d /opt kotlin-compiler-1.7.20.zip && rm kotlin-compiler-1.7.20.zip && \
    echo 'export PATH=$PATH:/opt/kotlinc/bin' >> /root/.bashrc
ENV PATH=$PATH:/opt/kotlinc/bin

# ngrok
RUN wget https://bin.equinox.io/c/bNyj1mQVY4c/ngrok-v3-stable-linux-amd64.tgz && \
    tar -xvzf ngrok-v3-stable-linux-amd64.tgz && rm ngrok-v3-stable-linux-amd64.tgz && \
    mv ngrok /usr/bin

# Ktor
COPY ./ /root/ktor/
RUN cd /root/ktor && gradle build && chmod +x /root/ktor/start.sh
CMD ./root/ktor/start.sh && /bin/bash
