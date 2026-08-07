package dev.fishmodpv.gui.tab;

import dev.fishmodpv.gui.CollectionIcons;
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

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CollectionsTab implements ProfileTab {
	@Override
	public String title() {
		return "Collections";
	}

	@Override
	public ItemStack icon() {
		return new ItemStack(Items.BOOKSHELF);
	}

	@Override
	public SkyblockItem render(GuiGraphics graphics, Font font, ProfileService.Result data,
			int x, int y, int width, int height, int mouseX, int mouseY) {
		SkyblockProfile profile = data.selectedOrFirst();
		if (profile == null) {
			return null;
		}
		ProfileMember member = profile.members.get(data.uuid());
		if (member == null || member.collections.isEmpty()) {
			graphics.drawString(font, Component.literal("No collection data available for this member."), x, y, GuiTheme.TEXT_MUTED, false);
			return null;
		}

		int contentY = GuiUtil.card(graphics, font, x, y, width, height, "Collections");

		List<Map.Entry<String, Long>> sorted = member.collections.entrySet().stream()
				.sorted((a, b) -> Long.compare(b.getValue(), a.getValue()))
				.collect(Collectors.toList());

		int rowHeight = 12;
		int rowsPerColumn = Math.max(1, (height - (contentY - y)) / rowHeight);
		int colWidth = width / 2 - 6;

		for (int i = 0; i < sorted.size(); i++) {
			int col = i / rowsPerColumn;
			int row = i % rowsPerColumn;
			int rowX = x + 5 + col * colWidth;
			if (col >= 2) {
				graphics.drawString(font, Component.literal("+ " + (sorted.size() - i) + " more..."), rowX - colWidth, contentY + row * rowHeight, GuiTheme.TEXT_MUTED, false);
				break;
			}
			Map.Entry<String, Long> entry = sorted.get(i);
			int rowY = contentY + row * rowHeight;
			graphics.renderItem(CollectionIcons.iconFor(entry.getKey()), rowX, rowY);
			String name = prettify(entry.getKey()) + ": " + String.format("%,d", entry.getValue());
			graphics.drawString(font, Component.literal(name), rowX + 16, rowY + 4, GuiTheme.TEXT_DARK, false);
		}

		return null;
	}

	private static String prettify(String id) {
		String[] parts = id.replace(':', ' ').split("_");
		StringBuilder out = new StringBuilder();
		for (String part : parts) {
			if (!part.isEmpty()) {
				out.append(Character.toUpperCase(part.charAt(0))).append(part.substring(1).toLowerCase()).append(' ');
			}
		}
		return out.toString().trim();
	}
}
