package org.uilib;

public final class FakeEventContext implements EventContext {

	//~ Methods --------------------------------------------------------------------------------------------------------

	@Override
	public void postEvent(final Object event) {

		// No-op
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