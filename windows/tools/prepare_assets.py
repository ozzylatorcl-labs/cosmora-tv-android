from pathlib import Path
from PIL import Image

root = Path(__file__).resolve().parents[2]
src = root / "app" / "src" / "main" / "res" / "drawable-nodpi" / "cosmora_icon.webp"
project = root / "windows" / "CosmoraTV.Windows"
assets = project / "Assets"
assets.mkdir(parents=True, exist_ok=True)

img = Image.open(src).convert("RGBA")
img.save(assets / "cosmora.png", "PNG", optimize=True)
img.save(
    project / "cosmora.ico",
    format="ICO",
    sizes=[(16, 16), (24, 24), (32, 32), (48, 48), (64, 64), (128, 128), (256, 256)],
)

print("Cosmora Windows assets prepared")
