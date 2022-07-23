package land.melon.lab.actsensors.active.objective;

import land.melon.lab.actsensors.GeneralTrigger;
import land.melon.lab.actsensors.Registerable;
import land.melon.lab.actsensors.SpigotLoader;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.function.Function;

public class PlayerEnumerableObjective<T extends Enum<T>> implements Registerable, GeneralTrigger {
    private final String objectiveName;
    private final Scoreboard scoreBoardEntry = Bukkit.getScoreboardManager().getMainScoreboard();
    private final Function<Player, Enum<T>> function;
    private final int capacity;
    private final Class<T> enumType;
    private final Map<T, Integer> enumIdMap = new HashMap<>();
    private final File enumIdDir;
    private Objective objective;

    public PlayerEnumerableObjective(String objectiveName, int capacity, Function<Player, Enum<T>> function, Class<T> clazz, File enumIdDir) {
        this.objectiveName = "+" + objectiveName;
        this.function = function;
        this.capacity = capacity;
        this.enumType = clazz;
        this.enumIdDir = enumIdDir;
        objective = getObjective();
        try {
            generateEnumIdMap();
        } catch (IOException e) {
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

    private void generateEnumIdMap() throws IOException {
        var usedId = new TreeSet<Integer>();
        var maxWide = 0;
        for (T x : enumType.getEnumConstants()) {
            var name = x.name();
            maxWide = Math.max(name.length(), maxWide);
            var hash = (int) (Integer.toUnsignedLong(name.toLowerCase(Locale.ROOT).replace("_", "").hashCode()) % capacity);
            while (usedId.contains(hash))
                hash++;
            enumIdMap.put(x, hash);
            usedId.add(hash);
        }
        writeIDMapToFile(enumType.getSimpleName().toLowerCase(Locale.ROOT), maxWide);
    }

    private void writeIDMapToFile(String enumName, int maxWide) throws IOException {
        var idFile = new File(enumIdDir, enumName + ".txt");
        idFile.delete();
        idFile.createNewFile();
        var fileWriter = new FileWriter(idFile, false);
        var capacityLength = String.valueOf(capacity - 1).length();
        var leftAlignFormat = "| %-" + maxWide + "s | %-" + capacityLength + "s |\n";
        String sectionLine = "+" + "-".repeat(maxWide + 2) + "+" + "-".repeat(capacityLength + 2) + "+" + "\n";
        fileWriter.append(sectionLine);
        fileWriter.append(String.format(leftAlignFormat, enumName, "id"));
        fileWriter.append(sectionLine);
        for (T x : enumIdMap.keySet().stream().sorted((e1, e2) -> e1.name().compareToIgnoreCase(e2.name())).toList())
            fileWriter.write(String.format(leftAlignFormat, x.name().toLowerCase(Locale.ROOT), enumIdMap.get(x)));
        fileWriter.append(sectionLine);
        fileWriter.flush();
        fileWriter.close();
        SpigotLoader.getPlugin(SpigotLoader.class).getLogger().info("Generated id table " + idFile.getAbsolutePath());
    }
}

