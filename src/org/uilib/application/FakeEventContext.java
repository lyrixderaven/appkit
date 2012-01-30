package org.uilib.application;

public final class FakeEventContext implements EventContext {

	//~ Methods --------------------------------------------------------------------------------------------------------

	@Override
	public void postEvent(final Object event) {

		// No-op
	}

	@Override
	public void backgroundTask(final Object task) {
		throw new IllegalStateException("this is not a real context");
	}
}