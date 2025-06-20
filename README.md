# X-Link Teleport & Skills Plugin

**X-Link** is a PaperMC plugin for Minecraft 1.21+ that adds advanced teleportation, warps, and a set of configurable, XP-based skills for players.  
All features are fully configurable via `config.yml` and accessible via commands or an in-game menu.

---

## Features

### Teleportation

- **/xtpa <player>**  
  Teleport to another player (with XP cost, configurable as constant or distance-based).
- **/xhome**  
  Teleport to your bed/home location.
- **/xwarp <name>**  
  Teleport to a global warp.
- **/xsetwarp <name>**  
  Set a global warp at your current location (requires permission `xlink.setwarp`).
- **/xdeletewarp <name>**  
  Delete a global warp.
- **/xback**  
  Teleport to your last death location (XP cost configurable).
- **Teleportation delay**  
  Teleportation requires standing still for a few seconds (duration scales with distance, max 5s). Moving cancels teleportation.
- **XP cost**  
  Teleportation costs raw XP points (configurable: constant or per block).
- **Dimension check**  
  Teleportation and warps are blocked between different dimensions.

### Skills (XP-based abilities)

All skills are configurable in `config.yml` (cost, duration, radius, etc.) and available via `/xskill <name>` or the in-game menu (`/xmenu` or sneak + swap hand key):

- **Scan**  
  Laser scan that highlights all mobs and sculk blocks in range. Shows how many mobs were detected.  
  *Configurable:* radius, duration, XP cost.
- **Fullbright**  
  Grants night vision for a configurable time.  
  *Configurable:* duration, XP cost.
- **Skulc Shock**  
  Disrupts all Deep Dark energy in range (wardens, sensors, shriekers), slows mobs, and plays effects.  
  *Configurable:* radius, duration, XP cost.
- **Fireball**  
  Shoots a ghast-like fireball.  
  *Configurable:* XP cost.
- **Decoy**  
  All mobs in range will focus on you for a set time, even from outside their normal detection range. After the effect ends, mobs return to normal AI.  
  *Configurable:* radius, duration, XP cost.

### In-Game Menu

- **/xmenu** or sneak + swap hand key  
  Opens a GUI menu with all skills and teleport options:
    - Scan
    - Fullbright
    - Skulc Shock
    - Home teleport
    - Teleport to player (opens player list)
    - Fireball
    - Decoy

### XP HUD

- **/xd**  
  Toggle a sidebar HUD showing your raw XP (formatted as e.g. `1.2k`) and your ping.

### Messaging

- **/tellnobenio <player> <message>**  
  Send a message to all online players except the selected player.

### Reload

- **/xlinkreload**  
  Reloads the plugin configuration (`config.yml`) without server restart.  
  Requires permission: `xlink.reload`.

### Help

- **/xlink help** or **/xtpa -help**  
  Shows a summary of all commands.

---

## Configuration (`config.yml`)

All skill parameters and teleportation costs are configurable. Example:

```yaml
scan:
  radius: 15
  duration: 10
  xp_cost: 25

skulcshock:
  radius: 20
  duration: 10
  xp_cost: 200

fireball:
  xp_cost: 100

fullbright:
  xp_cost: 50
  duration: 30

teleport:
  xp_cost_mode: by_distance
  xp_cost_value: 0.05

decoy:
  xp_cost: 100
  duration: 10
  radius: 40

xback:
  xp_cost: 7
```

- **teleport.xp_cost_mode:**  
  - `constant` — fixed XP cost per teleport  
  - `by_distance` — cost = distance * xp_cost_value
- **All skill costs, durations, and radii** can be adjusted.

---

## Permissions

- `xlink.setwarp` — Required to set warps.
- `xlink.reload` — Required to reload config with `/xlinkreload`.

---

## Commands

| Command                                 | Description                                      |
|------------------------------------------|--------------------------------------------------|
| `/xlink help`                            | Show help and command list                       |
| `/xhome`                                 | Teleport to your home (bed)                      |
| `/xtpa <player>`                         | Teleport to another player                       |
| `/xtpa xpcost <constant|by_distance> <value>` | Set XP cost for teleportation (admin)        |
| `/xtpaconf <constant|by_distance> <value>`    | Set XP cost for teleportation (admin)        |
| `/xback`                                 | Teleport to your last death location             |
| `/xsetwarp <name>`                       | Set a global warp (requires permission)          |
| `/xwarp <name>`                          | Teleport to a global warp                        |
| `/xdeletewarp <name>`                    | Delete a global warp                             |
| `/xmenu`                                 | Open the X-Link skills/teleport menu             |
| `/xskill <skill>`                        | Use a skill: scan, fullbright, skulcshock, fireball, decoy |
| `/xd`                                    | Toggle XP & ping HUD sidebar                     |
| `/tellnobenio <player> <message>`        | Send message to all except selected player        |
| `/xlinkreload`                           | Reload plugin config (requires permission)        |

---

## Tab Completion

- `/xwarp` and `/xdeletewarp` support tab completion for warp names.
- `/xtpa` supports tab completion for online player names.

---

## How to Use

1. Place the plugin JAR in your server's `plugins` folder.
2. Start the server. The plugin will generate a default `config.yml`.
3. Adjust `config.yml` as needed and use `/xlinkreload` to apply changes.
4. Use `/xlink help` in-game for a full command list.

---

## Notes

- Teleportation and skills cost **raw XP** (not levels).
- All skill and teleport costs, durations, and radii are fully configurable.
- The plugin is optimized to avoid server lag (HUD updates are efficient).
- All features are accessible via commands or the in-game menu.

---

## License

MIT or as specified by