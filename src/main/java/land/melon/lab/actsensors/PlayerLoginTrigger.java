package land.melon.lab.actsensors;

import org.bukkit.entity.Player;

public interface PlayerLoginTrigger {
    void triggerOnPlayerLogin(Player player);

    void triggerOnPlayerLogout(Player player);
}
