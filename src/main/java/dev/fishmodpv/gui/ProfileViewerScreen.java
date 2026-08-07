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
 * A tabbed profile viewer styled after real Hypixel SkyBlock profile viewers: a light "vanilla
 * GUI" panel sectioned into purple-headed cards (meowdding/skyblock-pv's look), with a row of
 * real-item tab icons poking above the panel (NotEnoughUpdates' classic profile viewer). The
 * panel eases into place and the active-tab highlight glides smoothly between tabs via
 * {@link SmoothValue} rather than snapping.
 */
public class ProfileViewerScreen extends Screen {
	private static final int PANEL_WIDTH = 350;
	private static final int PANEL_HEIGHT = 230;
	private static final int TAB_SIZE = 24;
	private static final int TAB_GAP = 2;
	private static final int HEADER_HEIGHT = 13;

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
	private final SmoothValue indicatorX = new SmoothValue(0f, 18f);
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
			tabHover.add(new SmoothValue(0f, 14f));
		}
	}

	@Override
	protected void init() {
		guiLeft = (this.width - PANEL_WIDTH) / 2;
		guiTop = (this.height - PANEL_HEIGHT) / 2 + TAB_SIZE / 2;
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
		for (SmoothValue hover : tabHover) {
			hover.update(dt);
		}
		return openAnim.get();
	}

	@Override
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
		float openFraction = tickAnimations();

		graphics.fill(0, 0, this.width, this.height, GuiTheme.scaleAlpha(0xB0000000, Math.min(1f, openFraction * 1.4f)));

		// Subtle pop-in: scale 92% -> 100% around the panel's centre while it fades in.
		float scale = 0.92f + 0.08f * openFraction;
		float pivotX = guiLeft + PANEL_WIDTH / 2f;
		float pivotY = guiTop + PANEL_HEIGHT / 2f;
		graphics.pose().pushPose();
		graphics.pose().translate(pivotX, pivotY, 0);
		graphics.pose().scale(scale, scale, 1f);
		graphics.pose().translate(-pivotX, -pivotY, 0);

		renderTabBar(graphics, mouseX, mouseY, openFraction);
		GuiUtil.panel(graphics, guiLeft, guiTop, PANEL_WIDTH, PANEL_HEIGHT, openFraction);
		renderHeader(graphics, openFraction);

		int contentX = guiLeft + 6;
		int contentY = guiTop + HEADER_HEIGHT + 4;
		int contentWidth = PANEL_WIDTH - 12;
		int contentHeight = PANEL_HEIGHT - HEADER_HEIGHT - 10;

		SkyblockItem hovered = null;
		switch (state) {
			case LOADING -> graphics.drawString(this.font, Component.literal("Loading " + requestedUsername + "'s profile..."),
					contentX, contentY, GuiTheme.TEXT_MUTED, false);
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
		String title = state == State.LOADED ? data.correctedName() + " - SkyBlock Profile" : "SkyBlock Profile Viewer";
		graphics.drawString(this.font, Component.literal(title), guiLeft + 6, guiTop + 3, GuiTheme.scaleAlpha(GuiTheme.TEXT_DARK, openFraction), false);
	}

	private void renderTabBar(GuiGraphics graphics, int mouseX, int mouseY, float openFraction) {
		tabRects.clear();
		int tabY = guiTop - TAB_SIZE - 2;
		int tabX = guiLeft;
		for (int i = 0; i < tabs.size(); i++) {
			boolean active = i == activeTab;
			boolean hovered = mouseX >= tabX && mouseX < tabX + TAB_SIZE && mouseY >= tabY && mouseY < tabY + TAB_SIZE;
			tabHover.get(i).set(hovered ? 1f : 0f);

			tabRects.add(new int[] {tabX, tabY, TAB_SIZE, TAB_SIZE});
			tabX += TAB_SIZE + TAB_GAP;
		}

		int[] activeRect = tabRects.get(activeTab);
		indicatorX.set(activeRect[0]);
		if (!indicatorInitialized) {
			indicatorX.snapTo(activeRect[0]);
			indicatorInitialized = true;
		}

		// Sliding highlight box drawn first so tab icons render on top of it.
		int hx = Math.round(indicatorX.get());
		GuiUtil.border(graphics, hx, tabY, TAB_SIZE, TAB_SIZE, GuiTheme.scaleAlpha(GuiTheme.TAB_INDICATOR, openFraction));
		downArrow(graphics, hx + TAB_SIZE / 2, tabY - 5, GuiTheme.scaleAlpha(GuiTheme.TAB_INDICATOR, openFraction));

		for (int i = 0; i < tabs.size(); i++) {
			int[] rect = tabRects.get(i);
			boolean active = i == activeTab;
			float hover = tabHover.get(i).get();
			int bg = active ? GuiTheme.TAB_BG_ACTIVE : lerpColor(GuiTheme.TAB_BG, GuiTheme.TAB_BG_HOVER, hover);
			graphics.fill(rect[0], rect[1], rect[0] + TAB_SIZE, rect[1] + TAB_SIZE, GuiTheme.scaleAlpha(bg, openFraction));
			GuiUtil.border(graphics, rect[0], rect[1], TAB_SIZE, TAB_SIZE, GuiTheme.scaleAlpha(GuiTheme.TAB_BORDER, openFraction));
			graphics.renderItem(tabs.get(i).icon(), rect[0] + 4, rect[1] + 4);
		}
	}

	/** A small downward-pointing marker above the active tab, like NEU's selection banner. */
	private static void downArrow(GuiGraphics graphics, int centerX, int topY, int color) {
		int width = 7;
		for (int row = 0; row < 4; row++) {
			int rowWidth = width - row * 2;
			if (rowWidth <= 0) {
				break;
			}
			graphics.fill(centerX - rowWidth / 2, topY + row, centerX - rowWidth / 2 + rowWidth, topY + row + 1, color);
		}
	}

	private static int lerpColor(int from, int to, float t) {
		t = Math.max(0, Math.min(1, t));
		int fa = (from >>> 24) & 0xFF, fr = (from >> 16) & 0xFF, fg = (from >> 8) & 0xFF, fb = from & 0xFF;
		int ta = (to >>> 24) & 0xFF, tr = (to >> 16) & 0xFF, tg = (to >> 8) & 0xFF, tb = to & 0xFF;
		int a = Math.round(fa + (ta - fa) * t);
		int r = Math.round(fr + (tr - fr) * t);
		int g = Math.round(fg + (tg - fg) * t);
		int b = Math.round(fb + (tb - fb) * t);
		return (a << 24) | (r << 16) | (g << 8) | b;
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
