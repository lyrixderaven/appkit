package org.uilib.widget.util;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Shell;

import org.uilib.util.Throttle;
import org.uilib.util.prefs.PrefStore;

public final class ShellUtils {

	//~ Methods --------------------------------------------------------------------------------------------------------

	public static Point getCenterPosition(final Shell shell) {

		/* Position in the middle of screen (and a little up) */
		Rectangle monitorBounds = shell.getDisplay().getPrimaryMonitor().getBounds();
		Rectangle shellBounds   = shell.getBounds();
		int x				    = monitorBounds.x + ((monitorBounds.width - shellBounds.width) / 2);
		int y				    = (monitorBounds.y + ((monitorBounds.height - shellBounds.height) / 2)) - 150;

		return new Point(x, y);
	}

	public static void rememberSizeAndPosition(final PrefStore prefStore, final Throttle throttler, final Shell shell,
											   final String memoryKey, final int defaultWidth, final int defaultHeight,
											   final int defaultX, final int defaultY) {
		new ShellMemory(
			prefStore,
			throttler,
			shell,
			memoryKey,
			defaultWidth,
			defaultHeight,
			defaultX,
			defaultY,
			false,
			false);
	}

	public static void rememberSize(final PrefStore prefStore, final Throttle throttler, final Shell shell,
									final String memoryKey, final int defaultWidth, final int defaultHeight) {
		new ShellMemory(prefStore, throttler, shell, memoryKey, defaultWidth, defaultHeight, 0, 0, false, true);
	}
}