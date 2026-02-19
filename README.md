# Ride Any Mob

Ride Any Mob is a Fabric mod that lets players ride and control almost any living entity in Minecraft, not just horses.

Built for Minecraft 1.20.1 (Fabric).

---

## Features

- Ride nearly any living mob
- Full movement control while mounted
- Optional whitelist system
- Server-side permission enforcement
- Lightweight and minimal

---

## Commands

### /ridewhitelist

Admin-only command (permission level 2+)

- `/ridewhitelist add <player>`  
  Adds a player to the ride whitelist.

- `/ridewhitelist remove <player>`  
  Removes a player from the whitelist.

- `/ridewhitelist list`  
  Shows all whitelisted UUIDs.

---

## How It Works

This mod uses mixins to modify entity riding behavior, allowing players to mount and control mobs similarly to horses.

Control is handled server-side to ensure proper multiplayer compatibility.

---

## Requirements

- Minecraft 1.20.1
- Fabric Loader
- Fabric API

---

## Installation

1. Install Fabric Loader
2. Install Fabric API
3. Place the mod `.jar` into your `mods` folder
4. Launch the game

---

## Permissions

Only players added to the whitelist (or operators) can use the riding feature if whitelist mode is enabled.

---

## Planned Features

- Config file support
- Entity blacklist
- Improved movement syncing
- Better whitelist display formatting

---

## Compatibility

Should work with most vanilla mobs.  
Compatibility with AI overhaul mods may vary.

---

## License

All Rights Reserved unless otherwise specified.
