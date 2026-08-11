# Organization Module Architecture

## 1. Overview

The Organization module provides the foundation for multi-tenant collaboration in DevOS.

An organization represents a company, team workspace, or collaborative software environment.

A user can belong to multiple organizations, and the user's role can be different in each organization.

For example:

```text
Mahesh
├── Organization A → OWNER
├── Organization B → DEVELOPER
└── Organization C → REVIEWER

```
# 2. Main Components

```
   organization/
   │
   ├── controller/
   │   └── OrganizationController.java
   │
   ├── service/
   │   ├── OrganizationService.java
   │   └── OrganizationAuthorizationService.java
   │
   ├── repository/
   │   ├── OrganizationRepository.java
   │   └── OrganizationMemberRepository.java
   │
   ├── entity/
   │   ├── Organization.java
   │   └── OrganizationMember.java
   │
   ├── dto/
   │   ├── request/
   │   └── response/
   │
   └── enums/
   └── OrganizationRole.java
```
# 3.Entity Relationship

A user and an organization have a many-to-many relationship.

However, DevOS does not use a direct @ManyToMany relationship.

Instead, an explicit OrganizationMember entity is used.

```
User
│
│ 1
│
│ *
▼
OrganizationMember
│
│ *
│
│ 1
▼
Organization

```

From the OrganizationMember perspective:

Many OrganizationMembers → One User

Many OrganizationMembers → One Organization
# 4. Why OrganizationMember Exists

OrganizationMember represents the relationship between a user and an organization.

The relationship contains additional information:

Organization role
Joined date

Example:
```
Mahesh
│
├── Google → ADMIN
├── Microsoft → DEVELOPER
└── StartupX → REVIEWER
```

Each relationship is represented by an OrganizationMember record.

# 5. Request Flow
```
   Client
   │
   ▼
   HTTP Request
   │
   ▼
   JWT Authentication
   │
   ▼
   SecurityContext
   │
   ▼
   OrganizationController
   │
   ▼
   OrganizationService
   │
   ▼
   OrganizationAuthorizationService
   │
   ▼
   Repository
   │
   ▼
   MySQL
   
   ```
# 6. Authentication and Authorization

Authentication is handled by Spring Security and JWT.

The authenticated user's identity is obtained from the security context.

The organization module then performs organization-level authorization.

```
JWT
↓
Authenticated User
↓
Organization Membership
↓
Organization Role
↓
Requested Action

A user must be a member of an organization to access its protected resources.

7. Organization Roles

```

The current organization roles are:

```
OWNER
ADMIN
PROJECT_MANAGER
DEVELOPER
REVIEWER
MEMBER
```

The organization role is stored in OrganizationMember.

A user's organization role is therefore independent for each organization.

# 8. Authorization Examples
```
   Organization access
   User belongs to organization
   ↓
   Access allowed
   User does not belong to organization
   ↓
   403 Forbidden
   Member management
   OWNER
   ├── Add member
   ├── Change member role
   └── Remove eligible members
   ADMIN
   ├── Add member
   ├── Change eligible member roles
   └── Remove eligible members
   ```

Normal members cannot perform administrative membership operations.

# 9. Transaction Management

Creating an organization involves two database operations:

```
Create Organization
+
Create OWNER membership
```
These operations are executed inside a transaction.

```
@Transactional
```
If one operation fails, the transaction can be rolled back so that the database does not contain an organization without its owner membership.

# 10. DTO Layer

The API does not directly expose JPA entities.

Request and response DTOs are used to provide:

Request validation
Controlled API contracts
Separation between database and API models
Flexibility for future changes
# 11. Exception Handling

Application exceptions are handled by the global exception handler.

Current response categories include:
```
401 Unauthorized
Authentication failure

403 Forbidden
Authorization failure

404 Not Found
Resource does not exist

409 Conflict
Resource/state conflict
```

# 12. Current Organization Architecture
```
    DevOS
    │
    ▼
    Authentication
    │
    ▼
    JWT
    │
    ▼
    SecurityContext
    │
    ▼
    Organization API
    │
    ┌───────────┴───────────┐
    ▼                       ▼
    Organization              Membership
    │                       │
    └───────────┬───────────┘
    ▼
    MySQL
```