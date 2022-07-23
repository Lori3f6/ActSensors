package land.melon.lab.actsensors.active.objective;

import land.melon.lab.actsensors.GeneralTrigger;
import land.melon.lab.actsensors.Registerable;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;

public class SeparateObjective implements Registerable, GeneralTrigger {
    private final String objectiveName;
    private final Scoreboard scoreBoardEntry = Bukkit.getScoreboardManager().getMainScoreboard();
    private final BiFunction<Server, String, Integer> prediction;
    private final List<String> items;
    private Objective objective;

    public SeparateObjective(String objectiveName, BiFunction<Server, String, Integer> prediction, List<String> items) {
        this.objectiveName = "+" + objectiveName;
        this.prediction = prediction;
        this.items = items;
        objective = getObjective();
    }

    @Override
    public void trigger() {
        try {
            for (String item : items) {
                objective.getScore("#" + item).setScore(prediction.apply(Bukkit.getServer(), item));
            }
        } catch (IllegalStateException e) {
            objective = registerObjective();
            trigger();
        }
    }

    private Objective getObjective() {
        return Objects.requireNonNullElseGet(scoreBoardEntry.getObjective(objectiveName), this::registerObjective);
    }


    public Objective registerObjective() {
        return scoreBoardEntry.registerNewObjective(objectiveName, "dummy", ChatColor.of("#ce79ed") + objectiveName);
    }

    @Override
    public void unregister() {
        objective.unregister();
    }

    @Override
    public void register() {
    }
}
