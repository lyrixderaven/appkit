package org.uilib.widget.util;

import com.google.common.base.Preconditions;
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

	protected ColumnAutoSizer(final ColumnController colController, final List<Integer> initialWeights) {
		Preconditions.checkArgument(
			colController.getColumnCount() == initialWeights.size(),
			"weight-count %s must equal column-count %s",
			initialWeights.size(),
			colController.getColumnCount());

		this.colController = colController;

		/* sum up the weights */
		int sum = 0;
		for (final int weight : initialWeights) {
			sum = sum + weight;
		}

		this.weights = Lists.newArrayList();
		for (int i = 0; i < colController.getColumnCount(); i++) {

			int weight = initialWeights.get(i);

			/* calculate weight */
			int correctedWeight = Ints.saturatedCast(Math.round((double) weight / sum * BASE));

			/* save weight */
			this.weights.add(correctedWeight);
		}

		/* size the columns */
		this.sizeColumns();

		/* add table resize-listener */
		this.colController.installControlListener(new ControlResizeListener());

		/* add column resize-listener */
		for (int i = 0; i < colController.getColumnCount(); i++) {
			this.colController.installColumnControlListener(i, new ColumnResizeListener());
		}
	}

	//~ Methods --------------------------------------------------------------------------------------------------------

	private final void recalculateWeights() {

		int controlWidth = this.colController.getAvailWidth();

		for (int i = 0; i < colController.getColumnCount(); i++) {

			int columnWidth = colController.getWidth(i);
			int weight	    = Ints.saturatedCast(Math.round((double) columnWidth / controlWidth * BASE));

			L.debug("calculated width {} for {} column", weight, i);

			this.weights.set(i, weight);
		}
	}

	private final void sizeColumns() {

		int controlWidth = this.colController.getAvailWidth();
		double fraction  = controlWidth / (double) BASE;

		L.debug("autosizing columns - control width {}, fraction {}", controlWidth, fraction);

		for (int i = 0; i < colController.getColumnCount(); i++) {

			int width = Ints.saturatedCast(Math.round(this.weights.get(i) * fraction));
			L.debug("sizing column {}, to width {}", i, width);

			/* -1 to avoid flickers of scrollbar */
			this.colController.setWidth(i, width - 1);
		}
	}

	//~ Inner Classes --------------------------------------------------------------------------------------------------

	private class ControlResizeListener implements ControlListener {
		@Override
		public void controlMoved(final ControlEvent event) {}

		@Override
		public void controlResized(final ControlEvent event) {
			L.debug("control resized");
			sizeColumns();
		}
	}

	private class ColumnResizeListener implements ControlListener {

		/* for tracking whether the whole table resizes */
		private int lastControlWidth = -1;

		@Override
		public void controlMoved(final ControlEvent event) {}

		@Override
		public void controlResized(final ControlEvent event) {
			if (colController.getAvailWidth() != lastControlWidth) {
				lastControlWidth = colController.getAvailWidth();
			} else {
				L.debug("column resized");
				recalculateWeights();
			}
		}
	}
}