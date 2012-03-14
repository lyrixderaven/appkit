package org.appkit.overlay;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import org.appkit.util.SWTSyncedTickReceiver;
import org.appkit.util.SmartExecutor;
import org.appkit.util.Ticker;
import org.appkit.util.Ticker.TickReceiver;
import org.appkit.widget.util.SWTUtils;

import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An overlay that can be displayed on top of an existing {@link Composite}.
 *
 * It currently uses a second shell that is modified to reflect size and position of the composite.
 *
 * <b>This is unfinished</b>
 *
 */
public final class Overlay {

	//~ Static fields/initializers -------------------------------------------------------------------------------------

	private static final Logger L = LoggerFactory.getLogger(Overlay.class);

	//~ Instance fields ------------------------------------------------------------------------------------------------

	private final SmartExecutor executor;
	private final Composite comp;
	private final OverlaySupplier supplier;

	/* data */
	private final Map<Control, PaintListener> registeredListeners = Maps.newHashMap();
	private Point lastCompSize									  = new Point(0, 0);
	private Ticker ticker;
	private Image currentImage;

	//~ Constructors ---------------------------------------------------------------------------------------------------

	/**
	 * creates a new overlay on the given {@link Composite}
	 * @param executor
	 */
	public Overlay(final SmartExecutor executor, final Composite comp, final OverlaySupplier supplier) {
		this.executor											  = executor;
		this.comp												  = comp;
		this.supplier											  = supplier;
	}

	//~ Methods --------------------------------------------------------------------------------------------------------

	/**
	 * shows the overlay
	 */
	public void show() {

		List<Control> controls = Lists.newArrayList();
		controls.add(this.comp);

		while (! controls.isEmpty()) {

			Control c = controls.remove(0);

			if (c instanceof Composite) {
				controls.addAll(Arrays.asList(((Composite) c).getChildren()));
			}

			L.debug("adding PaintListener to {}", c);

			PaintListener listener = new ControlPaintListener();
			c.addPaintListener(listener);
			this.registeredListeners.put(c, listener);
		}

		this.comp.redraw();

		if (this.supplier instanceof AnimatedOverlaySupplier) {

			final AnimatedOverlaySupplier aSupplier = (AnimatedOverlaySupplier) this.supplier;

			/* create a ticker */
			this.ticker =
				this.executor.createTicker(aSupplier.getTickerTime(TimeUnit.MILLISECONDS), TimeUnit.MILLISECONDS);
			this.ticker.notify(
				new SWTSyncedTickReceiver(
					comp.getDisplay(),
					new TickReceiver() {
							@Override
							public void tick() {
								aSupplier.tick();

								if (currentImage != null) {
									currentImage.dispose();
									currentImage = null;
								}

								comp.redraw();
								comp.update();
							}
						}));
		}
	}

	/**
	 * disposes the overlay
	 */
	public void dispose() {

		/* stop the animator */
		if (this.ticker != null) {
			this.ticker.stop();
		}

		/* remove the paint-listeners */
		for (final Entry<Control, PaintListener> entry : this.registeredListeners.entrySet()) {
			entry.getKey().removePaintListener(entry.getValue());
		}

		/* dispose the supplier */
		this.supplier.dispose();
	}

	//~ Inner Classes --------------------------------------------------------------------------------------------------

	private final class ControlPaintListener implements PaintListener {
		@Override
		public void paintControl(final PaintEvent e) {

			/* check if composite was resized */
			if ((currentImage == null) | ! comp.getSize().equals(lastCompSize)) {
				lastCompSize = comp.getSize();

				ImageData imageData = supplier.getImageData(comp.getSize().x, comp.getSize().y);
				imageData.alpha = supplier.getAlpha();

				if (currentImage != null) {
					currentImage.dispose();
				}

				/* make overlay image */
				currentImage = new Image(Display.getCurrent(), imageData);
			}

			Control c    = (Control) e.widget;
			Point refPos = SWTUtils.getPositionRelTo(c, comp);

			e.gc.drawImage(currentImage, refPos.x, refPos.y, e.width, e.height, 0, 0, e.width, e.height);
		}
	}
}