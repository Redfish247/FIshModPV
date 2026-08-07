package dev.fishmodpv.nbt;

import dev.fishmodpv.util.LegacyText;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * A decoded SkyBlock item: a best-effort vanilla {@link ItemStack} for rendering the icon, plus
 * the item's real display name/lore/id straight from Hypixel's data (which is what we actually
 * show in tooltips, rather than trusting the vanilla stack's own tooltip).
 */
public class SkyblockItem {
	private static final String[] RARITIES = {
			"SPECIAL", "VERY SPECIAL", "DIVINE", "MYTHIC", "LEGENDARY", "EPIC", "RARE", "UNCOMMON", "COMMON"
	};

	private final ItemStack renderStack;
	private final String rawDisplayName;
	private final List<String> rawLore;
	private final String skyblockId;
	private final CompoundTag extraAttributes;
	private final int count;

	public SkyblockItem(ItemStack renderStack, String rawDisplayName, List<String> rawLore,
			String skyblockId, CompoundTag extraAttributes, int count) {
		this.renderStack = renderStack;
		this.rawDisplayName = rawDisplayName;
		this.rawLore = rawLore;
		this.skyblockId = skyblockId;
		this.extraAttributes = extraAttributes;
		this.count = count;
	}

	public ItemStack renderStack() {
		return renderStack;
	}

	public int count() {
		return count;
	}

	public String skyblockId() {
		return skyblockId;
	}

	public CompoundTag extraAttributes() {
		return extraAttributes;
	}

	public MutableComponent displayName() {
		return rawDisplayName != null ? LegacyText.parse(rawDisplayName) : Component.literal(renderStack.getHoverName().getString());
	}

	public List<Component> loreText() {
		List<Component> lines = new ArrayList<>();
		if (rawLore != null) {
			for (String line : rawLore) {
				lines.add(LegacyText.parse(line));
			}
		}
		return lines;
	}

	/** Best-effort rarity guess parsed from the last lore line (e.g. "LEGENDARY SWORD"), or null. */
	public String rarity() {
		if (rawLore == null || rawLore.isEmpty()) {
			return null;
		}
		String lastLine = LegacyText.stripCodes(rawLore.get(rawLore.size() - 1)).toUpperCase();
		for (String rarity : RARITIES) {
			if (lastLine.contains(rarity)) {
				return rarity;
			}
		}
		return null;
	}
}
