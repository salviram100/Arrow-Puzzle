# Toolchain image for the Arrow Puzzle Android app.
# It contains ONLY the JDK, Gradle and the Android SDK — the project source is
# bind-mounted at ./  (never COPY'd), so edits in the sandbox rebuild without
# rebuilding this image.
FROM eclipse-temurin:21-jdk

ARG GRADLE_VERSION=9.5.0
ARG CMDLINE_TOOLS=commandlinetools-linux-16111833_latest.zip

ENV ANDROID_HOME=/opt/android-sdk \
    ANDROID_SDK_ROOT=/opt/android-sdk \
    GRADLE_USER_HOME=/root/.gradle

RUN apt-get update \
 && apt-get install -y --no-install-recommends ca-certificates curl unzip git \
 && rm -rf /var/lib/apt/lists/*

# Gradle
RUN curl -fsSL "https://services.gradle.org/distributions/gradle-${GRADLE_VERSION}-bin.zip" -o /tmp/gradle.zip \
 && unzip -q /tmp/gradle.zip -d /opt \
 && mv "/opt/gradle-${GRADLE_VERSION}" /opt/gradle \
 && rm /tmp/gradle.zip \
 && ln -s /opt/gradle/bin/gradle /usr/local/bin/gradle

# Android command line tools + platform/build-tools used by app/build.gradle.kts
RUN mkdir -p "${ANDROID_HOME}/cmdline-tools" \
 && curl -fsSL "https://dl.google.com/android/repository/${CMDLINE_TOOLS}" -o /tmp/cmdline-tools.zip \
 && unzip -q /tmp/cmdline-tools.zip -d "${ANDROID_HOME}/cmdline-tools" \
 && mv "${ANDROID_HOME}/cmdline-tools/cmdline-tools" "${ANDROID_HOME}/cmdline-tools/latest" \
 && rm /tmp/cmdline-tools.zip \
 && yes | "${ANDROID_HOME}/cmdline-tools/latest/bin/sdkmanager" --licenses > /dev/null \
 && "${ANDROID_HOME}/cmdline-tools/latest/bin/sdkmanager" \
      "platform-tools" \
      "platforms;android-36.1" \
      "build-tools;36.1.0" \
 && rm -rf "${ANDROID_HOME}/.temp" "${ANDROID_HOME}/.downloadIntermediates"

ENV PATH="${PATH}:/opt/gradle/bin:${ANDROID_HOME}/platform-tools:${ANDROID_HOME}/cmdline-tools/latest/bin"

WORKDIR /workspace
CMD ["bash"]
