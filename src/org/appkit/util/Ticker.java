package org.appkit.util;


/**
 * A ticker that notifies stuff.
 *
 * @see SmartExecutor
 */
public interface Ticker {

	//~ Methods --------------------------------------------------------------------------------------------------------

	void notify(final TickReceiver receiver);

	void stop();

	//~ Inner Interfaces -----------------------------------------------------------------------------------------------

	public interface TickReceiver {
		void tick();
	}
}