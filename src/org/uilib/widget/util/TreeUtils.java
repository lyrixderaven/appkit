package org.uilib.widget.util;

import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.widgets.Tree;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.uilib.util.Throttle;
import org.uilib.util.prefs.PrefStore;

public final class TreeUtils {

	//~ Static fields/initializers -------------------------------------------------------------------------------------

	private static final Logger L = LoggerFactory.getLogger(TreeUtils.class);

	//~ Methods --------------------------------------------------------------------------------------------------------

	public static void rememberColumnWeights(final PrefStore prefStore, final Throttle throttler, final Tree tree,
											 final String memoryKey) {
		new ColumnWeightMemory(new ColumnController.TreeColumnController(tree), prefStore, throttler, memoryKey);
	}

	public static void rememberColumnOrder(final PrefStore prefStore, final Throttle throttler, final Tree tree,
										   final String memoryKey) {
		new ColumnOrderMemory(new ColumnController.TreeColumnController(tree), prefStore, throttler, memoryKey);
	}

	/** installs a listener that automatically sizes the columns when the tree is resized.
	 *
	 * @param tree
	 */
	public static void autosizeColumns(final Tree tree) {
		new ColumnAutoSizer(new ColumnController.TreeColumnController(tree));
	}

	public static void fillTreeWidth(final Tree tree) {

		final ControlListener controlListener =
			new ControlListener() {
				@Override
				public void controlResized(final ControlEvent event) {

					int width = tree.getBounds().width / tree.getColumnCount() - 5;
					L.debug("fillTreeWidth: set column size to {}", width);

					for (int i = 0; i < tree.getColumnCount(); i++) {
						tree.getColumn(i).setWidth(width);
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