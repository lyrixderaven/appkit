package org.appkit.util;

import java.util.concurrent.TimeUnit;

/** throttled runnable */
final class ThrottledRunnable extends DelayedRunnable {

	//~ Instance fields ------------------------------------------------------------------------------------------------

	private final String throttleName;

	//~ Constructors ---------------------------------------------------------------------------------------------------

	public ThrottledRunnable(final Runnable runnable, final String throttleName, final long period,
							 final TimeUnit delayUnit) {
		super(runnable, period, delayUnit);

		this.throttleName = throttleName;
	}

	//~ Methods --------------------------------------------------------------------------------------------------------

	public String getThrottleName() {
		return this.throttleName;
	}
}