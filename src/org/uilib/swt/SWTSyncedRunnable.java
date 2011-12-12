package org.uilib.swt;

import org.apache.log4j.Logger;

import org.eclipse.swt.widgets.Display;

public abstract class SWTSyncedRunnable implements Runnable {

	//~ Instance fields ------------------------------------------------------------------------------------------------

	protected final Logger logger = Logger.getLogger(this.getClass());

	//~ Methods --------------------------------------------------------------------------------------------------------

	@Override
	public void run() {
		if (Display.getDefault().getThread() == Thread.currentThread()) {
			try {
				this.runChecked();
			} catch (final RuntimeException e) {
				this.logger.fatal(e.getMessage(), e);
			}
		} else {
			Display.getDefault().syncExec(this);
		}
	}

	protected abstract void runChecked();
}