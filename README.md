# XTeleport Plugin for Minecraft Paper

## Overview
XTeleport is a Minecraft plugin designed for Paper servers that allows players to teleport to their home location or to another player with ease. The plugin includes features such as a teleportation countdown, experience cost, and visual effects to enhance the teleportation experience.

## Features
- **Commands**:
  - `/xhome`: Teleports the player to their home location.
  - `/xtpa <player>`: Teleports the player to another specified player.
  
- **Teleportation Mechanics**:
  - Players must remain still for 5 seconds before teleportation.
  - Teleportation costs 5 experience levels.
  - Visual effects (PORTAL particles) are displayed during the teleportation countdown.
  - A lightning effect is triggered upon successful teleportation.
  - A teleport sound is played when the teleportation occurs.
  
- **Movement Detection**:
  - Teleportation is canceled if the player moves during the countdown.

## Installation
1. Download the latest version of the XTeleport plugin.
2. Place the downloaded `.jar` file into the `plugins` folder of your Paper server.
3. Restart the server to enable the plugin.
4. Configure the plugin settings in `plugin.yml` if necessary.

## Usage
- To set a home location, players can use the command `/sethome` (this command needs to be implemented).
- To teleport to home, use `/xhome`.
- To teleport to another player, use `/xtpa <player>`.

## Requirements
- Minecraft Paper 1.21.4 or higher.
- Java 17 or higher.

## Contributing
Contributions are welcome! If you have suggestions or improvements, feel free to submit a pull request.

## License
This project is licensed under the MIT License. See the LICENSE file for more details.