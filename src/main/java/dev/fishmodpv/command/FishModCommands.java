package dev.fishmodpv.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import dev.fishmodpv.config.ConfigManager;
import dev.fishmodpv.gui.ProfileViewerScreen;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

public final class FishModCommands {
	private FishModCommands() {
	}

	public static void register() {
		ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) ->
				dispatcher.register(ClientCommandManager.literal("fishmod")
						.executes(FishModCommands::openOwnProfile)
						.then(ClientCommandManager.literal("view")
								.executes(FishModCommands::openOwnProfile)
								.then(ClientCommandManager.argument("player", StringArgumentType.word())
										.executes(context -> openProfile(context.getSource(), StringArgumentType.getString(context, "player")))))
						.then(ClientCommandManager.literal("key")
								.then(ClientCommandManager.argument("apikey", StringArgumentType.greedyString())
										.executes(context -> {
											String key = StringArgumentType.getString(context, "apikey");
											ConfigManager.get().hypixelApiKey = key.trim();
											ConfigManager.save();
											context.getSource().sendFeedback(Text.literal("FishMod PV: Hypixel API key saved."));
											return 1;
										})))));
	}

	private static int openOwnProfile(com.mojang.brigadier.context.CommandContext<net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource> context) {
		String name = context.getSource().getPlayer().getGameProfile().getName();
		return openProfile(context.getSource(), name);
	}

	private static int openProfile(net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource source, String player) {
		if (ConfigManager.get().hypixelApiKey.isBlank()) {
			source.sendError(Text.literal("No Hypixel API key set. Run /fishmod key <your-key> first (get one with /api new on Hypixel)."));
			return 0;
		}
		MinecraftClient.getInstance().setScreen(new ProfileViewerScreen(player));
		return 1;
	}
}
