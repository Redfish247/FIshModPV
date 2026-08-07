package dev.fishmodpv.util;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;

/** Converts Minecraft's legacy "{@code §a}" formatting-code strings (as returned by the Hypixel API) into a {@link Component}. */
public final class LegacyText {
	private LegacyText() {
	}

	public static MutableComponent parse(String raw) {
		MutableComponent result = Component.literal("");
		if (raw == null || raw.isEmpty()) {
			return result;
		}

		Style style = Style.EMPTY;
		StringBuilder buffer = new StringBuilder();

		int i = 0;
		while (i < raw.length()) {
			char c = raw.charAt(i);
			if (c == '§' && i + 1 < raw.length()) {
				ChatFormatting formatting = ChatFormatting.getByCode(raw.charAt(i + 1));
				if (formatting != null) {
					if (buffer.length() > 0) {
						result.append(Component.literal(buffer.toString()).setStyle(style));
						buffer.setLength(0);
					}
					style = applyFormatting(style, formatting);
					i += 2;
					continue;
				}
			}
			buffer.append(c);
			i++;
		}

		if (buffer.length() > 0) {
			result.append(Component.literal(buffer.toString()).setStyle(style));
		}
		return result;
	}

	private static Style applyFormatting(Style style, ChatFormatting formatting) {
		return switch (formatting) {
			case RESET -> Style.EMPTY;
			case BOLD -> style.withBold(true);
			case ITALIC -> style.withItalic(true);
			case UNDERLINE -> style.withUnderlined(true);
			case STRIKETHROUGH -> style.withStrikethrough(true);
			case OBFUSCATED -> style.withObfuscated(true);
			default -> formatting.isColor() ? style.withColor(formatting) : style;
		};
	}

	/** Strips formatting codes entirely, e.g. for plain-text search/comparison. */
	public static String stripCodes(String raw) {
		if (raw == null) {
			return "";
		}
		return raw.replaceAll("§.", "");
	}
}
