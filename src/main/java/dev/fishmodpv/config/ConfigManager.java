package dev.fishmodpv.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import dev.fishmodpv.FishModPV;

/** Loads/saves {@link ModConfig} as JSON under the standard Fabric config directory. */
public final class ConfigManager {
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("fishmodpv.json");

	private static ModConfig instance;

	private ConfigManager() {
	}

	public static synchronized ModConfig get() {
		if (instance == null) {
			instance = load();
		}
		return instance;
	}

	private static ModConfig load() {
		if (Files.exists(CONFIG_PATH)) {
			try (Reader reader = Files.newBufferedReader(CONFIG_PATH, StandardCharsets.UTF_8)) {
				ModConfig loaded = GSON.fromJson(reader, ModConfig.class);
				if (loaded != null) {
					return loaded;
				}
			} catch (IOException e) {
				FishModPV.LOGGER.error("Failed to read fishmodpv.json, using defaults", e);
			}
		}
		return new ModConfig();
	}

	public static synchronized void save() {
		if (instance == null) {
			return;
		}
		try {
			Files.createDirectories(CONFIG_PATH.getParent());
			try (Writer writer = Files.newBufferedWriter(CONFIG_PATH, StandardCharsets.UTF_8)) {
				GSON.toJson(instance, writer);
			}
		} catch (IOException e) {
			FishModPV.LOGGER.error("Failed to write fishmodpv.json", e);
		}
	}
}
