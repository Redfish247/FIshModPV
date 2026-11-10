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
import net.minecraft.client.renderer.texture.AbstractTexture;

/** Switches a dynamic texture from nearest to linear sampling so scaled icons stay soft instead of blocky. */
public final class SmoothTexture {
	private static Field samplerField;
	private static boolean unavailable;

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
}
