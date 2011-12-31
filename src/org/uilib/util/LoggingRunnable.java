package org.uilib.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class LoggingRunnable implements Runnable {

	//~ Instance fields ------------------------------------------------------------------------------------------------

	private final Logger logger = this.getLogger();

	//~ Methods --------------------------------------------------------------------------------------------------------

	@Override
	public final void run() {
		try {
			this.runChecked();
		} catch (final RuntimeException e) {
			this.logger.error(e.getMessage(), e);
		}
	}

	protected abstract void runChecked();

	public Logger getLogger() {
		return LoggerFactory.getLogger(this.getClass());
	}
}