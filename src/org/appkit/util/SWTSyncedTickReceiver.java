package org.appkit.util;

import org.eclipse.swt.widgets.Display;

import org.appkit.util.Ticker.TickReceiver;

/**
 * Wrapper for a TickReceiver that will execute it in the given {@link Display}'s thread.
 *
 */
public final class SWTSyncedTickReceiver implements TickReceiver {

	//~ Instance fields ------------------------------------------------------------------------------------------------

	private final Display display;
	private final TickReceiver receiver;

	//~ Constructors ---------------------------------------------------------------------------------------------------

	public SWTSyncedTickReceiver(final Display display, final TickReceiver receiver) {
		this.display	  = display;
		this.receiver     = receiver;
	}

	//~ Methods --------------------------------------------------------------------------------------------------------

	@Override
	public final void tick() {
		if (this.display.isDisposed()) {
			return;
		}

		if (this.display.getThread() == Thread.currentThread()) {
			this.receiver.tick();
		} else {
			this.display.syncExec(
				new Runnable() {
						@Override
						public void run() {
							if (! display.isDisposed()) {
								receiver.tick();
							}
						}
					});
		}
	}
}