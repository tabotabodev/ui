#!/usr/bin/env bash
#
# Assembles the GitHub Pages site from already-built parts:
#   <out>/            website       (:website:wasmJsBrowserDistribution), plus an index.html per
#                                   route and a 404.html, written by scripts/route_pages.py
#   <out>/showcase/   showcase app  (:webApp:wasmJsBrowserDistribution)
#   <out>/api/        API reference (:tabotabo-ui:dokkaGeneratePublicationHtml)
#   <out>/skill/      Claude skill  (skill/tabotabo-ui, as SKILL.md and a zip)
#
# Usage: scripts/assemble-site.sh <out-dir>
#   SITE_BASE    path the site is served from (default /ui/, the GitHub Pages project path)
#   SITE_ORIGIN  scheme and host, for link previews (default https://tabotabodev.github.io)
set -euo pipefail

SITE_BASE="${SITE_BASE:-/ui/}"
SITE_ORIGIN="${SITE_ORIGIN:-https://tabotabodev.github.io}"

OUT="${1:?usage: $0 <out-dir>}"
ROOT="$(cd "$(dirname "$0")/.." && pwd)"

rm -rf "$OUT"
mkdir -p "$OUT/showcase" "$OUT/api" "$OUT/skill"
OUT="$(cd "$OUT" && pwd)"  # absolute, since the zip step changes directory
cp -R "$ROOT/website/build/dist/wasmJs/productionExecutable/." "$OUT/"
# GitHub Pages has no rewrites: every route gets its own index.html (with its own title and
# description), and unknown paths get 404.html, all loading the app from SITE_BASE.
python3 -I "$ROOT/scripts/route_pages.py" "$OUT" "$SITE_BASE" "$SITE_ORIGIN"
cp -R "$ROOT/webApp/build/dist/wasmJs/productionExecutable/." "$OUT/showcase/"
cp -R "$ROOT/ui-lib/build/dokka/html/." "$OUT/api/"
cp "$ROOT/skill/tabotabo-ui/SKILL.md" "$OUT/skill/SKILL.md"
(cd "$ROOT/skill" && zip -qr "$OUT/skill/tabotabo-ui-skill.zip" tabotabo-ui)

# Source maps stay out of the public site.
find "$OUT" -name '*.map' -delete
