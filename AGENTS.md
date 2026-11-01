# Arrow Puzzle — Base44 sandbox notes

Native Android app (Kotlin + Jetpack Compose, single `:app` module). There is no
web frontend and no Gradle wrapper in the repo: `gradle/wrapper/gradle-wrapper.properties`
pins Gradle 9.3.1, and the sandbox toolchain image supplies that Gradle plus the
Android SDK. Use `gradle`, not `./gradlew`.

## Running in the sandbox

```bash
docker compose -f docker-compose.base44.yml up -d --build
```

- `android` — toolchain container (`.base44/Dockerfile.android`: Temurin JDK 21,
  Gradle 9.3.1, Android platform 36 / 36.1, build-tools 36.1.0). Repo bind-mounted
  at `/workspace`, so the next Gradle run compiles edited sources — no rebuild.
- `shots` — one-shot-ish job: renders the real Compose screens to
  `.base44/preview/shots` (Robolectric + Roborazzi `PreviewScreenshotTest`) and
  copies the debug APK to `.base44/preview/apk`, then idles. Re-render with
  `docker compose -f docker-compose.base44.yml restart shots`.
- `preview` — nginx serving `.base44/preview` on host port 3000. A browser cannot
  run an Android UI, so this page shows the rendered screens and the APK link.
  No emulator is possible in the sandbox (no `/dev/kvm`).

## Checks

Run inside the `android` service (the sandbox host has no JDK):

```bash
docker compose -f docker-compose.base44.yml exec -T android gradle --no-daemon assembleDebug
docker compose -f docker-compose.base44.yml exec -T android gradle --no-daemon :app:testDebugUnitTest
```

## Quirks

- `app/build.gradle.kts` signs debug builds with `${rootDir}/debug.keystore`, but
  that file is gitignored, so a clean checkout cannot resolve it and the build
  failed in `:app:validateSigningDebug`. The build now falls back to AGP's
  built-in `debug` signing config (which generates `~/.android/debug.keystore`,
  kept in the `android-cache` volume) when the repository keystore is absent. If
  `./debug.keystore` exists it is still used, unchanged.
- `google-services.json` is absent by design: `googleServices.missing.passthrough=true`
  makes the Firebase plugin warn instead of fail.
- `GEMINI_API_KEY` (`.env.example`) is optional and only needed at runtime for the
  Gemini integration; the app builds and its tests pass without it.
- `app/src/test/screenshots/greeting.png` is a tracked Roborazzi baseline. Running
  the suite without `-Proborazzi.test.record=true` compares against it — don't
  leave regenerated images behind.
