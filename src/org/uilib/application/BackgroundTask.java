package org.uilib.application;

import org.eclipse.swt.widgets.Display;
import org.uilib.util.LoggingRunnable;

public abstract class BackgroundTask extends LoggingRunnable{

	@Override
	public final void runChecked() {
		try {
			Thread.sleep(200);
		} catch (final InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Display.getDefault().syncExec(
				new LoggingRunnable() {
					@Override
					public void runChecked() {
						//ov.dispose();
					}
				});
	}

	public void work() {

	}

	public void enter() {

	}

	public void done() {

	}
}
