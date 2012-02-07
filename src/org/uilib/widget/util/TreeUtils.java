package org.uilib.widget.util;

import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.widgets.Tree;

import org.uilib.util.Throttle;
import org.uilib.util.prefs.PrefStore;

public final class TreeUtils {

	//~ Methods --------------------------------------------------------------------------------------------------------

	public static void rememberColumnSizes(final PrefStore prefStore, final Throttle throttler, final Tree tree,
										   final String memoryKey) {
		new ColumnSizeMemory(new ColumnController.TreeColumnController(tree), prefStore, throttler, memoryKey);
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

					int width = tree.getBounds().width / tree.getColumnCount();
					for (int i = 0; i < tree.getColumnCount(); i++) {
						tree.getColumn(i).setWidth(width);
					}

					tree.removeControlListener(this);
				}

				@Override
				public void controlMoved(final ControlEvent event) {}
			};

		tree.addControlListener(controlListener);
	}
}