package dev.fishmodpv.api;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/** Resolves SkyBlock/Minecraft usernames to UUIDs. */
public final class MojangApiClient {
	private static final HttpClient HTTP = HttpClient.newBuilder()
			.connectTimeout(Duration.ofSeconds(10))
			.build();

	private MojangApiClient() {
	}

	/** Result of a name -> UUID lookup. */
	public record ResolvedPlayer(UUID uuid, String correctedName) {
	}

	public static CompletableFuture<ResolvedPlayer> resolveUuid(String username) {
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create("https://api.mojang.com/users/profiles/minecraft/" + username))
				.timeout(Duration.ofSeconds(10))
				.GET()
				.build();

		return HTTP.sendAsync(request, HttpResponse.BodyHandlers.ofString()).thenApply(response -> {
			if (response.statusCode() == 404) {
				throw new ApiException("No Minecraft account named '" + username + "' exists");
			}
			if (response.statusCode() != 200) {
				throw new ApiException("Mojang API returned HTTP " + response.statusCode());
			}
			JsonObject json = JsonParser.parseString(response.body()).getAsJsonObject();
			String rawId = json.get("id").getAsString();
			UUID uuid = dashUuid(rawId);
			String name = json.get("name").getAsString();
			return new ResolvedPlayer(uuid, name);
		});
	}

	private static UUID dashUuid(String raw) {
		if (raw.contains("-")) {
			return UUID.fromString(raw);
		}
		return UUID.fromString(raw.replaceFirst(
				"(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})", "$1-$2-$3-$4-$5"));
	}
}
