package org.uilib.widget.util;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.primitives.Ints;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.uilib.util.SWTSyncedRunnable;
import org.uilib.util.Throttle;
import org.uilib.util.prefs.PrefStore;

public final class TreeColumnOrderMemory {

	//~ Static fields/initializers -------------------------------------------------------------------------------------

	@SuppressWarnings("unused")
	private static final Logger L							 = LoggerFactory.getLogger(TreeColumnOrderMemory.class);

	//~ Instance fields ------------------------------------------------------------------------------------------------

	private final PrefStore prefStore;
	private final Throttle throttler;
	private final Tree tree;
	private final String memoryKey;

	//~ Constructors ---------------------------------------------------------------------------------------------------

	private TreeColumnOrderMemory(final PrefStore prefStore, final Throttle throttler, final Tree tree, final String key) {
		this.prefStore										 = prefStore;
		this.throttler										 = throttler;
		this.tree											 = tree;
		this.memoryKey										 = key + ".columnorder";

		/* reorder columns */
		String orderString     = this.prefStore.get(this.memoryKey, "");
		List<String> orderList = Lists.newArrayList(Splitter.on(",").split(orderString));
		if (orderList.size() == this.tree.getColumnCount()) {
			try {

				int order[] = new int[this.tree.getColumnCount()];
				int i	    = 0;
				for (final String pos : orderList) {
					order[i] = Integer.valueOf(pos);
					i++;
				}

				this.tree.setColumnOrder(order);
			} catch (final NumberFormatException e) {}
		}

		for (final TreeColumn column : this.tree.getColumns()) {
			/* set column movable */
			column.setMoveable(true);

			/* add listener */
			column.addControlListener(new ColumnMoveListener());
		}
	}

	//~ Methods --------------------------------------------------------------------------------------------------------

	public static void install(final PrefStore prefStore, final Throttle throttler, final Tree tree,
							   final String memoryKey) {
		new TreeColumnOrderMemory(prefStore, throttler, tree, memoryKey);
	}

	//~ Inner Classes --------------------------------------------------------------------------------------------------

	private class ColumnMoveListener implements ControlListener {
		@Override
		public void controlMoved(final ControlEvent event) {
			throttler.throttle(
				memoryKey,
				50,
				TimeUnit.MILLISECONDS,
				new SWTSyncedRunnable() {
						@Override
						public void runChecked() {
							if (tree.isDisposed()) {
								return;
							}

							List<Integer> order = Ints.asList(tree.getColumnOrder());
							prefStore.store(memoryKey, Joiner.on(",").join(order));
						}
					});
		}

		@Override
		public void controlResized(final ControlEvent event) {}
	}
}