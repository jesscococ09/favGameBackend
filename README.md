# FavGame Backend

A REST API built with Kotlin and Spring Boot for the FavGame video game tracker. The API lets users manage a personal game library, update profile information, and view shared game recommendations. Administrators can manage users and shared recommendations.

## Features

- **GitHub authentication**: users authenticate through GitHub OAuth using Authorization Code + PKCE
- **User profiles**: retrieve a profile, update display name and icon, and delete an account
- **Personal game library**: create, view, update, replace, search, filter, sort, and delete games
- **Game ratings and comments**: users can store ratings and optional comments with their games
- **Recommendations**: authenticated users can view shared game recommendations
- **Admin controls**: administrators can list users, view users, grant or revoke admin privileges, and delete users
- **Admin recommendations**: administrators can create, update, replace, and delete recommendations
- **Hosted database**: application data is stored using Supabase PostgreSQL
- **Docker deployment**: multi-stage Docker build for the Spring Boot backend
- **Public API**: backend deployed through Render

## Tech Stack

- Kotlin
- Spring Boot
- Spring Web
- Spring Security
- Spring Data JPA
- Gradle
- PostgreSQL
- Supabase
- GitHub OAuth
- Docker
- Render
- JUnit 5
- JaCoCo

## API URL

Production:

```text
https://favgame-backend.onrender.com
```

API base path:

```text
/favGame/v1
```

## Getting Started

### Prerequisites

- Java 17
- Gradle Wrapper included with the project
- Docker
- PostgreSQL database or Supabase database connection
- GitHub OAuth application

## Setup

1. Clone the repository:

```bash
git clone https://github.com/jesscococ09/favGameBackend.git
cd favGameBackend
```

2. Configure the required database and authentication environment variables.

3. Build the project:

```bash
./gradlew build
```

4. Run the Spring Boot application:

```bash
./gradlew bootRun
```

The local API is available at:

```text
http://localhost:8080
```

## Authentication

FavGame uses GitHub OAuth Authorization Code + PKCE.

The Android frontend performs the GitHub authorization flow and receives a GitHub access token.

Protected API requests send the token using:

```text
Authorization: Bearer <GitHub access token>
```

The backend uses the GitHub token to identify the authenticated user. If the GitHub user does not already have a FavGame account, the backend creates one.

Users with:

```text
isAdmin = false
```

receive normal user permissions.

Users with:

```text
isAdmin = true
```

receive administrator permissions.

## API Endpoints

### Users

```text
GET    /favGame/v1/users
PATCH  /favGame/v1/users/name
PATCH  /favGame/v1/users/icon
DELETE /favGame/v1/users
```

### Personal Games

```text
GET    /favGame/v1/users/games
POST   /favGame/v1/users/games
GET    /favGame/v1/users/games/search
GET    /favGame/v1/users/games/{id}
PATCH  /favGame/v1/users/games/{id}
PUT    /favGame/v1/users/games/{id}
DELETE /favGame/v1/users/games/{id}
POST   /favGame/v1/users/games/{id}/comments
```

### Recommendations

```text
GET    /favGame/v1/recommendations
POST   /favGame/v1/recommendations
PATCH  /favGame/v1/recommendations/{id}
PUT    /favGame/v1/recommendations/{id}
DELETE /favGame/v1/recommendations/{id}
```

Creating, updating, replacing, and deleting recommendations requires administrator privileges.

### Admin

```text
GET    /favGame/v1/admin
GET    /favGame/v1/admin/{userId}
PATCH  /favGame/v1/admin/{userId}
DELETE /favGame/v1/admin/{userId}
```

Admin endpoints require administrator privileges.

## Pagination

The personal game collection uses page-based pagination.

Example:

```text
GET /favGame/v1/users/games?page=0&size=20
```

`page` identifies the requested page and `size` controls the number of games returned.

## Filtering and Sorting

Personal games can be filtered by genre:

```text
GET /favGame/v1/users/games?genre=RPG
```

Personal games support sorting by:

```text
alphabetical
rating
genre
```

Example:

```text
GET /favGame/v1/users/games?sort=alphabetical
```

Games can also be searched by name:

```text
GET /favGame/v1/users/games/search?query=Zelda
```

## PUT vs POST

`POST` creates a new resource and is not idempotent.

Examples:

```text
POST /favGame/v1/users/games
POST /favGame/v1/recommendations
```

`PUT` replaces an existing resource and is idempotent.

Examples:

```text
PUT /favGame/v1/users/games/{id}
PUT /favGame/v1/recommendations/{id}
```

## API Versioning

FavGame endpoints use the versioned base path:

```text
/favGame/v1
```

Versioning allows later API changes to be introduced without changing the behavior expected by clients using version 1.

## Error Model

FavGame uses one RFC 9457 Problem Details structure for API errors:

```json
{
  "type": "about:blank",
  "title": "Not Found",
  "status": 404,
  "detail": "Game not found.",
  "instance": "/favGame/v1/users/games/123"
}
```

Standard error responses use:

```text
Content-Type: application/problem+json
```

The API documents:

```text
400 Bad Request
401 Unauthorized
403 Forbidden
404 Not Found
500 Internal Server Error
```

## Database

FavGame uses Supabase PostgreSQL as the hosted database.

The primary application data consists of:

```text
User
API
RecommendedGames
```

`User` stores user account information and the `isAdmin` role value.

`API` stores games belonging to individual users.

`RecommendedGames` stores the shared recommendation list.

The Android application does not connect directly to the database. The Android application communicates with the backend API, and the backend performs database operations.

## Docker

The backend uses a multi-stage Dockerfile.

```dockerfile
FROM eclipse-temurin:17-jdk AS build
WORKDIR /app

COPY gradlew settings.gradle.kts build.gradle.kts ./
COPY gradle/ gradle/
RUN chmod +x gradlew && ./gradlew dependencies --no-daemon --quiet

COPY src/ src/
RUN ./gradlew bootJar --no-daemon --quiet

FROM eclipse-temurin:17-jre AS runtime
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
```

The first stage uses the JDK and Gradle to build the Spring Boot jar. The second stage contains the Java runtime and resulting jar without copying the source tree or Gradle build environment into the final image.

## Build Docker Image

Build the backend image:

```bash
docker build -t favgame-backend .
```

Build an amd64 image:

```bash
docker buildx build --platform linux/amd64 -t favgame-backend:amd64 .
```

## Run with Docker

Run the backend locally:

```bash
docker run -p 8080:8080 favgame-backend
```

The API will be available at:

```text
http://localhost:8080
```

Database credentials and other secrets should be supplied at runtime rather than stored in the Docker image.

## Docker Compose

Start the API and local development database:

```bash
docker compose up -d --build
```

Stop the services:

```bash
docker compose down
```

The local PostgreSQL database is used for development. The deployed backend uses the remote hosted Supabase database.

## Deployment

The backend is deployed to Render.

Production URL:

```text
https://favgame-backend.onrender.com
```

The production database runs separately through Supabase rather than inside the application container.

## Testing

Run unit tests:

```bash
./gradlew test
```

Run the full build:

```bash
./gradlew build
```

Run JaCoCo coverage:

```bash
./gradlew jacocoTestReport
```

Run JaCoCo coverage verification:

```bash
./gradlew jacocoTestCoverageVerification
```

The service layer uses JUnit 5 tests with JaCoCo coverage enforcement.

## Documentation

Project design documentation is located in:

```text
docs/
├── adr-choose-github.md
├── adr-choose-supabase.md
├── auth-sequence-diagram.md
├── er-diagram.md
├── error-model.md
├── openapi.yaml
└── proposal.md
```

The documentation includes the OpenAPI contract, ER diagram, OAuth2 PKCE authentication sequence, RFC 9457 error model, project proposal, and architecture decision records.
