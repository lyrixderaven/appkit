package org.uilib.swt.memory;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.primitives.Ints;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import org.uilib.swt.SWTSyncedRunnable;
import org.uilib.util.PrefStore;
import org.uilib.util.SmartExecutor;

public final class TableColumnOrderMemory {

	//~ Static fields/initializers -------------------------------------------------------------------------------------

	@SuppressWarnings("unused")
	private static final Logger L							 = Logger.getLogger(TableColumnOrderMemory.class);

	//~ Instance fields ------------------------------------------------------------------------------------------------

	private final PrefStore prefStore;
	private final Table table;
	private final String memoryKey;

	//~ Constructors ---------------------------------------------------------------------------------------------------

	private TableColumnOrderMemory(final PrefStore prefStore, final Table table, final String memoryKey) {
		this.prefStore										 = prefStore;
		this.table											 = table;
		this.memoryKey										 = memoryKey + ".columnorder";

		/* reorder columns */
		String orderString     = this.prefStore.get(memoryKey, "");
		List<String> orderList = Lists.newArrayList(Splitter.on(",").split(orderString));
		if (orderList.size() == this.table.getColumnCount()) {
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

	public static void install(final PrefStore prefStore, final Table table, final String memoryKey) {
		new TableColumnOrderMemory(prefStore, table, memoryKey);
	}

	//~ Inner Classes --------------------------------------------------------------------------------------------------

	private class ColumnMoveListener implements ControlListener {
		@Override
		public void controlMoved(final ControlEvent event) {
			SmartExecutor.instance().throttle(
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