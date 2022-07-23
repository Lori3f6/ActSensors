package land.melon.lab.actsensors.active.tag;

import land.melon.lab.actsensors.GeneralTrigger;
import land.melon.lab.actsensors.PlayerLoginTrigger;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.function.Predicate;

public class PlayerTagTrigger implements PlayerLoginTrigger, GeneralTrigger {
    private final String tagName;
    private final Predicate<Player> predicate;

    public PlayerTagTrigger(String tagName, Predicate<Player> predicate) {
        this.tagName = "+" + tagName;
        this.predicate = predicate;
    }

    @Override
    public void trigger() {
        Bukkit.getOnlinePlayers().forEach(p -> {
            if (predicate.test(p)) {
                if (p.getScoreboardTags().size() == 1024) {
                    Bukkit.getLogger().warning("player " + p.getName() + " is going to have more than 1024 tags. Abort to add new tag " + tagName);
                } else {
                    p.addScoreboardTag(tagName);
                }
            } else {
                p.removeScoreboardTag(tagName);
            }
        });
    }

    @Override
    public void triggerOnPlayerLogin(Player player) {
        if (predicate.test(player)) {
            player.addScoreboardTag(tagName);
        }
    }

    @Override
    public void triggerOnPlayerLogout(Player player) {
        player.removeScoreboardTag(tagName);
    }
}
