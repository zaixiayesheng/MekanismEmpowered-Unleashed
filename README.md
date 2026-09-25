# Mekanism: Empowered Unleashed

> 通用机械（Mekanism）增强模组 —— **Mekanism: Empowered** 与 **Mekanism Unleashed** 的冲突消解合并版。
>
> English version: [README_EN.md](./README_EN.md)

[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](./LICENSE)
[![Minecraft](https://img.shields.io/badge/Minecraft-1.21.1-green.svg)](https://www.minecraft.net)
[![NeoForge](https://img.shields.io/badge/NeoForge-21.1-orange.svg)](https://neoforged.net)

---

## 这是什么

两个模组单独安装时会互相冲突：它们都通过 Mixin 修改 Mekanism 计算升级效果的公共函数 `MekanismUtils`，而 Unleashed 使用 `@Overwrite`（整段替换方法体），Empowered 使用定点注入——前者生效后原版指令消失，后者要么启动崩溃、要么静默失效。

本项目把两者**合并为一个模组**：

- 单一 jar、单一 mod id（`mekanism_empowered_unleashed`），不再需要独立的 Core 前置；
- 全部代码统一为 Kotlin；
- 注入点重新设计为互不重叠，两层效果按顺序相乘，天然兼容。

只兼容原版 Mekanism（1.21.1 / 10.7.19.x）。

## 特性

- **16+16 双层升级体系**：原版速度/能量升级（第一层，默认上限 16）+ 强化速度/能量升级（第二层，第一层插满后解锁）
- **一个配置项**：`maxUpgrades`（8–32，默认 16）同时控制两层上限与解锁门槛
- **修复减耗封顶 bug**：原版 Unleashed 的能量公式把能量升级计效封顶在 8，插 32 个和插 8 个消耗一样；现已跟随配置上限
- **四台机器支持每 tick 多操作**：泵、数字矿机、流体装填器、公式装配器（速度超过 1 tick/操作后继续加速）
- **Meka 装备附魔开关**（`enchantableMekaGear`，继承自 Unleashed，默认关闭）
- **上游自带的整合**：Mekanism Extras、More Machine、Evolved Mekanism、Mekanism Generators 等

## 安装

| 文件 | 必需 | 说明 |
|---|---|---|
| `MekanismEmpoweredUnleashed-1.21.1-21.1-1.0.4.jar` | ✅ | 本模组（唯一文件） |
| `kotlinforforge-5.12.0-all.jar` | ✅ | Kotlin 运行时前置 |
| Mekanism 1.21.1（官方 10.7.19.x）| ✅ | 本体依赖 |

- 平台：NeoForge 21.1.x，Minecraft 1.21.1
- **不要与原版同时安装**：合并版已包含 `mekanism-unleashed` 和 `mekanism-empowered` 的全部功能，原版两者都不要装
- `-sources.jar`、`-api.jar` 为开发者配套，无需安装

## 与无用之物（UselessMod）共存

两者可以一起安装，强化升级、Fast Item Eject 等功能都正常工作，无用之物自身的合金炉、牛肉工具、矿物生成器等内容也不受影响。

1.0.3 及以前本模组在元数据里声明过 `useless_mod` 不兼容，那时的表现是两个模组装在一起游戏直接起不来；这个声明在 1.0.4 已经去掉。

两边具体在哪几处碰到了一起、各自是怎么处理的，写在源码注释里（`UselessModCompat`、`mixin/unleashed` 下的各混入类），这里不展开了。

## 配置

配置文件：`config/mekanism_empowered_unleashed-general.toml`

| 配置项 | 默认值 | 范围 | 作用 |
|---|---|---|---|
| `maxUpgrades` | 16 | 8–32 | 原版与强化速度/能量升级的上限，同时决定强化升级的解锁门槛（插满 N 个原版后解锁） |
| `maxUpgradeMultiplier` | 20 | ≥1 | 第二层（强化升级）的效果倍率 |
| `enchantableMekaGear` | false | — | 原版 Meka 装备不可附魔；开启后允许附魔，但需自行把物品加进可附魔标签（本模组不代劳） |

## 升级体系与数值

以默认倍率（原版 10、强化 20）为例，16 速度 + 16 能量 + 16 强化速度 + 16 强化能量全满时：

| 指标 | 倍率 |
|---|---|
| 每操作耗时 | ×1/2000 |
| 每 tick 能耗 | ×2000 |
| **每操作总能耗** | **×1（不变）** |
| 能量存储 | ×40,000 |
| 加工速度 | ×2000 |

**耗电为什么不变**：速度升级让机器每秒耗电变多，能量升级让每秒耗电变少。只要两层各自保持"能量数 = 速度数"，两个效果正好抵消——**每加工一件物品的总耗电永远等于原版**，升级只是让机器干得更快，不会更费电。只插速度不插能量，耗电会暴涨，这是原版就有的设定。

## 与上游的差异（修复清单）

| 问题 | 处理 |
|---|---|
| `@Overwrite` 与 Empowered 定点注入冲突 | 全部改为 `@ModifyArg` / `@ModifyReturnValue`，注入点错开 |
| 能量升级超过 8 个不减耗 | 封顶值跟随 `maxUpgrades` 配置 |
| 强化能量升级的公式硬编码 8 | 按配置上限归一化，保持"每件物品耗电不变" |
| 注册表阶段读取未加载的配置导致崩溃 | 全部配置读取点加安全回退（默认值） |
| `MixinModifyRecalculationTarget$Energy` 字节码模式脆弱 | 改为 RETURN 注入 + 虚方法重放，版本无关 |
| 两个 mod 三个文件 | 合并为单一 mod、单一 jar |

## 更新日志

### 21.1-1.0.4

- 移除了与无用之物（UselessMod）的不兼容声明，两个模组可以一起安装
- 修复了两者同时安装时强化升级失效的问题，Fast Item Eject 也恢复正常

### 21.1-1.0.3

- 移除了内嵌的旧 id 兼容子模组（桥接），主 jar 恢复单一 mod id
- MekaJade 的升级图标支持改由 MekaJade Upgrades Fixed 直接适配，不再需要内置桥接

### 21.1-1.0.2

- 修复了资源蜜蜂：创世（Productive Bees Genesis）启动时把本模组误判为不支持的依赖并报错的问题

### 21.1-1.0.1

- 修复强化升级必须按顺序插入才生效的问题，现在无论先插哪个都立即生效。
- 修复泵、流体装填器、公式装配器、数字矿机插满速度升级后仍然每 tick 只干一次的问题，现在能一 tick 干多次，最多 128 次。
- 修复 MekaJadeUpgrades 显示增强升级显示异常。

## 构建

环境要求：**JDK 21**、可访问 Maven 仓库的网络。

```
gradlew build
```

## 许可证与署名

- 本合并版基于 [Mekanism: Empowered](https://github.com/Lapis256/MekanismEmpowered)（Copyright © 2025 Lapis，MIT）与 Mekanism Unleashed（Copyright © 2024 WhitePhant0m，MIT）
- 合并与修改：Copyright © 2026 zaixiayesheng，MIT License
- 完整协议见 [LICENSE](./LICENSE)
