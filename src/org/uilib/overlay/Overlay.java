package org.uilib.overlay;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Overlay {

	//~ Static fields/initializers -------------------------------------------------------------------------------------

	@SuppressWarnings("unused")
	private static final Logger L							 = LoggerFactory.getLogger(Overlay.class);

	//~ Instance fields ------------------------------------------------------------------------------------------------

	private final Composite comp;
	private final OverlaySupplier supplier;
	private Shell overlayShell;
	private ControlListener compResizeListener;

	//~ Constructors ---------------------------------------------------------------------------------------------------

	public Overlay(final Composite comp, final OverlaySupplier supplier) {
		this.comp											 = comp;
		this.supplier										 = supplier;
	}

	//~ Methods --------------------------------------------------------------------------------------------------------

	public void show() {
		/* create overlay shell with canvas */
		this.overlayShell = new Shell(comp.getShell(), SWT.NO_TRIM);
		this.overlayShell.setLayout(new FillLayout());

		final Canvas canvas = new Canvas(this.overlayShell, SWT.BORDER);

		this.supplier.setCanvas(canvas);

		/* add resize listener to comp */
		this.compResizeListener =
			new ControlListener() {
					@Override
					public void controlMoved(final ControlEvent event) {
						canvas.redraw();
						adjustLocation();
						canvas.redraw();
					}

					@Override
					public void controlResized(final ControlEvent event) {
						canvas.redraw();
						adjustSize();
						canvas.redraw();
					}
				};
		this.comp.addControlListener(compResizeListener);

		canvas.addPaintListener(
			new PaintListener() {
					@Override
					public void paintControl(final PaintEvent event) {

						int width    = comp.getBounds().width;
						int height   = comp.getBounds().height;

						Image buffer = new Image(Display.getCurrent(), width, height);
						GC gcBuffer  = new GC(buffer);

						/* take photo of composite into image */
						GC gcHiddenComp = new GC(comp.getParent());
						gcHiddenComp.copyArea(buffer, 0, 0);
						gcHiddenComp.dispose();

						/* get overlay image from supplier */
						Image image = new Image(Display.getCurrent(), supplier.getImageData(width, height));
						gcBuffer.drawImage(image, 0, 0);
						gcBuffer.dispose();
						image.dispose();

						event.gc.drawImage(buffer, 0, 0);
						buffer.dispose();
					}
				});

		/* set initial size and locatio and open */
		adjustLocation();
		adjustSize();
		overlayShell.open();
	}

	/** dispose the overlay */
	public void dispose() {
		this.supplier.dispose();
		this.comp.removeControlListener(this.compResizeListener);
		this.overlayShell.dispose();
	}

	/** move the shell where the comp is */
	private void adjustLocation() {

		final Rectangle compBounds = this.comp.getBounds();
		final Point absLocation    = this.comp.getParent().toDisplay(compBounds.x, compBounds.y);
		this.overlayShell.setLocation(absLocation);
	}

	/** size to shell to cover the comp */
	private void adjustSize() {

		final Rectangle compBounds = this.comp.getBounds();
		this.overlayShell.setSize(compBounds.width, compBounds.height);
	}
}