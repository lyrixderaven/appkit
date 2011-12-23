package org.uilib;

import org.eclipse.swt.widgets.Display;

// FIXME: Builder Syntax überall?
// FIXME: CrashHandler
public final class Application {

	//~ Constructors ---------------------------------------------------------------------------------------------------

	public Application(final String appName, final String appVersion, final Controller controller) {}

	//~ Methods --------------------------------------------------------------------------------------------------------

	public static void start(final Controller controller) {

		Application application = new Application("", "", controller);
		application.start();
	}

	public void start() {

		Display display = new Display();
		while (! display.isDisposed()) {
			if (! display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	public void postEvent(final Object event) {}
}