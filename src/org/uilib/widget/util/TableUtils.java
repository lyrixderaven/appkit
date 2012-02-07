package org.uilib.widget.util;

import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
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
	 */
	public static void autosizeColumns(final Table table) {
		new ColumnAutoSizer(new ColumnController.TableColumnController(table));
	}

	public static void fillTableWidth(final Table table) {

		final ControlListener controlListener =
			new ControlListener() {
				@Override
				public void controlResized(final ControlEvent event) {

					int width = table.getBounds().width / table.getColumnCount();
					for (int i = 0; i < table.getColumnCount(); i++) {
						table.getColumn(i).setWidth(width);
					}

					table.removeControlListener(this);
				}

				@Override
				public void controlMoved(final ControlEvent event) {}
			};

		table.addControlListener(controlListener);
	}

	public static void installScrollListener(final Table table, final ScrollListener listener) {
		new TableScrollDetector(table, listener);
	}
}