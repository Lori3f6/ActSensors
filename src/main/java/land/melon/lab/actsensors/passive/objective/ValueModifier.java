package land.melon.lab.actsensors.passive.objective;

import land.melon.lab.actsensors.GeneralTrigger;
import land.melon.lab.actsensors.Registerable;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.Objects;
import java.util.function.Predicate;
import java.util.function.ToIntBiFunction;

public class ValueModifier implements Registerable, GeneralTrigger {
    private final Scoreboard scoreBoardEntry = Bukkit.getScoreboardManager().getMainScoreboard();
    private final String objectiveName;
    private final ToIntBiFunction<Player, Integer> consumer;
    private final Predicate<Integer> validRangePredicate;
    private Objective objective;

    public ValueModifier(String objectiveName, ToIntBiFunction<Player, Integer> consumer, Predicate<Integer> validRangePredicate) {
        this.objectiveName = "+"+objectiveName;
        this.consumer = consumer;
        this.validRangePredicate = validRangePredicate;
        objective = getObjective();
    }

    private Objective getObjective() {
        return Objects.requireNonNullElseGet(scoreBoardEntry.getObjective(objectiveName), this::registerObjective);
    }

    public Objective registerObjective() {
        return scoreBoardEntry.registerNewObjective(objectiveName, "dummy", ChatColor.of("#85ed79") + objectiveName);
    }

    @Override
    public void unregister() {
        objective.unregister();
    }

    @Override
    public void register() {
    }

    @Override
    public void trigger() {
        try {
            Bukkit.getOnlinePlayers().forEach(p -> {
                var score = objective.getScore(p.getName());
                var scoreValue = score.getScore();
                if (validRangePredicate.test(scoreValue)) {
                    score.setScore(consumer.applyAsInt(p, scoreValue));
                }
            });
        } catch (IllegalStateException e) {
            objective = registerObjective();
            trigger();
        }
    }
}
