# ☢️ Nuclear Eclipse — Minecraft Forge 1.21.1

> **الكسوف النووي** — مود أسطوري يضيف 7 قنابل نووية فريدة بالكامل، لم ترَ مثلها في أي مود آخر على الإنترنت. كل قنبلة لها سلوك انفجار خاص، جزيئات مخصصة، وصوت مميز.

Seven legendary, never-before-seen nuclear bombs for Minecraft 1.21.1. Each bomb has its own bespoke detonation behaviour, custom particle stream, signature sound, and gorgeous procedurally-generated texture. Nothing here is recycled from existing mods — every effect was designed from scratch.

---

## 📋 المتطلبات / Requirements

| المكوّن | الإصدار |
|---------|---------|
| Minecraft | 1.21.1 |
| Forge | 52.0.53 (or any 52.0.x) |
| Java JDK | 21 |

---

## 💣 القنابل السبع / The Seven Bombs

### 1. قنبلة التفرد الكمي — Quantum Singularity Bomb
تنفجر في ثلاث مراحل: تسحب كل الكائنات نحو المركز (سحب جاذبي)، تحفر حفرة كروية عميقة، ثم تقذف كل شيء للخارج في ومضة بنفسجية. تعملق الكتل المحيطة كـ "falling blocks" تنهار نحو التفرد.

### 2. قنبلة دوامة الزمن — Chronos Vortex Bomb
موجة صدمية زمنية تجمّد كل كائن حي في المنطقة: بطء شديد، إرهاق تعدين، ضعف، تحليق، وتوهج. ذهب غبار الزمن الذهبي يُمطر المنطقة. الحفرة المتبقية تكون من الحجر الرملي الأملس — "حبيبات زمن مجمد".

### 3. قنبلة منشور البلورة — Crystal Shard Bomb
انفجار منشوري لا يدمّر الأرض بل يحوّلها إلى قشرة بلورية تركوازية لامعة + ينطلق انفجار نجومي من 200 شظية بلورية دوّارة. ضرر شظايا خفيف مع دفع قوي.

### 4. قنبلة صدى الفراغ — Void Echo Bomb
انفجار صامت يبتلع الضوء. الحفرة الكروية تُحفر بلا صوت، تُملأ بقشرة سوداء، وقبة من جسيمات الفراغ تنكمش نحو المركز وتبتلع نفسها. الكائنات تُسحب للداخل وتُعمى وتُصاب بالعمى.

### 5. قنبلة قلب النجم — Stellar Core Bomb
أخطر قنبلة: سوبرنوفا مصغّرة. عمود نار شمسي ضخم يرتفع 30 بناء للأعلى، الأرض تُصهر إلى مغما ونتّراك، بحيرة حمم في القلب، وموجة حرق تضرب 18 بناء. تشعل الحرائق عشوائياً.

### 6. قنبلة الأبواغ المتوهجة — Glow Spore Bomb
قنبلة "بيولوجية". لا تترك حفرة بل تطلق سحابة ضخمة من الأبواغ المضيئة الخضراء تستمر طويلاً. الكائنات تكتسب رؤية ليلية، تسمم، سقوط بطيء، وتوهج. الأرض تزدهر بـ spore blossoms وموس.

### 7. قنبلة الشفق القطبي — Aurora Bomb
قنبلة سلمية. تنشر ستارة شفق قطبي متلألئة ترتفع 40 بناء في السماء، تتحول ألوانها من أخضر إلى بنفسجي ببطء. الأرض تُغطى بالجليد المضغوط والسجاد الأزرق الفاتح. الكائنات تُبطّأ وتُحلّق بلطف — عرض لا مذبحة.

---

## 🛠️ بناء المود / Building the Mod

### المتطلبات
- **JDK 21** (مثل Adoptium Temurin 21)
- **اتصال إنترنت** (لاستخدام NeoForged/Forge Gradle)

### خطوات البناء (Linux / macOS / Windows)

```bash
# من جذر المشروع
./gradlew build        # على Linux/macOS
# أو
gradlew.bat build      # على Windows
```

الملف الناتج سيكون في:
```
build/libs/nucleareclipse-1.0.0.jar
```

### فتح المشروع في IDE
```bash
./gradlew --refresh-dependencies eclipse    # Eclipse
./gradlew genIntellijRuns                   # IntelliJ IDEA
./gradlew genEclipseRuns                   # Eclipse run configs
```

### التشغيل في بيئة تطوير
```bash
./gradlew runClient     # تشغيل لعبة تطويرية مع المود
./gradlew runServer     # تشغيل خادم تطويري
```

---

## 🎨 المنشآت / Crafting Recipes

كل قنبلة لها وصفة تصنيع محددة (انظر ملفات `data/nucleareclipse/recipes/`). مثال:

**قنبلة قلب النجم:**
```
 M B M
 B N B
 M B M
```
- M = magma_block, B = blaze_rod, N = nether_star

**قنبلة التفرد الكمي:**
```
 E E E
 E D E
 E E E
```
- E = ender_eye, D = diamond_block

(باقي الوصفات في مجلد recipes)

---

## 📂 هيكل المشروع / Project Structure

```
Minecraft Mod/
├── build.gradle, settings.gradle, gradle.properties
├── gradle/wrapper/gradle-wrapper.properties
├── generate_textures.py          # سكربت توليد التيكستجر
├── README.md
└── src/main/
    ├── java/com/nucleareclipse/
    │   ├── NuclearEclipse.java           # الكلاس الرئيسي
    │   ├── registry/                    # كل التسجيلات
    │   ├── item/BombItem.java           # صنف القنبلة
    │   ├── entity/                      # كيان القنبلة + التفجيرات
    │   │   ├── BombEntity.java
    │   │   ├── BombDetonation.java
    │   │   ├── BombDetonations.java
    │   │   └── detonation/              # التفجيرات السبع
    │   └── client/                      # الجزيئات + الـ renderer
    └── resources/
        ├── META-INF/mods.toml
        ├── pack.mcmeta
        └── assets/nucleareclipse/
            ├── lang/ (en_us, ar_sa)
            ├── models/ (item, block)
            ├── blockstates/
            ├── particles.json, sounds.json
            └── textures/ (item, block, particle, gui)
```

---

## ⚙️ كيف تعملق القنابل؟ / How it Works

1. **الصنف**: `BombItem` — عند النقر بالزر الأيمن، ينشئ `BombEntity` ويقذفه.
2. **الكيان**: `BombEntity` يحمل "kind" (نوع القنبلة) في بياناته المتزامنة. يدور كقذيفة، يصطدم بالكتل/الكائنات، ثم ينفجر.
3. **التفجير**: `BombDetonations.byKind(kind)` يرجع الاستراتيجية المناسبة (مثل `QuantumSingularityDetonation`) التي تنفّذ سلوك الانفجار على السيرفر.
4. **الجزيئات**: كل قنبلة لها جسيم مخصص (مثل `QuantumSparkParticle`) مع سلوك بصري فريد — تدرّج لوني، دوران، نبض، إلخ.
5. **الصوت**: كل قنبلة لها `SoundEvent` خاص يُبثّ عند الانفجار.

---

## 🎯 الاستخدام / Usage

1. ضع ملف `nucleareclipse-1.0.0.jar` في مجلد `mods/` الخاص بـ Forge 1.21.1.
2. شغّل اللعبة — ستجد التبويب **"Nuclear Eclipse"** في قائمة الإبداع.
3. كل القنابل متاحة هناك، أو صنّعها بالوصفات.
4. انقر بالزر الأيمن لرمي القنبلة — تنفجر عند الاصطدام.

> ⚠️ **تحذير**: بعض القنابل مدمّرة جداً (خاصة قلب النجم والتفرد الكمي). استخدمها بعيداً عن قواعدك!

---

## 📜 الترخيص / License

MIT License — حر في التعديل والتوزيع.

التيكستجر كلها مُولّدة برمجياً، لا توجد أصول خارجية.

## 👤 المؤلف

**Aymen** — Minecraft Forge 1.21.1

---

*"سبع قنابل، سبعة عوالم مختلفة من الدمار والجمال."*
