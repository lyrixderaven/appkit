package org.uilib.application;

public interface EventContext {

	//~ Static fields/initializers -------------------------------------------------------------------------------------

	public static final EventContext FAKE = new FakeEventContext();

	//~ Methods --------------------------------------------------------------------------------------------------------

	public void postEvent(final Object event);
}