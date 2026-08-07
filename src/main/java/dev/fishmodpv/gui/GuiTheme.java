package dev.fishmodpv.gui;

/**
 * "Liquid glass" palette: cool-toned translucent panels with soft gradients instead of flat
 * colours, gold accents for headers/active state (a nod to old NEU), evaluated purely through
 * {@link net.minecraft.client.gui.GuiGraphics#fillGradient} so every surface is a smooth,
 * GPU-interpolated blend - never a blurred/resampled texture, so it never looks pixelated.
 */
public final class GuiTheme {
	// Glass panel body: cool blue-grey, slightly more opaque at the top than the bottom.
	public static final int GLASS_TOP_RGB = 0x3A4250;
	public static final int GLASS_BOTTOM_RGB = 0x14161C;
	public static final int GLASS_TOP_ALPHA = 0xB8;
	public static final int GLASS_BOTTOM_ALPHA = 0x92;

	// A faint bright streak across the top of a panel, like light catching glass.
	public static final int GLASS_HIGHLIGHT_RGB = 0xFFFFFF;

	public static final int PANEL_BORDER = 0x80AEE0FF;
	public static final int HEADER_BG_TOP = 0x50FFFFFF;
	public static final int HEADER_BG_BOTTOM = 0x00FFFFFF;

	public static final int TAB_TEXT_ACTIVE = 0xFFFFAA00;
	public static final int TAB_INDICATOR = 0xFFFFC847;

	public static final int SLOT_BG = 0x552C333E;
	public static final int SLOT_BORDER = 0x60AEE0FF;
	public static final int SLOT_BG_HOVER = 0x80435068;

	public static final int TEXT_GOLD = 0xFFFFAA00;
	public static final int TEXT_WHITE = 0xFFFFFFFF;
	public static final int TEXT_GRAY = 0xFFAAAAAA;
	public static final int TEXT_GREEN = 0xFF55FF55;
	public static final int TEXT_RED = 0xFFFF5555;
	public static final int BAR_BG = 0x801A1A1A;
	public static final int BAR_FILL = 0xFF55FF9E;

	private GuiTheme() {
	}

	/** Packs an 0xRRGGBB colour with the given alpha (0-255) into ARGB. */
	public static int withAlpha(int rgb, int alpha) {
		return ((alpha & 0xFF) << 24) | (rgb & 0xFFFFFF);
	}

	/** Scales an already-packed ARGB colour's alpha channel by a 0-1 fraction. */
	public static int scaleAlpha(int argb, float fraction) {
		int alpha = Math.round(((argb >>> 24) & 0xFF) * Math.max(0, Math.min(1, fraction)));
		return (alpha << 24) | (argb & 0xFFFFFF);
	}

	public static int rarityColor(String rarity) {
		if (rarity == null) {
			return TEXT_WHITE;
		}
		return switch (rarity) {
			case "COMMON" -> 0xFFFFFFFF;
			case "UNCOMMON" -> 0xFF55FF55;
			case "RARE" -> 0xFF5555FF;
			case "EPIC" -> 0xFFAA00AA;
			case "LEGENDARY" -> 0xFFFFAA00;
			case "MYTHIC" -> 0xFFFF55FF;
			case "DIVINE" -> 0xFF55FFFF;
			case "SPECIAL", "VERY SPECIAL" -> 0xFFFF5555;
			default -> TEXT_WHITE;
		};
	}
}
