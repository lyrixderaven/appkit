package org.uilib.overlay;

import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Region;
import org.eclipse.swt.widgets.Canvas;

public interface OverlaySupplier {

	//~ Methods --------------------------------------------------------------------------------------------------------

	/**
	 * returns the region the overlay should occupy <b>This doesn't work yet</b>
	 */
	Region getRegion();

	/**
	 * renders the image given the size and returns it
	 *
	 * @param overlayWidth width of overlay
	 * @param overlayHeight
	 */
	ImageData getImageData(final int overlayWidth, final int overlayHeight);

	/**
	 * disposes this Supplier
	 */
	void dispose();

	/**
	 * used to hand the used canvas over to this Supplier so it can redraw the it
	 */
	void setCanvas(final Canvas canvas);
}