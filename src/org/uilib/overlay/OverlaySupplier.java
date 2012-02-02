package org.uilib.overlay;

import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Region;
import org.eclipse.swt.widgets.Canvas;

public interface OverlaySupplier {

	//~ Methods --------------------------------------------------------------------------------------------------------

	Region getRegion();

	ImageData getImageData(final int overlayWidth, final int overlayHeight);

	void dispose();

	/* for redrawing of animations */
	void setCanvas(final Canvas canvas);
}