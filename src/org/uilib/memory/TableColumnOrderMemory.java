package org.uilib.memory;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.primitives.Ints;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.uilib.util.PrefStore;
import org.uilib.util.SWTSyncedRunnable;
import org.uilib.util.Throttler;

public final class TableColumnOrderMemory {

	//~ Static fields/initializers -------------------------------------------------------------------------------------

	private static final Logger L = LoggerFactory.getLogger(TableColumnOrderMemory.class);

	//~ Instance fields ------------------------------------------------------------------------------------------------

	private final PrefStore prefStore;
	private final Throttler throttler;
	private final Table table;
	private final String memoryKey;

	//~ Constructors ---------------------------------------------------------------------------------------------------

	private TableColumnOrderMemory(final PrefStore prefStore, final Throttler throttler, final Table table,
								   final String key) {
		this.prefStore			  = prefStore;
		this.throttler			  = throttler;
		this.table				  = table;
		this.memoryKey			  = key + ".columnorder";

		/* reorder columns */
		String orderString = this.prefStore.get(this.memoryKey, "");
		L.debug("orderString: " + orderString);

		List<String> orderList = Lists.newArrayList(Splitter.on(",").split(orderString));
		if (orderList.size() == this.table.getColumnCount()) {
			L.debug("valid order " + orderList + " -> reordering columns");
			try {

				int order[] = new int[this.table.getColumnCount()];
				int i	    = 0;
				for (final String pos : orderList) {
					order[i] = Integer.valueOf(pos);
					i++;
				}

				this.table.setColumnOrder(order);
			} catch (final NumberFormatException e) {}
		}

		for (final TableColumn column : table.getColumns()) {
			/* set column movable */
			column.setMoveable(true);

			/* add listener */
			column.addControlListener(new ColumnMoveListener());
		}
	}

	//~ Methods --------------------------------------------------------------------------------------------------------

	public static void install(final PrefStore prefStore, final Throttler throttler, final Table table,
							   final String memoryKey) {
		new TableColumnOrderMemory(prefStore, throttler, table, memoryKey);
	}

	//~ Inner Classes --------------------------------------------------------------------------------------------------

	private class ColumnMoveListener implements ControlListener {
		@Override
		public void controlMoved(final ControlEvent event) {
			throttler.throttle(
				memoryKey,
				50,
				TimeUnit.MILLISECONDS,
				new SWTSyncedRunnable() {
						@Override
						protected void runChecked() {
							if (table.isDisposed()) {
								return;
							}

							List<Integer> order = Ints.asList(table.getColumnOrder());
							prefStore.store(memoryKey, Joiner.on(",").join(order));
						}
					});
		}

		@Override
		public void controlResized(final ControlEvent event) {}
	}
}