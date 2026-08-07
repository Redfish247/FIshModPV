package dev.fishmodpv.gui.tab;

import dev.fishmodpv.gui.GuiTheme;
import dev.fishmodpv.nbt.SkyblockItem;
import dev.fishmodpv.profile.PetData;
import dev.fishmodpv.profile.ProfileMember;
import dev.fishmodpv.profile.ProfileService;
import dev.fishmodpv.profile.SkyblockProfile;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

public class PetsTab implements ProfileTab {
	@Override
	public String title() {
		return "Pets";
	}

	@Override
	public SkyblockItem render(DrawContext context, TextRenderer textRenderer, ProfileService.Result data,
			int x, int y, int width, int height, int mouseX, int mouseY) {
		SkyblockProfile profile = data.selectedOrFirst();
		if (profile == null) {
			return null;
		}
		ProfileMember member = profile.members.get(data.uuid());
		if (member == null || member.pets.isEmpty()) {
			context.drawText(textRenderer, Text.literal("No pets found on this profile."), x, y, GuiTheme.TEXT_GRAY, false);
			return null;
		}

		// Pet XP-to-level curves differ per rarity and are not included in this pass, so we show
		// raw XP + tier rather than a guessed level.
		int rowHeight = 11;
		int rowY = y;
		for (PetData pet : member.pets) {
			if (rowY + rowHeight > y + height) {
				context.drawText(textRenderer, Text.literal("..."), x, rowY, GuiTheme.TEXT_GRAY, false);
				break;
			}
			int color = GuiTheme.rarityColor(pet.tier());
			String activeMarker = pet.active() ? " [Active]" : "";
			String held = pet.heldItem() != null ? "  (" + prettify(pet.heldItem()) + ")" : "";
			context.drawText(textRenderer, Text.literal(String.format("%s %s - %,.0f xp%s%s",
					pet.tier(), pet.displayName(), pet.exp(), activeMarker, held)), x, rowY, color, false);
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
