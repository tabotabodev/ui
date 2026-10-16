#!/usr/bin/env bash
#
# Assembles the GitHub Pages site from already-built parts:
#   <out>/            website       (:website:wasmJsBrowserDistribution)
#   <out>/showcase/   showcase app  (:webApp:wasmJsBrowserDistribution)
#   <out>/api/        API reference (:tabotabo-ui:dokkaGeneratePublicationHtml)
#   <out>/skill/      Claude skill  (skill/tabotabo-ui, as SKILL.md and a zip)
#
# Usage: scripts/assemble-site.sh <out-dir>
set -euo pipefail

OUT="${1:?usage: $0 <out-dir>}"
ROOT="$(cd "$(dirname "$0")/.." && pwd)"

rm -rf "$OUT"
mkdir -p "$OUT/showcase" "$OUT/api" "$OUT/skill"
OUT="$(cd "$OUT" && pwd)"  # absolute, since the zip step changes directory
cp -R "$ROOT/website/build/dist/wasmJs/productionExecutable/." "$OUT/"
cp -R "$ROOT/webApp/build/dist/wasmJs/productionExecutable/." "$OUT/showcase/"
cp -R "$ROOT/ui-lib/build/dokka/html/." "$OUT/api/"
cp "$ROOT/skill/tabotabo-ui/SKILL.md" "$OUT/skill/SKILL.md"
(cd "$ROOT/skill" && zip -qr "$OUT/skill/tabotabo-ui-skill.zip" tabotabo-ui)

# Source maps stay out of the public site.
find "$OUT" -name '*.map' -delete
