package dev.fishmodpv.gui.tab;

import dev.fishmodpv.gui.GuiTheme;
import dev.fishmodpv.gui.GuiUtil;
import dev.fishmodpv.nbt.SkyblockItem;
import dev.fishmodpv.profile.ProfileMember;
import dev.fishmodpv.profile.ProfileService;
import dev.fishmodpv.profile.SkillData;
import dev.fishmodpv.profile.SkillType;
import dev.fishmodpv.profile.SkyblockProfile;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class SkillsTab implements ProfileTab {
	@Override
	public String title() {
		return "Skills";
	}

	@Override
	public ItemStack icon() {
		return new ItemStack(Items.EXPERIENCE_BOTTLE);
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
			return null;
		}

		int contentY = GuiUtil.card(graphics, font, x, y, width, height, "Skills");

		int rowHeight = 26;
		int col = 0;
		int row = 0;
		int colWidth = width / 2 - 6;

		for (SkillType type : SkillType.values()) {
			SkillData skill = member.skills.get(type);
			int rowX = x + 5 + col * (colWidth + 6);
			int rowY = contentY + row * rowHeight;

			String levelLabel = skill.type().hasKnownLevelTable()
					? type.displayName + " " + skill.level()
					: type.displayName + " (xp only)";
			graphics.drawString(font, Component.literal(levelLabel), rowX, rowY, GuiTheme.TEXT_DARK, false);

			boolean maxed = skill.type().hasKnownLevelTable() && skill.progress().xpForNextLevel() <= 0;
			String xpLabel;
			if (maxed) {
				xpLabel = "Maxed";
			} else if (skill.type().hasKnownLevelTable()) {
				xpLabel = String.format("%,.0f / %,.0f xp", skill.progress().xpIntoLevel(), skill.progress().xpForNextLevel());
			} else {
				xpLabel = String.format("%,.0f xp total", skill.totalXp());
			}
			graphics.drawString(font, Component.literal(xpLabel), rowX, rowY + 9, maxed ? GuiTheme.TEXT_GREEN : GuiTheme.TEXT_MUTED, false);

			if (skill.type().hasKnownLevelTable()) {
				GuiUtil.progressBar(graphics, rowX, rowY + 19, colWidth - 4, 4, skill.progress().progressFraction(), GuiTheme.BAR_FILL);
			}

			row++;
			if (contentY + row * rowHeight > y + height - rowHeight) {
				row = 0;
				col++;
			}
		}

		return null;
	}
}
