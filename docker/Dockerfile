# Pull base image
FROM yti-docker-java-base:alpine

# Deploy project
ADD target/yti-messaging-api.jar ${deploy_dir}/yti-messaging-api.jar

# If you want to serve static content (e.g. /yti-messaging/swagger/index.html)
# from spring boot application that is running inside docker container you shall run into
# e.g. following problem:
#
# WARN  o.e.jetty.servlet.ServletHandler - /yti-codelist-intake-service/swagger/index.html
# java.io.FileNotFoundException: class path resource [static/swagger/index.html] cannot be resolved in the file system for resolving its last-modified timestamp
#
# Reason for this behaviour is explained here: https://jira.spring.io/browse/SPR-12862
# Workaround is described here: https://spring.io/guides/gs/spring-boot-docker/
#
# Enable following command to solve problem:
RUN bash -c 'touch ${deploy_dir}/yti-messaging-api.jar'

# Expose port
EXPOSE 9801

# Set default command on run
ENTRYPOINT ["/bootstrap.sh", "yti-messaging-api.jar"]
