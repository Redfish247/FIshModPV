package dev.fishmodpv.gui;

/**
 * Palette modeled directly on real Hypixel SkyBlock profile viewers rather than an invented
 * look: a light "vanilla GUI" grey body (like NEU's classic profile viewer), sectioned into
 * purple-headed cards (like meowdding/skyblock-pv's default theme), with proper light/dark
 * bevelled item slots and bright green progress bars. Colours were sampled from screenshots in
 * notenoughupdates/notenoughupdates and meowdding/skyblock-pv.
 */
public final class GuiTheme {
	// Outer panel body.
	public static final int PANEL_BG = 0xFFE3E3EA;
	public static final int PANEL_BORDER_DARK = 0xFF37373F;
	public static final int PANEL_BORDER_LIGHT = 0xFFFFFFFF;

	// Card (section) body, nested inside the panel.
	public static final int CARD_BG = 0xFFC6C6D0;
	public static final int CARD_BORDER = 0xFF8B8B99;

	// Section header bar (purple, like "Info" / "Skills" / "Slayer" headers).
	public static final int HEADER_PURPLE_BG = 0xFFC49CF2;
	public static final int HEADER_PURPLE_BORDER = 0xFF7C4FC2;
	public static final int HEADER_TEXT = 0xFF2B2A38;

	// Vanilla-style inventory slot bevel.
	public static final int SLOT_BG = 0xFF8B8B93;
	public static final int SLOT_BG_HOVER = 0xFFA3A3AD;
	public static final int SLOT_BORDER_DARK = 0xFF373740;
	public static final int SLOT_HIGHLIGHT = 0xFFC6C6D0;
	public static final int SLOT_SHADOW = 0xFF55555E;

	// Top icon-tab row.
	public static final int TAB_BG = 0xFFC6C6D0;
	public static final int TAB_BG_ACTIVE = 0xFFEDEDF2;
	public static final int TAB_BG_HOVER = 0xFFD6D6E0;
	public static final int TAB_BORDER = 0xFF8B8B99;
	public static final int TAB_INDICATOR = 0xFF38B653;

	public static final int TEXT_DARK = 0xFF2B2A38;
	public static final int TEXT_MUTED = 0xFF5B5A68;
	public static final int TEXT_GOLD = 0xFFB9740A;
	public static final int TEXT_RED = 0xFFB53434;
	public static final int TEXT_GREEN = 0xFF1F7A2C;

	public static final int BAR_BG = 0xFF3A3A42;
	public static final int BAR_FILL = 0xFF38B653;
	public static final int BAR_BORDER = 0xFF232228;

	private GuiTheme() {
	}

	/** Scales an already-packed ARGB colour's alpha channel by a 0-1 fraction. */
	public static int scaleAlpha(int argb, float fraction) {
		int alpha = Math.round(((argb >>> 24) & 0xFF) * Math.max(0, Math.min(1, fraction)));
		return (alpha << 24) | (argb & 0xFFFFFF);
	}

	public static int rarityColor(String rarity) {
		if (rarity == null) {
			return TEXT_DARK;
		}
		return switch (rarity) {
			case "COMMON" -> 0xFF3A3A42;
			case "UNCOMMON" -> 0xFF2FA83B;
			case "RARE" -> 0xFF3A5FE0;
			case "EPIC" -> 0xFF9A2FE0;
			case "LEGENDARY" -> 0xFFB9740A;
			case "MYTHIC" -> 0xFFC93FC0;
			case "DIVINE" -> 0xFF1FA8B8;
			case "SPECIAL", "VERY SPECIAL" -> 0xFFB53434;
			default -> TEXT_DARK;
		};
	}
}
