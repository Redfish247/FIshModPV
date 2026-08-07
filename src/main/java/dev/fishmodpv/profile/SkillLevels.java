package dev.fishmodpv.profile;

/**
 * Cumulative XP-to-level table for SkyBlock's "standard" skill curve, shared by Combat, Mining,
 * Farming, Foraging, Fishing, Enchanting, Alchemy and Taming (Runecrafting and Social use their
 * own, much shorter tables and are intentionally not covered here - see {@link SkillData}).
 *
 * <p>Values below level 50 are the long-standing, widely-documented community table. Levels 51-60
 * (the later Farming/Mining extension) are approximate best-effort figures and may drift slightly
 * from Hypixel's actual values - treat the displayed level for very high-level players as "close
 * enough", not authoritative.
 */
public final class SkillLevels {
	// Index i = cumulative XP required to reach level (i + 1).
	private static final long[] CUMULATIVE_XP = {
			50, 175, 375, 675, 1_175, 1_925, 2_925, 4_425, 6_425, 9_925,
			14_925, 22_425, 32_425, 47_425, 67_425, 97_425, 147_425, 222_425, 322_425, 522_425,
			822_425, 1_222_425, 1_722_425, 2_322_425, 3_022_425, 3_822_425, 4_722_425, 5_722_425, 6_822_425, 8_022_425,
			9_322_425, 10_722_425, 12_222_425, 13_822_425, 15_522_425, 17_322_425, 19_222_425, 21_222_425, 23_322_425, 25_522_425,
			27_822_425, 30_222_425, 32_722_425, 35_322_425, 38_072_425, 40_972_425, 44_022_425, 47_222_425, 50_572_425, 54_072_425,
			57_722_425, 61_522_425, 65_472_425, 69_572_425, 73_822_425, 78_222_425, 82_772_425, 87_472_425, 92_322_425, 97_322_425,
	};

	public static final int MAX_LEVEL = CUMULATIVE_XP.length;

	private SkillLevels() {
	}

	/** Returns [level (0-60), xp progress into current level, xp needed for next level] or -1 for the last field if maxed. */
	public static LevelProgress fromTotalXp(double totalXp, int skillMaxLevel) {
		int level = 0;
		for (int i = 0; i < Math.min(skillMaxLevel, CUMULATIVE_XP.length); i++) {
			if (totalXp >= CUMULATIVE_XP[i]) {
				level = i + 1;
			} else {
				break;
			}
		}

		if (level >= skillMaxLevel) {
			return new LevelProgress(level, 0, -1);
		}

		double previousThreshold = level == 0 ? 0 : CUMULATIVE_XP[level - 1];
		double nextThreshold = CUMULATIVE_XP[level];
		return new LevelProgress(level, totalXp - previousThreshold, nextThreshold - previousThreshold);
	}

	public record LevelProgress(int level, double xpIntoLevel, double xpForNextLevel) {
		public float progressFraction() {
			if (xpForNextLevel <= 0) {
				return 1f;
			}
			return (float) Math.max(0, Math.min(1, xpIntoLevel / xpForNextLevel));
		}
	}
}
