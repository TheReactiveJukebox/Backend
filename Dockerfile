FROM tomcat:9-alpine
MAINTAINER Danny Textores <textores.danny@gmail.com>

RUN rm -rf /usr/local/tomcat/webapps/ROOT && \
    sed -i /usr/local/tomcat/conf/server.xml -e 's/port="8080"/port="80"/g'

COPY target/server.war /usr/local/tomcat/webapps/ROOT.war

# log4j configfile
COPY log4j2.xml /usr/local/tomcat/log4j2.xml
ENV LOG4J_CONFIGURATION_FILE=/usr/local/tomcat/log4j2.xml
