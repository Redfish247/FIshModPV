package fishpv.compat;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

/** 26.3 side of the Minecraft API differences; overlays the 26.1.2 file of the same path. */
public final class PvCompat {
	public static final Item GRAY_DYE = Items.DYE.gray();
	public static final Item LIGHT_GRAY_DYE = Items.DYE.lightGray();
	public static final Item LIME_DYE = Items.DYE.lime();

	private PvCompat() {}

	public static boolean keyDown(int key) {
		return com.mojang.blaze3d.platform.InputConstants.isKeyDown(key);
	}

	public static Screen curScreen(Minecraft client) { return client.gui.screen(); }

	public static void showScreen(Minecraft client, Screen screen) { client.gui.setScreen(screen); }

	public static void chatMessage(Minecraft client, net.minecraft.network.chat.Component message) {
		client.gui.hud.getChat().addClientSystemMessage(message);
	}
}
