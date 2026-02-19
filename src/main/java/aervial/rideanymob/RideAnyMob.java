package aervial.rideanymob;

import aervial.rideanymob.whitelist.RideWhitelist;
import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.command.CommandRegistryAccess;

public class RideAnyMob implements ModInitializer {

	@Override
	public void onInitialize() {
		RideWhitelist.load();

		CommandRegistrationCallback.EVENT.register(this::registerCommands);
	}

	private void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher,
								  CommandRegistryAccess registryAccess,
								  CommandManager.RegistrationEnvironment environment) {


		dispatcher.register(CommandManager.literal("ridewhitelist")
				.requires(source -> source.hasPermissionLevel(2))

				.then(CommandManager.literal("add")
						.then(CommandManager.argument("player", net.minecraft.command.argument.EntityArgumentType.player())
								.executes(ctx -> {
									ServerPlayerEntity target =
											net.minecraft.command.argument.EntityArgumentType.getPlayer(ctx, "player");

									RideWhitelist.add(target.getUuid());
									ctx.getSource().sendFeedback(() ->
											Text.literal("Added to ride whitelist: " + target.getName().getString()), true);
									return 1;
								})))

				.then(CommandManager.literal("remove")
						.then(CommandManager.argument("player", net.minecraft.command.argument.EntityArgumentType.player())
								.executes(ctx -> {
									ServerPlayerEntity target =
											net.minecraft.command.argument.EntityArgumentType.getPlayer(ctx, "player");

									RideWhitelist.remove(target.getUuid());
									ctx.getSource().sendFeedback(() ->
											Text.literal("Removed from ride whitelist: " + target.getName().getString()), true);
									return 1;
								})))

				.then(CommandManager.literal("list")
						.executes(ctx -> {
							ctx.getSource().sendFeedback(() ->
									Text.literal("Whitelisted UUIDs: " + RideWhitelist.getAll()), false);
							return 1;
						}))
		);
	}
}
