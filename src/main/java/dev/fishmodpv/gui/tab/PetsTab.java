package dev.fishmodpv.gui.tab;

import dev.fishmodpv.gui.GuiTheme;
import dev.fishmodpv.gui.GuiUtil;
import dev.fishmodpv.nbt.SkyblockItem;
import dev.fishmodpv.profile.PetData;
import dev.fishmodpv.profile.ProfileMember;
import dev.fishmodpv.profile.ProfileService;
import dev.fishmodpv.profile.SkyblockProfile;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class PetsTab implements ProfileTab {
	@Override
	public String title() {
		return "Pets";
	}

	@Override
	public ItemStack icon() {
		return new ItemStack(Items.BONE);
	}

	@Override
	public SkyblockItem render(GuiGraphics graphics, Font font, ProfileService.Result data,
			int x, int y, int width, int height, int mouseX, int mouseY) {
		SkyblockProfile profile = data.selectedOrFirst();
		if (profile == null) {
			return null;
		}
		ProfileMember member = profile.members.get(data.uuid());

		int contentY = GuiUtil.card(graphics, font, x, y, width, height, "Pets");
		if (member == null || member.pets.isEmpty()) {
			graphics.drawString(font, Component.literal("No pets found on this profile."), x + 5, contentY, GuiTheme.TEXT_MUTED, false);
			return null;
		}

		// Pet XP-to-level curves differ per rarity and are not included in this pass, so we show
		// raw XP + tier rather than a guessed level.
		int rowHeight = 11;
		int rowY = contentY;
		for (PetData pet : member.pets) {
			if (rowY + rowHeight > y + height) {
				graphics.drawString(font, Component.literal("..."), x + 5, rowY, GuiTheme.TEXT_MUTED, false);
				break;
			}
			int color = GuiTheme.rarityColor(pet.tier());
			String activeMarker = pet.active() ? " [Active]" : "";
			String held = pet.heldItem() != null ? "  (" + prettify(pet.heldItem()) + ")" : "";
			graphics.drawString(font, Component.literal(String.format("%s %s - %,.0f xp%s%s",
					pet.tier(), pet.displayName(), pet.exp(), activeMarker, held)), x + 5, rowY, color, false);
			rowY += rowHeight;
		}

		return null;
	}

	private static String prettify(String id) {
		String[] parts = id.replace(':', ' ').split("_");
		StringBuilder out = new StringBuilder();
		for (String part : parts) {
			if (!part.isEmpty()) {
				out.append(Character.toUpperCase(part.charAt(0))).append(part.substring(1).toLowerCase()).append(' ');
			}
		}
		return out.toString().trim();
	}
}
