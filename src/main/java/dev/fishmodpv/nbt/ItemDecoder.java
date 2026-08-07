package dev.fishmodpv.nbt;

import dev.fishmodpv.FishModPV;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

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
 *
 * <p>Uses the post-1.21.5 "Or"-suffixed NBT accessors (e.g. {@code getCompoundOrEmpty},
 * {@code getListOrEmpty}, {@code getString(key, fallback)}) rather than the older accessors that
 * returned {@code Optional} or threw - see the README's porting notes if these don't quite match
 * what 26.1's actual `CompoundTag`/`ListTag` ended up shipping with.
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
			CompoundTag root;
			try (GZIPInputStream gzip = new GZIPInputStream(new ByteArrayInputStream(compressed));
					DataInputStream data = new DataInputStream(gzip)) {
				root = NbtIo.readCompound(data, NbtAccounter.unlimitedHeap());
			}

			ListTag items = root.getListOrEmpty("i");
			for (int i = 0; i < items.size(); i++) {
				result.add(decodeSlot(items.getCompoundOrEmpty(i)));
			}
		} catch (Exception e) {
			FishModPV.LOGGER.error("Failed to decode SkyBlock item bytes", e);
		}
		return result;
	}

	private static SkyblockItem decodeSlot(CompoundTag slot) {
		int legacyId = slot.getShort("id", (short) 0);
		int count = Byte.toUnsignedInt(slot.getByte("Count", (byte) 1));
		if (legacyId <= 0 || count <= 0) {
			return null;
		}

		ResourceLocation modernId = ResourceLocation.parse(LegacyItemIds.resolve(legacyId));
		Item resolved = BuiltInRegistries.ITEM.get(modernId);
		ItemStack renderStack = new ItemStack(resolved == null || resolved == Items.AIR ? Items.PLAYER_HEAD : resolved, count);

		String displayName = null;
		List<String> lore = null;
		String skyblockId = null;
		CompoundTag extraAttributes = null;

		CompoundTag tag = slot.getCompoundOrEmpty("tag");
		if (!tag.isEmpty()) {
			CompoundTag display = tag.getCompoundOrEmpty("display");
			if (!display.isEmpty()) {
				String name = display.getString("Name", "");
				displayName = name.isEmpty() ? null : name;

				ListTag loreList = display.getListOrEmpty("Lore");
				if (!loreList.isEmpty()) {
					lore = new ArrayList<>();
					for (int i = 0; i < loreList.size(); i++) {
						lore.add(loreList.getString(i, ""));
					}
				}
			}

			CompoundTag extra = tag.getCompoundOrEmpty("ExtraAttributes");
			if (!extra.isEmpty()) {
				extraAttributes = extra;
				String id = extra.getString("id", "");
				skyblockId = id.isEmpty() ? null : id;
			}
		}

		return new SkyblockItem(renderStack, displayName, lore, skyblockId, extraAttributes, count);
	}
}
