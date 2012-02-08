package org.uilib.application;

import com.google.common.eventbus.DeadEvent;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An {@link EventContext} which delivers events to the object passed in the constructor.
 * It uses event-bus, the target-object has to define public void methods with the desired
 * event-type as single parameter and the {@link Subscribe} annotation.
 */
public final class LocalEventContext implements EventContext {

	//~ Static fields/initializers -------------------------------------------------------------------------------------

	private static final Logger L = LoggerFactory.getLogger(LocalEventContext.class);

	//~ Instance fields ------------------------------------------------------------------------------------------------

	private final EventBus localBus;

	//~ Constructors ---------------------------------------------------------------------------------------------------

	/**
	 * a new LocalEventContext
	 *
	 * @param object to which deliver events
	 */
	public LocalEventContext(final Object object) {
		this.localBus			  = new EventBus();
		this.localBus.register(object);
		this.localBus.register(this);
	}

	//~ Methods --------------------------------------------------------------------------------------------------------

	/**
	 * <b>Not intended for public use.</b>
	 * This is public for now, because of EventBus restrictions.
	 */
	@Subscribe
	public void deadLocalEvent(final DeadEvent event) {
		L.debug("dead local event: " + event.getEvent());
	}

	@Override
	public void postEvent(final Object event) {
		this.localBus.post(event);
	}
}