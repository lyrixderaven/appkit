package org.uilib.swt.templating.components;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

import org.uilib.swt.templating.Options;

public class PlaceholderUI implements UIController {

	//~ Methods --------------------------------------------------------------------------------------------------------

	@Override
	public Control initialize(final Composite parent, final Options options) {
		Label label = new Label(parent, SWT.NONE);

		return label;
	}
}