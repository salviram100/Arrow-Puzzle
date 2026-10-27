# Setup notes for sandbox / automation work

This repository is a **native Android app** (Kotlin, Jetpack Compose, Room). There is no web
frontend and no HTTP API, so nothing in the project itself listens on a port.

## Running it here

Everything runs through `docker-compose.base44.yml` (development setup, source bind-mounted):

```bash
docker compose -f docker-compose.base44.yml up -d          # android + shots + preview
docker compose -f docker-compose.base44.yml exec android gradle :app:assembleDebug
docker compose -f docker-compose.base44.yml exec android gradle :app:testDebugUnitTest
```

- `android` — dev container: JDK 21 + Android SDK (platform 36 / 36.1, build-tools 36.0.0) +
  Gradle. Source is bind-mounted at `/workspace`, so no image rebuild is needed for code changes.
  Gradle and Android caches live in the named volumes `gradle-cache` / `android-cache`.
- `shots` — one-shot job: renders the real Compose screens to PNG (Robolectric + Roborazzi via
  `PreviewScreenshotTest`), builds the debug APK, and copies both into `.base44/preview/`.
- `preview` — nginx on host port 3000 serving `.base44/preview/`: a gallery of the rendered
  screens plus the APK download. This is the only thing a browser can show of this app; the game
  UI itself needs a device or emulator (no KVM in the sandbox, so no emulator here).

## Non-obvious things

- **No Gradle wrapper in the repo.** `gradle/wrapper/gradle-wrapper.properties` exists but there is
  no `gradlew` script or wrapper jar; the image installs the pinned Gradle 9.3.1 and the commands
  use `gradle` directly.
- **`debug.keystore` is required and gitignored.** `app/build.gradle.kts` signs the debug build with
  `${rootDir}/debug.keystore`, which Android Studio normally generates locally. The compose
  command creates it with `keytool` if it is missing, otherwise `assembleDebug` fails at
  `:app:validateSigningDebug`.
- **No `google-services.json`.** `googleServices.missing.passthrough=true` in `gradle.properties`
  makes the plugin warn instead of failing. Firebase artifacts are on the classpath but no code
  calls Firebase Auth or the Gemini API, so **no credentials are needed** to build or run.
- **`.env.example`'s `GEMINI_API_KEY` is unused.** Do not add it as a secret; nothing reads it.
- Robolectric downloads its `android-all-instrumented` jar on first test run, so the first
  `testDebugUnitTest` needs network and takes a few minutes.
- Generated artifacts (`.base44/preview/shots/`, `.base44/preview/apk/`) are gitignored; a fresh
  checkout shows the preview page with "rendering…" placeholders until `shots` runs.

## How to verify it works

```bash
curl -s -o /dev/null -w '%{http_code}\n' http://localhost:3000/                    # page
curl -s -o /dev/null -w '%{http_code}\n' http://localhost:3000/shots/03-lobby.png  # rendered screen
docker compose -f docker-compose.base44.yml exec -T android gradle :app:testDebugUnitTest
```

`PreviewScreenshotTest` also plays a real move by tapping a board cell through the Compose test
API, so a green run plus the `06-gameplay-after-move.png` capture confirm the puzzle engine works
through the UI.
