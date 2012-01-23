package org.uilib.widget.util;

import org.eclipse.swt.widgets.Table;

import org.uilib.util.Throttle;
import org.uilib.util.prefs.PrefStore;

public final class TableUtils {

	//~ Methods --------------------------------------------------------------------------------------------------------

	public static void rememberColumnSizes(final PrefStore prefStore, final Throttle throttler, final Table table,
										   final String memoryKey, final int defaultSize) {
		new TableColumnSizeMemory(prefStore, throttler, table, memoryKey, defaultSize);
	}

	public static void rememberColumnOrder(final PrefStore prefStore, final Throttle throttler, final Table table,
										   final String memoryKey) {
		new TableColumnOrderMemory(prefStore, throttler, table, memoryKey);
	}

	public static void setColumnsFillWidth(final Table table) {
		new FillTableLayout(table);
	}
}