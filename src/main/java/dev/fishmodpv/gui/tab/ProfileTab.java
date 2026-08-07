package dev.fishmodpv.gui.tab;

import dev.fishmodpv.nbt.SkyblockItem;
import dev.fishmodpv.profile.ProfileService;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;

/** One tab of the NEU-style profile viewer (Overview, Skills, Inventory, ...). */
public interface ProfileTab {
	String title();

	/**
	 * Draws the tab's content within the given content-area bounds and returns the SkyBlock item
	 * currently under the mouse (if any), so the screen can draw its tooltip on top afterwards.
	 */
	SkyblockItem render(GuiGraphics graphics, Font font, ProfileService.Result data,
			int x, int y, int width, int height, int mouseX, int mouseY);

	default void mouseScrolled(double mouseX, double mouseY, double amount) {
	}
}
