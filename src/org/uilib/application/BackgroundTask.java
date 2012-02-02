package org.uilib.application;

import org.uilib.util.LoggingRunnable;

public abstract class BackgroundTask extends LoggingRunnable {

	//~ Methods --------------------------------------------------------------------------------------------------------

	@Override
	public final void runChecked() {
		try {
			Thread.sleep(200);
		} catch (final InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void work() {}

	public void enter() {}

	public void done() {}
}