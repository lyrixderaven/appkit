package org.uilib.widget.util;

import org.eclipse.swt.custom.SashForm;

import org.uilib.util.Throttle;
import org.uilib.util.prefs.PrefStore;

/**
 * various utility-functions for working with {@link SashForm}s
 *
 */
public final class SashFormUtils {

	//~ Methods --------------------------------------------------------------------------------------------------------

	// ~ Methods
	// --------------------------------------------------------------------------------------------------------

	/**
	 * restores SashForm weights and tracks and saves changes
	 *
	 * @param prefStore
	 *            the prefStore used to load and save the weights
	 * @param throttler
	 *            throttler used to throttle calls to the prefStore
	 * @param memoryKey
	 *            the key to save to
	 * @param defaultWeights
	 *            default-weights if no saved are found
	 */
	public static void rememberWeights(final PrefStore prefStore, final Throttle throttler, final SashForm sashForm,
									   final String memoryKey, final int defaultWeights[]) {
		new SashFormWeightMemory(prefStore, throttler, sashForm, memoryKey, defaultWeights);
	}
}