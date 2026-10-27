#!/usr/bin/env bash
# Arrow Puzzle — sandbox build loop.
#
# There is no web frontend in this project (it is a native Android / Jetpack
# Compose app), so the sandbox keeps the debug APK built from the cloned source
# and publishes it for the preview web service on port 3000:
#
#   /preview/status.json    {"state":"building|ready|failed","build":N,...}
#   /preview/build.log      Gradle output of the most recent build
#   /preview/app-debug.apk  most recent successfully packaged debug APK
#
# The loop polls the project sources and rebuilds whenever they change.
set -uo pipefail

REPO="${REPO_DIR:-/workspace}"
OUT="${PREVIEW_DIR:-/preview}"
APK="$REPO/app/build/outputs/apk/debug/app-debug.apk"
BUILD=0
LAST=""

mkdir -p "$OUT"
: >"$OUT/build.log"

write_status() { # state started finished
  printf '{"state":"%s","build":%d,"startedAt":"%s","finishedAt":"%s"}\n' \
    "$1" "$BUILD" "${2:-}" "${3:-}" >"$OUT/status.json.tmp"
  mv "$OUT/status.json.tmp" "$OUT/status.json"
}

# Content hash of everything that affects the build output.
fingerprint() {
  find "$REPO/app/src" "$REPO/app/build.gradle.kts" "$REPO/build.gradle.kts" \
    "$REPO/settings.gradle.kts" "$REPO/gradle.properties" "$REPO/gradle" \
    -type f \( -name '*.kt' -o -name '*.java' -o -name '*.xml' -o -name '*.kts' \
    -o -name '*.toml' -o -name '*.pro' \) -print0 2>/dev/null |
    sort -z | xargs -0 sha1sum 2>/dev/null | sha1sum | cut -d' ' -f1
}

# app/build.gradle.kts signs the debug build with ./debug.keystore, which is
# gitignored, so generate it once per checkout.
ensure_keystore() {
  [ -f "$REPO/debug.keystore" ] && return 0
  keytool -genkeypair -keystore "$REPO/debug.keystore" -storepass android \
    -keypass android -alias androiddebugkey -dname "CN=Android Debug,O=Android,C=US" \
    -keyalg RSA -keysize 2048 -validity 10000 >/dev/null 2>&1
}

build_once() {
  BUILD=$((BUILD + 1))
  local started finished
  started="$(date -u +%FT%TZ)"
  echo "== build $BUILD started $started ==" >"$OUT/build.log"
  write_status building "$started" ""

  ensure_keystore
  if (cd "$REPO" && gradle assembleDebug --console=plain) >>"$OUT/build.log" 2>&1 && [ -f "$APK" ]; then
    cp "$APK" "$OUT/app-debug.apk.tmp" && mv "$OUT/app-debug.apk.tmp" "$OUT/app-debug.apk"
    finished="$(date -u +%FT%TZ)"
    echo "== build $BUILD succeeded $finished ==" >>"$OUT/build.log"
    write_status ready "$started" "$finished"
  else
    finished="$(date -u +%FT%TZ)"
    echo "== build $BUILD FAILED $finished ==" >>"$OUT/build.log"
    write_status failed "$started" "$finished"
  fi
}

echo "watching $REPO for changes; publishing to $OUT"
while true; do
  CURRENT="$(fingerprint)"
  if [ "$CURRENT" != "$LAST" ]; then
    LAST="$CURRENT"
    build_once
    LAST="$(fingerprint)"
  fi
  sleep 3
done
