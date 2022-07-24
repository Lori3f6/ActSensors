# ActSensors

Custom tags/objectives interfaces.

## Interfaces supported

_For easier identification, all names generated by plugin has a `+` prefix(e.g. tag or obj name `+flying`/`light_block` in game). It will not be specifically noted below._
_Indicators below are **read only**, and automatically update every tick._

### Indicator tags

| Tag       | Description                                        |
|-----------|----------------------------------------------------|
| sprinting | exist if a player is sprinting                     |
| flying    | exist if a player is flying                        |
| grounded  | exist if a player is grounded (client side status) |
| swimming  | exist if a player is swimming                      |
| sleeping  | exist if a player is sleeping                      |
| sneaking  | exist if a player is sneaking                      |
| gliding   | exist if a player is gliding with elytra           |
| glowing   | exist if a player is glowing                       |
| exposing  | exist if there is no solid blocks above a player   |

### Player Indicator Objectives

| Objective   | Value                                                                                                                                                                                                                                 |
|-------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| light       | light value of player's locations                                                                                                                                                                                                     |
| light_block | block light value of player's locations                                                                                                                                                                                               |
| sky_light   | sky light value of player's locations                                                                                                                                                                                                 |
| health      | health of players<br/>Values here are 1000 times scaled<br/>For example, 20000 stands for 20.0 in double                                                                                                                              |
| food_level  | food level of players                                                                                                                                                                                                                 |
| air         | air remaining of players                                                                                                                                                                                                              | 
| biome       | biome id of the player's locations <br/>_hashed id: org.bukkit.block.Biome_                                                                                                                                                           |
| item_hand0  | the id of the item in the player's main hand <br/>_hashed id: org.bukkit.Material_                                                                                                                                                    |
| item_hand1  | same as previous, but indicates player's offhand                                                                                                                                                                                      |
| meta_hand0  | a hash number generated by lore and customModelData of the item hold in player's main hand, which may be used as a identifier of a item with custom meta. <br/> Collision is theoretically possible, but the probability is very low. |
| meta_hand1  | same as previous, but indicates player's offhand                                                                                                                                                                                      |

_`hashed id` is a kind of numerical representation of alphabetical name or id. It will be generated at runtime. It might be slightly different between mc versions, which is unpredictable but tried to avoid. They will be generated as text file to `plugins/ActSensors/enumIDTables` folder while plugin initializing._
_You could also check hashed id [here](https://github.com/Lori3f6/ActSensors/hashedID). The number after letter `c` in the middle of the filename indicates times of hash collision when generating the id table. Some (but very few)  item id might change between table with different collision numbers(between mc versions)._
### Separate Indicator Objectives

| Objective | Entries                                                                   | Value                                                                                                                                                             |
|-----------|---------------------------------------------------------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| weather   | `#<world_name>`                                                           | indicates the weather of each world <br/> 0 for sunny, 1 for rainy/snowy and 2 for thunderstorm                                                                   |
| random    | `#pos_<channel>(0-7)`<br/>`#neg_<channel>(0-3)`<br/>`#gen_<channel>(0-3)` | random value <br/>`#pos...` and `#neg...` are positive and negative values from 0 to 65535 (absolute value)<br/> `#gen...` is a random value from -32768 to 32767 |


### Player Value Modifiers

_Modifiers are interface to modify internal value of the server, which is hard to modify by native commands.   
You could write a value in valid range to corresponding objectives, and the internal game value will be updated at next
tick._

| Objective       | Value                                | Valid Range                | Description                                                                                                                                                                                                                                                    |
|-----------------|--------------------------------------|----------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| alt_freeze_tick | frozen tick to set                   | `>= 0`                     | Change the frozen tick of a player                                                                                                                                                                                                                             |
| alt_fire_tick   | fire tick to set                     | `>= 0`                     | Change the fire tick of a player                                                                                                                                                                                                                               |
| alt_health      | health to set                        | `0 <= value <= max_health` | Change the health of a player<br/> Values here expected are 1000 times scaled<br/>For example, 20000 stands for 20.0 in double                                                                                                                                 |
| alt_food_level  | food level to set                    | `0 <= value <= 20`         | Change the food level of a player                                                                                                                                                                                                                              |
| alt_air         | remaining air to set                 | `>= 0`                     | Change the air remaining in tick of a player. The default maximum is 300.                                                                                                                                                                                      |
| alt_vector_look | velocity at looking direction to add | `!=0`                      | Change player speed on looking direction (3d)                                                                                                                                                                                                                  | 
| alt_vector_face | velocity at facing direction to add  | `!=0`                      | Change the player speed on facing direction (2d)                                                                                                                                                                                                               | 
| alt_vector_cros | velocity at cross direction to add   | `!=0`                      | Change player speed in sideways (2d)                                                                                                                                                                                                                           | 
| alt_vector_up   | velocity at upward direction to add  | `!=0`                      | Change player speed upward                                                                                                                                                                                                                                     | 
| alt_vector_x    | velocity at x-axis                   | `!=0`                      | Change player speed at x-axis                                                                                                                                                                                                                                  | 
| alt_vector_y    | velocity at y-axis                   | `!=0`                      | Change player speed at y-axis                                                                                                                                                                                                                                  | 
| alt_vector_z    | velocity at z-axis                   | `!=0`                      | Change player speed at z-axis                                                                                                                                                                                                                                  | 
| alt_no_dmg_tick | no damage tick to set                | `>=0`                      | Change no damage time of a player<br/>_After suffered damage, player will gain no damage ticks, which reducing 1 per tick <br/>If a player has positive no damage ticks, all the damage will be ignored<br/>you could set/reset this value via this interface_ | 