package dev.fishmodpv.gui.tab;

import dev.fishmodpv.gui.GuiTheme;
import dev.fishmodpv.nbt.SkyblockItem;
import dev.fishmodpv.profile.ProfileMember;
import dev.fishmodpv.profile.ProfileService;
import dev.fishmodpv.profile.SkyblockProfile;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

import java.util.UUID;

public class OverviewTab implements ProfileTab {
	@Override
	public String title() {
		return "Overview";
	}

	@Override
	public SkyblockItem render(GuiGraphics graphics, Font font, ProfileService.Result data,
			int x, int y, int width, int height, int mouseX, int mouseY) {
		SkyblockProfile profile = data.selectedOrFirst();
		if (profile == null) {
			graphics.drawString(font, Component.literal("This player has no SkyBlock profiles."), x, y, GuiTheme.TEXT_GRAY, false);
			return null;
		}
		ProfileMember member = profile.members.get(data.uuid());
		int lineY = y;

		graphics.drawString(font, Component.literal(data.correctedName()).withStyle(ChatFormatting.BOLD), x, lineY, GuiTheme.TEXT_GOLD, false);
		lineY += 12;
		graphics.drawString(font, Component.literal("Profile: " + profile.cuteName + "  (" + profile.gameMode + ")"), x, lineY, GuiTheme.TEXT_WHITE, false);
		lineY += 14;

		if (member != null) {
			graphics.drawString(font, Component.literal(String.format("SkyBlock Level: %.0f", member.skyblockLevel())), x, lineY, GuiTheme.TEXT_WHITE, false);
			lineY += 11;
			graphics.drawString(font, Component.literal(String.format("Skill Average: %.1f", member.skillAverage())), x, lineY, GuiTheme.TEXT_WHITE, false);
			lineY += 11;
			graphics.drawString(font, Component.literal(String.format("Purse: %,.0f coins", member.coinPurse)), x, lineY, GuiTheme.TEXT_GOLD, false);
			lineY += 11;
			graphics.drawString(font, Component.literal(String.format("Bank: %,.0f coins", profile.bankBalance)), x, lineY, GuiTheme.TEXT_GOLD, false);
			lineY += 16;
		}

		graphics.drawString(font, Component.literal("Members on this profile:"), x, lineY, GuiTheme.TEXT_GRAY, false);
		lineY += 11;
		for (UUID uuid : profile.members.keySet()) {
			String label = uuid.equals(data.uuid()) ? data.correctedName() + " (viewing)" : uuid.toString().substring(0, 8) + "...";
			graphics.drawString(font, Component.literal("- " + label), x + 4, lineY, GuiTheme.TEXT_WHITE, false);
			lineY += 10;
		}

		return null;
	}
}
