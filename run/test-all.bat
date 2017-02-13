@echo off
setlocal
cd ..
.\gradlew clean build test integrationTest -Penvironment=integration-test