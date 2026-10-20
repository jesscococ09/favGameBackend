# Use GitHub OAuth

## Status

Accepted

## Context

FavGame requires users to sign in before accessing their personal game library and other protected features.
The team considered OAuth providers including Google, GitHub, and Auth0. GitHub was selected as the authentication provider.
Because FavGame is a native Android application, the authentication flow also needed to work with the application's login and redirect process.
The application needs authentication information to identify users, associate personal games with the correct user, and determine whether a user has administrator privileges.

## Decision

We will use GitHub OAuth for FavGame authentication using an Authorization Code flow with PKCE.
The Android application will initiate authentication with GitHub. The application generates a PKCE code verifier and challenge, opens the GitHub authorization page, handles the callback, exchanges the authorization code for an access token, and stores the resulting access token.
Authenticated requests to the FavGame backend will send the GitHub access token as a Bearer token.
The Spring Boot backend will use the token to retrieve the authenticated GitHub user's information. The backend will then locate or create the corresponding FavGame user and establish that user as the authenticated principal.
Administrator authorization will use the user's isAdmin value to assign either the USER or ADMIN role.

## Consequences

Users do not need a separate FavGame username and password for authentication.
Authentication requires communication with GitHub in addition to communication between the Android frontend and FavGame backend.
The frontend must preserve authentication information, including the PKCE verifier during login and the access token after successful authentication.
The backend must authenticate protected API requests and associate them with the correct FavGame user.
Authorization can build on the authenticated user. Normal users receive the USER role, while users whose isAdmin value is true receive the ADMIN role.
Administrator endpoints and recommendation-management operations can therefore be protected separately from normal authenticated operations.
