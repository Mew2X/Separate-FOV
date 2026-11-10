# Separate FOV

[![Minecraft](https://img.shields.io/badge/Minecraft-1.21.11-62b47a)](https://www.minecraft.net/)
[![Loader](https://img.shields.io/badge/Fabric-0.18%2B-dbd0b4)](https://fabricmc.net/)
[![License](https://img.shields.io/badge/License-MIT-blue)](LICENSE)

A client-side Fabric mod that gives **first person** and **third person** their own field of view.

Most third person camera mods share one FOV setting with first person, so switching views feels either cramped or stretched. Separate FOV adds an offset per perspective on top of the normal FOV slider, so you can run a wide FOV in first person and a tighter one over the shoulder.

## Features

- Separate FOV offsets for first person and third person
- Config screen in [Mod Menu](https://modrinth.com/mod/modmenu), plus in-game commands
- Vanilla FOV effects (sprint, speed, spyglass, ...) keep working
- The FOV slider in Video Settings stays the master setting
- Client-side only, no server install needed

## How it works

The offset for the current perspective is added to your FOV slider value.

| FOV slider | First person offset | Third person offset | First person | Third person |
|-----------:|--------------------:|--------------------:|-------------:|-------------:|
| 90         | 0                   | -20                 | 90           | 70           |
| 100        | +5                  | -25                 | 105          | 75           |

Defaults are `0` for first person and `-20` for third person. Under the hood the mod hooks `GameRenderer.getFov` and scales vanilla's result by `(slider + offset) / slider`, so sprint, speed and spyglass effects stay proportional. The resulting FOV is clamped to 1-170.

## Requirements

- Minecraft 1.21.11
- [Fabric Loader](https://fabricmc.net/) 0.18.0 or newer
- [Fabric API](https://modrinth.com/mod/fabric-api)
- [Mod Menu](https://modrinth.com/mod/modmenu) (optional, for the config screen)

## Configuration

### Mod Menu

Open Mod Menu, select **Separate FOV** and press the configure button. Two sliders (-60 to +60 degrees, step 1), **Reset to defaults** and **Done**. Changes are saved when the screen closes.

### Commands

| Command | Description |
|---|---|
| `/separatefov` or `/separatefov show` | Show the current offsets |
| `/separatefov first <offset>` | Set the first person offset (-100 to 100) |
| `/separatefov third <offset>` | Set the third person offset (-100 to 100) |
| `/separatefov reload` | Reload the config file |

### Config file

Offsets are stored in `config/separatefov.json`:

```json
{
  "firstPersonOffset": 0.0,
  "thirdPersonOffset": -20.0
}
```

## Compatibility

The third person offset applies whenever the game is in the standard third person perspective, so it should also work with third person camera mods that use it.

## Building from source

You need JDK 21 or newer and Gradle 9.2 or newer (Loom 1.14).

```sh
gradle wrapper --gradle-version 9.2.1   # once, creates ./gradlew
./gradlew build
```

The mod jar is `build/libs/separate-fov-<version>.jar` (not the `-sources` one). To test in a dev client, run `./gradlew runClient`.

## Credits

Inspired by [Dynamic FOV Offset](https://modrinth.com/mod/dynamic-fov-offset) (Forge 1.20.1). This is an independent Fabric implementation written from scratch.

## AI disclosure

This mod was vibe coded: the code and the documentation were written with AI assistance from [Claude](https://claude.ai) (by Anthropic) and then built, tested in game and published by the maintainer. Bug reports and pull requests are welcome.

## License

[MIT](LICENSE)
