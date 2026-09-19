# Mekanism: Empowered Unleashed

> A Mekanism addon — the conflict-free merge of **Mekanism: Empowered** and **Mekanism Unleashed**.
>
> 中文说明：[README.md](./README.md)

[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](./LICENSE)
[![Minecraft](https://img.shields.io/badge/Minecraft-1.21.1-green.svg)](https://www.minecraft.net)
[![NeoForge](https://img.shields.io/badge/NeoForge-21.1-orange.svg)](https://neoforged.net)

---

## What this is

The two original mods conflict when installed together: both patch the shared utility functions in `MekanismUtils` via Mixin, but Unleashed uses `@Overwrite` (whole method-body replacement) while Empowered uses targeted injectors. Once the overwrite lands, the original instructions vanish and Empowered either crashes at startup or silently stops working.

This project merges them into **one mod**:

- Single jar, single mod id (`mekanism_empowered`) — no separate Core dependency;
- The entire codebase unified in Kotlin;
- Injection points redesigned to never overlap, so both layers' effects stack properly.

Targets vanilla Mekanism only (1.21.1 / 10.7.19.x).

## Features

- **16+16 two-tier upgrade system**: vanilla Speed/Energy upgrades (tier 1, default cap 16) + Empowered Speed/Energy upgrades (tier 2, unlocked once tier 1 is maxed)
- **A single config option**: `maxUpgrades` (8–32, default 16) controls both tier caps and the tier-2 unlock threshold
- **Energy-cap bug fixed**: original Unleashed capped energy upgrades' effect at 8 (32 upgrades consumed as much as 8); the cap now follows the configured maximum
- **Four machines support multiple operations per tick**: Electric Pump, Digital Miner, Fluidic Plenisher, Formulaic Assemblicator
- **Meka gear enchant toggle** (`enchantableMekaGear`, inherited from Unleashed, off by default)
- **Upstream integrations**: Mekanism Extras, More Machine, Evolved Mekanism, Mekanism Generators, etc.

## Installation

| File | Required | Notes |
|---|---|---|
| `MekanismEmpowered-1.21.1-21.1-1.0.0.jar` | ✅ | This mod (single file) |
| `kotlinforforge-5.12.0-all.jar` | ✅ | Kotlin runtime |
| Mekanism 1.21.1 (vanilla 10.7.19.x) | ✅ | Base dependency |

- Platform: NeoForge 21.1.x, Minecraft 1.21.1
- **Do not install the originals alongside this**: both `mekanism-unleashed` and `mekanism-empowered` are fully included here
- `-sources.jar` / `-api.jar` are developer artifacts, not required

## Configuration

Config file: `config/mekanism_empowered-general.toml`

| Option | Default | Range | Purpose |
|---|---|---|---|
| `maxUpgrades` | 16 | 8–32 | Cap of vanilla and Empowered Speed/Energy upgrades; also the tier-2 unlock threshold (maxed vanilla count) |
| `maxUpgradeMultiplier` | 20 | ≥1 | Performance factor of tier 2 (Empowered upgrades) |
| `enchantableMekaGear` | false | — | Vanilla Meka gear cannot be enchanted; turning this on allows it, but you must add the items to enchantable tags yourself |

## Numbers

With default factors (vanilla 10, Empowered 20), a fully maxed machine (16 speed + 16 energy + 16 empowered speed + 16 empowered energy):

| Metric | Factor |
|---|---|
| Time per operation | ×1/2000 |
| Energy per tick | ×2000 |
| **Energy per operation** | **×1 (unchanged)** |
| Energy storage | ×40,000 |
| Throughput | ×2000 |

**Why the power bill stays the same**: speed upgrades make the machine draw more power per second, energy upgrades make it draw less. As long as each tier keeps "energy count = speed count", the two effects cancel out — **the total power per crafted item stays at vanilla level**; upgrades only make the machine work faster, not more expensive. Speed without energy makes the power draw explode — that is by original design.

## Differences from upstream (fixes)

| Problem | Resolution |
|---|---|
| `@Overwrite` conflicting with Empowered injectors | All replaced by `@ModifyArg` / `@ModifyReturnValue` with non-overlapping points |
| Energy upgrades beyond 8 not reducing consumption | Cap follows the `maxUpgrades` config |
| Tier-2 energy formula hardcoded to 8 | Normalized against the configured cap, preserving energy neutrality |
| Config reads during registry init crashing the game | All read sites now fall back to defaults before config load |
| Fragile `MixinModifyRecalculationTarget$Energy` bytecode pattern | Rewritten as RETURN injection + virtual re-dispatch, version-independent |
| Two mods, three files | Merged into one mod, one jar |

## Building

Requirements: **JDK 21** and network access to Maven repositories.

```
gradlew build
```

## License & credits

- Based on [Mekanism: Empowered](https://github.com/Lapis256/MekanismEmpowered) (Copyright © 2025 Lapis, MIT) and Mekanism Unleashed (Copyright © 2024 WhitePhant0m, MIT)
- Merge & modifications: Copyright © 2026 zaixiayesheng, MIT License
- Full license in [LICENSE](./LICENSE)
