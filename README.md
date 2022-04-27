# ActSensors

Custom tags/objectives interfaces.

## Interfaces supported

_Indicators below are **read only**, and will automatically be updated every tick._

### Indicator tags

| Tag         | Description                                    |
|-------------|------------------------------------------------|
| isSprinting | exist if a player is sprinting                 |
| isFlying    | exist if a player is flying                    |
| isSneaking  | exist if a player is sneaking                  |
| isExposing  | exist if there is no solid blocks above player |

### Player Indicator Objectives

_For easier identification, objectives has a `+` prefix. It will not be specifically noted below._

| Objective   | Value                                                                                                    |
|-------------|----------------------------------------------------------------------------------------------------------|
| light       | light value of player location                                                                           |
| light_block | block light value of player location                                                                     |
| sky_light   | sky light value of player location                                                                       |
| health      | health of players<br/>Values here are 1000 times scaled<br/>For example, 20000 stands for 20.0 in double |
| food_level  | food level of players                                                                                    |
| air         | air remaining of players                                                                                 | 

### Separate Indicator Objectives

| Objective | Entries                                                                   | Value                                                                                                                                                             |
|-----------|---------------------------------------------------------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| weather   | `#<world_name>`                                                           | indicates the weather of each world <br/> 0 for sunny, 1 for rainy/snowy and 2 for thunderstorm                                                                   |
| random    | `#pos_<channel>(0-7)`<br/>`#neg_<channel>(0-3)`<br/>`#gen_<channel>(0-3)` | random value <br/>`#pos...` and `#neg...` are positive and negative values from 0 to 65535 (absolute value)<br/> `#gen...` is a random value from -32768 to 32767 |

_Modifiers are interface to modify internal value of the server, which is hard to modify by native commands.   
You could write a value in valid range to corresponding objectives, and the internal game value will be updated at next
tick._

### Player Value Modifiers

| Objective       | Value                                | Valid Range                | Description                                                                                                                    |
|-----------------|--------------------------------------|----------------------------|--------------------------------------------------------------------------------------------------------------------------------|
| alt_freeze_tick | frozen tick to set                   | `>= 0`                     | Change the frozen tick of a player                                                                                             |
| alt_fire_tick   | fire tick to set                     | `>= 0`                     | Change the fire tick of a player                                                                                               |
| alt_health      | health to set                        | `0 <= value <= max_health` | Change the health of a player<br/> Values here expected are 1000 times scaled<br/>For example, 20000 stands for 20.0 in double |
| alt_food_level  | food level to set                    | `0 <= value <= 20`         | Change the food level of a player                                                                                              |
| alt_air         | remaining air to set                 | `>= 0`                     | Change the air remaining in tick of a player. The default maximum is 300.                                                      |
| alt_vector_look | velocity at looking direction to add | `!=0`                      | Change player speed on looking direction (3d)                                                                                  | 
| alt_vector_face | velocity at facing direction to add  | `!=0`                      | Change the player speed on facing direction (2d)                                                                               | 
| alt_vector_cros | velocity at cross direction to add   | `!=0`                      | Change player speed in sideways (2d)                                                                                           | 
| alt_vector_up   | velocity at upward direction to add  | `!=0`                      | Change player speed upward                                                                                                     | 
| alt_vector_x    | velocity at x-axis                   | `!=0`                      | Change player speed at x-axis                                                                                                  | 
| alt_vector_y    | velocity at y-axis                   | `!=0`                      | Change player speed at y-axis                                                                                                  | 
| alt_vector_z    | velocity at z-axis                   | `!=0`                      | Change player speed at z-axis                                                                                                  | 