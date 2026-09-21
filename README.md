# Fair Vanilla Easy Place

A lightweight Litematica companion for vanilla-valid assisted block placement.

Fair Vanilla Easy Place automatically selects the block required by the current Litematica schematic and performs a normal Minecraft right-click when you are looking at a valid placement.

The mod does not:

- place blocks directly at schematic coordinates
- bypass vanilla placement rules
- extend interaction reach
- place blocks through walls
- automatically move or rotate the player
- pathfind or build autonomously
- place blocks without the required materials

Instead, you aim and move normally. The mod simply selects the required block and performs the right-click for you. Minecraft's normal interaction and block-placement logic determines whether the placement succeeds.

## How It Works

When enabled, Fair Vanilla Easy Place:

1. Finds the schematic ghost block relevant to your current line of sight.
2. Uses the furthest ghost block before the first real-world obstruction as the target.
3. Determines which block the schematic requires there.
4. Checks whether that block is available in your inventory.
5. Selects the required block.
6. Performs a normal vanilla block interaction against the block you are actually looking at.

If the placement would not be possible normally, the mod does not attempt to circumvent that restriction.

### Example

If you are looking through three schematic ghost blocks:

Player → Oak Ghost → Birch Ghost → Spruce Ghost → Stone

the mod selects Spruce, because it is the ghost block immediately before the first real block.

It does not simply select the first ghost block encountered.

## Requirements

- Minecraft 26.2
- Fabric
- Litematica
- MaLiLib

## License

Fair Vanilla Easy Place is licensed under the GNU General Public License v3.0.

See the LICENSE file for the full license text.