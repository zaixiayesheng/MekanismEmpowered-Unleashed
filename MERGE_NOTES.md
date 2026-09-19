# 工程说明与变更记录

本仓库是 **Mekanism: Empowered** 与 **Mekanism Unleashed** 的合并版。
单一模组（mod id `mekanism_empowered_unleashed`），面向 Minecraft 1.21.1 / NeoForge 21.1，
只适配原版 Mekanism（10.7.19.x）。

署名与许可证（MIT）：

- Mekanism: Empowered — © 2025 Lapis
- Mekanism Unleashed — © 2024 WhitePhant0m
- 合并与修改 — © 2026 zaixiayesheng

## 为什么合并

两个 mod 单独安装会互相冲突：它们都改 Mekanism 的全局数值中枢 `MekanismUtils`，
Unleashed 用 `@Overwrite` 整段替换方法体，Empowered 用定点注入。Overwrite 生效后
原版指令消失，Empowered 要么启动即崩（`InvalidInjectionException`），要么被整段抹掉。
另外 Unleashed 的能量公式把能量升级计效封顶在 8，插 32 个和插 8 个消耗一样。

## 主要改动

1. **冲突消解**：`MixinMekanismUtils.kt` 的 6 个 `@Overwrite` 全部改为
   `@ModifyArg` / `@ModifyReturnValue`，注入点与 Empowered 刻意错开
   （Math.pow 指数 vs ceilToLong 参数 / RETURN 分层），两层效果按顺序相乘。
2. **全 Kotlin 统一**：Unleashed 移植部分（10 个 Mixin + 工具类）全部用 Kotlin 重写。
   注意 Mixin 类里的 companion object 必须 `private`（Mixin 禁止非私有静态字段）。
3. **单模组**：原独立的 Core 已并入本体——一个 jar、一个 mod id、一份 mods.toml
   （注册两份 mixin 配置）；EasyNestConfig 以 JarJar 内嵌。
4. **统一配置**：`maxUpgrades`（8–32，默认 16）一个旋钮同时控制原版与强化
   速度/能量升级的上限，并作为强化升级的解锁门槛；另有 `enchantableMekaGear`。
5. **修复**：
   - 能量减耗封顶跟随配置（原硬编码 8）
   - tier-2 能耗公式按配置上限归一，保住"每操作能耗不变"的能量中性
   - 注册表阶段读取未加载配置导致的崩溃（所有读取点带默认值回退）
   - `MixinModifyRecalculationTarget$Energy` 由字节码表达式匹配改为
     RETURN 注入 + 虚方法重放，不再受 Mekanism 版本字节码差异影响
6. **版本栈**：模组 `21.1-1.0.0`；KotlinForForge 5.12.0；Kotlin 2.4.0。

## 数值（默认 maxUpgrades=16，原版倍率 10，强化倍率 20）

16 速度 + 16 能量 + 16 强化速度 + 16 强化能量全满：

- 每操作耗时 ×1/2000，每 tick 能耗 ×2000，每操作总能耗 ×1（不变）
- 能量存储 ×40,000，吞吐 ×2000

## 构建

JDK 21，`gradlew build`。

## 已知注意点

- `MixinMekanismArmorMaterials` 的目标 `lambda$static$7` 是编译期编号，Mekanism
  更新后可能失效（仅影响附魔开关）。
- 存档中新增升级使用独立 NBT 命名空间 `mekanism_empowered_unleashed:*`，
  与 mod id 同步改名后，1.0.0 之前的测试存档中的强化升级会丢失（原版升级不受影响）。
- `en_us.json` 由 datagen 生成，重跑 datagen 会重新生成（配置键自动带出，无需手工维护）。
