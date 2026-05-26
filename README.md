# auth-server-group7
Authentication and authorization server for a webshop microservice group project.

Tech tree:
Java 21
Spring Boot
Spring Security
JWT
PostgreSQL
Maven
Docker
Docker Hub
Render
GitHub Actions

Features:
- User registration
- User login
- JWT authentication
- RSA signed JWT token
- JWKS endpoint for public key verification
- Role based authorization
- PostgreSQL database
- Swagger
- Docker
- Render
- Github Actions

Base Url: https://auth-server-group7.onrender.com
Swagger: https://auth-server-group7.onrender.com/swagger-ui/index.html
JWKS endpoint: https://auth-server-group7.onrender.com/auth/jwks
Docker hub: https://hub.docker.com/r/94059102/auth-server-group7

Authentication flow:
User registers or logs in -> Auth server authenticates the user -> JWT token gets created and signed with private RSA key -> other services verify the token using public key from /auth/jwks
