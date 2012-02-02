package org.uilib.util;

import org.eclipse.swt.widgets.Display;

public final class SWTSyncedRunnable implements Runnable {

	//~ Instance fields ------------------------------------------------------------------------------------------------

	private final Display display;
	private final Runnable runnable;

	//~ Constructors ---------------------------------------------------------------------------------------------------

	public SWTSyncedRunnable(final Display display, final Runnable runnable) {
		this.display	  = display;
		this.runnable     = runnable;
	}

	//~ Methods --------------------------------------------------------------------------------------------------------

	@Override
	public final void run() {
		if (this.display.isDisposed()) {
			return;
		}

		if (this.display.getThread() == Thread.currentThread()) {
			this.runnable.run();
		} else {
			this.display.syncExec(this);
		}
	}
}