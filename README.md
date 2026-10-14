# Trade Refresh - Fabric Port

A Fabric port of the Trade Refresh mod for Minecraft 26.1.2.

## About

Trade Refresh adds a convenient button to the villager trading screen that allows you to refresh villager trades without needing to break and replace their workstations.

## Features

- **Refresh Button**: Adds a button to the merchant screen to refresh trades
- **Keybind**: Press T (configurable) to refresh trades
- **Emerald Block Force Restock**: Use an emerald block on a villager to force restock (server-side feature)
- **JEI Integration**: When JEI is installed, trades containing the item under your cursor won't be refreshed
- **Configurable**: Client and server-side configuration options

## Changes from NeoForge Version

This Fabric port includes the following changes:

- Replaced NeoForge with Fabric Loader and Fabric API
- Replaced l2serial networking library with Fabric Networking API
- Replaced NeoForge's ModConfigSpec with JSON-based configuration
- Replaced NeoForge events with Fabric API events
- Replaced access transformers (.cfg) with access wideners (.classtweaker)
- Removed NeoForge-specific dependencies

## Dependencies

- **Required**: Fabric API
- **Optional**: JEI (Just Enough Items) for recipe viewer integration

## Building

```bash
./gradlew build
```

The built jar will be in `build/libs/`.

## Configuration

Configuration files are stored in the `config/` directory:
- `traderefresh-client.json` - Client-side settings
- `traderefresh-server.json` - Server-side settings

### Client Options

- `showEnchProperties`: Show enchantment properties on enchanted books (default: false)
- `buttonSide`: Position of the refresh button - LEFT or RIGHT (default: LEFT)

### Server Options

- `alwaysAllowRefresh`: Always allow refreshing trades, even after trading (default: false)
- `allowEmeraldBlockForceRestock`: Allow using emerald blocks to force restock (default: true)

## License

LGPL-2.1 (same as the original mod)

## Credits

- Original mod by lcy0x1
- Fabric port maintained by the community
