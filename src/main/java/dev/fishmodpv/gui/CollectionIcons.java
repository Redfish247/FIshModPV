package dev.fishmodpv.gui;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/**
 * Hypixel's collection ids are mostly just the vanilla material name uppercased (e.g.
 * {@code WHEAT}, {@code DIAMOND}), so a direct lowercase lookup against the item registry covers
 * the majority of them. SkyBlock-only collections (Mithril Ore, Gemstone, ...) have no vanilla
 * item and fall back to a generic icon.
 */
public final class CollectionIcons {
	private CollectionIcons() {
	}

	public static ItemStack iconFor(String collectionId) {
		if (collectionId == null) {
			return new ItemStack(Items.BOOKSHELF);
		}
		String path = collectionId.toLowerCase();
		Item item = BuiltInRegistries.ITEM.get(ResourceLocation.parse("minecraft:" + path));
		return new ItemStack(item == null || item == Items.AIR ? Items.BOOKSHELF : item);
	}
}
