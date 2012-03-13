package org.appkit.overlay;

import org.eclipse.swt.graphics.ImageData;

public interface OverlaySupplier {

	//~ Methods --------------------------------------------------------------------------------------------------------

	/**
	 * renders the image given the size and returns it
	 *
	 * @param overlayWidth width of overlay
	 * @param overlayHeight
	 */
	ImageData getImageData(final int overlayWidth, final int overlayHeight);

	int getAlpha();

	/**
	 * disposes this Supplier
	 */
	void dispose();
}