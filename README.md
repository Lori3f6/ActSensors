# ActSensors
Custom tags/objectives interfaces.  
For easier identification, objectives has a `+` prefix. It will not be specifically noted below.

## Interfaces supported
### Tags
|Tag|Description|
|---|---|
|isSprinting|exist if a player is sprinting|
|isFlying|exist if a player is flying|
|isSneaking|exist if a player is sneaking|
|isExposing|exist if there is no solid blocks over player|


### Player Objectives
|Objective|Description|
|---|---|
|light|light value of player location|
|light_block|block light value of player location|
|sky_light|sky light value of player location|

### Saperate Objectives
|Objective|Entries|Description|
|---|---|---|
|weather|`#<world_name>`|indecates the weather of each world. 0 for sunny and 1 for rainy/snowy|
