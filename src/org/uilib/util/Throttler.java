package org.uilib.util;

import java.util.concurrent.TimeUnit;

public interface Throttler {

	//~ Methods --------------------------------------------------------------------------------------------------------

	void throttle(final String throttleName, final long delay, final TimeUnit timeUnit, final Runnable runnable);
}