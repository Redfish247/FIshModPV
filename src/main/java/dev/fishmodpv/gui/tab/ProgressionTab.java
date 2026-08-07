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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/**
 * A SkyPrizm-style "how far along is this profile" summary. This is a simple starting formula
 * (skill average + SkyBlock level + slayer XP, normalized against rough soft-caps) - not a
 * reverse-engineering of SkyPrizm's actual scoring, which isn't public. Treat the number as
 * a relative indicator, not an authoritative ranking.
 */
public class ProgressionTab implements ProfileTab {
	private static final double SLAYER_XP_SOFT_CAP = 1_000_000;

	@Override
	public String title() {
		return "Progression";
	}

	@Override
	public ItemStack icon() {
		return new ItemStack(Items.NETHER_STAR);
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

		int contentY = GuiUtil.card(graphics, font, x, y, width, height, "Progression");

		double skillScore = Math.min(1.0, member.skillAverage() / 60.0);
		double levelScore = Math.min(1.0, member.skyblockLevel() / 40.0);
		double slayerScore = Math.min(1.0, member.totalSlayerXp / SLAYER_XP_SOFT_CAP);
		double overall = (skillScore * 0.5 + levelScore * 0.25 + slayerScore * 0.25) * 100.0;

		int lineY = contentY;
		graphics.drawString(font, Component.literal(String.format("Score: %.1f / 100", overall)), x + 5, lineY, GuiTheme.TEXT_GOLD, false);
		lineY += 12;
		GuiUtil.progressBar(graphics, x + 5, lineY, width - 10, 6, (float) (overall / 100.0), GuiTheme.BAR_FILL);
		lineY += 14;

		lineY = drawMetric(graphics, font, x + 5, lineY, "Skill Average", member.skillAverage(), 60, "");
		lineY = drawMetric(graphics, font, x + 5, lineY, "SkyBlock Level", member.skyblockLevel(), 40, "");
		lineY = drawMetric(graphics, font, x + 5, lineY, "Total Slayer XP", member.totalSlayerXp, SLAYER_XP_SOFT_CAP, " xp");

		lineY += 6;
		graphics.drawString(font, Component.literal("Roadmap: catacombs level, sacks, minions and"), x + 5, lineY, GuiTheme.TEXT_MUTED, false);
		lineY += 10;
		graphics.drawString(font, Component.literal("net worth will feed into this score in a later pass."), x + 5, lineY, GuiTheme.TEXT_MUTED, false);

		return null;
	}

	private static int drawMetric(GuiGraphics graphics, Font font, int x, int y, String label, double value, double softCap, String suffix) {
		graphics.drawString(font, Component.literal(String.format("%s: %,.1f%s", label, value, suffix)), x, y, GuiTheme.TEXT_DARK, false);
		y += 9;
		GuiUtil.progressBar(graphics, x, y, 150, 4, (float) Math.min(1.0, value / softCap), GuiTheme.BAR_FILL);
		return y + 12;
	}
}
