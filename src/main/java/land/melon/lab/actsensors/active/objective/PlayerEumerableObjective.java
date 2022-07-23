package land.melon.lab.actsensors.active.objective;

import land.melon.lab.actsensors.GeneralTrigger;
import land.melon.lab.actsensors.Registerable;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Function;

public class PlayerEumerableObjective<T extends Enum<T>> implements Registerable, GeneralTrigger {
    private final String objectiveName;
    private final Scoreboard scoreBoardEntry = Bukkit.getScoreboardManager().getMainScoreboard();
    private final Function<Player, Enum<T>> function;
    private final int capacity;
    private Objective objective;

    private final Class<T> enumType;

    private final File enumIdDir;

    public PlayerEumerableObjective(String objectiveName, int capacity, Function<Player, Enum<T>> function, Class<T> clazz, File enumIdDir) {
        this.objectiveName = "+" + objectiveName;
        this.function = function;
        this.capacity = capacity;
        this.enumType = clazz;
        this.enumIdDir = enumIdDir;
        objective = getObjective();
        try{
            generateEnumId();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public void trigger() {
        try {
            Bukkit.getOnlinePlayers().forEach(p -> objective.getScore(p.getName()).setScore(
                    (int) (Integer.toUnsignedLong(function.apply(p).name().toLowerCase(Locale.ROOT).hashCode()) % capacity)
            ));
        } catch (IllegalStateException e) {
            objective = getObjective();
            trigger();
        }
    }

    private Objective getObjective() {
        return Objects.requireNonNullElseGet(scoreBoardEntry.getObjective(objectiveName), this::registerObjective);
    }

    public Objective registerObjective() {
        return scoreBoardEntry.registerNewObjective(objectiveName, "dummy", ChatColor.of("#7d79ed") + objectiveName);
    }

    @Override
    public void unregister() {
        objective.unregister();
    }

    @Override
    public void register() {

    }

    private void generateEnumId() throws IOException {
        var maxWide = 0;
        var elements = new ArrayList<String>();
        for (Object x : enumType.getEnumConstants()) {
            var element = x.toString().toLowerCase(Locale.ROOT);
            elements.add(element);
            if (element.length() > maxWide)
                maxWide = element.length();
        }
        var enumName = enumType.getSimpleName().toLowerCase(Locale.ROOT);
        var idFile = new File(enumIdDir, enumName + ".txt");
        var fileWriter = new FileWriter(idFile, false);
        var leftAlignFormat = "| %-" + (maxWide + 1) + "s| %-6s |\n";
        fileWriter.append(String.format(leftAlignFormat, enumName, "ID"));
        fileWriter.append(String.format(leftAlignFormat, "", ""));
        for (String s : elements) {
            fileWriter.write(String.format(leftAlignFormat, s, Integer.toUnsignedLong(s.hashCode()) % capacity));
        }
        fileWriter.flush();
        fileWriter.close();
    }

}

