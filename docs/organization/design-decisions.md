# Organization Design Decisions

## 1. Use an Explicit OrganizationMember Entity

### Decision

Use:

```text
Organization
OrganizationMember
User

instead of a direct JPA @ManyToMany relationship.

Reason

A user's relationship with an organization contains additional information such as:

Role
Joined date

Therefore, the relationship is represented as its own entity.

2. Users Can Belong to Multiple Organizations
Decision

A user is allowed to belong to multiple organizations.

Example
Mahesh
├── Organization A → OWNER
├── Organization B → DEVELOPER
└── Organization C → REVIEWER
Reason

DevOS is designed as a collaborative platform where users can participate in multiple organizations.

3. Organization Role Belongs to Membership
Decision

Store the organization role in:

OrganizationMember

rather than directly in:

User
Reason

The same user can have different roles in different organizations.

Example:

Mahesh
├── Organization A → ADMIN
└── Organization B → DEVELOPER

Therefore, the role belongs to the membership relationship.

4. Creator Automatically Becomes OWNER
Decision

The user who creates an organization automatically receives the:

OWNER

role.

Reason

Every organization needs an initial owner who can manage the organization and its members.

5. Use Transactions During Organization Creation
Decision

Creating an organization and creating its owner membership are performed within a transaction.

Reason

These operations represent one business operation.

We do not want the following inconsistent state:

Organization exists
+
Owner membership does not exist

If membership creation fails, the organization creation should also be rolled back.

6. Use Resource-Level Authorization
Decision

Organization access is checked using the user's membership in that organization.

Reason

Global roles alone are insufficient.

For example:

Mahesh
├── Organization A → ADMIN
└── Organization B → No membership

Being an ADMIN in Organization A should not give Mahesh access to Organization B.

Therefore, authorization also checks membership in the requested organization.

7. Use DTOs
Decision

Use request and response DTOs instead of exposing JPA entities directly.

Reason

DTOs provide:

Request validation
Controlled API contracts
Separation between API and persistence models
Easier future API changes
8. Use Specific Exceptions
Decision

Use specific application exceptions instead of relying on generic exceptions.

Examples:

ResourceNotFoundException
ResourceAlreadyExistsException
InvalidCredentialsException
EmailAlreadyExistsException
Reason

Different application failures represent different HTTP responses.

401 → Authentication failure
403 → Authorization failure
404 → Resource not found
409 → Resource conflict
9. Keep Security Exceptions Separate
Decision

Authentication and authorization failures are handled through Spring Security handlers.

Business/application exceptions are handled through:

GlobalExceptionHandler
Reason

The responsibilities are different.

Authentication failure
        ↓
RestAuthenticationEntryPoint
Authorization failure
        ↓
RestAccessDeniedHandler
Business/Application exception
        ↓
GlobalExceptionHandler
10. Prevent OWNER Assignment Through Normal Member APIs
Decision

The OWNER role cannot be assigned through normal member creation or role-update APIs.

Reason

Ownership is a highly privileged relationship.

Allowing normal member APIs to assign ownership could accidentally transfer control of the organization.

Ownership transfer should be handled as a separate operation with explicit rules.

11. Prevent Removing the OWNER
Decision

The normal remove-member API cannot remove the organization owner.

Reason

Removing the owner could leave the organization without an owner.

A future ownership-transfer flow can handle:

Current OWNER
      ↓
Transfer ownership
      ↓
New OWNER
      ↓
Previous OWNER
12. Prevent Self Role Changes
Decision

Users cannot change their own organization role through the role-update API.

Reason

This prevents accidental or unauthorized privilege changes.

For example:

OWNER
  ↓
Changes own role
  ↓
DEVELOPER

could leave the organization without an owner.

13. Prevent Self Removal
Decision

The remove-member API does not allow users to remove themselves.

Reason

Leaving an organization is a different business operation from an administrator removing a member.

A separate leave-organization flow can be implemented later.

14. Add Indexes Based on Query Patterns
Decision

Indexes are added based on actual repository query patterns.

Reason

Indexes can improve read performance but also consume storage and increase write-maintenance costs.

Therefore, DevOS avoids indexing every column.

Current important query patterns include:

findByUserId()
findByOrganizationId()
findByOrganizationIdAndUserId()

Indexes are selected based on these access patterns.

15. Use Lazy Relationships
Decision

OrganizationMember relationships use:

FetchType.LAZY
Reason

Related User and Organization entities should not always be loaded immediately.

This can reduce unnecessary database work.

However, lazy relationships require attention to avoid N+1 queries.

Performance optimization will be evaluated as the dataset and query complexity grow.