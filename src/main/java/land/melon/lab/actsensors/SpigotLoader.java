package land.melon.lab.actsensors;

import land.melon.lab.actsensors.objective.active.PlayerObjective;
import land.melon.lab.actsensors.objective.active.SeparateObjective;
import land.melon.lab.actsensors.tag.PlayerTagTrigger;
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

public class SpigotLoader extends JavaPlugin implements Listener {
    private final List<Registerable> registrable = new ArrayList<>();
    private final List<PlayerLoginTrigger> playerLoginTriggers = new ArrayList<>();
    private final List<GeneralTrigger> generalTriggers = new ArrayList<>();

    @Override
    public void onEnable() {
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
        //weather objective
        enableTrigger(new SeparateObjective("weather", (s, k) -> {
            var world = s.getWorld(k);
            if (world == null) return -1;
            else return world.hasStorm() ? world.isThundering() ? 2 : 1 : 0;
        }, Bukkit.getWorlds().stream().map(WorldInfo::getName).toList()));

        registrable.forEach(Registerable::register);
        Bukkit.getScheduler().runTaskTimer(this, () ->
                        generalTriggers.forEach(GeneralTrigger::trigger)
                , 200,1);
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
