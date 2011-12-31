package org.uilib;

import com.google.common.eventbus.EventBus;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.eclipse.swt.widgets.Display;

// TODO: LoggingRunnable,Interrupt / CrashHandler / Executor / TaskQueue
// TODO: Measurement (mutable Array)
public final class Application {

	//~ Static fields/initializers -------------------------------------------------------------------------------------

	private static final Logger L = LoggerFactory.getLogger(Application.class);

	//~ Instance fields ------------------------------------------------------------------------------------------------

	private final ExecutorService executor			   = Executors.newCachedThreadPool();
	private final Display display					   = new Display();
	private final EventBus localBus					   = new EventBus();
	private final String appName;
	private final String appVersion;
	private final ApplicationController mainController;
	private final BackgroundWorker bgController;

	//~ Constructors ---------------------------------------------------------------------------------------------------

	public Application(final String appName, final String appVersion, final ApplicationController mainController,
					   final BackgroundWorker bgController) {
		this.appName								   = appName;
		this.appVersion								   = appVersion;
		this.mainController							   = mainController;
		this.bgController							   = bgController;
	}

	//~ Methods --------------------------------------------------------------------------------------------------------

	public static void start(final ApplicationController controller, final BackgroundWorker bgController) {

		Application application = new Application("", "", controller, bgController);
		application.start();
	}

	public void start() {
		Display.setAppName(this.appName);
		Display.setAppVersion(this.appVersion);

		try {
			this.mainController.init(this);

			/* run event loop */
			while (! this.display.isDisposed()) {
				if (! this.display.readAndDispatch()) {
					this.display.sleep();
				}
			}
		} catch (final RuntimeException e) {
			L.error(e.getMessage(), e);
			this.shutdown();
		}
	}

	public void shutdown() {
		this.display.dispose();
		this.executor.shutdownNow();
	}

	public void initController(final Controller subController) {

		EventContext subContext = new RealAppContext(this, subController, this.localBus);
		subController.init(subContext);
	}

	public void backgroundTask(final Object task, final RealAppContext context) {
		this.executor.execute(
			new Runnable() {
					@Override
					public void run() {

						final Object response = bgController.request(task);

						if (response != null) {
							display.syncExec(
								new Runnable() {
										@Override
										public void run() {
											context.postLocal(response);
										}
									});
						}
					}
				});
	}
}