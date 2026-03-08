# Stage 1: Build com Maven
FROM eclipse-temurin:25-jdk-alpine AS build

# Instala dependências do Alpine
RUN apk add --no-cache bash curl unzip

# Instala Maven
RUN mkdir -p /opt/maven \
    && curl -fsSL https://archive.apache.org/dist/maven/maven-3/3.9.3/binaries/apache-maven-3.9.3-bin.zip -o /tmp/maven.zip \
    && unzip /tmp/maven.zip -d /opt/maven \
    && rm /tmp/maven.zip
ENV MAVEN_HOME=/opt/maven/apache-maven-3.9.3
ENV PATH=$MAVEN_HOME/bin:$PATH

# Diretório do app
WORKDIR /app

# Copia arquivos do projeto
COPY pom.xml .
COPY src ./src

# Build do projeto (pulando testes)
RUN mvn clean package -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:25-jdk-alpine

WORKDIR /app

# Copia o JAR gerado do stage 1
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]