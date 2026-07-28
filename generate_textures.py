#!/usr/bin/env python3
"""
Nuclear Eclipse — texture generator
====================================
Generates every PNG texture for the mod:
  - 7 bomb item icons (16x16) with glowing cores + metallic shells
  - 7 particle sprite sheets (multi-frame, 8x8) with additive-style gradients
  - 2 decorative block textures (scorch_glass, crystal_deposit) (16x16)
  - 1 creative-tab icon (64x64)

All textures are procedural — no external assets — so they're fully MIT-licensed.
Run:  python3 generate_textures.py
"""
import os
import math
import random
from PIL import Image, ImageFilter, ImageDraw

random.seed(20260728)

# Output root (resources)
ROOT = os.path.join(os.path.dirname(__file__),
    "src", "main", "resources", "assets", "nucleareclipse")

def out_path(*parts):
    p = os.path.join(ROOT, *parts)
    os.makedirs(os.path.dirname(p), exist_ok=True)
    return p

# ─────────────────────────── Helpers ───────────────────────────

def clamp(v, lo, hi): return max(lo, min(hi, v))

def lerp(a, b, t): return a + (b - a) * t

def lerp_color(c1, c2, t):
    return tuple(int(lerp(c1[i], c2[i], t)) for i in range(min(len(c1), len(c2), 4)))

def new_rgba(w, h, fill=(0,0,0,0)):
    return Image.new("RGBA", (w, h), fill)

def put_pixel(img, x, y, color):
    if 0 <= x < img.width and 0 <= y < img.height:
        img.putpixel((x, y), color)

def distance(x1, y1, x2, y2):
    return math.hypot(x1 - x2, y1 - y2)

def radial_fill(img, cx, cy, radius, center_color, edge_color, alpha_power=1.0):
    """Fill with a radial gradient from center_color to edge_color."""
    for y in range(img.height):
        for x in range(img.width):
            d = distance(x, y, cx, cy) / max(radius, 1)
            if d <= 1.0:
                c = lerp_color(center_color, edge_color, d)
                a = int(255 * (1.0 - d) ** alpha_power)
                put_pixel(img, x, y, (c[0], c[1], c[2], a))

def add_glow(img, cx, cy, radius, color, intensity=1.0):
    """Additive glow centered on (cx,cy)."""
    glow = new_rgba(img.width, img.height)
    radial_fill(glow, cx, cy, radius, color, (color[0]//2, color[1]//2, color[2]//2, 0),
                alpha_power=0.5)
    glow = glow.filter(ImageFilter.GaussianBlur(radius * 0.4))
    # Blend additively
    base = img.convert("RGBA")
    blended = Image.eval(base, lambda v: v)
    px = base.load(); gp = glow.load()
    for y in range(img.height):
        for x in range(img.width):
            r,g,b,a = px[x,y]
            gr,gg,gb,ga = gp[x,y]
            ia = intensity * (ga/255.0)
            put_pixel(blended, x, y, (
                clamp(int(r + gr*ia),0,255),
                clamp(int(g + gg*ia),0,255),
                clamp(int(b + gb*ia),0,255),
                a))
    img.paste(blended, (0,0))

def noise_overlay(img, amount=0.1, seed=0):
    rng = random.Random(seed)
    px = img.load()
    for y in range(img.height):
        for x in range(img.width):
            r,g,b,a = px[x,y]
            if a == 0: continue
            n = (rng.random() - 0.5) * 2 * amount
            put_pixel(img, x, y, (
                clamp(int(r + r*n),0,255),
                clamp(int(g + g*n),0,255),
                clamp(int(b + b*n),0,255),
                a))

# ─────────────────────────── Bomb icons ───────────────────────────
# Each bomb: a dark spherical shell with a glowing core + radial accent.
BOMB_SPECS = {
    "quantum_bomb":   {"core": (170, 80, 255), "shell": (40, 20, 70),   "accent": (120, 200, 255), "label": "Q"},
    "chronos_bomb":   {"core": (255, 210, 60), "shell": (60, 45, 10),   "accent": (255, 160, 0),  "label": "T"},
    "crystal_bomb":   {"core": (80, 255, 240), "shell": (15, 60, 70),   "accent": (180, 255, 255),"label": "C"},
    "void_bomb":      {"core": (20, 0, 40),    "shell": (5, 0, 10),     "accent": (90, 0, 130),   "label": "V"},
    "stellar_bomb":   {"core": (255, 200, 80), "shell": (80, 30, 0),    "accent": (255, 100, 0),  "label": "S"},
    "glow_spore_bomb":{"core": (120, 255, 120),"shell": (20, 70, 30),   "accent": (200, 255, 120),"label": "G"},
    "aurora_bomb":    {"core": (120, 255, 200),"shell": (10, 40, 70),   "accent": (180, 120, 255),"label": "A"},
}

def draw_bomb(name, spec, size=16):
    img = new_rgba(size, size)
    cx = cy = (size - 1) / 2
    R = size * 0.42
    # Outer shell — radial dark gradient
    radial_fill(img, cx, cy, R, spec["shell"], (0,0,0,0), alpha_power=0.7)
    # Inner core — bright glowing orb
    core_R = R * 0.45
    radial_fill(img, cx, cy, core_R, (255,255,255,255), spec["core"] + (255,), alpha_power=0.4)
    # Accent ring
    for y in range(size):
        for x in range(size):
            d = distance(x, y, cx, cy)
            if R*0.78 <= d <= R*0.98:
                t = (d - R*0.78) / (R*0.98 - R*0.78)
                c = spec["accent"]
                a = int(255 * (1.0 - abs(t - 0.5)*2) ** 0.5)
                put_pixel(img, x, y, (c[0], c[1], c[2], a))
    # Subtle scanlines for "tech" feel
    for y in range(0, size, 3):
        for x in range(size):
            r,g,b,a = img.getpixel((x,y))
            if a > 0:
                put_pixel(img, x, y, (clamp(int(r*0.7),0,255), clamp(int(g*0.7),0,255), clamp(int(b*0.7),0,255), a))
    noise_overlay(img, 0.08, seed=hash(name) & 0xffff)
    # Add overall glow
    add_glow(img, cx, cy, R*1.3, spec["accent"] + (255,), intensity=0.35)
    img.save(out_path("textures", "item", name + ".png"))
    print("  item:", name)

# ─────────────────────────── Particle sprites ───────────────────────────
# Multi-frame sprite sheets: each frame is an 8x8 glowing dot in a different
# stage of its "life" (bright→dim). Forge expects a vertical strip.

PARTICLE_SPECS = {
    "quantum_spark":  {"color": (150, 90, 255),  "frames": 6, "size": 8},
    "chronos_dust":   {"color": (255, 200, 80),  "frames": 6, "size": 8},
    "crystal_shard":  {"color": (80, 255, 240),  "frames": 6, "size": 8},
    "void_echo":      {"color": (40, 0, 80),     "frames": 6, "size": 8},
    "stellar_flare":  {"color": (255, 180, 60),  "frames": 6, "size": 8},
    "glow_spore":     {"color": (120, 255, 140), "frames": 6, "size": 8},
    "aurora_ribbon":  {"color": (120, 255, 200), "frames": 6, "size": 8},
}

def draw_particle(name, spec):
    size = spec["size"]
    frames = spec["frames"]
    color = spec["color"]
    sheet = new_rgba(size, size * frames)
    cx = cy = (size - 1) / 2
    R = size * 0.5
    for f in range(frames):
        # life t: 0 (bright) → 1 (dim)
        t = f / (frames - 1)
        brightness = 1.0 - t * 0.8
        shrink = 1.0 - t * 0.4
        # Frame color: hot center → particle color → dark edge
        center = (255,255,255) if brightness > 0.7 else \
                 tuple(int(c * brightness) for c in color)
        edge = tuple(int(c * brightness * 0.3) for c in color)
        frame_img = new_rgba(size, size)
        radial_fill(frame_img, cx, cy, R * shrink, center, edge + (0,), alpha_power=0.3)
        # Sparkly cross flare
        for d in range(int(R)):
            a = int(255 * (1.0 - d / R) * brightness)
            c = color
            put_pixel(frame_img, int(cx), int(cy - d), (c[0], c[1], c[2], a))
            put_pixel(frame_img, int(cx), int(cy + d), (c[0], c[1], c[2], a))
            put_pixel(frame_img, int(cx - d), int(cy), (c[0], c[1], c[2], a))
            put_pixel(frame_img, int(cx + d), int(cy), (c[0], c[1], c[2], a))
        # Blur a touch for soft glow
        frame_img = frame_img.filter(ImageFilter.GaussianBlur(0.6))
        sheet.paste(frame_img, (0, f * size))
    sheet.save(out_path("textures", "particle", name + ".png"))
    print("  particle:", name)

# ─────────────────────────── Block textures ───────────────────────────

def draw_scorch_glass(size=16):
    img = new_rgba(size, size)
    cx = cy = (size - 1) / 2
    radial_fill(img, cx, cy, size*0.5, (20,0,20,220), (0,0,0,255), alpha_power=0.2)
    # Cracks — thin glowing violet veins
    rng = random.Random(7)
    for _ in range(6):
        x, y = rng.randint(2,size-3), rng.randint(2,size-3)
        ang = rng.random()*math.tau
        for step in range(8):
            nx = int(x + math.cos(ang)*step)
            ny = int(y + math.sin(ang)*step)
            for ox,oy in [(0,0),(1,0),(0,1)]:
                put_pixel(img, nx+ox, ny+oy, (140, 60, 200, 200))
            ang += (rng.random()-0.5)*0.6
    add_glow(img, cx, cy, size*0.4, (120,40,180,255), intensity=0.25)
    img.save(out_path("textures", "block", "scorch_glass.png"))
    print("  block: scorch_glass")

def draw_crystal_deposit(size=16):
    img = new_rgba(size, size)
    rng = random.Random(13)
    for y in range(size):
        for x in range(size):
            # Facet shading based on position
            n = rng.random()
            base = (20, 140, 160) if (x+y) % 2 == 0 else (40, 200, 220)
            shade = 0.7 + 0.3*n
            put_pixel(img, x, y, (int(base[0]*shade), int(base[1]*shade), int(base[2]*shade), 255))
    # Bright crystal facets
    for _ in range(10):
        x, y = rng.randint(1,size-2), rng.randint(1,size-2)
        for ox,oy in [(0,0)]:
            put_pixel(img, x+ox, y+oy, (200,255,255,255))
    add_glow(img, size/2, size/2, size*0.5, (80,255,240,255), intensity=0.3)
    img.save(out_path("textures", "block", "crystal_deposit.png"))
    print("  block: crystal_deposit")

# ─────────────────────────── Creative tab icon ───────────────────────────

def draw_tab_icon(size=64):
    img = new_rgba(size, size)
    cx = cy = (size - 1) / 2
    R = size * 0.4
    # Background nebula
    radial_fill(img, cx, cy, size*0.5, (40,10,80,255), (0,0,0,0), alpha_power=0.5)
    # Aurora ribbons
    for i in range(3):
        c = [(120,255,200),(80,180,255),(180,120,255)][i]
        for y in range(size):
            for x in range(size):
                d = abs(distance(x, y, cx, cy + (i-1)*8) - R)
                if d < 3:
                    put_pixel(img, x, y, c + (180,))
    # Stellar bomb center
    radial_fill(img, cx, cy, R*0.5, (255,255,255,255), (255,140,0,255), alpha_power=0.3)
    add_glow(img, cx, cy, R, (255,140,0,255), intensity=0.4)
    img = img.filter(ImageFilter.GaussianBlur(0.4))
    img.save(out_path("textures", "gui", "tab_icon.png"))
    print("  gui: tab_icon")

# ─────────────────────────── Main ───────────────────────────

def main():
    print("Generating bomb item textures...")
    for name, spec in BOMB_SPECS.items():
        draw_bomb(name, spec)
    print("Generating particle sprite sheets...")
    for name, spec in PARTICLE_SPECS.items():
        draw_particle(name, spec)
    print("Generating block textures...")
    draw_scorch_glass()
    draw_crystal_deposit()
    print("Generating creative tab icon...")
    draw_tab_icon()
    print("Done.")

if __name__ == "__main__":
    main()
