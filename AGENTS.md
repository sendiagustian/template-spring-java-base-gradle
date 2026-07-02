## Project

Spring Boot 4 (Java 21, Gradle Kotlin DSL) template project for JWT-secured REST APIs. Root package: `com.sendistudio.base`. Intended to be copied as the base for new services.

## Commands

```powershell
.\gradlew build                    # Build
.\gradlew test                     # Run all tests (JUnit 5; stdout/stderr shown in test logs)
.\gradlew test --tests "com.sendistudio.base.AppPropertiesTest"   # Single test class
.\gradlew bootRun --args='--spring.profiles.active=local'         # Run (also: runLocal.bat / runDev.bat / runProd.bat)
.\gradlew classes --continuous     # Continuous compile for devtools hot restart (runClases.bat)
```

Profiles: `local` (default fallback, SQLite, port 5050), `dev` (PostgreSQL, port 8080), `prod` (PostgreSQL, port 8000). Docker builds have per-profile files (`Dockerfile-local/-dev/-prod`).

## Architecture

Layout under `src/main/java/com/sendistudio/base`:

- `app/` — framework plumbing: `configs/`, `filters/`, `handlers/` (+ custom exceptions), `properties/`, `annotations/`, `resolvers/`, `utils/`, `helpers/`, `security/`
- `domain/<feature>/` — business features (see `domain/system` as the sample):
  - `_datas/` — domain-local data types:
    - `requests/` — request body / query-param POJOs (validated with `jakarta.validation`)
    - `responses/` — response payload POJOs returned by the service (wrapped in `DataResponse`/`DataPaginationResponse` by the controller)
    - `enums/` — domain-specific enums
    - `models/` — internal DB-mapped models (with `RowMapper`); NOT exposed directly to controllers
  - `controllers/` — HTTP layer; delegates entirely to the service, no business logic here
  - `services/` — orchestration and business logic; calls sources for data access
  - `sources/` — data-access layer; plain JDBC queries via `JdbcTemplate`/`NamedParameterJdbcTemplate`
- `constants/` — e.g. `ScalarTagConst` (OpenAPI tag names), `ExcludeEndpointConst`
- `data/` — shared/global data types: `models/` (e.g. `UserModel`), `responses/` (`WebResponse`, `DataResponse`, `DataPaginationResponse` envelopes)

### Domain coding flow

Always follow this strict layer order when adding a new feature to `domain/<feature>/`:

```
Controller  →  Service  →  Source
```

1. **Source** (`sources/`) — write SQL / data-access first; expose typed methods returning domain `models/`.
2. **Service** (`services/`) — inject the source via `@RequiredArgsConstructor`; implement business rules; accept `_datas/requests/` as input, return `_datas/responses/` as output.
3. **Controller** (`controllers/`) — inject the service; map HTTP verbs to service calls; wrap results in `WebResponse`/`DataResponse`; annotate with `@Tag(name = ScalarTagConst.X)`.

Data types live in `_datas/` alongside the feature code:

| Type | Package | Purpose |
|------|---------|---------|
| Request POJO | `_datas/requests/` | `@RequestBody` or `@ModelAttribute`; validated with `@Valid` |
| Response POJO | `_datas/responses/` | Returned by service; controller wraps in envelope |
| Enum | `_datas/enums/` | Domain-specific enumerations |
| Model | `_datas/models/` | DB row mapping (`RowMapper`); internal only — never serialised to HTTP |

### Profile-driven configuration

`application.yaml` imports `src/main/resources/properties/{server,logging,database,scalar}.yaml`. Those files hold `local`/`dev`/`prod` blocks; typed `@ConfigurationProperties` classes in `app/properties/` bind them (registered via `@EnableConfigurationProperties` on `MainApplication`, prefixes `application`, `sendistudio.server`, `database`). `ServerConfig` and `DatabaseConfig` read `env.getActiveProfiles()[0]` and pick the matching block at runtime — port and DataSource are NOT set via standard `server.port`/`spring.datasource.*`. To add config, extend the yaml + its properties class, not `application.yaml` keys.

`DatabaseConfig` builds a Hikari `DataSource` manually. Persistence is plain JDBC (`spring-boot-starter-jdbc`), no JPA.

### Database engine selection

One engine for the whole project, set once via the top-level `database.engine` in `properties/database.yaml` (`sqlite`/`postgresql`/`mysql`/`oracle`) and shared by all profiles; each profile block only sets `url`/`username`/`password`. It binds to the `DatabaseEngine` enum in `app/properties/` which carries the driver class and pool sizing (SQLite gets pool size 1 — single writer). At build time, `build.gradle.kts` parses `database.yaml` and bundles the matching driver — changing the engine in the yaml is the only step needed (keep the profile URLs' `jdbc:` prefix consistent with it). `-PdbEngines=...` (comma-separated) overrides this for slim builds; Dockerfiles pass `ARG DB_ENGINES` so images carry only their profile's driver. `DatabaseConfig` fails fast at startup if the configured engine's driver is missing from the classpath, and exposes the active `DatabaseEngine` as a bean for engine-specific SQL. To add a new engine: one enum constant + one entry in `supportedDrivers` in `build.gradle.kts`.

### Security

`SecurityConfig` is stateless JWT: `JwtAuthFilter` extracts the `Authorization` header, validates via `JwtTokenUtil.AccessToken`, and populates the SecurityContext; unauthenticated requests hit `JwtAuthenticationEntryPoint`. Public endpoints are whitelisted centrally in `ExcludeEndpointConst` — add new public routes there, not in `SecurityConfig`. Token secrets live under `application.app-vars` in `application.yaml` (jasypt is on the classpath for encrypting them).

### API docs (Scalar)

springdoc + Scalar UI at `/api/docs` (spec at `/v3/api-docs`), configured in `properties/scalar.yaml` and `ScalarConfig`. Controllers tag endpoints with `@Tag(name = ScalarTagConst.X)` — add new tag names to `ScalarTagConst`. Disable in prod via `SCALAR_ENABLED=false`.

### Request-handling conventions

- Constructor injection via Lombok `@RequiredArgsConstructor` (project convention — no `@Autowired` fields).
- Pagination/sorting: annotate controller params with `@PageParams`/`@SortParams` (custom annotations resolved by `PageParamsResolver`/`SortParamsResolver` registered in `WebConfig`) yielding `PagingModel`/`SortingModel`; `@ApiPageParams`/`@ApiSortParams` add the matching OpenAPI docs.
- Errors: throw the custom exceptions in `app/handlers/exceptions/` (`BadRequestException`, `ResourceNotFoundException`, etc.); `GlobalExceptionHandler` maps them to the `WebResponse`-style envelope.
