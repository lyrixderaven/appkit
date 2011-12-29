package org.uilib;

import com.google.common.eventbus.EventBus;

public final class LocalEventContext implements EventContext {

	//~ Instance fields ------------------------------------------------------------------------------------------------

	private final EventBus localBus;

	//~ Constructors ---------------------------------------------------------------------------------------------------

	public LocalEventContext(final Object o) {
		this.localBus = new EventBus();
		this.localBus.register(o);
	}

	//~ Methods --------------------------------------------------------------------------------------------------------

	@Override
	public void postEvent(final Object event) {
		this.localBus.post(event);
	}

	@Override
	public void initController(final Controller subController) {
		throw new IllegalStateException("this is not a real context");
	}

	@Override
	public void backgroundTask(final Object task) {
		throw new IllegalStateException("this is not a real context");
	}

	@Override
	public void postLocal(final Object response) {
		throw new IllegalStateException("this is not a real context");
	}
}