FROM tomcat:9-alpine
MAINTAINER Danny Textores <textores.danny@gmail.com>

RUN rm -rf /usr/local/tomcat/webapps/ROOT && \
    sed -i /usr/local/tomcat/conf/server.xml -e 's/port="8080"/port="80"/g'

COPY target/server.war /usr/local/tomcat/webapps/ROOT.war

# log4j configfile
COPY log4j2.xml /usr/local/tomcat/log4j2.xml
ENV LOG4J_CONFIGURATION_FILE=/usr/local/tomcat/log4j2.xml

# set environment variables TODO remove, add to compose file
ENV SPOTIFY_CLIENT_ID=06b4a989398d48728a998eb0450d2e5a
ENV SPOTIFY_CLIENT_SECRET=22b61a1301634a72aae6e6b8064c3ecd
ENV JUKEBOX_INVITE_KEY=xxx
