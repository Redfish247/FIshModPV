package dev.fishmodpv.gui;

import dev.fishmodpv.nbt.SkyblockItem;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;

import java.util.List;

/** Small drawing helpers shared by every profile-viewer tab. */
public final class GuiUtil {
	public static final int SLOT_SIZE = 18;

	private GuiUtil() {
	}

	public static void border(GuiGraphics graphics, int x, int y, int w, int h, int color) {
		graphics.fill(x, y, x + w, y + 1, color);
		graphics.fill(x, y + h - 1, x + w, y + h, color);
		graphics.fill(x, y, x + 1, y + h, color);
		graphics.fill(x + w - 1, y, x + w, y + h, color);
	}

	public static void panel(GuiGraphics graphics, int x, int y, int w, int h) {
		glassPanel(graphics, x, y, w, h, 1f);
	}

	/**
	 * The "liquid glass" panel body: a smooth top-to-bottom gradient (translucent cool grey-blue,
	 * lighter/more opaque at the top) plus a thin bright highlight streak along the top edge and a
	 * soft glowing border - all {@link GuiGraphics#fillGradient} calls, so it stays perfectly smooth
	 * at any resolution instead of a resampled/blurred texture.
	 *
	 * @param openFraction 0-1 fade/reveal amount, driven by an opening animation.
	 */
	public static void glassPanel(GuiGraphics graphics, int x, int y, int w, int h, float openFraction) {
		int top = GuiTheme.withAlpha(GuiTheme.GLASS_TOP_RGB, Math.round(GuiTheme.GLASS_TOP_ALPHA * openFraction));
		int bottom = GuiTheme.withAlpha(GuiTheme.GLASS_BOTTOM_RGB, Math.round(GuiTheme.GLASS_BOTTOM_ALPHA * openFraction));
		graphics.fillGradient(x, y, x + w, y + h, top, bottom);

		int highlightHeight = Math.max(1, h / 5);
		int highlightTop = GuiTheme.withAlpha(GuiTheme.GLASS_HIGHLIGHT_RGB, Math.round(0x45 * openFraction));
		int highlightBottom = GuiTheme.withAlpha(GuiTheme.GLASS_HIGHLIGHT_RGB, 0);
		graphics.fillGradient(x, y, x + w, y + highlightHeight, highlightTop, highlightBottom);

		softCorners(graphics, x, y, w, h, openFraction);
		border(graphics, x, y, w, h, GuiTheme.scaleAlpha(GuiTheme.PANEL_BORDER, openFraction));
	}

	/** Nibbles the four corners with a small stairstep so the sharp square panel reads as gently rounded. */
	private static void softCorners(GuiGraphics graphics, int x, int y, int w, int h, float openFraction) {
		int cut = GuiTheme.scaleAlpha(0xC0000000, openFraction);
		// Top-left / top-right / bottom-left / bottom-right stairsteps (radius 3).
		for (int i = 0; i < 3; i++) {
			int inset = 3 - i;
			graphics.fill(x, y + i, x + inset, y + i + 1, cut);
			graphics.fill(x + w - inset, y + i, x + w, y + i + 1, cut);
			graphics.fill(x, y + h - 1 - i, x + inset, y + h - i, cut);
			graphics.fill(x + w - inset, y + h - 1 - i, x + w, y + h - i, cut);
		}
	}

	/** Draws one inventory-style slot with its item (if any) and returns true if the mouse is over it. */
	public static boolean slot(GuiGraphics graphics, Font font, int x, int y, SkyblockItem item, int mouseX, int mouseY) {
		boolean hovered = mouseX >= x && mouseX < x + SLOT_SIZE && mouseY >= y && mouseY < y + SLOT_SIZE;
		graphics.fill(x, y, x + SLOT_SIZE, y + SLOT_SIZE, hovered ? GuiTheme.SLOT_BG_HOVER : GuiTheme.SLOT_BG);
		border(graphics, x, y, SLOT_SIZE, SLOT_SIZE, GuiTheme.SLOT_BORDER);
		if (item != null) {
			graphics.renderItem(item.renderStack(), x + 1, y + 1);
			graphics.renderItemDecorations(font, item.renderStack(), x + 1, y + 1);
		}
		return hovered;
	}

	/**
	 * Renders a grid of items starting at (x, y), {@code columns} wide, 18px slots. Returns the
	 * item under the mouse, if any, so the caller can render a tooltip for it after everything
	 * else has been drawn.
	 */
	public static SkyblockItem itemGrid(GuiGraphics graphics, Font font, int x, int y,
			int columns, List<SkyblockItem> items, int mouseX, int mouseY) {
		SkyblockItem hoveredItem = null;
		for (int i = 0; i < items.size(); i++) {
			int col = i % columns;
			int row = i / columns;
			int slotX = x + col * SLOT_SIZE;
			int slotY = y + row * SLOT_SIZE;
			SkyblockItem item = items.get(i);
			if (slot(graphics, font, slotX, slotY, item, mouseX, mouseY) && item != null) {
				hoveredItem = item;
			}
		}
		return hoveredItem;
	}

	public static void progressBar(GuiGraphics graphics, int x, int y, int width, int height, float fraction, int fillColor) {
		graphics.fill(x, y, x + width, y + height, GuiTheme.BAR_BG);
		int filled = Math.round(width * Math.max(0, Math.min(1, fraction)));
		if (filled > 0) {
			graphics.fill(x, y, x + filled, y + height, fillColor);
		}
		border(graphics, x, y, width, height, GuiTheme.PANEL_BORDER);
	}
}
