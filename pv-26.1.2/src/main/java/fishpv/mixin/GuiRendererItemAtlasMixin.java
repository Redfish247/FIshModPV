/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package fishpv.mixin;

import fishpv.gui.ProfileViewerScreen;
import fishpv.gui.SmoothTexture;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.render.GuiRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Vanilla renders every GUI item into an atlas at 16 px times the GUI scale, then stretches that to the on-screen size.
 * The profile viewer lays itself out at fractional scales, which made items look blocky and uneven. While the viewer
 * is open the atlas is rendered at {@link #HI_RES} times that size, so the stretch becomes a clean downsample.
 */
@Mixin(GuiRenderer.class)
public abstract class GuiRendererItemAtlasMixin {
	private static final int HI_RES = 3;
	private static int betterpv$lastMultiplier = 1;

	@Shadow
	protected abstract void invalidateItemAtlas();

	@Inject(method = "getGuiScaleInvalidatingItemAtlasIfChanged", at = @At("RETURN"), cancellable = true)
	private void betterpv$hiResItems(CallbackInfoReturnable<Integer> cir) {
		int multiplier = fishpv.compat.PvCompat.curScreen(Minecraft.getInstance()) instanceof ProfileViewerScreen ? HI_RES : 1;
		if (multiplier != betterpv$lastMultiplier) {
			betterpv$lastMultiplier = multiplier;
			SmoothTexture.setItemAtlasLinear(multiplier != 1);
			invalidateItemAtlas();
		}
		if (multiplier != 1) {
			cir.setReturnValue(cir.getReturnValue() * multiplier);
		}
	}
}
