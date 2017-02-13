FROM krashid/centos-java-gradle

RUN echo THIS DOCKER CONTAINER IS FOR PRODUCTION USE ONLY

# COPY java application to image
RUN mkdir /usr/dev
WORKDIR /usr/dev
COPY . /usr/dev/krashidbuilt-java-api/
WORKDIR /usr/dev/krashidbuilt-java-api/

RUN pwd

# run all of the the tests if the exit code is anything other than 0 (a test fails) the image will not build
RUN ./gradlew clean test integrationTest -Penvironment=integration-test

#set the entry point to launch the app without any tests (they should have been ran when the image was built)
ENTRYPOINT ["./gradlew", "clean", "build", "-x", "test", "-x", "checkStyleMain", "-x", "pmdMain", "-x", "findBugsMain", "jettyRun"]
EXPOSE 8080



####################################################################################################
####################################################################################################
####################################################################################################
# BUILD THE DOCKER IMAGE
# docker build --no-cache=true -t krashidbuilt-java-api .
#
# RUN THE IMAGE IN A CONTAINER
# docker run -it --rm -p 8888:8080 krashidbuilt-java-api
####################################################################################################
####################################################################################################
####################################################################################################
