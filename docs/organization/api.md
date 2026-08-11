# Organization API Documentation

## 1. Base URL

```text
/api/organizations
2. Authentication

All organization APIs require a valid JWT unless otherwise specified.

Request header:

Authorization: Bearer <JWT>
3. Create Organization
Endpoint
POST /api/organizations
Description

Creates a new organization.

The authenticated user automatically becomes the OWNER of the organization.

Request Body
{
  "name": "DevOS Engineering",
  "slug": "devos-engineering",
  "description": "Software engineering team"
}
Success Response
201 Created

Example:

{
  "success": true,
  "message": "Organization created successfully",
  "data": {
    "id": 1,
    "name": "DevOS Engineering",
    "slug": "devos-engineering",
    "description": "Software engineering team",
    "logo": null,
    "createdAt": "...",
    "updatedAt": "..."
  },
  "timestamp": "..."
}
4. Get My Organizations
Endpoint
GET /api/organizations
Description

Returns all organizations in which the authenticated user is a member.

The user ID is obtained from the authenticated JWT/security context.

The client does not provide the user ID.

Success Response
200 OK
5. Get Organization
Endpoint
GET /api/organizations/{organizationId}
Description

Returns information about a specific organization.

The authenticated user must be a member of the requested organization.

Path Parameter
organizationId

Example:

GET /api/organizations/1
Success Response
200 OK
Authorization Failure

If the authenticated user is not a member of the organization:

403 Forbidden
6. Get Organization Members
Endpoint
GET /api/organizations/{organizationId}/members
Description

Returns the members of an organization.

The authenticated user must be a member of the organization.

Success Response
200 OK

Example:

{
  "success": true,
  "message": "Organization members retrieved successfully",
  "data": [
    {
      "userId": 1,
      "firstName": "Mahesh",
      "lastName": "Badampudi",
      "email": "example@email.com",
      "profilePicture": null,
      "role": "OWNER",
      "joinedAt": "..."
    }
  ],
  "timestamp": "..."
}
7. Add Organization Member
Endpoint
POST /api/organizations/{organizationId}/members
Description

Adds a user to an organization.

The authenticated user must have sufficient organization-level permissions.

Request Body
{
  "userId": 2,
  "role": "DEVELOPER"
}
Success Response
201 Created
Duplicate Member

If the user is already a member:

409 Conflict

Example:

{
  "success": false,
  "message": "User is already a member of this organization",
  "data": null,
  "timestamp": "..."
}
8. Update Member Role
Endpoint
PATCH /api/organizations/{organizationId}/members/{userId}/role
Description

Changes the organization role of an existing member.

Request Body
{
  "role": "PROJECT_MANAGER"
}
Success Response
200 OK
Authorization Rules

The current implementation prevents:

Normal members from changing roles
Users from changing their own organization role
Changing the OWNER role
Assigning the OWNER role
ADMIN users from modifying another ADMIN

Ownership transfer is intentionally not handled by this endpoint.

9. Remove Organization Member
Endpoint
DELETE /api/organizations/{organizationId}/members/{userId}
Description

Removes a member from an organization.

Success Response
200 OK

Example:

{
  "success": true,
  "message": "Member removed successfully",
  "data": null,
  "timestamp": "..."
}
Authorization Rules

The current implementation prevents:

Normal members from removing members
Users from removing themselves
Removing the organization OWNER
ADMIN users from removing another ADMIN
10. Error Responses
401 Unauthorized

The request is not properly authenticated.

Example:

{
  "success": false,
  "message": "Authentication required",
  "data": null,
  "timestamp": "..."
}
403 Forbidden

The user is authenticated but does not have sufficient permission.

Example:

{
  "success": false,
  "message": "You do not have permission to perform this action",
  "data": null,
  "timestamp": "..."
}
404 Not Found

The requested resource does not exist.

Example:

{
  "success": false,
  "message": "Organization member not found",
  "data": null,
  "timestamp": "..."
}
409 Conflict

The requested operation conflicts with the current state of the resource.

Example:

User is already a member of this organization.