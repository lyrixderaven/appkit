package org.uilib.util;

import java.util.concurrent.TimeUnit;

/**
 * A throttle mechanism. Can be used to throttle expensive calls triggered by often-recurring events.
 *
 * @see SmartExecutor
 */
public interface Throttle {

	//~ Methods --------------------------------------------------------------------------------------------------------

	/**
	 * Schedules a Runnable to be executed a fixed period of time after it was scheduled
	 * If a new Runnable with the same throttleName is scheduled, it will overwrite Runnable which were
	 * scheduled before but not yet run. This way only the last Runnable in a series will be executed.
	 *
	 */
	void throttle(final String throttleName, final long delay, final TimeUnit timeUnit, final Runnable runnable);
}