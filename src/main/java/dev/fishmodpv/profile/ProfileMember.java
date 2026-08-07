package dev.fishmodpv.profile;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dev.fishmodpv.nbt.ItemDecoder;
import dev.fishmodpv.nbt.SkyblockItem;
import dev.fishmodpv.util.JsonPath;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/** One player's data within a single SkyBlock profile (island). */
public class ProfileMember {
	public final UUID uuid;
	public final double coinPurse;
	public final double skyblockXp;
	public final double totalSlayerXp;
	public final Map<SkillType, SkillData> skills;
	public final Map<String, Long> collections;
	public final List<PetData> pets;
	public final Map<String, List<SkyblockItem>> inventories;

	private ProfileMember(UUID uuid, double coinPurse, double skyblockXp, double totalSlayerXp,
			Map<SkillType, SkillData> skills, Map<String, Long> collections, List<PetData> pets,
			Map<String, List<SkyblockItem>> inventories) {
		this.uuid = uuid;
		this.coinPurse = coinPurse;
		this.skyblockXp = skyblockXp;
		this.totalSlayerXp = totalSlayerXp;
		this.skills = skills;
		this.collections = collections;
		this.pets = pets;
		this.inventories = inventories;
	}

	public double skyblockLevel() {
		return skyblockXp / 100.0;
	}

	public double skillAverage() {
		double total = 0;
		int count = 0;
		for (SkillData data : skills.values()) {
			if (data.type().hasKnownLevelTable()) {
				total += data.level();
				count++;
			}
		}
		return count == 0 ? 0 : total / count;
	}

	public static ProfileMember parse(UUID uuid, JsonObject member) {
		double coinPurse = JsonPath.firstAsDouble(member, 0, "currencies.coin_purse", "coin_purse");
		double skyblockXp = JsonPath.firstAsDouble(member, 0, "leveling.experience", "experience_skyblock_level");

		Map<SkillType, SkillData> skills = new EnumMap<>(SkillType.class);
		for (SkillType type : SkillType.values()) {
			skills.put(type, SkillData.parse(member, type));
		}

		Map<String, Long> collections = new LinkedHashMap<>();
		JsonObject collectionObj = JsonPath.firstAsObject(member, "collection", "collections");
		if (collectionObj != null) {
			for (Map.Entry<String, com.google.gson.JsonElement> entry : collectionObj.entrySet()) {
				if (entry.getValue().isJsonPrimitive()) {
					collections.put(entry.getKey(), entry.getValue().getAsLong());
				}
			}
		}

		List<PetData> pets = new ArrayList<>();
		JsonArray petsArray = JsonPath.firstAsArray(member, "pets_data.pets", "pets");
		if (petsArray != null) {
			for (var el : petsArray) {
				if (el.isJsonObject()) {
					pets.add(PetData.parse(el.getAsJsonObject()));
				}
			}
		}

		Map<String, List<SkyblockItem>> inventories = new LinkedHashMap<>();
		putInventory(inventories, member, "inventory", "inventory.inv_contents.data", "inv_contents.data");
		putInventory(inventories, member, "armor", "inventory.inv_armor.data", "inv_armor.data");
		putInventory(inventories, member, "ender_chest", "inventory.ender_chest_contents.data", "ender_chest_contents.data");
		putInventory(inventories, member, "wardrobe", "inventory.wardrobe_contents.data", "wardrobe_contents.data");
		putInventory(inventories, member, "accessory_bag", "inventory.bag_contents.talisman_bag.data", "talisman_bag.data");
		putInventory(inventories, member, "personal_vault", "inventory.personal_vault_contents.data", "personal_vault_contents.data");

		double slayerXp = 0;
		JsonObject slayerBosses = JsonPath.firstAsObject(member, "slayer.slayer_bosses", "slayer_bosses");
		if (slayerBosses != null) {
			for (String boss : slayerBosses.keySet()) {
				var bossObj = slayerBosses.get(boss);
				if (bossObj.isJsonObject() && bossObj.getAsJsonObject().has("xp")) {
					slayerXp += bossObj.getAsJsonObject().get("xp").getAsDouble();
				}
			}
		}

		return new ProfileMember(uuid, coinPurse, skyblockXp, slayerXp, skills, collections, pets, inventories);
	}

	private static void putInventory(Map<String, List<SkyblockItem>> out, JsonObject member, String key, String... candidatePaths) {
		String data = JsonPath.firstAsString(member, null, candidatePaths);
		out.put(key, data != null ? ItemDecoder.decode(data) : List.of());
	}
}
