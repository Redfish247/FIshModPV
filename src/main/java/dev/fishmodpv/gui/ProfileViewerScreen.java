package dev.fishmodpv.gui;

import dev.fishmodpv.gui.tab.CollectionsTab;
import dev.fishmodpv.gui.tab.GardenTab;
import dev.fishmodpv.gui.tab.InventoryTab;
import dev.fishmodpv.gui.tab.OverviewTab;
import dev.fishmodpv.gui.tab.PetsTab;
import dev.fishmodpv.gui.tab.ProfileTab;
import dev.fishmodpv.gui.tab.ProgressionTab;
import dev.fishmodpv.gui.tab.ShardsTab;
import dev.fishmodpv.gui.tab.SkillsTab;
import dev.fishmodpv.nbt.SkyblockItem;
import dev.fishmodpv.profile.ProfileService;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * A "liquid glass" take on the old-NEU-style tabbed profile viewer: a translucent gradient panel
 * with a row of tabs across the top (Overview / Inventory / Skills / ...) and item-grid content
 * below. Every surface is drawn with smooth gradient fills (never a resampled/blurred texture, so
 * it never looks pixelated), and the chrome eases into place with {@link SmoothValue} rather than
 * snapping - the panel pops in on open and the active-tab indicator glides between tabs.
 */
public class ProfileViewerScreen extends Screen {
	private static final int PANEL_WIDTH = 330;
	private static final int PANEL_HEIGHT = 220;
	private static final int TAB_HEIGHT = 18;
	private static final int HEADER_HEIGHT = 14;

	private final String requestedUsername;
	private final List<ProfileTab> tabs = new ArrayList<>();
	private final List<int[]> tabRects = new ArrayList<>();
	private final List<SmoothValue> tabHover = new ArrayList<>();
	private int activeTab = 0;

	private boolean fetchStarted = false;
	private volatile State state = State.LOADING;
	private volatile ProfileService.Result data;
	private volatile String errorMessage;

	private int guiLeft;
	private int guiTop;

	private long lastRenderNanos = -1;
	private final SmoothValue openAnim = new SmoothValue(0f, 9f);
	private final SmoothValue indicatorX = new SmoothValue(0f, 16f);
	private final SmoothValue indicatorW = new SmoothValue(0f, 16f);
	private boolean indicatorInitialized = false;

	private enum State { LOADING, LOADED, ERROR }

	public ProfileViewerScreen(String requestedUsername) {
		super(Component.literal("SkyBlock Profile Viewer"));
		this.requestedUsername = requestedUsername;
		tabs.add(new OverviewTab());
		tabs.add(new InventoryTab());
		tabs.add(new SkillsTab());
		tabs.add(new CollectionsTab());
		tabs.add(new PetsTab());
		tabs.add(new ProgressionTab());
		tabs.add(new GardenTab());
		tabs.add(new ShardsTab());
		for (int i = 0; i < tabs.size(); i++) {
			tabHover.add(new SmoothValue(0f, 12f));
		}
	}

	@Override
	protected void init() {
		guiLeft = (this.width - PANEL_WIDTH) / 2;
		guiTop = (this.height - PANEL_HEIGHT) / 2;
		openAnim.set(1f);

		if (!fetchStarted) {
			fetchStarted = true;
			ProfileService.fetch(requestedUsername).whenComplete((result, throwable) ->
					Minecraft.getInstance().execute(() -> {
						if (throwable != null) {
							Throwable cause = throwable.getCause() != null ? throwable.getCause() : throwable;
							errorMessage = cause.getMessage() != null ? cause.getMessage() : cause.toString();
							state = State.ERROR;
						} else if (result.profiles().isEmpty()) {
							errorMessage = result.correctedName() + " has no SkyBlock profiles.";
							state = State.ERROR;
						} else {
							data = result;
							state = State.LOADED;
						}
					}));
		}
	}

	private float tickAnimations() {
		long now = System.nanoTime();
		float dt = lastRenderNanos < 0 ? 0f : (now - lastRenderNanos) / 1_000_000_000f;
		lastRenderNanos = now;
		dt = Math.min(dt, 0.1f); // guard against huge jumps after lag spikes / the first frame

		openAnim.update(dt);
		indicatorX.update(dt);
		indicatorW.update(dt);
		for (SmoothValue hover : tabHover) {
			hover.update(dt);
		}
		return openAnim.get();
	}

	@Override
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
		float openFraction = tickAnimations();

		graphics.fill(0, 0, this.width, this.height, GuiTheme.scaleAlpha(0xC0000000, Math.min(1f, openFraction * 1.4f)));

		// Subtle pop-in: scale 92% -> 100% around the panel's centre while it fades/gradients in.
		float scale = 0.92f + 0.08f * openFraction;
		float pivotX = guiLeft + PANEL_WIDTH / 2f;
		float pivotY = guiTop + PANEL_HEIGHT / 2f;
		graphics.pose().pushPose();
		graphics.pose().translate(pivotX, pivotY, 0);
		graphics.pose().scale(scale, scale, 1f);
		graphics.pose().translate(-pivotX, -pivotY, 0);

		GuiUtil.glassPanel(graphics, guiLeft, guiTop, PANEL_WIDTH, PANEL_HEIGHT, openFraction);
		renderHeader(graphics, openFraction);
		renderTabBar(graphics, mouseX, mouseY, openFraction);

		int contentX = guiLeft + 8;
		int contentY = guiTop + HEADER_HEIGHT + TAB_HEIGHT + 6;
		int contentWidth = PANEL_WIDTH - 16;
		int contentHeight = PANEL_HEIGHT - HEADER_HEIGHT - TAB_HEIGHT - 14;

		SkyblockItem hovered = null;
		switch (state) {
			case LOADING -> graphics.drawString(this.font, Component.literal("Loading " + requestedUsername + "'s profile..."),
					contentX, contentY, GuiTheme.TEXT_GRAY, false);
			case ERROR -> graphics.drawString(this.font, Component.literal("Error: " + errorMessage),
					contentX, contentY, GuiTheme.TEXT_RED, false);
			case LOADED -> {
				ProfileTab tab = tabs.get(activeTab);
				hovered = tab.render(graphics, this.font, data, contentX, contentY, contentWidth, contentHeight, mouseX, mouseY);
			}
		}

		graphics.pose().popPose();

		if (hovered != null) {
			graphics.renderTooltip(this.font, buildTooltip(hovered), mouseX, mouseY);
		}
	}

	private List<Component> buildTooltip(SkyblockItem item) {
		List<Component> lines = new ArrayList<>();
		lines.add(item.displayName());
		lines.addAll(item.loreText());
		return lines;
	}

	private void renderHeader(GuiGraphics graphics, float openFraction) {
		graphics.fillGradient(guiLeft, guiTop, guiLeft + PANEL_WIDTH, guiTop + HEADER_HEIGHT,
				GuiTheme.scaleAlpha(GuiTheme.HEADER_BG_TOP, openFraction), GuiTheme.scaleAlpha(GuiTheme.HEADER_BG_BOTTOM, openFraction));
		String title = state == State.LOADED ? data.correctedName() + " - SkyBlock Profile" : "SkyBlock Profile Viewer";
		graphics.drawString(this.font, Component.literal(title), guiLeft + 6, guiTop + 3, GuiTheme.TEXT_GOLD, false);
	}

	private void renderTabBar(GuiGraphics graphics, int mouseX, int mouseY, float openFraction) {
		tabRects.clear();
		int tabY = guiTop + HEADER_HEIGHT + 2;
		int tabX = guiLeft + 4;
		for (int i = 0; i < tabs.size(); i++) {
			String label = tabs.get(i).title();
			int tabWidth = this.font.width(label) + 10;
			boolean active = i == activeTab;
			boolean hovered = mouseX >= tabX && mouseX < tabX + tabWidth && mouseY >= tabY && mouseY < tabY + TAB_HEIGHT - 2;
			tabHover.get(i).set(hovered || active ? 1f : 0f);

			int glow = GuiTheme.scaleAlpha(GuiTheme.withAlpha(0xFFFFFF, 0x30), tabHover.get(i).get() * openFraction);
			graphics.fillGradient(tabX, tabY, tabX + tabWidth, tabY + TAB_HEIGHT - 2, glow, GuiTheme.withAlpha(0xFFFFFF, 0));
			graphics.drawString(this.font, Component.literal(label), tabX + 5, tabY + 4,
					active ? GuiTheme.TAB_TEXT_ACTIVE : GuiTheme.TEXT_WHITE, false);

			tabRects.add(new int[] {tabX, tabY, tabWidth, TAB_HEIGHT - 2});
			tabX += tabWidth + 2;
		}

		int[] activeRect = tabRects.get(activeTab);
		indicatorX.set(activeRect[0]);
		indicatorW.set(activeRect[2]);
		if (!indicatorInitialized) {
			indicatorX.snapTo(activeRect[0]);
			indicatorW.snapTo(activeRect[2]);
			indicatorInitialized = true;
		}

		int barY = tabY + TAB_HEIGHT - 3;
		int barColor = GuiTheme.scaleAlpha(GuiTheme.TAB_INDICATOR, openFraction);
		graphics.fill(Math.round(indicatorX.get()), barY, Math.round(indicatorX.get() + indicatorW.get()), barY + 1, barColor);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		for (int i = 0; i < tabRects.size(); i++) {
			int[] rect = tabRects.get(i);
			if (mouseX >= rect[0] && mouseX < rect[0] + rect[2] && mouseY >= rect[1] && mouseY < rect[1] + rect[3]) {
				activeTab = i;
				return true;
			}
		}
		return super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
		if (state == State.LOADED) {
			tabs.get(activeTab).mouseScrolled(mouseX, mouseY, verticalAmount);
		}
		return true;
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}

	@Override
	public boolean shouldCloseOnEsc() {
		return true;
	}
}
