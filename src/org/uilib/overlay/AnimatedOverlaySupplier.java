package org.uilib.overlay;

import java.util.concurrent.TimeUnit;

public interface AnimatedOverlaySupplier extends OverlaySupplier {

	//~ Methods --------------------------------------------------------------------------------------------------------

	void tick();

	/**
	 * allows the Overlay to specify how often it should be repainted
	 */
	long getTickerTime(final TimeUnit targetUnit);
}