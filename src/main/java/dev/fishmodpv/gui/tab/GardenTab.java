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

/** Placeholder for SkyMutations-inspired garden mutation fusion + greenhouse-layout tools. */
public class GardenTab implements ProfileTab {
	@Override
	public String title() {
		return "Garden";
	}

	@Override
	public ItemStack icon() {
		return new ItemStack(Items.OAK_SAPLING);
	}

	@Override
	public SkyblockItem render(GuiGraphics graphics, Font font, ProfileService.Result data,
			int x, int y, int width, int height, int mouseX, int mouseY) {
		int lineY = GuiUtil.card(graphics, font, x, y, width, height, "Garden & Mutations - coming soon");
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
			graphics.drawString(font, Component.literal(line), x + 5, lineY, GuiTheme.TEXT_MUTED, false);
			lineY += 11;
		}
		return null;
	}
}
