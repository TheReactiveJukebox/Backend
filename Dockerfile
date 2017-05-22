FROM tomcat:9-alpine
MAINTAINER Danny Textores <textores.danny@gmail.com>

RUN rm -rf /usr/local/tomcat/webapps/ROOT
COPY target/server.war /usr/local/tomcat/webapps/ROOT.war