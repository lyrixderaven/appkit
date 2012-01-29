package org.uilib.application;

import com.google.common.eventbus.EventBus;

public final class RealEventContext implements EventContext {

	//~ Instance fields ------------------------------------------------------------------------------------------------

	private final Application application;
	private final EventBus parentBus;
	private final EventBus localBus;

	//~ Constructors ---------------------------------------------------------------------------------------------------

	public RealEventContext(final Application application, final Controller controller, final EventBus parentBus) {
		this.application     = application;

		/* the parentBus is used so the parent can receive events */
		this.parentBus		 = parentBus;

		/* the local bus is used to receive events of sub-contexts */
		this.localBus		 = new EventBus();
		this.localBus.register(controller);
	}

	//~ Methods --------------------------------------------------------------------------------------------------------

	@Override
	public void postEvent(final Object event) {
		this.parentBus.post(event);
	}

	@Override
	public void initController(final Controller subController) {

		EventContext subContext = new RealEventContext(this.application, subController, localBus);
		subController.init(subContext);
	}

	@Override
	public void backgroundTask(final Object task) {
		this.application.backgroundTask(task, this);
	}

	@Override
	public void postLocal(final Object response) {
		this.localBus.post(response);
	}
}