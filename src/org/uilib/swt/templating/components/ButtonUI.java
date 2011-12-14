package org.uilib.swt.templating.components;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import org.uilib.swt.templating.Options;

public class ButtonUI implements UIController {

	//~ Methods --------------------------------------------------------------------------------------------------------

	@Override
	public Control initialize(final Composite parent, final Options options) {
		int style = SWT.NONE;

		return new Button(parent, style);
	}
}