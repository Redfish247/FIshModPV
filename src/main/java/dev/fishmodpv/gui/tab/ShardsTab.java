package dev.fishmodpv.gui.tab;

import dev.fishmodpv.gui.GuiTheme;
import dev.fishmodpv.gui.GuiUtil;
import dev.fishmodpv.nbt.SkyblockItem;
import dev.fishmodpv.profile.ProfileService;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/** Placeholder for SkyShards-inspired attribute shard fusion calculator + tracker. */
public class ShardsTab implements ProfileTab {
	@Override
	public String title() {
		return "Shards";
	}

	@Override
	public ItemStack icon() {
		return new ItemStack(Items.AMETHYST_SHARD);
	}

	@Override
	public SkyblockItem render(GuiGraphics graphics, Font font, ProfileService.Result data,
			int x, int y, int width, int height, int mouseX, int mouseY) {
		int lineY = GuiUtil.card(graphics, font, x, y, width, height, "Attribute Shards - coming soon");
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
			graphics.drawString(font, Component.literal(line), x + 5, lineY, GuiTheme.TEXT_MUTED, false);
			lineY += 11;
		}
		return null;
	}
}
