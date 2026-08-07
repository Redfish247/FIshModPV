package dev.fishmodpv.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.fishmodpv.config.ConfigManager;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Thin wrapper around the public Hypixel API (https://api.hypixel.net).
 *
 * <p>Every player needs their own Hypixel API key (generated in-game via {@code /api new} on
 * Hypixel, or on the developer dashboard) since Hypixel retired shared/global keys. The key is
 * stored locally in {@code config/fishmodpv.json} and never sent anywhere but api.hypixel.net.
 */
public final class HypixelApiClient {
	private static final String BASE_URL = "https://api.hypixel.net";
	private static final HttpClient HTTP = HttpClient.newBuilder()
			.connectTimeout(Duration.ofSeconds(10))
			.build();

	private HypixelApiClient() {
	}

	/** Raw list of the player's SkyBlock profiles (islands), one JsonObject per profile. */
	public static CompletableFuture<JsonArray> fetchProfiles(UUID uuid) {
		String apiKey = ConfigManager.get().hypixelApiKey;
		if (apiKey == null || apiKey.isBlank()) {
			return CompletableFuture.failedFuture(
					new ApiException("No Hypixel API key set. Run /fishmod key <your-key> first (get one with /api new in-game on Hypixel)."));
		}

		String noDashUuid = uuid.toString().replace("-", "");
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(BASE_URL + "/v2/skyblock/profiles?key=" + apiKey + "&uuid=" + noDashUuid))
				.timeout(Duration.ofSeconds(15))
				.GET()
				.build();

		return HTTP.sendAsync(request, HttpResponse.BodyHandlers.ofString()).thenApply(response -> {
			JsonObject json;
			try {
				json = JsonParser.parseString(response.body()).getAsJsonObject();
			} catch (Exception e) {
				throw new ApiException("Hypixel API returned an unexpected response (HTTP " + response.statusCode() + ")", e);
			}

			boolean success = json.has("success") && json.get("success").getAsBoolean();
			if (!success) {
				String cause = json.has("cause") ? json.get("cause").getAsString() : ("HTTP " + response.statusCode());
				throw new ApiException("Hypixel API error: " + cause);
			}

			JsonArray profiles = json.has("profiles") && !json.get("profiles").isJsonNull()
					? json.getAsJsonArray("profiles")
					: new JsonArray();
			return profiles;
		});
	}
}
