package dev.fishmodpv.profile;

/** The skills SkyBlock tracks. XP field paths cover both the modern (2023+) and legacy Hypixel API schemas. */
public enum SkillType {
	FARMING("Farming", "SKILL_FARMING", "experience_skill_farming", 60),
	MINING("Mining", "SKILL_MINING", "experience_skill_mining", 60),
	COMBAT("Combat", "SKILL_COMBAT", "experience_skill_combat", 60),
	FORAGING("Foraging", "SKILL_FORAGING", "experience_skill_foraging", 50),
	FISHING("Fishing", "SKILL_FISHING", "experience_skill_fishing", 50),
	ENCHANTING("Enchanting", "SKILL_ENCHANTING", "experience_skill_enchanting", 60),
	ALCHEMY("Alchemy", "SKILL_ALCHEMY", "experience_skill_alchemy", 50),
	TAMING("Taming", "SKILL_TAMING", "experience_skill_taming", 50),
	CARPENTRY("Carpentry", "SKILL_CARPENTRY", "experience_skill_carpentry", 50),
	// Runecrafting/Social use a shorter, distinct XP curve we don't have table data for yet,
	// so we only show raw XP for these two rather than guessing at level numbers.
	RUNECRAFTING("Runecrafting", "SKILL_RUNECRAFTING", "experience_skill_runecrafting", 25),
	SOCIAL("Social", "SKILL_SOCIAL2", "experience_skill_social2", 25);

	public final String displayName;
	public final String modernKey;
	public final String legacyKey;
	public final int maxLevel;

	SkillType(String displayName, String modernKey, String legacyKey, int maxLevel) {
		this.displayName = displayName;
		this.modernKey = modernKey;
		this.legacyKey = legacyKey;
		this.maxLevel = maxLevel;
	}

	public boolean hasKnownLevelTable() {
		return this != RUNECRAFTING && this != SOCIAL;
	}
}
