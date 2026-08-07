package dev.fishmodpv.gui;

import dev.fishmodpv.nbt.SkyblockItem;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.List;

/** Drawing helpers shared by every profile-viewer tab: light "vanilla GUI" panels, purple-headed cards, and bevelled item slots. */
public final class GuiUtil {
	public static final int SLOT_SIZE = 18;
	private static final int HEADER_HEIGHT = 14;

	private GuiUtil() {
	}

	public static void border(GuiGraphics graphics, int x, int y, int w, int h, int color) {
		graphics.fill(x, y, x + w, y + 1, color);
		graphics.fill(x, y + h - 1, x + w, y + h, color);
		graphics.fill(x, y, x + 1, y + h, color);
		graphics.fill(x + w - 1, y, x + w, y + h, color);
	}

	/** The main window panel: a flat light "vanilla GUI" body with a dark outer / light inner bevel. */
	public static void panel(GuiGraphics graphics, int x, int y, int w, int h, float openFraction) {
		graphics.fill(x, y, x + w, y + h, GuiTheme.scaleAlpha(GuiTheme.PANEL_BG, openFraction));
		border(graphics, x, y, w, h, GuiTheme.scaleAlpha(GuiTheme.PANEL_BORDER_DARK, openFraction));
		graphics.fill(x + 1, y + 1, x + w - 1, y + 2, GuiTheme.scaleAlpha(GuiTheme.PANEL_BORDER_LIGHT, openFraction * 0.5f));
		graphics.fill(x + 1, y + 1, x + 2, y + h - 1, GuiTheme.scaleAlpha(GuiTheme.PANEL_BORDER_LIGHT, openFraction * 0.5f));
	}

	/**
	 * A section "card" like NEU/skyblock-pv's Info/Skills/Slayer boxes: a purple title bar over a
	 * slightly darker body. Returns the y-coordinate content should start being drawn at.
	 */
	public static int card(GuiGraphics graphics, Font font, int x, int y, int w, int h, String title) {
		graphics.fill(x, y, x + w, y + HEADER_HEIGHT, GuiTheme.HEADER_PURPLE_BG);
		border(graphics, x, y, w, HEADER_HEIGHT, GuiTheme.HEADER_PURPLE_BORDER);
		int titleWidth = font.width(title);
		graphics.drawString(font, Component.literal(title), x + (w - titleWidth) / 2, y + 3, GuiTheme.HEADER_TEXT, false);

		int bodyY = y + HEADER_HEIGHT;
		int bodyH = h - HEADER_HEIGHT;
		graphics.fill(x, bodyY, x + w, bodyY + bodyH, GuiTheme.CARD_BG);
		border(graphics, x, bodyY, w, bodyH, GuiTheme.CARD_BORDER);
		return bodyY + 5;
	}

	/** Draws one bevelled inventory-style slot with its item (if any) and returns true if the mouse is over it. */
	public static boolean slot(GuiGraphics graphics, Font font, int x, int y, SkyblockItem item, int mouseX, int mouseY) {
		boolean hovered = mouseX >= x && mouseX < x + SLOT_SIZE && mouseY >= y && mouseY < y + SLOT_SIZE;
		graphics.fill(x, y, x + SLOT_SIZE, y + SLOT_SIZE, GuiTheme.SLOT_BORDER_DARK);
		graphics.fill(x + 1, y + 1, x + SLOT_SIZE - 1, y + SLOT_SIZE - 1, hovered ? GuiTheme.SLOT_BG_HOVER : GuiTheme.SLOT_BG);
		// Inset bevel: light line top+left, dark line bottom+right, like vanilla inventory slots.
		graphics.fill(x + 1, y + 1, x + SLOT_SIZE - 1, y + 2, GuiTheme.SLOT_HIGHLIGHT);
		graphics.fill(x + 1, y + 1, x + 2, y + SLOT_SIZE - 1, GuiTheme.SLOT_HIGHLIGHT);
		graphics.fill(x + 1, y + SLOT_SIZE - 2, x + SLOT_SIZE - 1, y + SLOT_SIZE - 1, GuiTheme.SLOT_SHADOW);
		graphics.fill(x + SLOT_SIZE - 2, y + 1, x + SLOT_SIZE - 1, y + SLOT_SIZE - 1, GuiTheme.SLOT_SHADOW);
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

	/** A compact "icon + big number" stat, like the Skills/Slayer/Essence rows in skyblock-pv. */
	public static void statChip(GuiGraphics graphics, Font font, int x, int y, ItemStack icon, String label, int color) {
		graphics.renderItem(icon, x, y);
		graphics.drawString(font, Component.literal(label), x + 18, y + 4, color, false);
	}

	public static void progressBar(GuiGraphics graphics, int x, int y, int width, int height, float fraction, int fillColor) {
		graphics.fill(x, y, x + width, y + height, GuiTheme.BAR_BG);
		int filled = Math.round(width * Math.max(0, Math.min(1, fraction)));
		if (filled > 0) {
			graphics.fill(x, y, x + filled, y + height, fillColor);
		}
		border(graphics, x, y, width, height, GuiTheme.BAR_BORDER);
	}
}
