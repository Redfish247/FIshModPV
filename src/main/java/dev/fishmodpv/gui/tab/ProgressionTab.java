package dev.fishmodpv.gui.tab;

import dev.fishmodpv.gui.GuiTheme;
import dev.fishmodpv.gui.GuiUtil;
import dev.fishmodpv.nbt.SkyblockItem;
import dev.fishmodpv.profile.ProfileMember;
import dev.fishmodpv.profile.ProfileService;
import dev.fishmodpv.profile.SkyblockProfile;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

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
	public SkyblockItem render(DrawContext context, TextRenderer textRenderer, ProfileService.Result data,
			int x, int y, int width, int height, int mouseX, int mouseY) {
		SkyblockProfile profile = data.selectedOrFirst();
		if (profile == null) {
			return null;
		}
		ProfileMember member = profile.members.get(data.uuid());
		if (member == null) {
			return null;
		}

		double skillScore = Math.min(1.0, member.skillAverage() / 60.0);
		double levelScore = Math.min(1.0, member.skyblockLevel() / 40.0);
		double slayerScore = Math.min(1.0, member.totalSlayerXp / SLAYER_XP_SOFT_CAP);
		double overall = (skillScore * 0.5 + levelScore * 0.25 + slayerScore * 0.25) * 100.0;

		int lineY = y;
		context.drawText(textRenderer, Text.literal(String.format("Progression Score: %.1f / 100", overall)), x, lineY, GuiTheme.TEXT_GOLD, false);
		lineY += 14;
		GuiUtil.progressBar(context, x, lineY, width - 10, 6, (float) (overall / 100.0), GuiTheme.BAR_FILL);
		lineY += 16;

		lineY = drawMetric(context, textRenderer, x, lineY, "Skill Average", member.skillAverage(), 60, "");
		lineY = drawMetric(context, textRenderer, x, lineY, "SkyBlock Level", member.skyblockLevel(), 40, "");
		lineY = drawMetric(context, textRenderer, x, lineY, "Total Slayer XP", member.totalSlayerXp, SLAYER_XP_SOFT_CAP, " xp");

		lineY += 8;
		context.drawText(textRenderer, Text.literal("Roadmap: catacombs level, sacks, minions and"), x, lineY, GuiTheme.TEXT_GRAY, false);
		lineY += 10;
		context.drawText(textRenderer, Text.literal("net worth will feed into this score in a later pass."), x, lineY, GuiTheme.TEXT_GRAY, false);

		return null;
	}

	private static int drawMetric(DrawContext context, TextRenderer textRenderer, int x, int y, String label, double value, double softCap, String suffix) {
		context.drawText(textRenderer, Text.literal(String.format("%s: %,.1f%s", label, value, suffix)), x, y, GuiTheme.TEXT_WHITE, false);
		y += 9;
		GuiUtil.progressBar(context, x, y, 150, 4, (float) Math.min(1.0, value / softCap), GuiTheme.BAR_FILL);
		return y + 12;
	}
}
