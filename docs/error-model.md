Error Model

One consistent error body is used by every endpoint.

Standard Error Body:

{
"type": "about:blank",
"title": "Not Found",
"status": 404,
"detail": "Game not found.",
"instance": "/favGame/v1/users/games/123"
}

Fields:

type:
Identifies the type of problem. "about:blank" is used for standard HTTP errors.

title:
A short description of the HTTP error.

status:
The HTTP status code associated with the error.

detail:
A specific explanation of what went wrong.

instance:
The API endpoint where the error occurred.


400 Bad Request

Returned when a request contains invalid data or is missing information required by that specific operation.

Example:

{
"type": "about:blank",
"title": "Bad Request",
"status": 400,
"detail": "Display name is required.",
"instance": "/favGame/v1/users/name"
}

May be returned by:

PATCH /favGame/v1/users/name
PATCH /favGame/v1/users/icon
POST /favGame/v1/users/games
PATCH /favGame/v1/users/games/{id}
PUT /favGame/v1/users/games/{id}
POST /favGame/v1/users/games/{id}/comments
POST /favGame/v1/recommendations
PATCH /favGame/v1/recommendations/{id}
PUT /favGame/v1/recommendations/{id}
PATCH /favGame/v1/admin/{userId}


401 Unauthorized

Returned when the request does not contain valid authentication.

Example:

{
"type": "about:blank",
"title": "Unauthorized",
"status": 401,
"detail": "Authentication is required.",
"instance": "/favGame/v1/users/games"
}

All FavGame API endpoints require authentication except the server health endpoint.

The frontend obtains a GitHub OAuth access token using Authorization Code with PKCE and sends the token as:

Authorization: Bearer <GitHub access token>


403 Forbidden

Returned when an authenticated user attempts an operation that requires administrator privileges.

Example:

{
"type": "about:blank",
"title": "Forbidden",
"status": 403,
"detail": "Admin privileges are required.",
"instance": "/favGame/v1/admin"
}

Admin-only endpoints:

GET /favGame/v1/admin
GET /favGame/v1/admin/{userId}
PATCH /favGame/v1/admin/{userId}
DELETE /favGame/v1/admin/{userId}

POST /favGame/v1/recommendations
PATCH /favGame/v1/recommendations/{id}
PUT /favGame/v1/recommendations/{id}
DELETE /favGame/v1/recommendations/{id}

GET /favGame/v1/recommendations is available to all authenticated users.


404 Not Found

Returned when the requested resource cannot be found or when a personal game does not belong to the authenticated user.

Example:

{
"type": "about:blank",
"title": "Not Found",
"status": 404,
"detail": "Game not found.",
"instance": "/favGame/v1/users/games/123"
}

May be returned by:

GET /favGame/v1/users/games/{id}
PATCH /favGame/v1/users/games/{id}
PUT /favGame/v1/users/games/{id}
DELETE /favGame/v1/users/games/{id}
POST /favGame/v1/users/games/{id}/comments

PATCH /favGame/v1/recommendations/{id}
PUT /favGame/v1/recommendations/{id}
DELETE /favGame/v1/recommendations/{id}

GET /favGame/v1/admin/{userId}
PATCH /favGame/v1/admin/{userId}
DELETE /favGame/v1/admin/{userId}


500 Internal Server Error

Returned when an unexpected server-side error prevents the request from completing.

Example:

{
"type": "about:blank",
"title": "Internal Server Error",
"status": 500,
"detail": "An unexpected error occurred.",
"instance": "/favGame/v1/users/games"
}


Error Content Type

All standardized API error responses use:

Content-Type: application/problem+json

and use the same structure:

{
"type": "about:blank",
"title": "HTTP error title",
"status": 400,
"detail": "Specific description of the error.",
"instance": "/favGame/v1/endpoint"
}