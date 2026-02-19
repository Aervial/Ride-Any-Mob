package aervial.rideanymob;

import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.text.Text;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class RideAnyMobClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		ClientCommandRegistrationCallback.EVENT.register(this::registerCommands);
	}

	private void registerCommands(
			CommandDispatcher<FabricClientCommandSource> dispatcher,
			CommandRegistryAccess registryAccess) {

		dispatcher.register(literal("toggleride")
				.executes(ctx -> {

					RideToggle.toggle();

					ctx.getSource().sendFeedback(
							Text.literal("RideAnyMob is now "
									+ (RideToggle.isEnabled() ? "ENABLED" : "DISABLED"))
					);

					return 1;
				}));
	}
}
