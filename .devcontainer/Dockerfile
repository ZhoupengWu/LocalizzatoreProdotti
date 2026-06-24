# Dev Container per build di un progetto Gradle / Android
FROM mcr.microsoft.com/devcontainers/java:1-17-bookworm

# --- Parametri modificabili in base al progetto ---
ARG ANDROID_SDK_ROOT=/opt/android-sdk
ARG ANDROID_PLATFORM=android-34
ARG ANDROID_BUILD_TOOLS=34.0.0

ENV ANDROID_SDK_ROOT=${ANDROID_SDK_ROOT} \
    ANDROID_HOME=${ANDROID_SDK_ROOT} \
    PATH="${PATH}:${ANDROID_SDK_ROOT}/cmdline-tools/latest/bin:${ANDROID_SDK_ROOT}/platform-tools"

USER root

RUN apt-get update && apt-get install -y --no-install-recommends \
        unzip curl ca-certificates \
    && rm -rf /var/lib/apt/lists/*

# Scarica dinamicamente l'ultima versione delle Android command line tools
# (con un fallback hardcoded nel caso il parsing della pagina fallisca)
RUN set -eux; \
    mkdir -p "${ANDROID_SDK_ROOT}/cmdline-tools"; \
    CMDLINE_URL=$(curl -sL https://developer.android.com/studio \
        | grep -o 'https://dl\.google\.com/android/repository/commandlinetools-linux-[0-9]*_latest\.zip' \
        | head -1); \
    if [ -z "$CMDLINE_URL" ]; then \
        CMDLINE_URL="https://dl.google.com/android/repository/commandlinetools-linux-14742923_latest.zip"; \
    fi; \
    echo "Scarico cmdline-tools da: $CMDLINE_URL"; \
    curl -sL "$CMDLINE_URL" -o /tmp/cmdline-tools.zip; \
    unzip -q /tmp/cmdline-tools.zip -d "${ANDROID_SDK_ROOT}/cmdline-tools"; \
    mv "${ANDROID_SDK_ROOT}/cmdline-tools/cmdline-tools" "${ANDROID_SDK_ROOT}/cmdline-tools/latest"; \
    rm /tmp/cmdline-tools.zip

# Accetta tutte le licenze e installa i componenti SDK necessari per buildare
RUN yes | "${ANDROID_SDK_ROOT}/cmdline-tools/latest/bin/sdkmanager" --licenses > /dev/null 2>&1 || true && \
    "${ANDROID_SDK_ROOT}/cmdline-tools/latest/bin/sdkmanager" \
        "platform-tools" \
        "platforms;${ANDROID_PLATFORM}" \
        "build-tools;${ANDROID_BUILD_TOOLS}" > /dev/null

# Rende l'SDK scrivibile dall'utente vscode (serve se Gradle deve scaricare
# altri componenti SDK non preinstallati durante la build)
RUN chown -R vscode:vscode "${ANDROID_SDK_ROOT}"

USER vscode
