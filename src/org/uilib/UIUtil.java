package org.uilib;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Shell;

public final class UIUtil {

	//~ Methods --------------------------------------------------------------------------------------------------------

	public static Point getCenterPosition(final Shell shell) {

		/* Position in the middle of screen (and a little up) */
		Rectangle monitorBounds = shell.getDisplay().getPrimaryMonitor().getBounds();
		Rectangle shellBounds   = shell.getBounds();
		int x				    = monitorBounds.x + ((monitorBounds.width - shellBounds.width) / 2);
		int y				    = (monitorBounds.y + ((monitorBounds.height - shellBounds.height) / 2)) - 150;

		return new Point(x, y);
	}
}