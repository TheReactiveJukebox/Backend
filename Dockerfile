FROM tomcat:9-alpine
MAINTAINER Danny Textores <textores.danny@gmail.com>

RUN rm -rf /usr/local/tomcat/webapps/ROOT && \
    sed -i /usr/local/tomcat/conf/server.xml -e 's/port="8080"/port="80"/g'

COPY target/server.war /usr/local/tomcat/webapps/ROOT.war

# mount volume for event log
VOLUME /usr/local/tomcat/logger/
