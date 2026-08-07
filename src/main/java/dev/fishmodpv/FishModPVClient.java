package dev.fishmodpv;

import dev.fishmodpv.command.FishModCommands;
import net.fabricmc.api.ClientModInitializer;

public class FishModPVClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		FishModCommands.register();
		FishModPV.LOGGER.info("FishMod PV client initialized - use /fishmod view <player> or /fishmod key <apikey>");
	}
}
