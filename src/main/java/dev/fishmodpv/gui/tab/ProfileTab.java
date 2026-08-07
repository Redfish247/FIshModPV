package dev.fishmodpv.gui.tab;

import dev.fishmodpv.nbt.SkyblockItem;
import dev.fishmodpv.profile.ProfileService;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/** One tab of the profile viewer (Overview, Skills, Inventory, ...). */
public interface ProfileTab {
	String title();

	/** The vanilla item drawn as this tab's icon button, NEU/skyblock-pv style. */
	default ItemStack icon() {
		return new ItemStack(Items.BARRIER);
	}

	/**
	 * Draws the tab's content within the given content-area bounds and returns the SkyBlock item
	 * currently under the mouse (if any), so the screen can draw its tooltip on top afterwards.
	 */
	SkyblockItem render(GuiGraphics graphics, Font font, ProfileService.Result data,
			int x, int y, int width, int height, int mouseX, int mouseY);

	default void mouseScrolled(double mouseX, double mouseY, double amount) {
	}
}
