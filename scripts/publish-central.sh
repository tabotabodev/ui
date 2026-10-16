#!/usr/bin/env bash
#
# Builds, signs and uploads tabotabo-ui to Maven Central through the Central Portal Publisher API,
# using only Gradle's built-in maven-publish + signing plugins plus curl.
#
# Usage: scripts/publish-central.sh <version>          e.g. scripts/publish-central.sh 0.1.0
#
# Required environment:
#   CENTRAL_USERNAME, CENTRAL_PASSWORD   Central Portal user token (central.sonatype.com → Account)
#   ORG_GRADLE_PROJECT_signingKey        ASCII-armored private PGP key
#   ORG_GRADLE_PROJECT_signingPassword   its passphrase
#   (the two signing values may instead live in ~/.gradle/gradle.properties as signingKey / signingPassword)
# Optional:
#   PUBLISHING_TYPE   USER_MANAGED (default: release by clicking "Publish" in the Portal) or AUTOMATIC
#
# Must run on macOS: the iOS artifacts can only be built there.
set -euo pipefail

VERSION="${1:?usage: $0 <version>}"
PUBLISHING_TYPE="${PUBLISHING_TYPE:-USER_MANAGED}"
: "${CENTRAL_USERNAME:?CENTRAL_USERNAME is not set}"
: "${CENTRAL_PASSWORD:?CENTRAL_PASSWORD is not set}"

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
STAGING="$ROOT/ui-lib/build/staging-repo"
BUNDLE="$ROOT/ui-lib/build/central-bundle.zip"
API="https://central.sonatype.com/api/v1/publisher"

case "$VERSION" in
  *-SNAPSHOT) echo "Refusing to release a SNAPSHOT version: $VERSION" >&2; exit 1 ;;
esac

echo "==> Building and signing tabotabo-ui $VERSION"
rm -rf "$STAGING" "$BUNDLE"
"$ROOT/gradlew" -p "$ROOT" :tabotabo-ui:publishAllPublicationsToStagingRepository \
  -PreleaseVersion="$VERSION" --no-configuration-cache -q

echo "==> Checking every artifact is signed"
if [[ ! -d "$STAGING/io" ]]; then
  echo "Nothing was published to $STAGING." >&2
  exit 1
fi
unsigned=0
while IFS= read -r file; do
  if [[ ! -f "$file.asc" ]]; then
    echo "missing signature: ${file#"$STAGING"/}" >&2
    unsigned=1
  fi
done < <(find "$STAGING" -type f \( -name '*.pom' -o -name '*.module' -o -name '*.jar' -o -name '*.aar' -o -name '*.klib' -o -name '*.json' \))
if [[ "$unsigned" -ne 0 ]]; then
  echo "Signing key missing or invalid; aborting before upload." >&2
  exit 1
fi

echo "==> Zipping bundle"
(cd "$STAGING" && zip -qr "$BUNDLE" io -x '*maven-metadata*')

TOKEN="$(printf '%s:%s' "$CENTRAL_USERNAME" "$CENTRAL_PASSWORD" | base64 | tr -d '\n')"
# GitHub only masks secrets verbatim; mask the derived token too in case anything echoes it.
if [[ -n "${GITHUB_ACTIONS:-}" ]]; then echo "::add-mask::$TOKEN"; fi

# curl with the auth header read from a file descriptor, so the token never appears in the
# process list (`ps`) the way a -H argument would.
central_curl() {
  curl --fail-with-body -sS -H @<(printf 'Authorization: Bearer %s\n' "$TOKEN") "$@"
}

echo "==> Uploading ($PUBLISHING_TYPE)"
DEPLOYMENT_ID="$(central_curl -X POST \
  "$API/upload?publishingType=$PUBLISHING_TYPE&name=tabotabo-ui-$VERSION" \
  -F "bundle=@$BUNDLE")"
echo "Deployment id: $DEPLOYMENT_ID"

echo "==> Waiting for validation"
for _ in $(seq 1 60); do
  STATUS="$(central_curl -X POST "$API/status?id=$DEPLOYMENT_ID")"
  STATE="$(printf '%s' "$STATUS" | grep -o '"deploymentState" *: *"[A-Z]*"' | grep -o '[A-Z]\{4,\}' || true)"
  echo "state: ${STATE:-unknown}"
  case "$STATE" in
    VALIDATED)
      echo "Validated. Release it at https://central.sonatype.com/publishing/deployments"
      exit 0 ;;
    PUBLISHING|PUBLISHED)
      echo "Publishing to Maven Central; it appears there within ~30 minutes."
      exit 0 ;;
    FAILED)
      echo "$STATUS" >&2
      exit 1 ;;
  esac
  sleep 10
done
echo "Timed out waiting for validation; check https://central.sonatype.com/publishing/deployments" >&2
exit 1
