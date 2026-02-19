package aervial.rideanymob.whitelist;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class RideWhitelist {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path FILE = FabricLoader.getInstance()
            .getConfigDir()
            .resolve("rideanymob-whitelist.json");

    private static final Set<UUID> WHITELIST = new HashSet<>();

    public static void load() {
        if (!Files.exists(FILE)) {
            save();
            return;
        }

        try (Reader reader = Files.newBufferedReader(FILE)) {
            Type type = new TypeToken<Set<UUID>>(){}.getType();
            Set<UUID> data = GSON.fromJson(reader, type);
            if (data != null) {
                WHITELIST.clear();
                WHITELIST.addAll(data);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void save() {
        try (Writer writer = Files.newBufferedWriter(FILE)) {
            GSON.toJson(WHITELIST, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean isWhitelisted(UUID uuid) {
        return WHITELIST.contains(uuid);
    }

    public static void add(UUID uuid) {
        WHITELIST.add(uuid);
        save();
    }

    public static void remove(UUID uuid) {
        WHITELIST.remove(uuid);
        save();
    }

    public static Set<UUID> getAll() {
        return WHITELIST;
    }
}
