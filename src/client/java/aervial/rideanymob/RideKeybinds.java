package aervial.rideanymob;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public class RideKeybinds {

    private static KeyBinding toggleRideKey;

    public static void register() {

        toggleRideKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.rideanymob.toggle",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_R,
                "category.rideanymob"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(RideKeybinds::handleKeyPress);
    }

    private static void handleKeyPress(MinecraftClient client) {

        while (toggleRideKey.wasPressed()) {

            RideToggle.toggle();
            boolean newState = RideToggle.isEnabled();

            if (client.player == null) return;

            // Create buffer manually
            PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
            buf.writeBoolean(newState);

            ClientPlayNetworking.send(RideAnyMob.TOGGLE_PACKET, buf);

            client.player.sendMessage(
                    Text.literal("Ride Mode: " + (newState ? "ON" : "OFF")),
                    true
            );
        }
    }
}
