package org.uilib.widget.util;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.uilib.util.SWTSyncedRunnable;
import org.uilib.util.Throttle;
import org.uilib.util.prefs.PrefStore;

public final class ColumnSizeMemory {

	//~ Static fields/initializers -------------------------------------------------------------------------------------

	private static final Logger L = LoggerFactory.getLogger(ColumnSizeMemory.class);

	//~ Instance fields ------------------------------------------------------------------------------------------------

	private final PrefStore prefStore;
	private final Throttle throttler;
	private final ColumnController colController;
	private final String memoryKey;

	//~ Constructors ---------------------------------------------------------------------------------------------------

	protected ColumnSizeMemory(final ColumnController colController, final PrefStore prefStore,
							   final Throttle throttler, final String key) {
		this.prefStore			  = prefStore;
		this.throttler			  = throttler;
		this.colController		  = colController;
		this.memoryKey			  = key + ".columnsizes";

		/* load the stored info */
		String widthString = this.prefStore.get(this.memoryKey, "");
		L.debug("widthString: " + widthString);

		List<String> widths = Lists.newArrayList(Splitter.on(",").split(widthString));
		if (widths.size() == colController.getColumnCount()) {
			L.debug("valid width " + widths + " -> sizing columns");
			for (int i = 0; i < colController.getColumnCount(); i++) {
				try {
					colController.setWidth(i, Integer.valueOf(widths.get(i)));
				} catch (final NumberFormatException e) {}
			}
		}

		/* add listeners */
		for (int i = 0; i < colController.getColumnCount(); i++) {
			colController.installColumnControlListener(i, new ColumnResizeListener());
		}
	}

	//~ Methods --------------------------------------------------------------------------------------------------------

	private void saveSizes() {

		List<Integer> widths = Lists.newArrayList();
		for (int i = 0; i < colController.getColumnCount(); i++) {
			widths.add(colController.getWidth(i));
		}

		/* store */
		final String widthString = Joiner.on(",").join(widths);
		throttler.throttle(
			memoryKey,
			250,
			TimeUnit.MILLISECONDS,
			new SWTSyncedRunnable() {
					@Override
					public void runChecked() {
						L.debug("writing out widths {} to key", widthString, memoryKey);
						prefStore.store(memoryKey, widthString);
					}
				});
	}

	//~ Inner Classes --------------------------------------------------------------------------------------------------

	private class ColumnResizeListener implements ControlListener {
		@Override
		public void controlMoved(final ControlEvent event) {}

		@Override
		public void controlResized(final ControlEvent event) {
			saveSizes();
		}
	}
}