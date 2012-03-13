package org.appkit.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** a Runnable that logs all RuntimeException */
public abstract class LoggingRunnable implements Runnable {

	//~ Static fields/initializers -------------------------------------------------------------------------------------

	private static final Logger L = LoggerFactory.getLogger(LoggingRunnable.class);

	//~ Methods --------------------------------------------------------------------------------------------------------

	@Override
	public final void run() {
		try {
			this.runChecked();
		} catch (final RuntimeException e) {
			L.error(e.getMessage(), e);
		}
	}

	/** overwrite to call your standard <code>run()</code> method */
	public abstract void runChecked();
}