package dev.fishmodpv.gui.tab;

import dev.fishmodpv.gui.GuiTheme;
import dev.fishmodpv.nbt.SkyblockItem;
import dev.fishmodpv.profile.ProfileService;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

/** Placeholder for SkyShards-inspired attribute shard fusion calculator + tracker. */
public class ShardsTab implements ProfileTab {
	@Override
	public String title() {
		return "Shards";
	}

	@Override
	public SkyblockItem render(DrawContext context, TextRenderer textRenderer, ProfileService.Result data,
			int x, int y, int width, int height, int mouseX, int mouseY) {
		int lineY = y;
		context.drawText(textRenderer, Text.literal("Attribute Shards - coming soon"), x, lineY, GuiTheme.TEXT_GOLD, false);
		lineY += 14;
		String[] lines = {
				"Planned, inspired by SkyShards (skyshards.com):",
				" - Cheapest-fusion-tree calculator for a target shard",
				" - Chat-based catch/fusion counter with a live HUD goal tracker",
				" - Full shard catalog browser (rarity/category filters)",
				"",
				"This needs shard catalog data and a chat-log listener that",
				"aren't wired up in this foundation pass.",
		};
		for (String line : lines) {
			context.drawText(textRenderer, Text.literal(line), x, lineY, GuiTheme.TEXT_GRAY, false);
			lineY += 11;
		}
		return null;
	}
}
