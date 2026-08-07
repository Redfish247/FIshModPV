package dev.fishmodpv.profile;

import com.google.gson.JsonObject;
import dev.fishmodpv.util.JsonPath;

/** A single skill's parsed XP/level state for a profile member. */
public record SkillData(SkillType type, double totalXp, SkillLevels.LevelProgress progress) {
	public static SkillData parse(JsonObject member, SkillType type) {
		double xp = JsonPath.firstAsDouble(member, 0,
				"player_data.experience." + type.modernKey,
				type.legacyKey);
		SkillLevels.LevelProgress progress = type.hasKnownLevelTable()
				? SkillLevels.fromTotalXp(xp, type.maxLevel)
				: new SkillLevels.LevelProgress(0, xp, -1);
		return new SkillData(type, xp, progress);
	}

	public int level() {
		return progress.level();
	}
}
