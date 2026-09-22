# AGENTS.md

Spring Boot 4 (parent 4.1.1) / **Spring Data REST** demo project on **Java 25** (enforced by the
maven-enforcer plugin). Single Maven module, package `ch.dboeckli.guru.jpa.rest`. It exposes
**Spring Data JPA repositories as REST endpoints** (`BeerRepository` published under
`/api/v9` via `spring.data.rest.base-path`, plus `BeerWebController` for the web UI) for the
entities `Beer` and `Customer`, against H2 and MySQL, with schema management via **Flyway**.
App port `8080`.

## Build & test commands

- Full build: `./mvnw clean verify` — format checks, unit (`*Test`, surefire) + IT (`*IT`, failsafe)
  tests, Helm lint/template.
- Unit tests only: `./mvnw test` (H2-based tests). Single test:
  `./mvnw test -Dtest=BeerRepositoryTest#methodName`.
- `./mvnw clean install` additionally builds the Docker image and packages the Helm chart into
  `target/helm/repo/`. Skip the Docker build with `-Dskip.docker.build=true`.
- `-Dskip.start.stop.springboot=true` skips the in-build app boot (spring-boot:start/stop).
- Run locally: `./mvnw spring-boot:run -Dspring-boot.run.profiles=h2` (or `mysql`).

After changing code, always verify: run the relevant Maven goal above and report its output
(evidence, not just "done").

## Profiles

- `h2`: in-memory H2 (no Docker).
- `mysql`: MySQL via Docker Compose — `compose-mysql.yaml`; schema via Flyway (`db/migration`).
- IntelliJ run configs in `.run/`: `Spring6Application h2`, `Spring6Application mysql`,
  `deploy-k8s`, `test-k8s`, `uninstall-k8s`, `clear docker`.

## Sandbox build quirk (background)

This sandbox mounts the repo via filesystem passthrough, which blocks symlinks — Spotless's
`npm install` (prettier) would fail with `EPERM` unless npm skips bin links. The sandbox kit sets
`npm_config_bin_links=false` globally (`spec.yaml` → `environment.variables`), so no manual export
is needed here. On a normal host (Windows/CI) this does not apply either.

## Formatting is enforced (fails the `validate` phase)

- Java: Spring Java Format → fix with `./mvnw spring-javaformat:apply`.
- Everything else (pom.xml, `**/*.md`, json, `src/main/resources/application*.yaml`, `**/*.sh`):
  Spotless → fix with `./mvnw spotless:apply`.
- Spotless flexmark also formats markdown, so this file and any `.md` edits must stay flexmark-clean;
  run `./mvnw spotless:apply` after editing markdown.

## Test conventions

- Naming matters: `*Test` = unit (surefire), `*IT` = integration (failsafe).
- H2 tests: repository tests (`repository/h2`) and controller tests (`controller/h2`) against
  in-memory H2.
- MySQL ITs: `@ActiveProfiles("test_mysql")` (`repository/mysql`, `controller/mysql`); they need
  Docker (MySQL from the test profile).
- A custom `TestClassOrderer` sorts test classes; `LocaleExtension` forces `Locale.US`.

## Architecture

- `repository/` Spring Data JPA repositories, exposed as REST via Spring Data REST
  (`spring.data.rest.base-path: /api/v9` in the profile YAMLs); `domain/` JPA entities
  (`Beer`, `Customer`); `ui/` MVC controller for the web UI; `bootstrap/BeerLoader` seeds data.
- Schema migrations: `src/main/resources/db/migration` (Flyway only).
- `log/` (`LogMessage`, `ConfigChangeListener`) + `config/RequestLoggingConfig` (next to
  `config/OpenApiConfiguration`) for contextual logging/request tracing.

## Deploy / CI

- Deployment is Helm-only: chart in `helm-charts/` (parent `sdjpa-spring-data-rest-chart`, MySQL
  subchart `sdjpa-spring-data-rest-mysql-chart`), packaged to `target/helm/repo/`, release name =
  artifactId, namespace `sdjpa-spring-data-rest`.
- CI (`.github/workflows/`): `maven-build.yml` builds + deploys snapshots and triggers
  `deploy-and-test-cluster.yml`; `release.yml` runs `mvn release:prepare release:perform` on
  main/master only (version must be `-SNAPSHOT`); SonarCloud analysis runs in the `analyze` job.
- Dependency updates are managed via `.github/renovate.json`; validate changes with
  `renovate-config-validator`.
