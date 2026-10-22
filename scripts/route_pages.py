#!/usr/bin/env python3
"""Writes one index.html per website route, plus 404.html, for GitHub Pages.

GitHub Pages has no rewrites, so a deep link like /ui/principles/color/ needs a real file. Each
copy loads the same app (through <base href>) but carries its own <title>, description,
link-preview tags and #fallback text, which search engines read and visitors see while the app
loads.

Usage: route_pages.py <site-dir> <site-base> <site-origin>
  site-dir     assembled site whose index.html is the website's built index.html
  site-base    path the site is served from, e.g. /ui/
  site-origin  scheme and host, e.g. https://tabotabodev.github.io
"""
import html
import re
import sys
from pathlib import Path

SITE_NAME = "Tabotabo UI"

# One row per page. Keep in sync with SitePage in website/.../Routes.kt:
# (path, nav label, plain title words, key phrase, description).
PAGES = [
    ("", "Home", "Tabotabo", "UI",
     "Tabotabo UI is a minimal, editorial design system for Compose Multiplatform: one random hue, "
     "Montserrat type, outlined surfaces and quiet motion, on Android, iOS, desktop and the web."),
    ("principles/", "Principles", "What makes it", "Tabotabo",
     "The ideas behind Tabotabo UI: one hue, one type family, outlined surfaces, calm layouts and quiet motion."),
    ("principles/color/", "Color", "One hue,", "every color",
     "How every Tabotabo UI color is derived from a single hue, with a live preview."),
    ("principles/text/", "Text", "One family,", "weight and color",
     "Tabotabo UI's type scale and its Light + Bold emphasis rule, with a live playground."),
    ("principles/surfaces/", "Surfaces", "Outlined, not", "elevated",
     "Cards, buttons, pills and dialogs in Tabotabo UI: outlined, never elevated."),
    ("principles/layout/", "Layout", "Calm", "layouts",
     "Tabotabo UI's layouts: left-aligned content, a fixed spacing scale and no layout shifts."),
    ("principles/motion/", "Motion", "Purposeful", "motion",
     "Motion in Tabotabo UI: typewriter headlines, pop-ins, pulses and the occasional celebration."),
    ("components/", "Components", "A", "quick tour",
     "A live tour of Tabotabo UI's components: buttons, glow cards, pills, dialogs, layouts and more."),
    ("start/", "Get started", "Add it in", "one line",
     "Add Tabotabo UI to a Compose Multiplatform or Android app, and teach Claude the system."),
]

NOT_FOUND = ("404.html", "Not found", "Page not", "found", "This page doesn't exist.")


def replace_once(text: str, pattern: str, replacement: str) -> str:
    """Replaces the single match of [pattern]; fails loudly if index.html changed shape."""
    result, count = re.subn(pattern, lambda _: replacement, text, flags=re.S)
    if count != 1:
        sys.exit(f"route_pages.py: expected one match for {pattern!r} in index.html, found {count}")
    return result


def page_html(template: str, base: str, url: str, label: str, plain: str, focus: str, description: str,
              index: bool) -> str:
    """The built index.html, filled in for one page."""
    title = f"{SITE_NAME} · A Compose Multiplatform design system" if label == "Home" else f"{label} · {SITE_NAME}"
    e = html.escape
    out = template
    out = replace_once(out, r'<base href="/">', f'<base href="{e(base)}">')
    out = replace_once(out, r"<title>.*?</title>", f"<title>{e(title)}</title>")
    out = replace_once(out, r'<meta name="description" content=".*?">', f'<meta name="description" content="{e(description)}">')
    out = replace_once(out, r'<meta property="og:title" content=".*?">', f'<meta property="og:title" content="{e(title)}">')
    out = replace_once(out, r'<meta property="og:description" content=".*?">', f'<meta property="og:description" content="{e(description)}">')
    out = replace_once(out, r'<meta property="og:url" content=".*?">', f'<meta property="og:url" content="{e(url)}">')
    out = replace_once(out, r'<h1 id="fallback-title">.*?</h1>', f'<h1 id="fallback-title">{e(plain)} <b>{e(focus)}</b></h1>')
    out = replace_once(out, r'<p id="fallback-text">.*?</p>', f'<p id="fallback-text">{e(description)}</p>')
    if not index:
        out = replace_once(out, r"<title>", '<meta name="robots" content="noindex">\n    <title>')
    return out


def main() -> None:
    if len(sys.argv) != 4:
        sys.exit(__doc__)
    site, base, origin = Path(sys.argv[1]), sys.argv[2], sys.argv[3].rstrip("/")
    template = (site / "index.html").read_text(encoding="utf-8")
    for path, label, plain, focus, description in PAGES:
        target = site / path / "index.html"
        target.parent.mkdir(parents=True, exist_ok=True)
        target.write_text(page_html(template, base, origin + base + path, label, plain, focus, description, index=True), encoding="utf-8")
    name, label, plain, focus, description = NOT_FOUND
    (site / name).write_text(page_html(template, base, origin + base, label, plain, focus, description, index=False), encoding="utf-8")


if __name__ == "__main__":
    main()
