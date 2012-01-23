package org.uilib.widget.util;

import org.eclipse.swt.custom.SashForm;

import org.uilib.util.Throttle;
import org.uilib.util.prefs.PrefStore;

public final class SashFormUtils {

	//~ Methods --------------------------------------------------------------------------------------------------------

	public static void rememberWeights(final PrefStore prefStore, final Throttle throttler, final SashForm sashForm,
									   final String memoryKey, final int defaultWeights[]) {
		new SashFormWeightMemory(prefStore, throttler, sashForm, memoryKey, defaultWeights);
	}
}