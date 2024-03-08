FROM openjdk:17-alpine

ADD /build/libs/api-gateway-0.0.1-SNAPSHOT.jar /app/

CMD ["java", "-Xmx200m", "-jar", "/app/api-gateway-0.0.1-SNAPSHOT.jar"]

EXPOSE 8083