package dev.fishmodpv.profile;

import com.google.gson.JsonObject;
import dev.fishmodpv.api.HypixelApiClient;
import dev.fishmodpv.api.MojangApiClient;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/** Ties Mojang UUID resolution + the Hypixel API together into ready-to-render profile data. */
public final class ProfileService {
	private ProfileService() {
	}

	public record Result(UUID uuid, String correctedName, List<SkyblockProfile> profiles) {
		public SkyblockProfile selectedOrFirst() {
			return profiles.stream().filter(p -> p.selected).findFirst().orElse(profiles.isEmpty() ? null : profiles.get(0));
		}
	}

	public static CompletableFuture<Result> fetch(String username) {
		return MojangApiClient.resolveUuid(username).thenCompose(resolved ->
				HypixelApiClient.fetchProfiles(resolved.uuid()).thenApply(profilesJson -> {
					List<SkyblockProfile> profiles = new ArrayList<>();
					for (var element : profilesJson) {
						if (element.isJsonObject()) {
							JsonObject profileJson = element.getAsJsonObject();
							profiles.add(SkyblockProfile.parse(profileJson));
						}
					}
					return new Result(resolved.uuid(), resolved.correctedName(), profiles);
				}));
	}
}
