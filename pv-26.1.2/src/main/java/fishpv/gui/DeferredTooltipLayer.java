/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Derived from BetterPV by Vyriv (https://github.com/Vyriv/BetterPV).
 */
package fishpv.gui;

import fishpv.gui.bestiary.BestiaryPage;
import fishpv.gui.collections.CollectionsPage;
import fishpv.gui.crimson.CrimsonPage;
import fishpv.gui.dungeons.DungeonChestsPage;
import fishpv.gui.dungeons.DungeonPage;
import fishpv.gui.events.EventsPage;
import fishpv.gui.fishing.FishingPage;
import fishpv.gui.fishing.FishingTrophyTrialPage;
import fishpv.gui.foraging.ForagingPage;
import fishpv.gui.garden.GardenPage;
import fishpv.gui.home.HomePage;
import fishpv.gui.home.page.EssencePage;
import fishpv.gui.home.page.MiscStatsPage;
import fishpv.gui.home.page.RecordsPage;
import fishpv.gui.inventories.InventoryPage;
import fishpv.gui.mining.MiningPage;
import fishpv.gui.museum.MuseumPage;
import fishpv.gui.nav.IconButtonBar;
import fishpv.gui.nav.PvSubTab;
import fishpv.gui.nav.PvTab;
import fishpv.gui.pets.PetsPage;
import fishpv.gui.rift.RiftPage;
import java.util.List;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;

/** Collects and draws deferred page / selector tooltips above search chrome. */
final class DeferredTooltipLayer {
	private static final int PAD = 8;

	private DeferredTooltipLayer() {
	}

	static void render(
		GuiGraphicsExtractor g,
		Font font,
		int width,
		int height,
		int panelX,
		int panelY,
		int panelW,
		int panelH,
		int mouseX,
		int mouseY,
		PvTab tab,
		PvSubTab homeSub,
		ProfileSelectorController selector,
		InventorySplitLayout inventoryLayout,
		BestiarySplitLayout bestiaryLayout,
		IconButtonBar inventoryBar,
		HomePage homePage,
		MiscStatsPage homeMiscPage,
		RecordsPage homeRecordsPage,
		EssencePage homeEssencePage,
		DungeonPage dungeonPage,
		DungeonChestsPage dungeonChestsPage,
		PetsPage petsPage,
		CollectionsPage collectionsPage,
		GardenPage gardenPage,
		MiningPage miningPage,
		ForagingPage foragingPage,
		FishingPage fishingPage,
		FishingTrophyTrialPage fishingTrophyTrialPage,
		CrimsonPage crimsonPage,
		RiftPage riftPage,
		MuseumPage museumPage,
		EventsPage eventsPage,
		InventoryPage inventoryPage,
		BestiaryPage bestiaryPage
	) {
		boolean footerHover = selector.footerHover(mouseX, mouseY);
		List<PvTooltip.Line> profileTip = selector.tooltip(mouseX, mouseY, footerHover);
		if (profileTip != null && !selector.menuOpen()) {
			PvTooltip.drawStyled(g, font, profileTip, mouseX, mouseY, width, height);
		}
		if (selector.menuOpen() && selector.combinedHover(mouseX, mouseY)) {
			return;
		}

		int bodyX = panelX + PAD;
		int bodyY = panelY + PAD;
		int bodyW = panelW - PAD * 2;
		int bodyH = panelH - PAD * 2;

		switch (tab) {
			case HOME -> {
				if (homeSub == PvSubTab.HOME_MISC) {
					homeMiscPage.renderTooltip(g, font, mouseX, mouseY, width, height);
				} else if (homeSub == PvSubTab.HOME_RECORDS) {
					homeRecordsPage.renderTooltip(g, font, mouseX, mouseY, width, height);
				} else if (homeSub == PvSubTab.HOME_ESSENCE) {
					homeEssencePage.renderTooltip(g, font, mouseX, mouseY, width, height);
				} else {
					homePage.renderTooltip(g, font, mouseX, mouseY, width, height);
				}
			}
			case DUNGEONS -> {
				if (homeSub == PvSubTab.DUNGEON_CHESTS) {
					dungeonChestsPage.renderTooltip(g, font, mouseX, mouseY, width, height);
				} else {
					dungeonPage.renderTooltip(g, font, mouseX, mouseY, width, height);
				}
			}
			case PETS -> petsPage.renderTooltip(g, font, mouseX, mouseY, width, height);
			case COLLECTIONS -> collectionsPage.renderTooltip(g, font, mouseX, mouseY, width, height);
			case GARDEN -> gardenPage.renderTooltip(g, font, mouseX, mouseY, width, height);
			case MINING -> miningPage.renderTooltip(g, font, mouseX, mouseY, width, height);
			case FORAGING -> foragingPage.renderTooltip(g, font, mouseX, mouseY, width, height);
			case FISHING -> {
				if (homeSub == PvSubTab.FISHING_TROPHIES) {
					fishingTrophyTrialPage.renderTooltip(g, font, mouseX, mouseY, width, height);
				} else {
					fishingPage.renderTooltip(g, font, mouseX, mouseY, width, height);
				}
			}
			case CRIMSON -> crimsonPage.renderTooltip(g, font, mouseX, mouseY, width, height);
			case RIFT -> riftPage.renderTooltip(g, font, mouseX, mouseY, width, height);
			case MUSEUM -> museumPage.renderTooltip(g, font, mouseX, mouseY, width, height);
			case EVENTS -> eventsPage.renderTooltip(
				g, font, mouseX, mouseY, width, height, bodyX, bodyY, bodyW, bodyH
			);
			default -> { }
		}

		if (tab.isInventorySplit()) {
			inventoryPage.renderTooltip(g, font, mouseX, mouseY, width, height);
		}
		if (tab.isBestiarySplit()) {
			bestiaryPage.renderTooltip(g, font, mouseX, mouseY, width, height);
			if (bestiaryLayout.categoryTip() != null) {
				inventoryBar.maybeTooltip(
					g,
					font,
					bestiaryLayout.categoryTip(),
					true,
					bestiaryLayout.categoryTipX(),
					bestiaryLayout.categoryTipY()
				);
			}
		}
	}
}
