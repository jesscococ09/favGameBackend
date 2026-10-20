# Choose Supabase for Database Hosting

## Status
Accepted

## Context

FavGame requires persistent storage for user accounts, personal game libraries, and shared recommended games. 
The application needs a database that can be accessed by the Spring Boot backend and that can store the relationships defined in the project's ER diagram.
The team considered different database hosting options, including Supabase, Neon, Railway, and Atlas.
A member of the team already had experience working with Supabase. Using a technology that someone on the team was familiar with reduced the amount of setup and learning required during development.
The final application stores users, personal games, and recommended games in the database. Personal games are associated with users through their GitHub ID, while recommended games are shared across users.

## Decision

We will use Supabase to host the FavGame database.
The Spring Boot backend will communicate with the database. The Android frontend will not access Supabase directly. Instead, the frontend will communicate with the Spring Boot REST API, and the backend will perform database operations.

## Consequences

Using Supabase made database setup easier because the team already had experience with the platform.
The database remains separated from the Android frontend. Database operations are handled through the backend's service and repository layers, which gives the application a consistent path for accessing stored data.
The project depends on an external database hosting service and requires the backend to be configured with the correct database connection information.
Changes to the database structure must remain consistent with the backend JPA models and repositories.