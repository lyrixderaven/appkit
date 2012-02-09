package org.uilib.widget.util;

import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.widgets.Tree;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.uilib.util.Throttle;
import org.uilib.util.prefs.PrefStore;

/**
 * various utility-functions for working with {@link Tree}s
 *
 */
public final class TreeUtils {

	//~ Static fields/initializers -------------------------------------------------------------------------------------

	private static final Logger L = LoggerFactory.getLogger(TreeUtils.class);

	//~ Methods --------------------------------------------------------------------------------------------------------

	/**
	 * restores column-weights, tracks and saves changes.
	 *
	 * @param prefStore the prefStore used to load and save weights
	 * @param throttler throttler used to throttle calls to the prefStore
	 * @param memoryKey prefStore key to use
	 */
	public static void rememberColumnWeights(final PrefStore prefStore, final Throttle throttler, final Tree tree,
											 final String memoryKey) {
		new ColumnWeightMemory(new ColumnController.TreeColumnController(tree), prefStore, throttler, memoryKey);
	}

	/**
	 * restores column-order, tracks and saves changes.
	 *
	 * @param prefStore the prefStore used to load and save order
	 * @param throttler throttler used to throttle calls to the prefStore
	 * @param memoryKey prefStore key to use
	 */
	public static void rememberColumnOrder(final PrefStore prefStore, final Throttle throttler, final Tree tree,
										   final String memoryKey) {
		new ColumnOrderMemory(new ColumnController.TreeColumnController(tree), prefStore, throttler, memoryKey);
	}

	/**
	 * installs a listener that proportionally resizes all columns when the tree is resized.
	 *
	 */
	public static void autosizeColumns(final Tree tree) {
		new ColumnAutoSizer(new ColumnController.TreeColumnController(tree));
	}

	/**
	 * resizes all columns equally to fill the entire width of the tree
	 *
	 */
	public static void fillTreeWidth(final Tree tree) {

		final ControlListener controlListener =
			new ControlListener() {
				@Override
				public void controlResized(final ControlEvent event) {

					int width = tree.getClientArea().width;
					width = width - (tree.getBorderWidth() * 2);

					int colWidth = width / tree.getColumnCount();

					L.debug("fillTreeWidth: set column width to {}", colWidth);

					for (int i = 0; i < tree.getColumnCount(); i++) {
						tree.getColumn(i).setWidth(colWidth);
					}

					tree.removeControlListener(this);
					L.debug("fillTreeWidth: done and listener removed");
				}

				@Override
				public void controlMoved(final ControlEvent event) {}
			};

		tree.addControlListener(controlListener);
	}
}