package org.appkit.templating.components;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import org.appkit.application.EventContext;
import org.appkit.templating.Options;

/** for creating simple {@link Button} components */
public class ButtonUI implements ComponentUI {

	//~ Methods --------------------------------------------------------------------------------------------------------

	@Override
	public Control initialize(final EventContext app, final Composite parent, final String name, final String type,
							  final Options options) {

		int style  = SWT.PUSH;

		Button btn = new Button(parent, style);

		return btn;
	}
}