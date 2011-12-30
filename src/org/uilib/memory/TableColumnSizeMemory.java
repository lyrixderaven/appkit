package org.uilib.memory;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import org.uilib.util.PrefStore;
import org.uilib.util.SWTSyncedRunnable;
import org.uilib.util.Throttler;

// FIXME: rename: table utils, shell utils, widget utils?
public final class TableColumnSizeMemory {

	//~ Static fields/initializers -------------------------------------------------------------------------------------

	private static final Logger L = Logger.getLogger(TableColumnSizeMemory.class);

	//~ Instance fields ------------------------------------------------------------------------------------------------

	private final PrefStore prefStore;
	private final Throttler throttler;
	private final Table table;
	private final String memoryKey;
	private final int defaultWeight;

	//~ Constructors ---------------------------------------------------------------------------------------------------

	private TableColumnSizeMemory(final PrefStore prefStore, final Throttler throttler, final Table table,
								  final String key, final int defaultWeight) {
		this.prefStore			  = prefStore;
		this.throttler			  = throttler;
		this.table				  = table;
		this.memoryKey			  = key + ".columnsizes";
		this.defaultWeight		  = defaultWeight;

		/* install layout into parentComposite of Table */
		TableColumnLayout layout = new TableColumnLayout();
		table.getParent().setLayout(layout);

		/* size all the columns */
		String widthString = this.prefStore.get(this.memoryKey, "");
		L.debug("widthString: " + widthString);

		List<String> widths = Lists.newArrayList(Splitter.on(",").split(widthString));
		if (widths.size() == table.getColumnCount()) {
			L.debug("valid width " + widths + " -> sizing columns");
			for (int i = 0; i < table.getColumnCount(); i++) {

				int wData = defaultWeight;
				try {
					wData = Integer.valueOf(widths.get(i));
				} catch (final NumberFormatException e) {}

				layout.setColumnData(table.getColumn(i), new ColumnWeightData(wData));
			}
		} else {
			L.debug("setting default widths");

			/* default size */
			for (final TableColumn column : table.getColumns()) {
				layout.setColumnData(column, new ColumnWeightData(this.defaultWeight));
			}
		}

		/* add listeners */
		for (final TableColumn column : table.getColumns()) {
			column.addControlListener(new ColumnResizeListener());
		}
	}

	//~ Methods --------------------------------------------------------------------------------------------------------

	public static void install(final PrefStore prefStore, final Throttler throttler, final Table table,
							   final String memoryKey, final int defaultSize) {
		new TableColumnSizeMemory(prefStore, throttler, table, memoryKey, defaultSize);
	}

	//~ Inner Classes --------------------------------------------------------------------------------------------------

	private class ColumnResizeListener implements ControlListener {
		@Override
		public void controlMoved(final ControlEvent event) {}

		@Override
		public void controlResized(final ControlEvent event) {
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

							List<Integer> widths = Lists.newArrayList();
							for (final TableColumn column : table.getColumns()) {
								widths.add(column.getWidth());
							}
							prefStore.store(memoryKey, Joiner.on(",").join(widths));
						}
					});
		}
	}
}