# FishMod PV

A Hypixel SkyBlock companion mod for Fabric: an in-game **profile viewer** with an
old-[NEU](https://github.com/NotEnoughUpdates/NotEnoughUpdates)-inspired tabbed GUI (rendered in a
"liquid glass" style - translucent gradient panels, glowing borders, and a sliding animated tab
indicator), pulling the kind of data [SkyCrypt](https://sky.shiiyu.moe) and
[SkyPrizm](https://skyprizm.com) show on the web, directly into the game. It also reserves tab
slots for [SkyMutations](https://www.t0r0nt0.dev)-style garden/mutation tools and
[SkyShards](https://skyshards.com)-style attribute shard tools, currently stubbed as "coming soon".

## Status: foundation pass

This is the first pass: the mod skeleton, Hypixel API integration, and a real, working profile
viewer GUI (Overview / Inventory & Armor / Skills / Collections / Pets / Progression). The Garden
(SkyMutations) and Shards (SkyShards) tabs are placeholders describing what's planned - see
[Roadmap](#roadmap).

**This code has not been compiled.** The sandbox this was written in has no network access to
Maven Central, the Fabric Maven, or Mojang's library servers, so `./gradlew build` could not be
run here. The code was written carefully against well-established, stable Fabric/Yarn 1.21.1 APIs
and passed a local `javac` syntax check, but you should expect to fix a handful of minor
API-mapping mismatches (a renamed method, an argument order) on the first real build. Please open
an issue (or just fix and commit) if something doesn't line up - see [Known risk areas](#known-risk-areas)
below for exactly where to look first.

## Requirements

- Minecraft 1.21.1, Fabric Loader 0.16.9+, Fabric API
- Java 21
- Your own [Hypixel API key](https://developer.hypixel.net/) (Hypixel retired shared keys; generate
  one with `/api new` in-game on Hypixel, or via the developer dashboard)

## Building

```
./gradlew build
```

(No Gradle wrapper jar is checked in since this environment couldn't download one - run
`gradle wrapper` once you have network access, or use a local Gradle 8.8+ install directly, which
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
  `GuiUtil` (the glass-panel drawing helpers, all `DrawContext#fillGradient`-based) and
  `SmoothValue` (the frame-rate-independent easing helper driving the open animation and the
  sliding tab indicator).
- `config/` - `ModConfig`/`ConfigManager`: a small JSON file in the standard Fabric config dir.

## Known risk areas

Written from strong general Fabric/Yarn 1.21.1 knowledge but **unverified by an actual compile**.
If the build fails, these are the most likely spots, roughly in order of how exotic the API use is:

1. **`ItemDecoder`** (`nbt/ItemDecoder.java`) - `NbtIo.readCompound(DataInput, NbtSizeTracker)` and
   `NbtElement.COMPOUND_TYPE`/`STRING_TYPE` constants. If the compiler complains here, check
   whether your mappings want a different `NbtIo` overload (e.g. a version without the tracker
   argument) or expose the type constants differently.
2. **`ProfileViewerScreen`** - `context.getMatrices().push()/translate()/scale()/pop()` for the
   panel's open-animation scale, and `context.fillGradient(...)` for every glass surface. Also
   `context.drawTooltip(...)` and `context.drawItemInSlot(...)`.
3. **`FishModCommands`** - Fabric's client command API v2 (`ClientCommandManager`,
   `ClientCommandRegistrationCallback`, `FabricClientCommandSource`). Registration shape is stable
   across recent versions but double-check the `dispatcher.register(...)` lambda's parameter list.
4. **Hypixel API field paths** in `profile/*.java` - these are best-effort based on the documented
   schema; if a value reads as 0/empty for a real player, the field probably moved. Add the new
   path alongside the old one in the relevant `JsonPath.firstOf(...)` call rather than replacing it.

Everything else (Gson DTOs, `java.net.http` calls, plain data records) uses long-stable JDK/Gson
APIs and is much lower risk.

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
