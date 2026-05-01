FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

COPY build/libs/tagvote.jar /app/app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-Duser.timezone=Asia/Seoul", "-jar", "/app/app.jar"]
