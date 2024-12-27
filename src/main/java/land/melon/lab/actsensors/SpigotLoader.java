package land.melon.lab.actsensors;

import land.melon.lab.actsensors.active.objective.PlayerEnumerableObjective;
import land.melon.lab.actsensors.active.objective.PlayerNumericalObjective;
import land.melon.lab.actsensors.active.objective.SeparateObjective;
import land.melon.lab.actsensors.active.tag.PlayerTagTrigger;
import land.melon.lab.actsensors.passive.objective.ValueModifier;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.generator.WorldInfo;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import java.io.File;
import java.util.*;

import static org.bukkit.attribute.Attribute.GENERIC_MAX_HEALTH;

public class SpigotLoader extends JavaPlugin implements Listener {
    private final List<Registerable> registrable = new ArrayList<>();
    private final List<PlayerLoginTrigger> playerLoginTriggers = new ArrayList<>();
    private final List<GeneralTrigger> generalTriggers = new ArrayList<>();

    private final Map<String, Map<Object, Integer>> globalEnumIDMap = new HashMap<>();

    private final File dataDir = getDataFolder();

    private final File enumIdDir = new File(dataDir, "enumIdTables");

    private final double DOUBLE_TO_INT_SCALE = 1000.0D;

    @Override
    public void onEnable() {

        //-------------------------
        // generate enum id tables
        //-------------------------
        dataDir.mkdir();
        enumIdDir.mkdir();

        //-------------------------
        // Indicator tags
        //-------------------------
        //isSprinting tag
        enableTrigger(new PlayerTagTrigger("sprinting", Player::isSprinting));
        //isFlying tag
        enableTrigger(new PlayerTagTrigger("flying", Player::isFlying));
        //isGrounded tag
        enableTrigger(new PlayerTagTrigger("grounded", Player::isOnGround));
        //isSwimming tag
        enableTrigger(new PlayerTagTrigger("swimming", Player::isSwimming));
        //isSleeping tag
        enableTrigger(new PlayerTagTrigger("sleeping", Player::isSleeping));
        //isSneaking tag
        enableTrigger(new PlayerTagTrigger("sneaking", Player::isSneaking));
        //isGliding tag
        enableTrigger(new PlayerTagTrigger("gliding", Player::isGliding));
        //isGlowing tag
        enableTrigger(new PlayerTagTrigger("glowing", Player::isGlowing));
        //exposureToSky tag
        enableTrigger(new PlayerTagTrigger("exposing", p -> {
            var currentLoc = p.getLocation().add(0, 1.6, 0);
            for (int height = currentLoc.getBlockY(); height < 255; height++) {
                currentLoc = currentLoc.add(0, 1, 0);
                if (currentLoc.getBlock().getType().isSolid())
                    return false;
            }
            return true;
        }));

        //-------------------------
        // Player Indicator Objectives
        //-------------------------

        //light objective
        enableTrigger(new PlayerNumericalObjective("light", p ->
                p.getLocation().getBlock().getLightLevel()
        ));
        //blockLight objective
        enableTrigger(new PlayerNumericalObjective("block_light", p ->
                p.getLocation().getBlock().getLightFromBlocks()
        ));
        //skyLight objective
        enableTrigger(new PlayerNumericalObjective("sky_light", p ->
                p.getLocation().getBlock().getLightFromSky()
        ));
        //health objective
        enableTrigger(new PlayerNumericalObjective("health", p -> (int) (p.getHealth() * DOUBLE_TO_INT_SCALE)));
        //foodLevel objective
        enableTrigger(new PlayerNumericalObjective("food_level", HumanEntity::getFoodLevel));
        //air objective
        enableTrigger(new PlayerNumericalObjective("air", LivingEntity::getRemainingAir));
        //biome objective
        //enableTrigger(new PlayerEnumerableObjective<>("biome", 100000, p -> p.getLocation().getBlock().getBiome(), Biome.class, enumIdDir, globalEnumIDMap));
        //temperature objective
        enableTrigger(new PlayerNumericalObjective("temperature", p -> (int) (p.getLocation().getBlock().getTemperature() * 10)));
        //item in main hand objective
        enableTrigger(new PlayerEnumerableObjective<>("item_hand0", 100000, p -> p.getInventory().getItemInMainHand().getType(), Material.class, enumIdDir, globalEnumIDMap));
        //item in offhand objective
        enableTrigger(new PlayerEnumerableObjective<>("item_hand1", 100000, p -> p.getInventory().getItemInOffHand().getType(), Material.class, enumIdDir, globalEnumIDMap));
        //main hand item lore meta indicator
        enableTrigger(new PlayerNumericalObjective("meta_hand0", p -> getItemSimpleMetaHash(p.getInventory().getItemInMainHand())));
        //offhand item lore meta indicator
        enableTrigger(new PlayerNumericalObjective("meta_hand1", p -> getItemSimpleMetaHash(p.getInventory().getItemInOffHand())));
        //flying status indicator
        enableTrigger(new PlayerNumericalObjective("flying", p -> p.isFlying() ? 1 : 0));
        //flying speed indicator
        enableTrigger(new PlayerNumericalObjective("flying_speed", p -> doubleToInt(p.getFlySpeed())));
        //walk speed indicator
        enableTrigger(new PlayerNumericalObjective("walk_speed", p -> doubleToInt(p.getWalkSpeed())));
        //-------------------------
        // Separate Indicator Objectives
        //-------------------------

        //weather objective
        enableTrigger(new SeparateObjective("weather", (s, k) -> {
            var world = s.getWorld(k);
            if (world == null) return -1;
            else return world.hasStorm() ? world.isThundering() ? 2 : 1 : 0;
        }, Bukkit.getWorlds().stream().map(WorldInfo::getName).toList()));
        //random generator
        var random = new Random();
        enableTrigger(new SeparateObjective("random", (s, k) -> {
            if (k.startsWith("pos"))
                return random.nextInt(0, 65536);
            else if (k.startsWith("neg"))
                return -random.nextInt(0, 65536);
            else if (k.startsWith("gen"))
                return random.nextInt(0, 65536) - 32768;
            else return 0;
        }, List.of("pos_0", "pos_1", "pos_2", "pos_3", "pos_4", "pos_5", "pos_6", "pos_7",
                "neg_0", "neg_1", "neg_2", "neg_3", "gen_0", "gen_1", "gen_2", "gen_3")));

        //-------------------------
        // Value Modifier
        //-------------------------
        //fireTick modifier
        enableTrigger(new ValueModifier("alt_fire_tick", (p, v) -> {
            p.setFireTicks(v);
            return -1;
        }, t -> t >= 0, -1));
        //freezeTick modifier
        enableTrigger(new ValueModifier("alt_freeze_tick", (p, v) -> {
            p.setFreezeTicks(v);
            return -1;
        }, t -> t >= 0, -1));
        //health modifier
        enableTrigger(new ValueModifier("alt_health", (p, v) -> {
            var healthValue = v / DOUBLE_TO_INT_SCALE;
            var maxHealth = Objects.requireNonNull(p.getAttribute(GENERIC_MAX_HEALTH)).getValue();
            p.setHealth(Math.min(healthValue, maxHealth));
            return -1;
        }, t -> t >= 0, -1));
        //foodLevel modifier
        enableTrigger(new ValueModifier("alt_food_level", (p, v) -> {
            p.setFoodLevel(Math.min(v, 20));
            return -1;
        }, t -> t >= 0, -1));
        //air modifier
        enableTrigger(new ValueModifier("alt_air", (p, v) -> {
            p.setRemainingAir(v);
            return -1;
        }, t -> t >= 0, -1));
        //vector modifier for looking direction
        enableTrigger(new ValueModifier("alt_vector_look", (p, v) -> {
            p.setVelocity(p.getVelocity().add(p.getLocation().getDirection().multiply(intToDouble(v))));
            return 0;
        }, t -> t != 0, 0));
        //facing vector modifier
        enableTrigger(new ValueModifier("alt_vector_face", (p, v) -> {
            var vec2d = p.getLocation().getDirection().setY(0);
            p.setVelocity(p.getVelocity().add(vec2d.multiply(intToDouble(v))));
            return 0;
        }, t -> t != 0, 0));
        //side vector modifier
        var yAxisUnit = new Vector(0, 1, 0);
        enableTrigger(new ValueModifier("alt_vector_cros", (p, v) -> {
            var vec2d = p.getLocation().getDirection().setY(0);
            p.setVelocity(p.getVelocity().add(vec2d.rotateAroundAxis(yAxisUnit, Math.PI / 2).multiply(intToDouble(v))));
            return 0;
        }, t -> t != 0, 0));
        //upward vector modifier
        enableTrigger(new ValueModifier("alt_vector_up", (p, v) -> {
            p.setVelocity(p.getVelocity().add(new Vector(0, intToDouble(v), 0)));
            return 0;
        }, t -> t != 0, 0));
        //vector modifier for x
        enableTrigger(new ValueModifier("alt_vector_x", (p, v) -> {
            p.setVelocity(p.getVelocity().add(new Vector(intToDouble(v), 0, 0)));
            return 0;
        }, t -> t != 0, 0));
        //vector modifier for y
        enableTrigger(new ValueModifier("alt_vector_y", (p, v) -> {
            p.setVelocity(p.getVelocity().add(new Vector(0, intToDouble(v), 0)));
            return 0;
        }, t -> t != 0, 0));
        //vector modifier for z
        enableTrigger(new ValueModifier("alt_vector_z", (p, v) -> {
            p.setVelocity(p.getVelocity().add(new Vector(0, 0, intToDouble(v))));
            return 0;
        }, t -> t != 0, 0));
        //vector multiplier
        enableTrigger(new ValueModifier("alt_vector_bypct", (p, v) -> {
            p.setVelocity(p.getVelocity().multiply(intToDouble(v)));
            return 0;
        }, t -> t != 0, 0));
        //no damage tick modifier
        enableTrigger(new ValueModifier("alt_no_dmg_tick", (p, v) -> {
            p.setNoDamageTicks(v);
            return -1;
        }, t -> t >= 0, -1));
        //flying status modifier
        enableTrigger(new ValueModifier("alt_flying", (p, v) -> {
            p.setAllowFlight(v > 0);
            p.setFlying(v > 0);
            return -1;
        }, t -> t >= 0, -1));
        //flying speed modifier
        enableTrigger(new ValueModifier("alt_flying_speed", (p, v) -> {
            p.setFlySpeed((float) intToDouble(v));
            return -1;
        }, t -> t >= 0, -1));
        //walking speed modifier
        enableTrigger(new ValueModifier("alt_walking_speed", (p, v) -> {
            p.setWalkSpeed((float) intToDouble(v));
            return -1;
        }, t -> t >= 0, -1));

        //-------------------------
        // Final Setup
        //-------------------------
        registrable.forEach(Registerable::register);
        Bukkit.getScheduler().runTaskTimer(this, () ->
                        generalTriggers.forEach(GeneralTrigger::trigger)
                , 0, 1);
    }

    private double intToDouble(int doubleAsInt) {
        return doubleAsInt / DOUBLE_TO_INT_SCALE;
    }

    private int doubleToInt(double value) {
        return (int) (value * DOUBLE_TO_INT_SCALE);
    }

    @Override
    public void onDisable() {
        registrable.forEach(Registerable::unregister);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        playerLoginTriggers.forEach(p -> p.triggerOnPlayerLogin(event.getPlayer()));
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        playerLoginTriggers.forEach(p -> p.triggerOnPlayerLogout(event.getPlayer()));
    }

    private void enableTrigger(Object o) {
        if (o instanceof GeneralTrigger generalTrigger)
            generalTriggers.add(generalTrigger);
        if (o instanceof PlayerLoginTrigger playerLoginTrigger)
            playerLoginTriggers.add(playerLoginTrigger);
        if (o instanceof Registerable registerable)
            registrable.add(registerable);
    }

    private int getItemSimpleMetaHash(ItemStack itemStack) {
        if (!itemStack.hasItemMeta()) {
            return 0;
        } else {
            var itemMeta = itemStack.getItemMeta();
            var strBuilder = new StringBuilder();
            if (itemMeta.hasCustomModelData()) {
                strBuilder.append("::").append(itemMeta.getCustomModelData());
            }
            if (itemMeta.hasLore()) {
                strBuilder.append("::").append(
                        String.join("", itemMeta.getLore())
                );
            }
            return strBuilder.isEmpty() ? 0 : (int) (Integer.toUnsignedLong(strBuilder.toString().hashCode()) % 100000000);
        }
    }
}
