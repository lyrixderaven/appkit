package org.uilib.widget.util;

import java.util.List;

import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ColumnAutoSizer {

	//~ Static fields/initializers -------------------------------------------------------------------------------------

	private static final Logger L = LoggerFactory.getLogger(ColumnAutoSizer.class);

	//~ Instance fields ------------------------------------------------------------------------------------------------

	private final ColumnController colController;
	private List<Integer> weights;

	//~ Constructors ---------------------------------------------------------------------------------------------------

	protected ColumnAutoSizer(final ColumnController colController) {
		this.colController		  = colController;

		/* add table resize-listener */
		this.colController.installControlListener(new ControlResizeListener());

		/* add column resize-listener */
		for (int i = 0; i < colController.getColumnCount(); i++) {
			this.colController.installColumnControlListener(i, new ColumnResizeListener());
		}
	}

	//~ Inner Classes --------------------------------------------------------------------------------------------------

	private class ControlResizeListener implements ControlListener {
		@Override
		public void controlMoved(final ControlEvent event) {}

		@Override
		public void controlResized(final ControlEvent event) {
			L.debug("control resized -> resizing columns");
			if (weights == null) {
				L.debug("initial weight calculation");
				weights = colController.calculateWeights();
			}
			colController.setWeights(weights);
		}
	}

	private class ColumnResizeListener implements ControlListener {

		private int lastAvailWidth = -1;

		@Override
		public void controlMoved(final ControlEvent event) {}

		@Override
		public void controlResized(final ControlEvent event) {
			if (lastAvailWidth == -1) {
				lastAvailWidth = colController.getAvailWidth();
				return;
			}

			if (colController.getAvailWidth() == lastAvailWidth) {
				L.debug(
					"width {} didn't change -> columns resized -> recalculating weights",
					colController.getAvailWidth());
				weights = colController.calculateWeights();
			} else {
				L.debug(
					"width {} differs from lastWidth {} -> control resized",
					colController.getAvailWidth(),
					lastAvailWidth);
				lastAvailWidth = colController.getAvailWidth();
			}
		}
	}
}