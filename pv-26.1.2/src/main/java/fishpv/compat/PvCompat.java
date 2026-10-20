package fishpv.compat;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

/** 26.1 side of the Minecraft API differences; the 26.2 module overlays this file. */
public final class PvCompat {
	public static final Item GRAY_DYE = Items.GRAY_DYE;
	public static final Item LIGHT_GRAY_DYE = Items.LIGHT_GRAY_DYE;
	public static final Item LIME_DYE = Items.LIME_DYE;

	private PvCompat() {}

	public static Screen curScreen(Minecraft client) { return client.screen; }

	public static void showScreen(Minecraft client, Screen screen) { client.setScreen(screen); }

	public static void chatMessage(Minecraft client, net.minecraft.network.chat.Component message) {
		client.gui.getChat().addClientSystemMessage(message);
	}
}
