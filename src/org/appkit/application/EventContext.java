package org.appkit.application;


/**
 * A context that is handed to an object that fires events. The events will be fired in this
 * context, which means that the objects registered in the context will receive it. Registration
 * is specified by the implementation.
 *
 * @see LocalEventContext
 *
 */
public interface EventContext {

	//~ Static fields/initializers -------------------------------------------------------------------------------------

	/** the fake event context, events fired in this context will go nowhere */
	public static final EventContext FAKE = new FakeEventContext();

	//~ Methods --------------------------------------------------------------------------------------------------------

	/**
	 * post any kind of object as event
	 *
	 * @param event the event-object, doesn't have to be, but should be thought of a Serializable
	 */
	public void postEvent(final Object event);
}