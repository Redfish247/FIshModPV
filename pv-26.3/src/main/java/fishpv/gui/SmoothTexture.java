/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package fishpv.gui;

import net.minecraft.client.renderer.texture.AbstractTexture;

/** 26.3 moved the sampler classes, so linear filtering of dynamic icons is skipped there. */
public final class SmoothTexture {
	private SmoothTexture() {}

	public static <T extends AbstractTexture> T apply(T texture) {
		return texture;
	}

	public static void setItemAtlasLinear(boolean linear) {}
}
