package dev.fishmodpv.nbt;

import dev.fishmodpv.FishModPV;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtSizeTracker;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.zip.GZIPInputStream;

/**
 * Decodes the base64-gzip-NBT blobs Hypixel's API uses for inventory contents
 * ({@code inv_contents}, {@code inv_armor}, {@code ender_chest_contents}, bag contents, ...) into
 * {@link SkyblockItem}s.
 *
 * <p>The root NBT compound has a single list tag, {@code "i"}, with one compound per inventory
 * slot in order (empty slots are present as empty/id-0 compounds so slot positions line up with
 * the real inventory grid). This class returns a same-size list where empty slots are {@code null}.
 */
public final class ItemDecoder {
	private ItemDecoder() {
	}

	public static List<SkyblockItem> decode(String base64) {
		List<SkyblockItem> result = new ArrayList<>();
		if (base64 == null || base64.isBlank()) {
			return result;
		}

		try {
			byte[] compressed = Base64.getDecoder().decode(base64);
			NbtCompound root;
			try (GZIPInputStream gzip = new GZIPInputStream(new ByteArrayInputStream(compressed));
					DataInputStream data = new DataInputStream(gzip)) {
				root = NbtIo.readCompound(data, NbtSizeTracker.ofUnlimited());
			}

			NbtList items = root.getList("i", NbtElement.COMPOUND_TYPE);
			for (int i = 0; i < items.size(); i++) {
				result.add(decodeSlot(items.getCompound(i)));
			}
		} catch (Exception e) {
			FishModPV.LOGGER.error("Failed to decode SkyBlock item bytes", e);
		}
		return result;
	}

	private static SkyblockItem decodeSlot(NbtCompound slot) {
		if (!slot.contains("id")) {
			return null;
		}
		int legacyId = slot.getShort("id");
		int count = slot.contains("Count") ? Byte.toUnsignedInt(slot.getByte("Count")) : 1;
		if (legacyId <= 0 || count <= 0) {
			return null;
		}

		Identifier modernId = Identifier.of(LegacyItemIds.resolve(legacyId));
		ItemStack renderStack = new ItemStack(Registries.ITEM.getOrEmpty(modernId).orElse(Items.PLAYER_HEAD), count);

		String displayName = null;
		List<String> lore = null;
		String skyblockId = null;
		NbtCompound extraAttributes = null;

		if (slot.contains("tag")) {
			NbtCompound tag = slot.getCompound("tag");
			if (tag.contains("display")) {
				NbtCompound display = tag.getCompound("display");
				if (display.contains("Name")) {
					displayName = display.getString("Name");
				}
				if (display.contains("Lore")) {
					NbtList loreList = display.getList("Lore", NbtElement.STRING_TYPE);
					lore = new ArrayList<>();
					for (int i = 0; i < loreList.size(); i++) {
						lore.add(loreList.getString(i));
					}
				}
			}
			if (tag.contains("ExtraAttributes")) {
				extraAttributes = tag.getCompound("ExtraAttributes");
				if (extraAttributes.contains("id")) {
					skyblockId = extraAttributes.getString("id");
				}
			}
		}

		return new SkyblockItem(renderStack, displayName, lore, skyblockId, extraAttributes, count);
	}
}
