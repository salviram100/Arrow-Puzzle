# Arrow Puzzle — Base44 sandbox notes

Native Android app (Kotlin + Jetpack Compose, single `:app` module). There is no
web frontend and no Gradle wrapper in the repo — the sandbox supplies Gradle and
the Android SDK from a toolchain image.

## Running in the sandbox

```bash
docker compose -f docker-compose.base44.yml up -d --build
```

- `android` — toolchain container (`docker/android.Dockerfile`: Temurin JDK 21,
  Gradle 9.5, Android platform 36.1 / build-tools 36.1). Mounts the repo at
  `/workspace` and runs `.base44/scripts/build-watch.sh`, which rebuilds
  `assembleDebug` whenever the sources change and publishes the result to
  `.base44/preview/` (`status.json`, `build.log`, `app-debug.apk`).
- `web` — nginx serving `.base44/preview/` on host port 3000: build status plus
  the downloadable debug APK. There is no emulator in the sandbox (no `/dev/kvm`),
  so the APK is installed on a real device/emulator by the user.

First build after a fresh stack takes ~2–3 minutes (dependency download); later
incremental builds are fast because `~/.gradle` is a named volume.

## Quirks

- `app/build.gradle.kts` signs the debug build with `./debug.keystore`, which is
  gitignored. The build loop generates it with `keytool` (store/key password
  `android`, alias `androiddebugkey`) if missing — the build fails with
  `Keystore file '.../debug.keystore' not found` otherwise.
- `google-services.json` is absent by design; `googleServices.missing.passthrough`
  is set, so the Firebase plugin only warns.
- Firebase AI (Gemini) needs `GEMINI_API_KEY` in `.env` (copied from
  `.env.example`) at runtime; the app compiles and builds without it.

## Checks

Run inside the `android` service (the sandbox host has no JDK):

```bash
docker compose -f docker-compose.base44.yml exec -T android gradle compileDebugKotlin --console=plain
docker compose -f docker-compose.base44.yml exec -T android gradle testDebugUnitTest --console=plain
```

Roborazzi screenshot output lands in `app/src/test/screenshots/` (tracked files —
do not leave regenerated images behind unless the change is intentional).
