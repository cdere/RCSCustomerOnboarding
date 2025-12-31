## RCS Customer Onboarding System – Backend

### Overview
The RCS Customer Onboarding System is a Spring Boot–based backend service designed to manage Customer Order Forms and Qualification Forms with full versioning, workflow control, role-based access, and auditability.

The system provides a structured and auditable onboarding workflow supporting multiple internal and external stakeholders such as Customers, TPMs, Sales, and Admin users.

## Key Features

- Customer application Versioning
  - Supports evolving application structures without impacting historical submissions
  - Latest and historical versions can be retrieved
- Submission Workflow
  - Multi-step lifecycle:
  DRAFT → SUBMITTED → IN_REVIEW → APPROVED / REJECTED
  - Strict role-based transition rules
- Role-Based Access Control (RBAC)
  - Roles: CUSTOMER, TPM, SALES, ADMIN
  - Enforced at service and controller layers
- Audit Trail
  - Every status transition is logged
  - Captures user, timestamp, previous state, new state, and remarks
- Clean / Hexagonal Architecture
  - Clear separation between Web, Application, and Persistence layers
  - Business logic isolated from infrastructure concerns

## Tech Stack
| Category | Technology                  |
| ------ |-----------------------------|
| Language | Java 17+                    |
| Framework | Spring Boot 4.x             |
| Persistence | Spring Data JPA / Hibernate |
| Database | H2 (local)                  |
| Security | Spring Security             |
| Build Tool | Maven                       |
| API Docs | OpenAPI / Swagger           |
| Testing | JUnit 5, Mockito            |

## Architecture Overview
    ┌────────────────────┐
    │    Web Layer       │  → REST Controllers, DTOs
    └────────────────────┘
            ↓
    ┌────────────────────┐
    │ Application Layer  │  → Services, Workflow, Validation
    └────────────────────┘
            ↓
    ┌────────────────────┐
    │ Domain Layer       │  → Entities, Enums, Business Rules
    └────────────────────┘
            ↓
    ┌────────────────────┐
    │ Infrastructure     │  → JPA Repositories, Security, DB
    └────────────────────┘

## Domain Model (High-Level)
- Form
  - Represents a logical form (Order / Qualification)
- FormVersion
  - Versioned definition of a form (JSON-based schema)
- Submission
  - A filled form instance with workflow status
- AuditLog
  - Tracks all workflow transitions

## Workflow States
| State     | Description               |
| --------- | ------------------------- |
| DRAFT     | Created but not submitted |
| SUBMITTED | Submitted by Customer     |
| IN_REVIEW | Reviewed by TPM           |
| APPROVED  | Approved by Sales/Admin   |
| REJECTED  | Rejected with remarks     |

## Role Responsibilities
| State    | Description              |
|----------|--------------------------|
| CUSTOMER | Create and submit drafts |
| TPM      | Review submissions       |
| SALES    | Approve submissions      |
| ADMIN    | Full access              |


## API Endpoints (Summary)
| Method | Endpoint                                               | Description         |
|--------|--------------------------------------------------------|---------------------|
| POST   | `/api/v1/onboarding/customer/addCustomer`              | Add Customer Record |
| GET    | `/api/v1/onboarding/customer/getCustomer`              | Get Customer        |
| PATCH  | `/api/v1/onboarding/customer/validateForm/{id}/review` | Review submission   |
| GET    | `/api/v1/forms/audit/{id}/history`                     | Get audit trail     |

## Swagger UI available at:
http://localhost:9092/swagger-ui.html

## Postman cURL
### Add Customer Application/Record - Username: client_user, Password: pass (Basic Auth)
```bash
curl --location 'http://localhost:9092/api/v1/onboarding/customer/addCustomer' \
--header 'Content-Type: application/json' \
--header 'Authorization: Basic Y2xpZW50X3VzZXI6cGFzcw==' \
--data '{
  "customerName": "abc",
  "status": "DRAFT",
  "remarks": "abc1",
  "rcsProvider": "abcd",
  "expectedMonthlyVolume": "7000"
}'
```
### Validate Customer Application/Record - Review  - Username: client_user/tpm_sales/admin, Password: pass (Basic Auth)
```bash
curl --location --request PATCH 'http://localhost:9092/api/v1/onboarding/customer/validateForm/2/review?status=APPROVED&remarks=remark00000' \
--header 'accept: */*' \
--header 'Authorization: Basic dHBtX3VzZXI6cGFzcw=='
``` 
### Get All Customer Application/Records - Username: client_user/tpm_sales/admin, Password: pass (Basic Auth)
```bash
curl --location 'http://localhost:9092/api/v1/onboarding/customer/getCustomer' \
--header 'accept: */*' \
--header 'Authorization: Basic dHBtX3VzZXI6cGFzcw=='
```
### Get Audit History for a Customer Application/Record - Username: tpm_sales/admin, Password: pass (Basic Auth)
```bash
curl --location 'http://localhost:9092/api/v1/forms/audit/1/history?id=1' \
--header 'accept: */*' \
--header 'Authorization: Basic dHBtX3VzZXI6cGFzcw=='
```

## EC2 Postman Collection

### Add Customer Application/Record - Username: client_user, Password: pass (Basic Auth)
```bash
curl --location 'http://35.164.221.57:9092/api/v1/onboarding/customer/addCustomer' \
--header 'Content-Type: application/json' \
--header 'Authorization: Basic Y2xpZW50X3VzZXI6cGFzcw==' \
--data '{
  "customerName": "abc",
  "status": "DRAFT",
  "remarks": "abc1",
  "rcsProvider": "abcd",
  "expectedMonthlyVolume": "7000"
}'
```
### Validate Customer Application/Record - Review  - Username: client_user/tpm_sales/admin, Password: pass (Basic Auth)
```bash
curl --location --request PATCH 'http://35.164.221.57:9092/api/v1/onboarding/customer/validateForm/2/review?status=APPROVED&remarks=remark00000' \
--header 'accept: */*' \
--header 'Authorization: Basic dHBtX3VzZXI6cGFzcw=='
``` 
### Get All Customer Application/Records - Username: client_user/tpm_sales/admin, Password: pass (Basic Auth)
```bash
curl --location 'http://35.164.221.57:9092/api/v1/onboarding/customer/getCustomer' \
--header 'accept: */*' \
--header 'Authorization: Basic dHBtX3VzZXI6cGFzcw=='
```
### Get Audit History for a Customer Application/Record - Username: tpm_sales/admin, Password: pass (Basic Auth)
```bash
curl --location 'http://35.164.221.57:9092/api/v1/forms/audit/1/history?id=1' \
--header 'accept: */*' \
--header 'Authorization: Basic dHBtX3VzZXI6cGFzcw=='
```

## Validation Strategy
- Mandatory fields validated per Form Version
- Business rules enforced before persistence
- Invalid state transitions rejected
- Unauthorized role actions blocked

## Audit Logging
Each workflow transition records:
- Previous Status
- New Status
- User ID
- Timestamp
- Remarks

## Configuration
#### application.properties
spring.application.name=RCECustomerOnboardingApplication
server.port = 9092
###### H2 Database configuration
spring.datasource.url=jdbc:h2:mem:rcsapp
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

###### H2 Console configuration
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console

###### JPA and Hibernate configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.generate-ddl=true
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.sql.init.mode=always

###### Envers naming convention for the version table
spring.jpa.properties.org.hibernate.envers.audit_table_suffix=_AUDIT
spring.jpa.properties.org.hibernate.envers.revision_field_name=REVISION_ID
spring.jpa.properties.org.hibernate.envers.revision_type_field_name=REVISION_TYPE

## Architecture Diagrams
### 1. High-Level System Architecture
flowchart LR
Client[Client / UI / API Consumer]

    subgraph Web Layer
        Controller[REST Controllers]
        DTO[DTOs & Mappers]
    end

    subgraph Application Layer
        Service[Application Services]
        Workflow[Workflow / State Logic]
        Validation[Business Validation]
    end

    subgraph Domain Layer        
        Form[Form]
        FormVersion[FormVersion]
        Audit[AuditLog]
    end

    subgraph Infrastructure Layer
        Repo[JPA Repositories]
        Security[Spring Security]
        DB[(Database)]
    end

    Client --> Controller
    Controller --> DTO
    DTO --> Service
    Service --> Workflow
    Service --> Validation
    Workflow --> Status change
    Status change --> Audit
    Service --> Repo
    Repo --> DB
    Controller --> Security

## Submission Workflow State Diagram

    stateDiagram-v2    
    [*] --> DRAFT
    DRAFT --> SUBMITTED : Customer submits
    SUBMITTED --> IN_REVIEW : TPM reviews
    IN_REVIEW --> APPROVED : Sales/Admin approves
    IN_REVIEW --> REJECTED : Sales/Admin rejects



