package dev.fishmodpv;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Common (server+client) entrypoint. This mod is client-only, so this mainly exists for logging/id purposes. */
public class FishModPV implements ModInitializer {
	public static final String MOD_ID = "fishmodpv";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("FishMod PV initialized");
	}
}
