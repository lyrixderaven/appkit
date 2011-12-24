package org.uilib;

public interface AppContext {

	//~ Static fields/initializers -------------------------------------------------------------------------------------

	public static final AppContext FAKE = new FakeAppContext();

	//~ Methods --------------------------------------------------------------------------------------------------------

	public void postEvent(final Object event);

	public void initController(final Controller subController);

	public void backgroundTask(final Object task);

	public void postLocal(final Object response);
}