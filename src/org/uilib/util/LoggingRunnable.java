package org.uilib.util;

import org.apache.log4j.Logger;

public abstract class LoggingRunnable implements Runnable {

	//~ Instance fields ------------------------------------------------------------------------------------------------

	private final Logger logger = this.getLogger();

	//~ Methods --------------------------------------------------------------------------------------------------------

	@Override
	public final void run() {
		try {
			this.runChecked();
		} catch (final RuntimeException e) {
			this.logger.fatal(e.getMessage(), e);
		}
	}

	protected abstract void runChecked();

	public Logger getLogger() {
		return Logger.getLogger(this.getClass());
	}
}