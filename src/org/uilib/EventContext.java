package org.uilib;

public interface EventContext {

	//~ Static fields/initializers -------------------------------------------------------------------------------------

	public static final EventContext FAKE = new FakeEventContext();

	//~ Methods --------------------------------------------------------------------------------------------------------

	public void postEvent(final Object event);

	public void initController(final Controller subController);

	public void backgroundTask(final Object task);

	public void postLocal(final Object response);
}