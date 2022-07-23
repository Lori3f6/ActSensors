package land.melon.lab.actsensors.active.objective;

import land.melon.lab.actsensors.GeneralTrigger;
import land.melon.lab.actsensors.Registerable;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.Objects;
import java.util.function.ToIntFunction;

public class PlayerNumericalObjective implements Registerable, GeneralTrigger {
    private final String objectiveName;
    private final Scoreboard scoreBoardEntry = Bukkit.getScoreboardManager().getMainScoreboard();
    private final ToIntFunction<Player> prediction;
    private Objective objective;

    public PlayerNumericalObjective(String objectiveName, ToIntFunction<Player> prediction) {
        this.objectiveName = "+" + objectiveName;
        this.prediction = prediction;
        objective = getObjective();
    }

    @Override
    public void trigger() {
        try {
            Bukkit.getOnlinePlayers().forEach(p -> objective.getScore(p.getName()).setScore(prediction.applyAsInt(p)));
        } catch (IllegalStateException e) {
            objective = getObjective();
            trigger();
        }
    }

    private Objective getObjective() {
        return Objects.requireNonNullElseGet(scoreBoardEntry.getObjective(objectiveName), this::registerObjective);
    }

    public Objective registerObjective() {
        return scoreBoardEntry.registerNewObjective(objectiveName, "dummy", ChatColor.of("#7d79ed") + objectiveName );
    }

    @Override
    public void unregister() {
        objective.unregister();
    }

    @Override
    public void register() {

    }
}
