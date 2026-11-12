# FishPV

A Hypixel SkyBlock profile viewer for Fabric (client-side). Open it in game with `/pv <player>` to see networth, dungeons, slayers, skills, weights and more.

Derived from [BetterPV](https://github.com/) by Vyriv. Licensed under MPL-2.0.

## Features

- Profile viewer GUI with card-based layout (`/pv`, `/pv <player>`)
- Networth, dungeon stats and PBs, slayers, skills, attribute shards, weight
- Hover tooltips for items, with shift+wheel panning for tall tooltips
- Settings screen via `/pvsettings` or ModMenu

## Supported versions

| Module | Minecraft |
|--------|-----------|
| `pv-26.1.2` | 26.1.2 |
| `pv-26.2` | 26.2 |
| `pv-26.3` | 26.3 |

Requires Fabric Loader, Fabric API and Java 25.

## Building

```
./gradlew build
```

Jars are written to `pv-<version>/build/libs/`. Install by dropping the jar for your Minecraft version into your `mods` folder.

## Troubleshooting

If profile requests fail, set the `FISHPV_SELFTEST` environment variable (or `-Dfishpv.selftest`) to run a startup self-test that reports why fetching failed.
