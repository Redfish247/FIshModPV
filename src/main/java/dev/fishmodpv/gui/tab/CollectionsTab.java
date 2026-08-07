package dev.fishmodpv.gui.tab;

import dev.fishmodpv.gui.GuiTheme;
import dev.fishmodpv.nbt.SkyblockItem;
import dev.fishmodpv.profile.ProfileMember;
import dev.fishmodpv.profile.ProfileService;
import dev.fishmodpv.profile.SkyblockProfile;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CollectionsTab implements ProfileTab {
	@Override
	public String title() {
		return "Collections";
	}

	@Override
	public SkyblockItem render(DrawContext context, TextRenderer textRenderer, ProfileService.Result data,
			int x, int y, int width, int height, int mouseX, int mouseY) {
		SkyblockProfile profile = data.selectedOrFirst();
		if (profile == null) {
			return null;
		}
		ProfileMember member = profile.members.get(data.uuid());
		if (member == null || member.collections.isEmpty()) {
			context.drawText(textRenderer, Text.literal("No collection data available for this member."), x, y, GuiTheme.TEXT_GRAY, false);
			return null;
		}

		List<Map.Entry<String, Long>> sorted = member.collections.entrySet().stream()
				.sorted((a, b) -> Long.compare(b.getValue(), a.getValue()))
				.collect(Collectors.toList());

		int rowHeight = 10;
		int rowsPerColumn = Math.max(1, height / rowHeight);
		int colWidth = 140;

		for (int i = 0; i < sorted.size(); i++) {
			int col = i / rowsPerColumn;
			int row = i % rowsPerColumn;
			int rowX = x + col * colWidth;
			if (rowX + colWidth > x + width) {
				context.drawText(textRenderer, Text.literal("+ " + (sorted.size() - i) + " more..."), rowX, y + row * rowHeight, GuiTheme.TEXT_GRAY, false);
				break;
			}
			Map.Entry<String, Long> entry = sorted.get(i);
			String name = prettify(entry.getKey());
			context.drawText(textRenderer, Text.literal(name + ": " + String.format("%,d", entry.getValue())),
					rowX, y + row * rowHeight, GuiTheme.TEXT_WHITE, false);
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
