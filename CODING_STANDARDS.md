# Base Feature Implementation Guide

This document outlines the strict step-by-step workflow for implementing new features in the **Base** project. AI Agents must follow this pattern to maintain consistency across the codebase.

## Core Architecture
The project follows a **Strict Domain-Driven Design (DDD)** structure with **Native SQL** execution using a custom `QueryUtil`.
All data structures (models, DTOs, schemas) MUST be collocated within their respective domain's `_data` folder.

**Base Package:** `com.sendistudio.base`

---

## Step 1: Define Database Schema
**Location:** `src/main/java/com/sendistudio/base/domain/{module}/_data/schemas`

Create or update a Schema class to define table names and column names as constants. This prevents "magic strings" in SQL queries.

* **Pattern:** Create a static inner class for the table.
* **Naming:** Use `Snake_Case` for values, `UPPER_CAMEL` for constants.

```java
public class UserSchema {
    public static final String SCHEMA_NAME = "core";
    public static final String TABLE = SCHEMA_NAME + ".users";

    public static final String ID = "id";
    public static final String TENANT_ID = "tenant_id";
    public static final String EMAIL = "email";
    public static final String PASSWORD_HASH = "password_hash";
    public static final String GLOBAL_ROLE = "global_role";
    public static final String CREATED_AT = "created_at";
    public static final String UPDATED_AT = "updated_at";
    public static final String STATUS = "status";
}
```

---

## Step 2: Create Model & RowMapper

**Location:** `src/main/java/com/sendistudio/base/domain/{module}/_data/models`

Create a POJO that represents the database entity.

1. Use Lombok `@Data`, `@Builder`, `@NoArgsConstructor`, `@AllArgsConstructor`.
2. Implement a `static class` implementing `RowMapper<ModelName>` inside the model to map DB rows to the Object.

```java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContactModel {
    private String id;
    private String name;

    public static class ContactModelRowMapper implements RowMapper<ContactModel> {
        @Override
        public ContactModel mapRow(ResultSet rs, int rowNum) throws SQLException {
            return ContactModel.builder()
                .id(rs.getString(CrmSchema.Contacts.ID))
                .name(rs.getString(CrmSchema.Contacts.NAME))
                .build();
        }
    }
}
```

---

## Step 3: Create DTOs (Requests & Responses)

**Location:** * **Requests:** `src/main/java/com/sendistudio/base/domain/{module}/_data/requests/CreateContactRequest.java`
* **Responses:** `src/main/java/com/sendistudio/base/domain/{module}/_data/responses/ContactResponse.java`

Use Java Bean Validation (`@NotBlank`, `@NotNull`, etc.) in Request DTOs.

```java
@Data
public class CreateContactRequest {
    @NotBlank(message = "Name is required")
    private String name;
}
```

---

## Step 4: Create Data Source (Repository)

**Location:** `src/main/java/com/sendistudio/base/domain/{module}/sources`

Use **`QueryUtil`** for all database interactions. **Do not use JPA/Hibernate Repositories.**

* Use `"""` (Text Blocks) for SQL.
* Use `.formatted()` to inject Table/Column names from **Schema**.
* Use `?` for value parameters.
* Use `TypeUtil.StringRowMapper` for single column mapping.

```java
@Repository
@RequiredArgsConstructor
public class ContactSource {
    
    private final QueryUtil query;

    public List<ContactModel> findAll(String tenantId) {
        String sql = """
            SELECT * FROM %s WHERE %s = ?::uuid
            """.formatted(CrmSchema.Contacts.TABLE, CrmSchema.Contacts.TENANT_ID);
            
        return query.query(sql, new ContactModel.ContactModelRowMapper(), tenantId);
    }
}
```

---

## Step 5: Create Service

**Location:** `src/main/java/com/sendistudio/base/domain/{module}/services`

Handle business logic, exception handling, and transaction management here.

* Inject `Source`.
* Validate business rules.
* Throw `ResponseStatusException` for errors.

```java
@Service
@RequiredArgsConstructor
public class ContactService {
    private final ContactSource contactSource;

    public List<ContactModel> getContacts(String tenantId) {
        return contactSource.findAll(tenantId);
    }
}
```

---

## Step 6: Register Scalar Tags

**Location:** `src/main/java/com/sendistudio/base/constants/ScalarTagConst.java`

Add a new constant for the controller documentation tag.

```java
public class ScalarTagConst {
    // ... existing tags
    public static final String CRM_CONTACT = "CRM - Contacts";
}
```

---

## Step 7: Create Controller

**Location:** `src/main/java/com/sendistudio/base/domain/{module}/controllers`

Expose the endpoints.

* Use `@RestController` and `@RequestMapping`.
* Use `@Tag(name = ScalarTagConst.CRM_CONTACT)` for documentation.
* Inject `Service`.
* Return `ResponseEntity`.

```java
@RestController
@RequestMapping("/api/crm/contacts")
@RequiredArgsConstructor
@Tag(name = ScalarTagConst.CRM_CONTACT)
public class ContactController {
    
    private final ContactService contactService;

    @GetMapping
    public ResponseEntity<?> getContacts(Authentication auth) {
        // ... implementation
        return ResponseEntity.ok(result);
    }
}
```

---

## Step 8: Verification & Testing (MCP)
Tool Usage: Database MCP Server

If you need to verify database schemas, debug SQL errors, or check if data is inserted correctly, YOU MUST use the available MCP Tools.

- Read Schema: Use read_schema tool to inspect table structure before writing queries.
- Verify Data: Use run_select_query to check actual data in the database.
- Do NOT Guess: Never assume column names or data types; always verify with MCP tools if unsure.

---

## Summary Checklist for AI Agent

1. [ ] **Schema:** Defined in `domain/{module}/_data/schemas`?
2. [ ] **Model:** POJO + RowMapper created in `domain/{module}/_data/models`?
3. [ ] **DTO:** Request/Response classes created in `domain/{module}/_data/requests` & `responses`?
4. [ ] **Source:** SQL written using `QueryUtil` & Schema constants?
5. [ ] **Service:** Logic implemented?
6. [ ] **ScalarTag:** Tag constant added?
7. [ ] **Controller:** Endpoint exposed with correct Tag?
8. [ ] **Verification:** Database structure/data verified using MCP Tools?