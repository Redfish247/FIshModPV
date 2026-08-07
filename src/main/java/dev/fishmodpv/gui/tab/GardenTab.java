package dev.fishmodpv.gui.tab;

import dev.fishmodpv.gui.GuiTheme;
import dev.fishmodpv.nbt.SkyblockItem;
import dev.fishmodpv.profile.ProfileService;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

/** Placeholder for SkyMutations-inspired garden mutation fusion + greenhouse-layout tools. */
public class GardenTab implements ProfileTab {
	@Override
	public String title() {
		return "Garden";
	}

	@Override
	public SkyblockItem render(DrawContext context, TextRenderer textRenderer, ProfileService.Result data,
			int x, int y, int width, int height, int mouseX, int mouseY) {
		int lineY = y;
		context.drawText(textRenderer, Text.literal("Garden & Mutations - coming soon"), x, lineY, GuiTheme.TEXT_GOLD, false);
		lineY += 14;
		String[] lines = {
				"Planned, inspired by SkyMutations (t0r0nt0.dev / skymutations.eu):",
				" - Mutation fusion requirement calculator",
				" - Greenhouse plot layout planner + import/export",
				" - Live garden level, visitor and composter tracking",
				"",
				"This needs the /v2/skyblock/garden endpoint and mutation",
				"recipe data that aren't wired up in this foundation pass.",
		};
		for (String line : lines) {
			context.drawText(textRenderer, Text.literal(line), x, lineY, GuiTheme.TEXT_GRAY, false);
			lineY += 11;
		}
		return null;
	}
}
