package org.uilib.swt.memory;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;

import org.uilib.swt.SWTSyncedRunnable;
import org.uilib.util.PrefStore;
import org.uilib.util.Throttler;

public final class SashFormWeightMemory {

	//~ Static fields/initializers -------------------------------------------------------------------------------------

	@SuppressWarnings("unused")
	private static final Logger L							 = Logger.getLogger(SashFormWeightMemory.class);

	//~ Instance fields ------------------------------------------------------------------------------------------------

	private final PrefStore prefStore;
	private final Throttler throttler;
	private final SashForm sashForm;
	private final String memoryKey;
	private final int defaultWeights[];

	//~ Constructors ---------------------------------------------------------------------------------------------------

	private SashFormWeightMemory(final PrefStore prefStore, final Throttler throttler, final SashForm sashForm,
								 final String key, final int defaultWeights[]) {
		this.prefStore										 = prefStore;
		this.throttler										 = throttler;
		this.sashForm										 = sashForm;
		this.memoryKey										 = key + ".sashsizes";
		this.defaultWeights									 = defaultWeights;

		/* position shell */
		String weightString     = this.prefStore.get(this.memoryKey, "");
		List<String> weightList = Lists.newArrayList(Splitter.on(",").split(weightString));
		if (weightList.size() == this.sashForm.getChildren().length) {
			try {

				int weights[] = new int[this.sashForm.getChildren().length];
				int i		  = 0;
				for (final String weight : weightList) {
					weights[i] = Integer.valueOf(weight);
					i++;
				}

				this.sashForm.setWeights(weights);
			} catch (final NumberFormatException e) {
				this.sashForm.setWeights(this.defaultWeights);
			}
		} else {
			this.sashForm.setWeights(this.defaultWeights);
		}

		/* add listener */
		this.sashForm.getChildren()[0].addControlListener(new SashMovedListener());
	}

	//~ Methods --------------------------------------------------------------------------------------------------------

	public static void install(final PrefStore prefStore, final Throttler throttler, final SashForm sashForm,
							   final String memoryKey, final int defaultWeights[]) {
		new SashFormWeightMemory(prefStore, throttler, sashForm, memoryKey, defaultWeights);
	}

	//~ Inner Classes --------------------------------------------------------------------------------------------------

	private class SashMovedListener implements ControlListener {
		@Override
		public void controlMoved(final ControlEvent event) {}

		@Override
		public void controlResized(final ControlEvent event) {
			throttler.throttle(
				memoryKey,
				50,
				TimeUnit.MILLISECONDS,
				new SWTSyncedRunnable() {
						@Override
						protected void runChecked() {

							int weights[] = sashForm.getWeights();
							prefStore.store(memoryKey, Joiner.on(",").join(weights[0], weights[1]));
						}
					});
		}
	}
}