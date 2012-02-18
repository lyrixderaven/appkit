package org.uilib.util;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/** delayed runnable */
class DelayedRunnable implements Delayed, Runnable {

	//~ Instance fields ------------------------------------------------------------------------------------------------

	protected final Runnable runnable;
	private final long endOfDelay;

	//~ Constructors ---------------------------------------------------------------------------------------------------

	public DelayedRunnable(final Runnable runnable, final long delay, final TimeUnit delayUnit) {
		this.runnable	    = runnable;
		this.endOfDelay     = delayUnit.toMillis(delay) + System.currentTimeMillis();
	}

	//~ Methods --------------------------------------------------------------------------------------------------------

	@Override
	public int compareTo(final Delayed other) {

		final Long delay1 = this.getDelay(TimeUnit.MILLISECONDS);
		final Long delay2 = other.getDelay(TimeUnit.MILLISECONDS);

		return delay1.compareTo(delay2);
	}

	@Override
	public long getDelay(final TimeUnit unit) {
		return unit.convert(this.endOfDelay - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
	}

	public Runnable getRunnable() {
		return this.runnable;
	}

	@Override
	public void run() {
		this.runnable.run();
	}
}