package org.uilib.widget.util;

import com.google.common.collect.Lists;

import java.util.List;

import org.eclipse.swt.widgets.Tree;

import org.uilib.util.Throttle;
import org.uilib.util.prefs.PrefStore;

public final class TreeUtils {

	//~ Methods --------------------------------------------------------------------------------------------------------

	public static void rememberColumnSize(final PrefStore prefStore, final Throttle throttler, final Tree tree,
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
	 * @param initialWeights initial weights for the columns, the length of this array has to equal the column-count of the table.
	 */
	public static void autosizeColumns(final Tree tree, final List<Integer> initialWeights) {
		new ColumnAutoSizer(new ColumnController.TreeColumnController(tree), initialWeights);
	}

	public static void autosizeColumns(final Tree tree) {

		List<Integer> weights = Lists.newArrayList();
		for (int i = 0; i < tree.getColumnCount(); i++) {
			weights.add(tree.getColumn(i).getWidth());
		}

		new ColumnAutoSizer(new ColumnController.TreeColumnController(tree), weights);
	}
}