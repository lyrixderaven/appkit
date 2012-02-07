package org.uilib.widget.util;

import com.google.common.collect.Lists;
import com.google.common.primitives.Ints;

import java.util.List;

import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ColumnAutoSizer {

	//~ Static fields/initializers -------------------------------------------------------------------------------------

	private static final Logger L = LoggerFactory.getLogger(ColumnAutoSizer.class);
	private static final int BASE = 10000;

	//~ Instance fields ------------------------------------------------------------------------------------------------

	private final ColumnController colController;
	private final List<Integer> weights;

	//~ Constructors ---------------------------------------------------------------------------------------------------

	protected ColumnAutoSizer(final ColumnController colController) {
		this.colController     = colController;
		this.weights		   = Lists.newArrayList();

		/* add table resize-listener */
		this.colController.installControlListener(new ControlResizeListener());

		/* add column resize-listener */
		for (int i = 0; i < colController.getColumnCount(); i++) {
			this.colController.installColumnControlListener(i, new ColumnResizeListener());
		}
	}

	//~ Methods --------------------------------------------------------------------------------------------------------

	private final void recalculateWeights() {
		if (weights.isEmpty()) {
			for (int i = 0; i < colController.getColumnCount(); i++) {
				weights.add(0);
			}
		}

		int controlWidth = this.colController.getAvailWidth();
		for (int i = 0; i < colController.getColumnCount(); i++) {

			int columnWidth = colController.getWidth(i);
			int weight	    = Ints.saturatedCast(Math.round((double) columnWidth / controlWidth * BASE));

			L.debug("calculated weight {} for column {}", weight, i);

			this.weights.set(i, weight);
		}
	}

	private final void sizeColumns() {

		int controlWidth = this.colController.getAvailWidth();
		double fraction  = controlWidth / (double) BASE;

		L.debug("autosizing columns - control width {}, fraction {}", controlWidth, fraction);

		int sum = 0;
		for (int i = 0; i < colController.getColumnCount(); i++) {
			L.debug("weight for column {} is {}", i, this.weights.get(i));

			int width = Ints.saturatedCast(Math.round(this.weights.get(i) * fraction));

			sum = sum + width;

			/* if it's the last column it just up to 5px larger than the table we reduce it a bit to reduce flickering of the scrollbar */
			if (i == (colController.getColumnCount() - 1)) {

				int diff = sum - controlWidth;
				if ((diff > 0) && (diff < 5)) {
					width = width - diff;
				}
			}

			L.debug("sizing column {} to width {}", i, width);
			this.colController.setWidth(i, width);
		}
	}

	//~ Inner Classes --------------------------------------------------------------------------------------------------

	private class ControlResizeListener implements ControlListener {
		@Override
		public void controlMoved(final ControlEvent event) {}

		@Override
		public void controlResized(final ControlEvent event) {
			L.debug("control resized -> resizing columns");
			if (weights.isEmpty()) {
				L.debug("initial weight calculation");
				recalculateWeights();
			}
			sizeColumns();
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
			}

			if (colController.getAvailWidth() == lastAvailWidth) {
				L.debug("columns resized -> recalculating weights");
				recalculateWeights();
			} else {
				L.debug("lastAvail: {}, avail: {}", lastAvailWidth, colController.getAvailWidth());
				lastAvailWidth = colController.getAvailWidth();
			}
		}
	}
}