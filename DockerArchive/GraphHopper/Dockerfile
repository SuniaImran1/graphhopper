# Builds a graphhopper instance on centos baseline with java-7
FROM centos:latest
MAINTAINER Ordnance Survey WS-PIP

RUN ["yum","-y", "install", "java-1.7.0-openjdk.x86_64"]

ADD apache-tomcat-8.0.20.tar.gz /usr/

RUN ["ln","-s","/usr/apache-tomcat-8.0.20", "/usr/tomcat"]
EXPOSE 8000
EXPOSE 8080
EXPOSE 5701

RUN echo "AWS_ACCESS_KEY_ID=***********" >> /usr/tomcat/conf/catalina.properties
RUN echo "AWS_SECRET_KEY=*************" >> /usr/tomcat/conf/catalina.properties
RUN echo "CONFIG_BUCKET=os-gs-eb" >> /usr/tomcat/conf/catalina.properties
RUN echo "KEYSTORE_CONFIG=api.conf" >> /usr/tomcat/conf/catalina.properties
RUN echo "KEY_PASS=*******" >> /usr/tomcat/conf/catalina.properties
RUN echo "OS_PLACES_KEY=***********" >> /usr/tomcat/conf/catalina.properties
RUN echo "ROUTING_ARCHIVE=DPN.LATEST.tar.bz2" >> /usr/tomcat/conf/catalina.properties
RUN echo "ROUTING_CONFIG=dpn-config.properties" >> /usr/tomcat/conf/catalina.properties
RUN echo "TRUST_PASS=********" >> /usr/tomcat/conf/catalina.properties
RUN echo "WEBAPP_DIR=/var/lib/tomcat7/webapps/graphhopper-web-0.4-SNAPSHOT-t7mp" >> /usr/tomcat/conf/catalina.properties
RUN echo "graphhopper.config=/config.properties" >> /usr/tomcat/conf/catalina.properties
RUN echo "graphhopper.graph.location=/graphhopper-gh" >> /usr/tomcat/conf/catalina.properties
RUN echo "graphhopper_graph_location=/graphhopper-gh" >> /usr/tomcat/conf/catalina.properties

RUN echo "graph.location=/graphhopper-gh" >> /usr/tomcat/conf/catalina.properties

RUN ["rm -rf /usr/tomcat/webapps/ROOT/*"]

ADD graphhopper-web/ /usr/tomcat/webapps/ROOT/
RUN ["sed -i 's/OS_PLACES_KEY/******/g' /usr/tomcat/webapps/ROOT/js/search.js"]
ADD config.properties /config.properties

ADD graphhopper-gh/ /graphhopper-gh/

RUN ["ls -lat /usr/tomcat/webapps/ROOT"]

#expose debug port and start tomcat with jpda debug enabled
ENV JPDA_ADDRESS 8000
ENV JPDA_TRANSPORT dt_socket
CMD /usr/tomcat/bin/catalina.sh jpda start && tail -F /usr/tomcat/logs/catalina.out



