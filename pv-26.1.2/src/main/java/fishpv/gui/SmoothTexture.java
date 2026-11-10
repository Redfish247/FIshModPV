/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package fishpv.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.AddressMode;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuSampler;
import fishpv.BetterPV;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.Identifier;

/** Switches a dynamic texture from nearest to linear sampling so scaled icons stay soft instead of blocky. */
public final class SmoothTexture {
	private static Field samplerField;
	private static boolean unavailable;
	private static final Map<Identifier, GpuSampler> ORIGINAL_ATLAS_SAMPLERS = new HashMap<>();
	private static final Identifier[] ITEM_ATLASES = {
		Identifier.withDefaultNamespace("textures/atlas/items.png"),
		Identifier.withDefaultNamespace("textures/atlas/blocks.png")
	};

	private SmoothTexture() {}

	public static <T extends AbstractTexture> T apply(T texture) {
		if (unavailable) {
			return texture;
		}
		try {
			if (samplerField == null) {
				samplerField = AbstractTexture.class.getDeclaredField("sampler");
				samplerField.setAccessible(true);
			}
			GpuSampler sampler = RenderSystem.getSamplerCache().getSampler(
				AddressMode.CLAMP_TO_EDGE, AddressMode.CLAMP_TO_EDGE, FilterMode.LINEAR, FilterMode.LINEAR, false
			);
			samplerField.set(texture, sampler);
		} catch (ReflectiveOperationException | RuntimeException exception) {
			unavailable = true;
			BetterPV.LOGGER.warn("Smooth icon filtering unavailable", exception);
		}
		return texture;
	}

	/**
	 * Item sprites are 16x16 pixel art. While the viewer is open, the item and block atlases are sampled with linear
	 * filtering so enlarged items are smoothly interpolated instead of showing hard texel blocks.
	 */
	public static void setItemAtlasLinear(boolean linear) {
		if (unavailable) {
			return;
		}
		try {
			if (samplerField == null) {
				samplerField = AbstractTexture.class.getDeclaredField("sampler");
				samplerField.setAccessible(true);
			}
			for (Identifier id : ITEM_ATLASES) {
				AbstractTexture atlas = Minecraft.getInstance().getTextureManager().getTexture(id);
				if (!(atlas instanceof TextureAtlas)) {
					continue;
				}
				if (linear) {
					ORIGINAL_ATLAS_SAMPLERS.computeIfAbsent(id, key -> atlas.getSampler());
					samplerField.set(atlas, RenderSystem.getSamplerCache().getSampler(
						AddressMode.CLAMP_TO_EDGE, AddressMode.CLAMP_TO_EDGE, FilterMode.LINEAR, FilterMode.LINEAR, false
					));
				} else {
					GpuSampler original = ORIGINAL_ATLAS_SAMPLERS.remove(id);
					if (original != null) {
						samplerField.set(atlas, original);
					}
				}
			}
		} catch (ReflectiveOperationException | RuntimeException exception) {
			unavailable = true;
			BetterPV.LOGGER.warn("Smooth item filtering unavailable", exception);
		}
	}
}

