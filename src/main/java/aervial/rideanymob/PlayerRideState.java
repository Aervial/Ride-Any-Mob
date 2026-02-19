package aervial.rideanymob;

import net.minecraft.server.network.ServerPlayerEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerRideState {

    private static final Map<UUID, Boolean> STATES = new HashMap<>();

    public static boolean isEnabled(ServerPlayerEntity player) {
        return STATES.getOrDefault(player.getUuid(), true);
    }

    public static void set(ServerPlayerEntity player, boolean value) {
        STATES.put(player.getUuid(), value);
    }
}
