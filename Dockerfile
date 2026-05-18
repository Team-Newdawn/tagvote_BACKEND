FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

COPY build/libs/tagvote.jar /app/app.jar

ENV JAVA_TOOL_OPTIONS="-Xms256m -Xmx512m -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/tmp"

EXPOSE 8080

ENTRYPOINT ["java", "-Duser.timezone=Asia/Seoul", "-jar", "/app/app.jar"]
