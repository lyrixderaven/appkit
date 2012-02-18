package org.uilib.widget.util;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Control;

/**
 * Various utility-functions
 *
 */
public final class SWTUtils {

	//~ Methods --------------------------------------------------------------------------------------------------------

	public static Point getPositionRelTo(final Control control, final Control referenceControl) {

		Point refPos     = referenceControl.toDisplay(0, 0);
		Point controlPos = control.toDisplay(0, 0);

		return new Point(controlPos.x - refPos.x, controlPos.y - refPos.y);
	}

	public static Point getPositionRelToDisplay(final Control control) {
		return control.toDisplay(0, 0);
	}
}