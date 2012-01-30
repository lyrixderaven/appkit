package org.uilib.widget.util;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.primitives.Ints;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.uilib.util.SWTSyncedRunnable;
import org.uilib.util.Throttle;
import org.uilib.util.prefs.PrefStore;

public final class ColumnOrderMemory {

	//~ Static fields/initializers -------------------------------------------------------------------------------------

	private static final Logger L = LoggerFactory.getLogger(ColumnOrderMemory.class);

	//~ Instance fields ------------------------------------------------------------------------------------------------

	private final PrefStore prefStore;
	private final Throttle throttler;
	private final ColumnController colController;
	private final String memoryKey;

	/* save order */
	private List<Integer> lastOrder;

	//~ Constructors ---------------------------------------------------------------------------------------------------

	protected ColumnOrderMemory(final ColumnController colController, final PrefStore prefStore,
								final Throttle throttler, final String key) {
		this.prefStore			  = prefStore;
		this.throttler			  = throttler;
		this.colController		  = colController;
		this.memoryKey			  = key + ".columnorder";

		/* reorder columns */
		String orderString = this.prefStore.get(this.memoryKey, "");
		L.debug("orderString: " + orderString);

		List<String> orderList = Lists.newArrayList(Splitter.on(",").split(orderString));
		if (orderList.size() == this.colController.getColumnCount()) {
			L.debug("valid order " + orderList + " -> reordering columns");
			try {

				int order[] = new int[this.colController.getColumnCount()];
				int i	    = 0;
				for (final String pos : orderList) {
					order[i] = Integer.valueOf(pos);
					i++;
				}

				this.colController.setColumnOrder(order);

			} catch (final NumberFormatException e) {}
		}

		/* save last order */
		this.lastOrder = Ints.asList(colController.getColumnOrder());

		/* set columns movable */
		this.colController.setColumnsMoveable();

		/* add listeners */
		for (int i = 0; i < this.colController.getColumnCount(); i++) {
			this.colController.installColumnControlListener(i, new ColumnMoveListener());
		}
	}

	//~ Methods --------------------------------------------------------------------------------------------------------

	private void saveOrder() {

		final List<Integer> order = Ints.asList(colController.getColumnOrder());
		if (order.equals(lastOrder)) {
			return;
		}

		this.lastOrder = order;

		final String orderString = Joiner.on(",").join(order);
		this.throttler.throttle(
			memoryKey,
			250,
			TimeUnit.MILLISECONDS,
			new SWTSyncedRunnable() {
					@Override
					public void runChecked() {
						L.debug("writing out order {} to key", orderString, memoryKey);
						prefStore.store(memoryKey, orderString);
					}
				});
	}

	//~ Inner Classes --------------------------------------------------------------------------------------------------

	private class ColumnMoveListener implements ControlListener {
		@Override
		public void controlMoved(final ControlEvent event) {
			saveOrder();
		}

		@Override
		public void controlResized(final ControlEvent event) {}
	}
}