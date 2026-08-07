package dev.fishmodpv.gui.tab;

import dev.fishmodpv.gui.GuiTheme;
import dev.fishmodpv.gui.GuiUtil;
import dev.fishmodpv.nbt.SkyblockItem;
import dev.fishmodpv.profile.ProfileMember;
import dev.fishmodpv.profile.ProfileService;
import dev.fishmodpv.profile.SkyblockProfile;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

import java.util.List;

/** Armor + main inventory + ender chest, laid out like the vanilla inventory screen. */
public class InventoryTab implements ProfileTab {
	@Override
	public String title() {
		return "Inventory";
	}

	@Override
	public SkyblockItem render(GuiGraphics graphics, Font font, ProfileService.Result data,
			int x, int y, int width, int height, int mouseX, int mouseY) {
		SkyblockProfile profile = data.selectedOrFirst();
		if (profile == null) {
			return null;
		}
		ProfileMember member = profile.members.get(data.uuid());
		if (member == null) {
			graphics.drawString(font, Component.literal("No inventory data (co-op member privacy settings?)"), x, y, GuiTheme.TEXT_GRAY, false);
			return null;
		}

		SkyblockItem hovered = null;

		// Armor column: stored boots -> helmet, displayed helmet on top like the vanilla screen.
		List<SkyblockItem> armor = member.inventories.getOrDefault("armor", List.of());
		graphics.drawString(font, Component.literal("Armor"), x, y, GuiTheme.TEXT_GRAY, false);
		int armorY = y + 11;
		for (int i = armor.size() - 1; i >= 0; i--) {
			SkyblockItem item = armor.get(i);
			int slotY = armorY + (armor.size() - 1 - i) * GuiUtil.SLOT_SIZE;
			if (GuiUtil.slot(graphics, font, x, slotY, item, mouseX, mouseY) && item != null) {
				hovered = item;
			}
		}

		int invX = x + GuiUtil.SLOT_SIZE + 8;
		graphics.drawString(font, Component.literal("Inventory"), invX, y, GuiTheme.TEXT_GRAY, false);
		List<SkyblockItem> inv = member.inventories.getOrDefault("inventory", List.of());
		int mainRowsCount = Math.max(0, inv.size() - 9);
		List<SkyblockItem> mainRows = inv.subList(0, Math.min(mainRowsCount, inv.size()));
		List<SkyblockItem> hotbar = inv.size() > 9 ? inv.subList(inv.size() - 9, inv.size()) : inv;

		int invY = y + 11;
		SkyblockItem hoveredMain = GuiUtil.itemGrid(graphics, font, invX, invY, 9, mainRows, mouseX, mouseY);
		if (hoveredMain != null) {
			hovered = hoveredMain;
		}
		int hotbarY = invY + ((mainRows.size() + 8) / 9) * GuiUtil.SLOT_SIZE + 4;
		SkyblockItem hoveredHotbar = GuiUtil.itemGrid(graphics, font, invX, hotbarY, 9, hotbar, mouseX, mouseY);
		if (hoveredHotbar != null) {
			hovered = hoveredHotbar;
		}

		// Ender chest, off to the right if there's room.
		int enderX = invX + 9 * GuiUtil.SLOT_SIZE + 12;
		if (enderX + 9 * GuiUtil.SLOT_SIZE <= x + width) {
			graphics.drawString(font, Component.literal("Ender Chest"), enderX, y, GuiTheme.TEXT_GRAY, false);
			List<SkyblockItem> enderChest = member.inventories.getOrDefault("ender_chest", List.of());
			SkyblockItem hoveredEnder = GuiUtil.itemGrid(graphics, font, enderX, y + 11, 9, enderChest, mouseX, mouseY);
			if (hoveredEnder != null) {
				hovered = hoveredEnder;
			}
		}

		return hovered;
	}
}
