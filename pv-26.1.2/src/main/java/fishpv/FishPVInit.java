package fishpv;

import net.fabricmc.api.ClientModInitializer;

public final class FishPVInit implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		BetterPVClient.init();
	}
}
