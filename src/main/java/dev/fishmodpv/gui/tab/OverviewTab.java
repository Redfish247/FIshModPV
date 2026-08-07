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

public class OverviewTab implements ProfileTab {
	@Override
	public String title() {
		return "Overview";
	}

	@Override
	public ItemStack icon() {
		return new ItemStack(Items.PLAYER_HEAD);
	}

	@Override
	public SkyblockItem render(GuiGraphics graphics, Font font, ProfileService.Result data,
			int x, int y, int width, int height, int mouseX, int mouseY) {
		SkyblockProfile profile = data.selectedOrFirst();
		if (profile == null) {
			graphics.drawString(font, Component.literal("This player has no SkyBlock profiles."), x, y, GuiTheme.TEXT_MUTED, false);
			return null;
		}
		ProfileMember member = profile.members.get(data.uuid());

		int infoWidth = width / 2 - 4;
		int lineY = GuiUtil.card(graphics, font, x, y, infoWidth, height, "Info");
		lineY = infoLine(graphics, font, x + 5, lineY, "Profile", profile.cuteName + " (" + profile.gameMode + ")");
		if (member != null) {
			lineY = infoLine(graphics, font, x + 5, lineY, "Purse", String.format("%,.0f", member.coinPurse));
			lineY = infoLine(graphics, font, x + 5, lineY, "Bank", String.format("%,.0f", profile.bankBalance));
			lineY = infoLine(graphics, font, x + 5, lineY, "SkyBlock Level", String.format("%.0f", member.skyblockLevel()));
			lineY = infoLine(graphics, font, x + 5, lineY, "Skill Average", String.format("%.1f", member.skillAverage()));
		}
		lineY += 3;
		graphics.drawString(font, Component.literal("Members:"), x + 5, lineY, GuiTheme.TEXT_MUTED, false);
		lineY += 10;
		for (var uuid : profile.members.keySet()) {
			String label = uuid.equals(data.uuid()) ? data.correctedName() + " (viewing)" : uuid.toString().substring(0, 8) + "...";
			graphics.drawString(font, Component.literal("- " + label), x + 8, lineY, GuiTheme.TEXT_DARK, false);
			lineY += 10;
		}

		int skillsX = x + infoWidth + 8;
		int skillsWidth = width - infoWidth - 8;
		if (member != null) {
			int chipY = GuiUtil.card(graphics, font, skillsX, y, skillsWidth, height, "Skills");
			int col = 0;
			int row = 0;
			int chipW = skillsWidth / 3;
			for (SkillType type : SkillType.values()) {
				SkillData skill = member.skills.get(type);
				String label = skill.type().hasKnownLevelTable() ? String.valueOf(skill.level()) : "-";
				GuiUtil.statChip(graphics, font, skillsX + 5 + col * chipW, chipY + row * 16, skillIcon(type), label, GuiTheme.TEXT_DARK);
				col++;
				if (col >= 3) {
					col = 0;
					row++;
				}
			}
		}

		return null;
	}

	private static int infoLine(GuiGraphics graphics, Font font, int x, int y, String label, String value) {
		graphics.drawString(font, Component.literal(label + ": "), x, y, GuiTheme.TEXT_MUTED, false);
		graphics.drawString(font, Component.literal(value), x + font.width(label + ": "), y, GuiTheme.TEXT_GOLD, false);
		return y + 10;
	}

	private static ItemStack skillIcon(SkillType type) {
		return new ItemStack(switch (type) {
			case FARMING -> Items.WHEAT;
			case MINING -> Items.IRON_PICKAXE;
			case COMBAT -> Items.IRON_SWORD;
			case FORAGING -> Items.OAK_LOG;
			case FISHING -> Items.FISHING_ROD;
			case ENCHANTING -> Items.ENCHANTING_TABLE;
			case ALCHEMY -> Items.BREWING_STAND;
			case TAMING -> Items.BONE;
			case CARPENTRY -> Items.CRAFTING_TABLE;
			case RUNECRAFTING -> Items.END_ROD;
			case SOCIAL -> Items.PLAYER_HEAD;
		});
	}
}
