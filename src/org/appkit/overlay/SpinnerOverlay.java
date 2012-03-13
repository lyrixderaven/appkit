package org.appkit.overlay;

import java.util.concurrent.TimeUnit;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.widgets.Display;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An {@link Overlay} that displays a semi-transparent spinner.
 *
 */
public final class SpinnerOverlay implements AnimatedOverlaySupplier {

	//~ Static fields/initializers -------------------------------------------------------------------------------------

	@SuppressWarnings("unused")
	private static final Logger L							 = LoggerFactory.getLogger(SpinnerOverlay.class);
	private static final int SPINNER_SIDE					 = 70;
	private static final int INNER_CIRCLE_RADIUS			 = 20;

	//~ Instance fields ------------------------------------------------------------------------------------------------

	private int step = 0;

	//~ Methods --------------------------------------------------------------------------------------------------------

	@Override
	public void dispose() {}

	@Override
	public int getAlpha() {
		return 90;
	}

	@Override
	public void tick() {

		int temp = this.step;
		temp++;
		if (temp == 12) {
			temp = 0;
		}
		this.step = temp;
	}

	@Override
	public long getTickerTime(final TimeUnit targetUnit) {
		return targetUnit.convert(70, TimeUnit.MILLISECONDS);
	}

	/** return an image of this spinner at it's current step */
	@Override
	public ImageData getImageData(final int overlayWidth, final int overlayHeight) {

		/* create a new image width the given height and width */
		Image overlayImage = new Image(Display.getCurrent(), overlayWidth, overlayHeight);

		if ((overlayWidth > SPINNER_SIDE) && (overlayWidth > SPINNER_SIDE)) {

			/* draw the spinner and set it in the middle of the image */
			GC gc			   = new GC(overlayImage);

			Image spinnerImage = this.drawSpinner();
			int x			   = rDiv(overlayWidth - spinnerImage.getBounds().width, 2);
			int y			   = rDiv(overlayHeight - spinnerImage.getBounds().height, 2);
			gc.drawImage(spinnerImage, x, y);

			/* dispose temp-stuff */
			spinnerImage.dispose();
			gc.dispose();
		}

		/* save image data and dispose image */
		ImageData imageData = overlayImage.getImageData();
		overlayImage.dispose();

		return imageData;
	}

	/* draw spinner */
	private final Image drawSpinner() {

		/* draw spinner */
		Image image = new Image(Display.getCurrent(), SPINNER_SIDE, SPINNER_SIDE);
		GC gc	    = new GC(image);

		/* fill rectangle with white */
		gc.fillRectangle(0, 0, image.getBounds().width, image.getBounds().height);
		gc.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_BLACK));

		/* draw rounded rectangle */
		int borderRadius = rDiv(SPINNER_SIDE, 4);
		gc.fillRoundRectangle(0, 0, SPINNER_SIDE, SPINNER_SIDE, borderRadius, borderRadius);

		/* normal arc-angle = 10 */
		int spans[] = new int[12];
		for (int i = 0; i < 12; i++) {
			spans[i] = 10;
		}

		/* active arc: 25, previous: 20, previous: 15 */
		spans[step]				    = 15;
		spans[(step + 11) % 12]     = 13;
		spans[(step + 10) % 12]     = 11;

		/* draw the arcs */
		gc.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		for (int i = 0; i < 12; i++) {

			int startAngle = (i * 30) - Math.round(spans[i] / (float) 2);
			this.paintArc(gc, startAngle, spans[i]);
		}

		/* draw circle in the middle */
		gc.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_BLACK));
		gc.fillOval(
			rDiv(SPINNER_SIDE, 2) - rDiv(INNER_CIRCLE_RADIUS, 2),
			rDiv(SPINNER_SIDE, 2) - rDiv(INNER_CIRCLE_RADIUS, 2),
			INNER_CIRCLE_RADIUS,
			INNER_CIRCLE_RADIUS);

		/* dispose gc */
		gc.dispose();

		return image;
	}

	/* utility function: division */
	private final int rDiv(final int dividend, final int divisor) {
		return Math.round(dividend / (float) divisor);
	}

	/* utility function: arc-painting */
	private final void paintArc(final GC gc, final int startAngle, final int span) {

		int adjustedStartAngle = startAngle - 90;

		int diameter		   = Math.round(SPINNER_SIDE * (float) 0.6);
		int x				   = rDiv(SPINNER_SIDE - diameter, 2);
		int y				   = x;
		gc.fillArc(x, y, diameter, diameter, -adjustedStartAngle, -span);
	}
}