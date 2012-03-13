package org.appkit.widget.util;

import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Table;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.appkit.util.SmartExecutor;
import org.appkit.util.Throttle;
import org.appkit.util.prefs.PrefStore;
import org.appkit.widget.util.TableScrollDetector.ScrollListener;

/**
 * various utility-functions for working with {@link Table}s
 *
 */
public final class TableUtils {

	//~ Static fields/initializers -------------------------------------------------------------------------------------

	private static final Logger L = LoggerFactory.getLogger(TableUtils.class);

	//~ Methods --------------------------------------------------------------------------------------------------------

	/**
	 * restores column-weights, tracks and saves changes.
	 *
	 * @param prefStore the prefStore used to load and save weights
	 * @param executor used to create a {@link Throttle} to the save function
	 * @param memoryKey prefStore key to use
	 */
	public static void rememberColumnWeights(final PrefStore prefStore, final SmartExecutor executor,
											 final Table table, final String memoryKey) {
		new ColumnWeightMemory(new ColumnController.TableColumnController(table), prefStore, executor, memoryKey);
	}

	/**
	 * restores column-order, tracks and saves changes.
	 *
	 * @param prefStore the prefStore used to load and save order
	 * @param executor used to create a {@link Throttle} to the save function
	 * @param memoryKey prefStore key to use
	 */
	public static void rememberColumnOrder(final PrefStore prefStore, final SmartExecutor executor, final Table table,
										   final String memoryKey) {
		new ColumnOrderMemory(new ColumnController.TableColumnController(table), prefStore, executor, memoryKey);
	}

	/**
	 * installs a listener that proportionally resizes all columns when the table is resized.
	 *
	 */
	public static void autosizeColumns(final Table table) {
		new ColumnAutoSizer(new ColumnController.TableColumnController(table));
	}

	/**
	 * resizes all columns equally to fill the entire width of the table
	 *
	 */
	public static void fillTableWidth(final Table table) {

		final ControlListener controlListener =
			new ControlListener() {
				@Override
				public void controlResized(final ControlEvent event) {
					if (table.getColumnCount() != 0) {

						int width = table.getClientArea().width;
						width = width - (table.getBorderWidth() * 2);

						int colWidth = width / table.getColumnCount();

						L.debug("fillTableWidth: set column width to {}", colWidth);
						for (int i = 0; i < table.getColumnCount(); i++) {
							table.getColumn(i).setWidth(colWidth);
						}
					} else {
						L.debug("fillTableWidth: no columns in table");
					}

					table.removeControlListener(this);
					L.debug("fillTableWidth: done and listener removed");
				}

				@Override
				public void controlMoved(final ControlEvent event) {}
			};

		table.addControlListener(controlListener);
	}

	/**
	 * installs a ScrollListener on the table
	 */
	public static void installScrollListener(final Table table, final ScrollListener listener) {
		new TableScrollDetector(table, listener);
	}

	/**
	 * returns the last visible row
	 *
	 * @return -1 if no data in the table
	 */
	public static int getBottomIndex(final Table table) {
		if (table.getItemCount() == 0) {
			return -1;
		}

		Rectangle rect   = table.getClientArea();
		int itemHeight   = table.getItemHeight();
		int headerHeight = table.getHeaderHeight();

		int visibleCount = ((rect.height - headerHeight + itemHeight) - 1) / itemHeight;

		return (table.getTopIndex() + visibleCount) - 1;
	}
}