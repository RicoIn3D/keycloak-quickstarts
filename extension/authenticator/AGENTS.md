# Repository Guidelines

## Project Structure & Module Organization
The Maven module in this folder builds a single Keycloak extension JAR. Source lives in `src/main/java`, organized under `org.keycloak.nemdkv.authenticator` for the authenticator factory, required action, and `credential/` DTOs. Freemarker templates used for UI are under `src/main/resources/theme-resources/templates`, while SPI registration files belong in `src/main/resources/META-INF/services`. Build artifacts land in `target/`, notably `target/active-organization-authenticator.jar`, which is the file copied into `%KEYCLOAK_HOME%/providers`.

## Build, Test, and Development Commands
Run `mvn -Pextension clean install` to produce a release JAR; add `-DskipTests=true` only when builds must succeed without tests. Use `mvn test` (or `mvn verify` for full lifecycle) to execute the unit test suite once it exists. After building, deploy locally with `copy target\\active-organization-authenticator.jar %KEYCLOAK_HOME%\\providers\\` on Windows (use `cp` on macOS/Linux) and start the server via `kc.bat start-dev` or `kc.sh start-dev`.

## Coding Style & Naming Conventions
Code targets Java 17 and Keycloak SPIs. Follow 4-space indentation, braces on new lines, and avoid field injection; prefer constructor or method parameters. Keep packages prefixed with `org.keycloak.nemdkv.authenticator` and suffix primary classes with their role (e.g., `*Authenticator`, `*RequiredActionFactory`). New SPI implementations require corresponding entries in `META-INF/services/<fully-qualified-interface>`. Template files use lowercase kebab-case (`organization-selection.ftl`) and should expose message bundles instead of literals where possible.

## Testing Guidelines
Add unit tests under `src/test/java`, mirroring the package being tested and naming classes `*Test` (e.g., `SecretQuestionCredentialModelTest`). Use JUnit 5 with Mockito for SPI contracts, and isolate Keycloak dependencies with the provided testing utilities or lightweight mocks. Aim to cover success, failure, and multi-organization scenarios before submitting. When manual verification is unavoidable, document the steps in the PR and include the `kc start-dev` log snippet showing the authenticator loading.

## Commit & Pull Request Guidelines
Commits in this repo use short imperative sentences (see `git log --oneline`), so prefer messages like `Add validation to NemDkvOrganizationRequiredAction`. Each PR should describe the motivator, list test evidence (`mvn test`, server log), and link to any issue or Keycloak discussion. Include screenshots or console captures whenever UI templates or required actions change. Avoid bundling unrelated refactors with feature work; open a supporting PR if necessary.

## Security & Configuration Tips
Do not commit secrets, realm exports, or production URLs. Keep organization-specific constants in configuration or environment variables, not source files. When reading user attributes, validate inputs and sanitize template variables to avoid leaking sensitive answers in logs.
