# Auth Sequence Diagram

The FavGame Android application uses GitHub OAuth Authorization Code with PKCE for authentication. 
After authentication, the GitHub access token is stored on the device and sent as a Bearer token with protected API requests.

```mermaid
sequenceDiagram
    actor User
    participant App as Android App
    participant Storage as AuthStorage
    participant GitHub as GitHub OAuth
    participant Backend as FavGame Backend
    participant Filter as GithubAuthenticationFilter
    participant GitHubAPI as GitHub API
    participant UserService as GithubUserService
    participant UserRepo as UserRepository
    participant DB as Supabase Database
    participant Controller as API Controller

    User->>App: Launch FavGame
    App->>Storage: Load saved access token

    alt No saved access token
        Storage-->>App: No token
        App-->>User: Display Login Screen

        User->>App: Select Login with GitHub

        App->>App: Generate PKCE code verifier
        App->>Storage: Save code verifier
        App->>App: Generate SHA-256 code challenge

        App->>GitHub: Open GitHub authorization URL<br/>client_id + redirect_uri + challenge + S256

        GitHub-->>User: Display GitHub authorization
        User->>GitHub: Authorize FavGame

        GitHub-->>App: Redirect to favgame://callback<br/>with authorization code

        App->>Storage: Load PKCE code verifier
        Storage-->>App: Return verifier

        App->>GitHub: Exchange authorization code<br/>and verifier for access token
        GitHub-->>App: Return GitHub access token

        App->>Storage: Save access token
        App->>App: Restart MainActivity
        App->>Storage: Load access token
        Storage-->>App: Return access token

    else Saved access token exists
        Storage-->>App: Return access token
    end

    App->>App: Create Retrofit client
    App->>App: Create repositories and ViewModels

    App->>Backend: Protected API request<br/>Authorization: Bearer access_token

    Backend->>Filter: Request passes through security filter
    Filter->>UserService: loadOrCreateUser(accessToken)

    UserService->>GitHubAPI: GET https://api.github.com/user<br/>Authorization: Bearer access_token
    GitHubAPI-->>UserService: GitHub user information

    UserService->>UserRepo: findByGithubId(githubId)
    UserRepo->>DB: Find user by GitHub ID
    DB-->>UserRepo: User or no existing user

    alt Existing FavGame user
        UserRepo-->>UserService: Return existing user
    else First FavGame login
        UserService->>UserRepo: Save new user
        UserRepo->>DB: INSERT user
        DB-->>UserRepo: Saved user
        UserRepo-->>UserService: Return new user
    end

    UserService-->>Filter: Return FavGame user
    Filter->>Filter: Create authenticated principal<br/>ROLE_USER or ROLE_ADMIN
    Filter->>Backend: Continue authenticated request

    Backend->>Controller: Route request to controller
    Controller->>Controller: Read authenticated User from auth.principal
    Controller-->>App: Return authorized API response
    App-->>User: Display authenticated application data
```

## Authentication Summary

1. FavGame launches and checks for a saved GitHub access token.
2. If no token exists, the user is shown the GitHub login option.
3. The application generates a PKCE code verifier and SHA-256 code challenge.
4. The verifier is stored locally while the challenge is included in the GitHub authorization request.
5. GitHub authenticates the user and redirects back to `favgame://callback` with an authorization code.
6. FavGame retrieves the saved verifier and exchanges the authorization code and verifier for a GitHub access token.
7. The access token is saved locally.
8. FavGame creates its Retrofit client, repositories, and ViewModels.
9. Protected backend requests send the GitHub access token using the `Authorization: Bearer` header.
10. The FavGame backend authentication filter extracts the token.
11. The backend sends the token to the GitHub user API to identify the authenticated GitHub account.
12. The backend searches for a FavGame user with that GitHub ID.
13. If the user does not exist, a new FavGame user is created.
14. The backend creates an authenticated principal with either `ROLE_USER` or `ROLE_ADMIN`.
15. The authenticated request continues to the requested controller.
16. The controller can access the authenticated FavGame user through `auth.principal`.
17. The backend processes the authorized API request and returns the response to the Android application.