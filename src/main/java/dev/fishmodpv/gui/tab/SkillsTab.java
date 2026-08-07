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

public class SkillsTab implements ProfileTab {
	@Override
	public String title() {
		return "Skills";
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

		int rowHeight = 24;
		int col = 0;
		int row = 0;
		int colWidth = width / 2;

		for (SkillType type : SkillType.values()) {
			SkillData skill = member.skills.get(type);
			int rowX = x + col * colWidth;
			int rowY = y + row * rowHeight;

			String levelLabel = skill.type().hasKnownLevelTable()
					? type.displayName + " " + skill.level()
					: type.displayName + " (xp only)";
			graphics.drawString(font, Component.literal(levelLabel), rowX, rowY, GuiTheme.TEXT_WHITE, false);

			String xpLabel = skill.type().hasKnownLevelTable() && skill.progress().xpForNextLevel() > 0
					? String.format("%,.0f / %,.0f xp", skill.progress().xpIntoLevel(), skill.progress().xpForNextLevel())
					: String.format("%,.0f xp total", skill.totalXp());
			graphics.drawString(font, Component.literal(xpLabel), rowX, rowY + 9, GuiTheme.TEXT_GRAY, false);

			if (skill.type().hasKnownLevelTable()) {
				GuiUtil.progressBar(graphics, rowX, rowY + 18, colWidth - 10, 3, skill.progress().progressFraction(), GuiTheme.BAR_FILL);
			}

			row++;
			if (row * rowHeight > height - rowHeight) {
				row = 0;
				col++;
			}
		}

		return null;
	}
}
