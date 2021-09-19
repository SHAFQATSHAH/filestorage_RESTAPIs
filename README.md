This project implements a file storage system through REST APIs to perform CRUD operations.

### Technology Stack:-
* Java 11
* Spring Boot 2.5.3
* Maven
* Docker
* MySQL 8
* Swagger 2
* jUnit

### Prerequisites:-
1) Docker, Maven and Java should be installed on environment to build this project.

### Installation/Setup:-
1) Clone repository.
2) Browse to cloned repository's root directory and build project using following command in terminal.
   ```mvn clean install -DskipTests=true```
   Note: Docker image will be built with image tag "filestorage:1.0.0"
3) Execute following command to deploy MYSQL container.
   ```docker-compose up mysql-db```
   Wait for few minutes for MYSQL to deploy.
4) Open a new terminal in project's root directory and execte following command to deploy file storage app.
   ```docker-compose up file-storage-system```
5) Access swagger UI through following URL: http://localhost:8085/swagger-ui/index.html
6) Use credentials ```admin/admin``` to login.
