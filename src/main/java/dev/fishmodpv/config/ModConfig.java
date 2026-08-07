package dev.fishmodpv.config;

/** Plain data holder, serialized to/from JSON by {@link ConfigManager}. */
public class ModConfig {
	public String hypixelApiKey = "";
	public boolean cacheProfiles = true;
	public int cacheDurationSeconds = 60;
}
