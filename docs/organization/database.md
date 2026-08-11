# Organization Database Design

## 1. Overview

The Organization module uses MySQL with Spring Data JPA.

The main tables involved are:

```text
users
organizations
organization_members
2. Organizations Table

Table:

organizations
Columns
Column	Description
id	Primary key
name	Organization name
slug	Unique organization slug
description	Organization description
logo	Organization logo URL
created_at	Creation timestamp
updated_at	Last update timestamp
3. Organization Members Table

Table:

organization_members
Columns
Column	Description
id	Primary key
organization_id	Organization foreign key
user_id	User foreign key
role	Organization role
joined_at	Membership timestamp
4. Entity Relationship
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

This represents:

One User
    ↓
Many OrganizationMember records

and:

One Organization
    ↓
Many OrganizationMember records

Therefore:

User ↔ Organization

is effectively a many-to-many relationship implemented through the explicit OrganizationMember entity.

5. Why OrganizationMember?

A direct many-to-many relationship is not sufficient because the relationship itself contains additional information.

The membership contains:

role
joinedAt

Therefore:

OrganizationMember

represents the relationship between a user and an organization.

6. Foreign Keys

organization_id references:

organizations.id

Conceptually:

organization_members.organization_id
                │
                ▼
        organizations.id

user_id references:

users.id

Conceptually:

organization_members.user_id
                │
                ▼
              users.id
7. Unique Constraint

The following combination must be unique:

organization_id + user_id

This is represented using:

@UniqueConstraint(
    columnNames = {"organization_id", "user_id"}
)

This prevents the same user from being added to the same organization more than once.

For example:

Organization 1 + User 5

can exist only once.

However:

Organization 1 + User 5
Organization 2 + User 5

is valid.

This allows one user to belong to multiple organizations.

8. Database Indexing

An explicit index is maintained on:

user_id

This supports queries that retrieve organizations for a specific user.

The repository contains a query equivalent to:

findByUserId(Long userId)

which conceptually results in:

SELECT *
FROM organization_members
WHERE user_id = ?;

The index can help the database locate matching rows more efficiently.

9. Composite Unique Structure

The unique constraint on:

organization_id + user_id

provides an indexed structure for the combination.

It is useful for queries such as:

SELECT *
FROM organization_members
WHERE organization_id = ?
AND user_id = ?;

It can also support queries filtering by:

organization_id

because it is the first column in the composite structure.

10. Why Not Index Every Column?

Indexes can improve read performance, but they also have costs.

Indexes require:

Additional storage
Index maintenance
Additional work during INSERT operations
Additional work during UPDATE operations
Additional work during DELETE operations

Therefore, indexes should be created based on actual query patterns.

11. Important Query Patterns
Find Organizations for a User
findByUserId(userId)

Uses:

user_id
Find Members of an Organization
findByOrganizationId(organizationId)

Can benefit from the composite structure beginning with:

organization_id
Check Organization Membership
findByOrganizationIdAndUserId(
    organizationId,
    userId
)

Uses:

organization_id + user_id
12. Query Performance

Creating an index does not guarantee that MySQL will always use it.

MySQL's query optimizer decides how to execute a query.

The query plan can be inspected using:

EXPLAIN

Example:

EXPLAIN
SELECT *
FROM organization_members
WHERE user_id = 5;

Useful query-plan information includes:

possible_keys
key
rows
type
13. Lazy Relationships

The relationships from OrganizationMember to Organization and User use:

FetchType.LAZY

This means related entities are not intended to be loaded immediately when the membership is retrieved.

Lazy loading can reduce unnecessary database work.

However, careless access to lazy relationships can result in the N+1 query problem.

14. N+1 Query Problem

For example:

1 query
    ↓
Retrieve 100 members

100 additional queries
    ↓
Retrieve each user's data

Total:

101 queries

This is known as the N+1 query problem.

Potential solutions include:

JOIN FETCH
EntityGraph
DTO projections
Batch fetching

These should be evaluated based on actual query requirements.