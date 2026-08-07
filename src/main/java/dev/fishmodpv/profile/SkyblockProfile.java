package dev.fishmodpv.profile;

import com.google.gson.JsonObject;
import dev.fishmodpv.util.JsonPath;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/** One SkyBlock profile ("island"), potentially co-op, as returned by /v2/skyblock/profiles. */
public class SkyblockProfile {
	public final String profileId;
	public final String cuteName;
	public final String gameMode;
	public final boolean selected;
	public final double bankBalance;
	public final Map<UUID, ProfileMember> members;

	private SkyblockProfile(String profileId, String cuteName, String gameMode, boolean selected,
			double bankBalance, Map<UUID, ProfileMember> members) {
		this.profileId = profileId;
		this.cuteName = cuteName;
		this.gameMode = gameMode;
		this.selected = selected;
		this.bankBalance = bankBalance;
		this.members = members;
	}

	public static SkyblockProfile parse(JsonObject profile) {
		String profileId = profile.has("profile_id") ? profile.get("profile_id").getAsString() : "";
		String cuteName = profile.has("cute_name") ? profile.get("cute_name").getAsString() : "Unknown";
		String gameMode = profile.has("game_mode") && !profile.get("game_mode").isJsonNull()
				? profile.get("game_mode").getAsString() : "classic";
		boolean selected = profile.has("selected") && profile.get("selected").getAsBoolean();
		double bank = JsonPath.firstAsDouble(profile, 0, "banking.balance");

		Map<UUID, ProfileMember> members = new LinkedHashMap<>();
		if (profile.has("members")) {
			JsonObject membersObj = profile.getAsJsonObject("members");
			for (String uuidString : membersObj.keySet()) {
				UUID uuid = dashUuid(uuidString);
				members.put(uuid, ProfileMember.parse(uuid, membersObj.getAsJsonObject(uuidString)));
			}
		}

		return new SkyblockProfile(profileId, cuteName, gameMode, selected, bank, members);
	}

	private static UUID dashUuid(String raw) {
		if (raw.contains("-")) {
			return UUID.fromString(raw);
		}
		return UUID.fromString(raw.replaceFirst(
				"(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})", "$1-$2-$3-$4-$5"));
	}
}
