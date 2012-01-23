package org.uilib.widget.util;

import org.eclipse.swt.widgets.Tree;

import org.uilib.util.Throttle;
import org.uilib.util.prefs.PrefStore;

public final class TreeUtils {

	//~ Methods --------------------------------------------------------------------------------------------------------

	public static void rememberColumnSize(final PrefStore prefStore, final Throttle throttler, final Tree tree,
										  final String memoryKey, final int defaultSize) {
		new TreeColumnSizeMemory(prefStore, throttler, tree, memoryKey, defaultSize);
	}

	public static void rememberColumnOrder(final PrefStore prefStore, final Throttle throttler, final Tree tree,
										   final String memoryKey) {
		new TreeColumnOrderMemory(prefStore, throttler, tree, memoryKey);
	}

	public static void setColumnsFillWidth(final Tree tree) {
		new FillTreeLayout(tree);
	}
}