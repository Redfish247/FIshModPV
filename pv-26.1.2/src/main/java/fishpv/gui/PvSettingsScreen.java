package fishpv.gui;

import fishpv.api.BetterPVConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/** FishPV settings: enable toggle, theme, scale and overflow skills, saved to betterpv.json. */
public final class PvSettingsScreen extends Screen {
	private final Screen parent;
	private Button enabledButton;
	private Button themeButton;
	private Button scaleButton;
	private Button overflowButton;

	public PvSettingsScreen(Screen parent) {
		super(Component.literal("FishPV Settings"));
		this.parent = parent;
	}

	@Override
	protected void init() {
		int w = 200;
		int x = this.width / 2 - w / 2;
		int y = this.height / 4;
		this.enabledButton = Button.builder(Component.empty(), b -> {
			BetterPVConfig.setEnabled(!BetterPVConfig.enabled());
			refresh();
		}).bounds(x, y, w, 20).build();
		this.themeButton = Button.builder(Component.empty(), b -> {
			String[] names = PvTheme.NAMES;
			int next = 0;
			for (int i = 0; i < names.length; i++) {
				if (names[i].equalsIgnoreCase(BetterPVConfig.theme())) {
					next = (i + 1) % names.length;
					break;
				}
			}
			BetterPVConfig.setTheme(names[next]);
			refresh();
		}).bounds(x, y + 26, w, 20).build();
		this.scaleButton = Button.builder(Component.empty(), b -> {
			int next = BetterPVConfig.profileViewerScalePercent() + 5;
			BetterPVConfig.setProfileViewerScalePercent(next > 150 ? 75 : next);
			refresh();
		}).bounds(x, y + 52, w, 20).build();
		this.overflowButton = Button.builder(Component.empty(), b -> {
			BetterPVConfig.setOverflowSkills(!BetterPVConfig.overflowSkills());
			refresh();
		}).bounds(x, y + 78, w, 20).build();
		this.addRenderableWidget(this.enabledButton);
		this.addRenderableWidget(this.themeButton);
		this.addRenderableWidget(this.scaleButton);
		this.addRenderableWidget(this.overflowButton);
		this.addRenderableWidget(Button.builder(Component.literal("Done"), b -> onClose())
			.bounds(x, y + 114, w, 20).build());
		refresh();
	}

	private void refresh() {
		this.enabledButton.setMessage(Component.literal("Profile Viewer: " + (BetterPVConfig.enabled() ? "ON" : "OFF")));
		this.themeButton.setMessage(Component.literal("Theme: " + BetterPVConfig.theme()));
		this.scaleButton.setMessage(Component.literal("Scale: " + BetterPVConfig.profileViewerScalePercent() + "% (click +5)"));
		this.overflowButton.setMessage(Component.literal("Overflow Skill Levels: " + (BetterPVConfig.overflowSkills() ? "ON" : "OFF")));
	}

	@Override
	public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
		super.extractRenderState(graphics, mouseX, mouseY, delta);
		graphics.centeredText(this.font, this.title, this.width / 2, this.height / 4 - 20, 0xFFFFFFFF);
	}

	@Override
	public void onClose() {
		fishpv.compat.PvCompat.showScreen(Minecraft.getInstance(), this.parent);
	}
}
