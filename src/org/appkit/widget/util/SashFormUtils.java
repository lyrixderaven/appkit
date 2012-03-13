package org.appkit.widget.util;

import org.eclipse.swt.custom.SashForm;

import org.appkit.util.SmartExecutor;
import org.appkit.util.Throttle;
import org.appkit.util.prefs.PrefStore;

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
	 * @param executor
	 *            executor used to create a {@link Throttle} to the save function
	 * @param memoryKey
	 *            the key to save to
	 * @param defaultWeights
	 *            default-weights if no saved are found
	 */
	public static void rememberWeights(final PrefStore prefStore, final SmartExecutor executor,
									   final SashForm sashForm, final String memoryKey, final int defaultWeights[]) {
		new SashFormWeightMemory(prefStore, executor, sashForm, memoryKey, defaultWeights);
	}
}