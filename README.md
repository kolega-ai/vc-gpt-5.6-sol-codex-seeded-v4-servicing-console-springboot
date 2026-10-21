# Northstar Back-Office Customer Servicing Console

An internal retail-banking console built as three independently secured Spring Boot tiers:

- `console` (port 8080) authenticates staff and renders Thymeleaf screens.
- `entitlements-service` (internal port 8081) authorizes every request from its own staff, policy, scope, and delegation data.
- `core-mock` (internal port 8082) models the vendor core and independently verifies decision-minted HMAC scope tokens.

The **console authenticates; entitlements-service authorizes**. The channel does not accept role, scope, branch, threshold, approval limit, ownership, status, or authorization basis through bound domain objects. Core operations use a short-lived signed scope token and ignore caller-supplied identity or scope headers.

## Run locally

Requirements: Docker Compose. No external service or hosted account is used.

```bash
docker compose up --build
```

Open <http://localhost:8080>. PostgreSQL migrations and realistic seed data load automatically. Uploaded documents are stored in the `servicing-uploads` local Docker volume. Stop with `docker compose down`; add `-v` only when you intentionally want to erase local data.

For a host build with Java 21:

```bash
./gradlew clean test bootJar
```

## Demo credentials

All seeded accounts use password `Welcome123!`.

| Role | Email | Scope |
|---|---|---|
| CSR | `csr@northstar.test` | B01 |
| Branch officer | `officer@northstar.test` | B01 |
| Branch supervisor | `supervisor@northstar.test` | B01 |
| Regional manager | `regional@northstar.test` | R1 |
| Head-office operations | `headoffice@northstar.test` | Bank |
| Security administrator | `security@northstar.test` | Administration only |

Same-name leakage checks use **Alex Morgan** (`C0001`, `C0010`) and **Jamie Patel** (`C0002`, `C0011`) across different branches. Vulnerable test customers include `C0003`, `C0017`, and `C0029`.

## Implemented workflows

The app includes role-aware dashboards; scoped customer search and the single JSON typeahead; Customer 360; contact confirmation; card block, unblock, and reissue; MyBatis transaction filters and CSV export; threshold-routed reversals/overrides with maker–checker approval and idempotent core posting; service-request transitions; internal/disclosable notes and documents; regional metrics; staff creation and roster import; fee schedule maintenance; notifications; and append-only redacted audit export.

Security controls include CSRF, session fixation protection, one active session, BCrypt credentials, five-minute signed scope tokens, decision-tier scope resolution, core-side scope enforcement, explicit state edges, `BigDecimal` money, server-owned policy values, escaped Thymeleaf text, constrained uploads, canonical downloads, redacted audits, disabled diagnostics, bound/whitelisted MyBatis sorting, and parameter-bound free-text search.

## Scheduled operations

The entitlement job accepts an explicit review date:

```bash
curl -X POST -H 'X-Internal-Client: local-console-client-secret' \
  'http://localhost:8081/internal/jobs/delegations?asOfDate=2026-08-26'
```

Ports 8081/8082 are intentionally not published by Compose. Run the command from inside the Compose network or expose a port temporarily only for local review.

## UI

The responsive “operations ledger” shell uses a dark authority rail, a persistent live-scope card, paper-white work surfaces, teal primary actions, amber risk markers, compact financial tables, strong focus indicators, and text-plus-color statuses. Navigation groups customer work, queues, regional oversight, policy, staff administration, and audit evidence.

## Known limitations

No external integrations are required; local email is represented by the `outbox` table. The regional dashboard uses current seeded operational aggregates rather than a separate reporting warehouse.
