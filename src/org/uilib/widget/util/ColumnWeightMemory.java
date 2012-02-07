package org.uilib.widget.util;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.widgets.Display;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.uilib.util.LoggingRunnable;
import org.uilib.util.SWTSyncedRunnable;
import org.uilib.util.Throttle;
import org.uilib.util.prefs.PrefStore;

public final class ColumnWeightMemory {

	//~ Static fields/initializers -------------------------------------------------------------------------------------

	private static final Logger L		   = LoggerFactory.getLogger(ColumnWeightMemory.class);
	private static final int THROTTLE_TIME = 250;

	//~ Instance fields ------------------------------------------------------------------------------------------------

	private final PrefStore prefStore;
	private final Throttle throttler;
	private final ColumnController colController;
	private final String memoryKey;

	//~ Constructors ---------------------------------------------------------------------------------------------------

	protected ColumnWeightMemory(final ColumnController colController, final PrefStore prefStore,
								 final Throttle throttler, final String key) {
		this.prefStore		   = prefStore;
		this.throttler		   = throttler;
		this.colController     = colController;
		this.memoryKey		   = key + ".columnweights";

		/* add initial listener for initial size-setting */
		colController.installControlListener(
			new ControlListener() {
					@Override
					public void controlResized(final ControlEvent event) {
						loadWidths();
						colController.removeControlListener(this);
					}

					@Override
					public void controlMoved(final ControlEvent event) {}
				});

		/* add listeners */
		for (int i = 0; i < colController.getColumnCount(); i++) {
			colController.installColumnControlListener(i, new ColumnResizeListener());
		}
	}

	//~ Methods --------------------------------------------------------------------------------------------------------

	private void loadWidths() {

		/* load the stored info */
		String weightString = this.prefStore.get(this.memoryKey, "");
		L.debug("loading weights: '{}'", weightString);

		List<String> weightStrings = Lists.newArrayList(Splitter.on(",").split(weightString));
		if (weightStrings.size() == colController.getColumnCount()) {

			List<Integer> weights = Lists.newArrayList();

			for (int i = 0; i < weightStrings.size(); i++) {
				try {
					weights.add(Integer.valueOf(weightStrings.get(i)));
				} catch (final NumberFormatException e) {
					L.debug("no weight: '{}'", weightStrings.get(i));
					return;
				}
			}

			this.colController.setWeights(weights);
		}
	}

	private void saveWeights() {

		/* store */
		final String widthString = Joiner.on(",").join(this.colController.calculateWeights());

		Runnable runnable		 =
			new LoggingRunnable() {
				@Override
				public void runChecked() {
					L.debug("writing out weights {} to key {}", widthString, memoryKey);
					prefStore.store(memoryKey, widthString);
				}
			};

		this.throttler.throttle(
			memoryKey,
			THROTTLE_TIME,
			TimeUnit.MILLISECONDS,
			new SWTSyncedRunnable(Display.getCurrent(), runnable));
	}

	//~ Inner Classes --------------------------------------------------------------------------------------------------

	private class ColumnResizeListener implements ControlListener {

		private int lastAvailWidth = -1;

		@Override
		public void controlMoved(final ControlEvent event) {}

		@Override
		public void controlResized(final ControlEvent event) {
			if (lastAvailWidth == -1) {
				lastAvailWidth = colController.getAvailWidth();
				return;
			}

			if (colController.getAvailWidth() == lastAvailWidth) {
				L.debug(
					"width {} didn't change -> columns resized -> saving new weights",
					colController.getAvailWidth());
				saveWeights();
			} else {
				lastAvailWidth = colController.getAvailWidth();
			}
		}
	}
}