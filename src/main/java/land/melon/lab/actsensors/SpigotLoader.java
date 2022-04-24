package land.melon.lab.actsensors;

import land.melon.lab.actsensors.active.objective.PlayerObjective;
import land.melon.lab.actsensors.active.objective.SeparateObjective;
import land.melon.lab.actsensors.active.tag.PlayerTagTrigger;
import land.melon.lab.actsensors.passive.objective.ValueModifier;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.generator.WorldInfo;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SpigotLoader extends JavaPlugin implements Listener {
    private final List<Registerable> registrable = new ArrayList<>();
    private final List<PlayerLoginTrigger> playerLoginTriggers = new ArrayList<>();
    private final List<GeneralTrigger> generalTriggers = new ArrayList<>();

    @Override
    public void onEnable() {

        //-------------------------
        // Indicator tags
        //-------------------------

        //isSprinting tag
        enableTrigger(new PlayerTagTrigger("isSprinting", Player::isSprinting));
        //isFlying tag
        enableTrigger(new PlayerTagTrigger("isFlying", Player::isFlying));
        //isSneaking tag
        enableTrigger(new PlayerTagTrigger("isSneaking", Player::isSneaking));
        //exposureToSky tag
        enableTrigger(new PlayerTagTrigger("isExposing", p -> {
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
        enableTrigger(new PlayerObjective("light", p ->
                p.getLocation().getBlock().getLightLevel()
        ));
        //blockLight objective
        enableTrigger(new PlayerObjective("block_light", p ->
                p.getLocation().getBlock().getLightFromBlocks()
        ));
        //skyLight objective
        enableTrigger(new PlayerObjective("sky_light", p ->
                p.getLocation().getBlock().getLightFromSky()
        ));

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
        enableTrigger(new ValueModifier("fire_tick_mfr", (p, v) -> {
            p.setFireTicks(v);
            return -1;
        }, t -> t > 0));

        enableTrigger(new ValueModifier("freeze_tick_mfr", (p, v) -> {
            p.setFreezeTicks(v);
            return -1;
        }, t -> t > 0));

        registrable.forEach(Registerable::register);
        Bukkit.getScheduler().runTaskTimer(this, () ->
                        generalTriggers.forEach(GeneralTrigger::trigger)
                , 200, 1);
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
}
