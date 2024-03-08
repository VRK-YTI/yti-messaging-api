# Pull base image
FROM yti-docker-java-base:corretto-11.0.22

# Deploy project
ADD target/yti-messaging-api.jar ${deploy_dir}/yti-messaging-api.jar

# Expose port
EXPOSE 9801

# Set default command on run
ENTRYPOINT ["/bootstrap.sh", "yti-messaging-api.jar"]
