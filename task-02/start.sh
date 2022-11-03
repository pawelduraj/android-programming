#!/bin/bash

cd /root/ktor
nohup gradle run >gradle.log 2>&1 &
echo 'App is running in the background!'
ngrok http 8080
