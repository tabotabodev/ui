"""Generates every app icon, favicon and logo in the repository from mascot-source.png.

The source mascot has transparent cut-outs for its eye whites and mouth, which would show
whatever is behind them (a dark browser tab, a dark Dokka header). This script first fills
every transparent region *enclosed* by the drawing with white, leaving only the outside
transparent, then renders each platform's variant:

    branding/icon.png                           README logo (transparent)
    androidApp/.../drawable-*dpi/ic_launcher_foreground.png   adaptive foreground (safe zone)
    androidApp/.../mipmap-*dpi/ic_launcher[_round].png        legacy launcher icons
    iosApp/.../AppIcon.appiconset/app-icon-1024.png           opaque, on white
    desktopApp/src/main/resources/icon.png      window icon
    desktopApp/icons/icon.{icns,ico,png}        installer icons (macOS / Windows / Linux)
    {website,webApp}/.../favicon.png, apple-touch-icon.png, icon-512.png
    ui-lib/dokka/logo-icon.svg                  Dokka logo + favicon (PNG embedded in SVG)

Run from anywhere (macOS needed for the .icns step): python3 branding/generate_icons.py
"""

import base64
import io
import shutil
import subprocess
import tempfile
from pathlib import Path

from PIL import Image, ImageDraw

ROOT = Path(__file__).resolve().parent.parent
SOURCE = ROOT / "branding" / "mascot-source.png"
WHITE = (255, 255, 255, 255)


def filled_mascot() -> Image.Image:
    """The mascot cropped to its drawing, with enclosed transparent regions painted white."""
    img = Image.open(SOURCE).convert("RGBA")
    w, h = img.size
    # Flood-fill the outside (anything transparent connected to the border) on a mask, then
    # paint every remaining transparent pixel (the cut-outs) white.
    mask = Image.new("L", (w + 2, h + 2), 0)
    mask.paste(img.getchannel("A").point(lambda a: 255 if a > 0 else 0), (1, 1))
    ImageDraw.floodfill(mask, (0, 0), 128)
    outside = mask.crop((1, 1, w + 1, h + 1)).point(lambda v: 255 if v == 128 else 0)
    holes = Image.eval(img.getchannel("A"), lambda a: 255 if a == 0 else 0)
    holes = Image.composite(Image.new("L", (w, h), 0), holes, outside)
    filled = Image.composite(Image.new("RGBA", (w, h), WHITE), img, holes)
    return filled.crop(filled.getbbox())


def place(mascot: Image.Image, size: int, fraction: float, background=None, shape: str = "square") -> Image.Image:
    """[mascot] scaled so its larger side is [fraction] of [size], centred on a [size] canvas."""
    canvas = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    if background is not None:
        bg = Image.new("RGBA", (size, size), (0, 0, 0, 0))
        draw = ImageDraw.Draw(bg)
        if shape == "circle":
            draw.ellipse((0, 0, size - 1, size - 1), fill=background)
        elif shape == "rounded":
            draw.rounded_rectangle((0, 0, size - 1, size - 1), radius=size // 5, fill=background)
        else:
            draw.rectangle((0, 0, size, size), fill=background)
        canvas = Image.alpha_composite(canvas, bg)
    scale = fraction * size / max(mascot.size)
    art = mascot.resize((max(1, round(mascot.width * scale)), max(1, round(mascot.height * scale))), Image.LANCZOS)
    canvas.alpha_composite(art, ((size - art.width) // 2, (size - art.height) // 2))
    return canvas


def save(img: Image.Image, path: Path, opaque: bool = False) -> None:
    path.parent.mkdir(parents=True, exist_ok=True)
    (img.convert("RGB") if opaque else img).save(path, optimize=True)
    print(f"  {path.relative_to(ROOT)}  {img.size[0]}x{img.size[1]}")


def main() -> None:
    mascot = filled_mascot()

    print("README")
    save(place(mascot, 256, 0.96), ROOT / "branding" / "icon.png")

    print("Android")
    res = ROOT / "androidApp" / "src" / "main" / "res"
    densities = {"mdpi": 1, "hdpi": 1.5, "xhdpi": 2, "xxhdpi": 3, "xxxhdpi": 4}
    for name, factor in densities.items():
        # Adaptive foreground: 108dp canvas, art kept inside the 66dp safe zone.
        save(place(mascot, round(108 * factor), 0.6), res / f"drawable-{name}" / "ic_launcher_foreground.png")
        legacy = round(48 * factor)
        save(place(mascot, legacy, 0.78, WHITE, "rounded"), res / f"mipmap-{name}" / "ic_launcher.png")
        save(place(mascot, legacy, 0.72, WHITE, "circle"), res / f"mipmap-{name}" / "ic_launcher_round.png")

    print("iOS")
    save(place(mascot, 1024, 0.8, WHITE), ROOT / "iosApp" / "iosApp" / "Assets.xcassets" / "AppIcon.appiconset" / "app-icon-1024.png", opaque=True)

    print("Desktop")
    save(place(mascot, 512, 0.94), ROOT / "desktopApp" / "src" / "main" / "resources" / "icon.png")
    icons = ROOT / "desktopApp" / "icons"
    installer = place(mascot, 1024, 0.86)
    save(installer, icons / "icon.png")
    installer.save(icons / "icon.ico", sizes=[(16, 16), (32, 32), (48, 48), (64, 64), (128, 128), (256, 256)])
    print(f"  {(icons / 'icon.ico').relative_to(ROOT)}")
    if shutil.which("iconutil"):
        with tempfile.TemporaryDirectory() as tmp:
            iconset = Path(tmp) / "icon.iconset"
            iconset.mkdir()
            for base in (16, 32, 128, 256, 512):
                place(mascot, base, 0.86).save(iconset / f"icon_{base}x{base}.png")
                place(mascot, base * 2, 0.86).save(iconset / f"icon_{base}x{base}@2x.png")
            subprocess.run(["iconutil", "-c", "icns", str(iconset), "-o", str(icons / "icon.icns")], check=True)
        print(f"  {(icons / 'icon.icns').relative_to(ROOT)}")
    else:
        print("  (iconutil not found: skipped icon.icns; run on macOS)")

    print("Web")
    for web in (ROOT / "website" / "src" / "wasmJsMain" / "resources", ROOT / "webApp" / "src" / "webMain" / "resources"):
        save(place(mascot, 64, 0.98), web / "favicon.png")
        save(place(mascot, 180, 0.82, WHITE), web / "apple-touch-icon.png", opaque=True)
        save(place(mascot, 512, 0.9), web / "icon-512.png")

    print("Dokka")
    buffer = io.BytesIO()
    place(mascot, 128, 0.98).save(buffer, format="PNG", optimize=True)
    data = base64.b64encode(buffer.getvalue()).decode("ascii")
    svg = (
        '<svg xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" '
        'width="128" height="128" viewBox="0 0 128 128">'
        f'<image width="128" height="128" xlink:href="data:image/png;base64,{data}"/></svg>\n'
    )
    dokka = ROOT / "ui-lib" / "dokka" / "logo-icon.svg"
    dokka.parent.mkdir(parents=True, exist_ok=True)
    dokka.write_text(svg)
    print(f"  {dokka.relative_to(ROOT)}")


if __name__ == "__main__":
    main()
