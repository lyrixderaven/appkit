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
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

// TODO: OverLay Region, so you can access parts of it
public final class Overlay {

	//~ Instance fields ------------------------------------------------------------------------------------------------

	private final Composite comp;
	private Shell overlayShell;
	private ControlListener compResizeListener;

	//~ Constructors ---------------------------------------------------------------------------------------------------

	public Overlay(final Composite comp) {
		this.comp = comp;
	}

	//~ Methods --------------------------------------------------------------------------------------------------------

	public void show() {
		this.overlayShell		    = new Shell(comp.getShell(), SWT.NO_TRIM);

		this.compResizeListener =
			new ControlListener() {
					@Override
					public void controlMoved(final ControlEvent event) {
						cover();
						overlayShell.redraw();
					}

					@Override
					public void controlResized(final ControlEvent event) {
						cover();
						overlayShell.redraw();
					}
				};

		this.comp.addControlListener(compResizeListener);

		this.overlayShell.addPaintListener(
			new PaintListener() {
					@Override
					public void paintControl(final PaintEvent event) {

						Image buffer = new Image(Display.getCurrent(), comp.getBounds());

						/* take photo of composite into image */
						GC gc = new GC(comp.getParent());
						gc.copyArea(buffer, 0, 0);
						gc.dispose();

						/* draw a line */
						GC gcBuffer = new GC(buffer);
						gcBuffer.drawLine(0, 0, comp.getBounds().width, comp.getBounds().height);
						gcBuffer.dispose();

						event.gc.drawImage(buffer, 0, 0);
						buffer.dispose();
					}
				});

		cover();
		overlayShell.open();
	}

	public void dispose() {
		this.comp.removeControlListener(this.compResizeListener);
		this.overlayShell.dispose();
	}

	private void cover() {

		final Rectangle compBounds = this.comp.getBounds();

		final Point absLocation    = this.comp.getParent().toDisplay(compBounds.x, compBounds.y);
		this.overlayShell.setLocation(absLocation);
		this.overlayShell.setSize(compBounds.width, compBounds.height);
	}
}