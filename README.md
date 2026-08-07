# FishMod PV

A Hypixel SkyBlock companion mod for Fabric: an in-game **profile viewer** whose look is modeled
directly on real SkyBlock profile viewers rather than invented from scratch - a light "vanilla
GUI" panel sectioned into purple-headed cards, like [meowdding/skyblock-pv](https://github.com/meowdding/skyblock-pv)'s
default theme, with a row of real-item tab icons poking above the panel, like
[NotEnoughUpdates](https://github.com/notenoughupdates/notenoughupdates)'s classic profile viewer
(both repos' actual source/screenshots were pulled in and used as reference while building this).
It pulls the kind of data [SkyCrypt](https://sky.shiiyu.moe) and [SkyPrizm](https://skyprizm.com)
show on the web directly into the game, and reserves tab slots for
[SkyMutations](https://www.t0r0nt0.dev)-style garden/mutation tools and
[SkyShards](https://skyshards.com)-style attribute shard tools, currently stubbed as "coming soon".

## Status: foundation pass

This is the first pass: the mod skeleton, Hypixel API integration, and a real, working profile
viewer GUI (Overview / Inventory & Armor / Skills / Collections / Pets / Progression). The Garden
(SkyMutations) and Shards (SkyShards) tabs are placeholders describing what's planned - see
[Roadmap](#roadmap).

**This code has not been compiled.** The sandbox this was written in has no network access to
Maven Central, the Fabric Maven, or Mojang's library servers, so `./gradlew build` could not be
run here. See [Known risk areas](#known-risk-areas) for exactly where to look first if the build
doesn't come up clean.

### Why this targets 26.1.2

Minecraft moved to year-based versioning in 2026 (`26.1` = the first 2026 game drop, `26.1.2` a
hotfix on it) - see [minecraft.net's announcement](https://www.minecraft.net/en-us/article/minecraft-new-version-numbering-system).
More importantly for this codebase: **starting at 26.1, Fabric stopped maintaining Yarn mappings**
and moved to Mojang's own official mappings, because Mojang began shipping the game with
unobfuscated names. That's not a cosmetic change - it renames nearly every vanilla class and
package this mod touches (`DrawContext` → `GuiGraphics`, `Text` → `Component`, `NbtCompound` →
`CompoundTag`, `Identifier` → `ResourceLocation`, `MatrixStack` → `PoseStack`, and so on). Every
file in this repo has been ported to those official names. The good news: "Mojang's official
mappings" aren't new - they're the same ones NeoForge/Forge mods have used for years, so this port
leans on that long-established naming rather than guessing at something brand new.

One more knock-on effect worth knowing about: Minecraft's NBT read API changed around 1.21.5
(before 26.1) so that `CompoundTag`/`ListTag` getters either return an `Optional` or require an
explicit default/fallback value (e.g. `tag.getCompoundOrEmpty("key")`, `tag.getShort("key", (short) 0)`)
instead of silently defaulting the old way. `nbt/ItemDecoder.java` is written against that newer
API shape - see [Known risk areas](#known-risk-areas) if it doesn't quite match what 26.1.2 shipped.

## Requirements

- Minecraft 26.1.2, Fabric Loader 0.18.4+, Fabric API 0.146.1+26.1.2
- Java 25 (Minecraft 26.1 bumped its minimum JDK)
- Gradle 9.4.0+ / Fabric Loom 1.15 (IntelliJ IDEA 2025.3+ if you want mixins to work in the IDE)
- Your own [Hypixel API key](https://developer.hypixel.net/) (Hypixel retired shared keys; generate
  one with `/api new` in-game on Hypixel, or via the developer dashboard)

## Building

```
./gradlew build
```

(No Gradle wrapper jar is checked in since this environment couldn't download one - run
`gradle wrapper` once you have network access, or use a local Gradle 9.4+ install directly, which
is what the command above assumes.)

## Using it in-game

```
/fishmod key <your-hypixel-api-key>   # one-time setup, saved to config/fishmodpv.json
/fishmod view <username>              # open the profile viewer for any player
/fishmod                              # shorthand for viewing your own profile
```

## Architecture

- `api/` - `HypixelApiClient` / `MojangApiClient`: thin async HTTP wrappers (`java.net.http`)
  around `api.hypixel.net` and `api.mojang.com`.
- `profile/` - data models (`SkyblockProfile`, `ProfileMember`, `SkillData`, `PetData`) parsed
  defensively from the raw Hypixel JSON via `util/JsonPath`, which tries several candidate field
  paths per value. Hypixel has reshuffled the SkyBlock API's member schema more than once (e.g.
  top-level `coin_purse` vs. nested `currencies.coin_purse`); this keeps the mod from silently
  breaking every time that happens, and localizes the field-path list to one place to update.
- `nbt/` - `ItemDecoder` turns the base64-gzip-NBT inventory blobs Hypixel returns into
  `SkyblockItem`s. Hypixel's backend still serializes items in the pre-Flattening (1.12-era,
  numeric id + damage) NBT format; `LegacyItemIds` maps the common ones to a modern item id for
  the icon, and the real name/lore/stats are read straight from the item's own `display`/
  `ExtraAttributes` NBT and rendered ourselves (not vanilla's tooltip), matching how NEU/SkyCrypt
  do it.
- `gui/` - the tabbed `ProfileViewerScreen` and its `tab/` implementations, plus `GuiTheme`/
  `GuiUtil` (the light-panel/purple-card/vanilla-slot drawing helpers - colours sampled from real
  NEU and skyblock-pv screenshots, not invented) and `SmoothValue` (the frame-rate-independent
  easing helper driving the panel's open animation and the sliding active-tab highlight).
- `config/` - `ModConfig`/`ConfigManager`: a small JSON file in the standard Fabric config dir.

## Known risk areas

Written from strong general knowledge of Mojang's official mappings (the same ones NeoForge/Forge
have used for years) but **unverified by an actual compile against real 26.1.2 jars**, and 26.1 is
new enough that there's little to cross-check against. If the build fails, these are the most
likely spots, roughly in order of how exotic the API use is:

1. **`ItemDecoder`** (`nbt/ItemDecoder.java`) - the biggest single risk in this port. It's written
   against the post-1.21.5 "`Or`-suffixed" NBT accessor style (`getCompoundOrEmpty(key)`,
   `getListOrEmpty(key)` with *no* type-int parameter, `getString(key, fallback)`,
   `getShort(key, fallback)`, `getByte(key, fallback)`). The `OrEmpty` methods on `CompoundTag` are
   confirmed to exist in this shape; the exact `ListTag` element accessors (e.g. whether
   `ListTag.getString(int, String)` takes a fallback the same way) are the least-verified part of
   this whole codebase. If the compiler complains here, this is where to look first.
2. **`ProfileViewerScreen`** - `graphics.pose().pushPose()/translate()/scale()/popPose()` for the
   panel's open-animation scale, and `graphics.renderTooltip(this.font, List<Component>, x, y)` -
   this last one is the least certain;
   Mojang's tooltip rendering has grown extra parameters over time (e.g. an `Optional` rich-tooltip
   component) so this simpler overload may not exist verbatim.
3. **`GuiUtil.slot`** - draws an item's icon/count via `graphics.renderItem(stack, x, y)` +
   `graphics.renderItemDecorations(font, stack, x, y)` (the two calls Yarn's old combined
   `drawItemInSlot` used to make internally). Double-check both still exist as public `GuiGraphics`
   methods.
4. **`FishModCommands`** - Fabric's client command API v2 (`ClientCommandManager`,
   `ClientCommandRegistrationCallback`, `FabricClientCommandSource`) is Fabric-authored, not a
   remapped vanilla class, so it likely didn't move - but Fabric API did rename some of its own
   classes to match Mojang's naming conventions in this release (e.g. `ItemGroupEvents` became
   `CreativeModeTabEvents`), so it's worth a quick check if this file doesn't compile.
5. **Hypixel API field paths** in `profile/*.java` - unrelated to the Minecraft version, these are
   best-effort based on the documented Hypixel schema; if a value reads as 0/empty for a real
   player, the field probably moved. Add the new path alongside the old one in the relevant
   `JsonPath.firstOf(...)` call rather than replacing it.

Everything else (Gson DTOs, `java.net.http` calls, plain data records, `ChatFormatting`/
`Component`/`ResourceLocation`/`BuiltInRegistries` usage) is written against Mojmap names that have
been stable across many Minecraft versions and is much lower risk.

## Roadmap

Rough shape of what's still missing to actually combine all four inspirations, in the order it
probably makes sense to build them:

- **Net worth** (SkyCrypt/SkyPrizm) - price items using `/skyblock/bazaar` + `/skyblock/auctions`,
  feed it into the Progression tab.
- **Dungeons/Catacombs + Slayer detail** - currently only total slayer XP feeds the progression
  score; a real per-boss/per-dungeon-class breakdown tab.
- **Garden tab** (SkyMutations-inspired) - wire up `/skyblock/garden`, add a mutation fusion
  requirement calculator and a greenhouse layout planner/importer.
- **Shards tab** (SkyShards-inspired) - shard catalog data, a cheapest-fusion-tree calculator, and
  a chat-log listener for a live catch/fusion HUD counter.
- **Pet levels** - the pet XP-to-level curve differs per rarity and isn't included yet; pets
  currently show raw XP only.
- **Runecrafting/Social skill levels** - `SkillLevels` only covers the shared 1-60 curve; these two
  skills use their own shorter tables that aren't included (shown as raw XP for now).
