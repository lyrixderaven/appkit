package org.appkit.util;

import java.util.concurrent.TimeUnit;

/** repeating runnable */
final class RepeatingRunnable extends DelayedRunnable {

	//~ Instance fields ------------------------------------------------------------------------------------------------

	private final long periodInMillis;

	//~ Constructors ---------------------------------------------------------------------------------------------------

	public RepeatingRunnable(final Runnable runnable, final long period, final TimeUnit delayUnit) {
		super(runnable, period, delayUnit);

		this.periodInMillis = delayUnit.convert(period, TimeUnit.MILLISECONDS);
	}

	//~ Methods --------------------------------------------------------------------------------------------------------

	public RepeatingRunnable reschedule() {
		return new RepeatingRunnable(this.runnable, this.periodInMillis, TimeUnit.MILLISECONDS);
	}
}