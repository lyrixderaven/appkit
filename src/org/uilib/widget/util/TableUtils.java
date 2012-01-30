package org.uilib.widget.util;

import com.google.common.collect.Lists;

import java.util.List;

import org.eclipse.swt.widgets.Table;

import org.uilib.util.Throttle;
import org.uilib.util.prefs.PrefStore;
import org.uilib.widget.util.TableScrollDetector.ScrollListener;

public final class TableUtils {

	//~ Methods --------------------------------------------------------------------------------------------------------

	public static void rememberColumnSizes(final PrefStore prefStore, final Throttle throttler, final Table table,
										   final String memoryKey) {
		new ColumnSizeMemory(new ColumnController.TableColumnController(table), prefStore, throttler, memoryKey);
	}

	public static void rememberColumnOrder(final PrefStore prefStore, final Throttle throttler, final Table table,
										   final String memoryKey) {
		new ColumnOrderMemory(new ColumnController.TableColumnController(table), prefStore, throttler, memoryKey);
	}

	/** installs a listener that automatically sizes the columns when the table is resized.
	 *
	 * @param table
	 * @param initialWeights initial weights for the column, the length of this array has to equal the column-count of the table.
	 */
	public static void autosizeColumns(final Table table, final List<Integer> initialWeights) {
		new ColumnAutoSizer(new ColumnController.TableColumnController(table), initialWeights);
	}

	public static void autosizeColumns(final Table table) {

		List<Integer> weights = Lists.newArrayList();
		for (int i = 0; i < table.getColumnCount(); i++) {
			weights.add(table.getColumn(i).getWidth());
		}

		new ColumnAutoSizer(new ColumnController.TableColumnController(table), weights);
	}

	public static void installScrollListener(final Table table, final ScrollListener listener) {
		new TableScrollDetector(table, listener);
	}
}