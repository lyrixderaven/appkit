package org.uilib.templating.components;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import org.uilib.templating.Component;
import org.uilib.templating.Options;

public class ButtonUI implements ComponentUI {

	//~ Methods --------------------------------------------------------------------------------------------------------

	@Override
	public Control initialize(final Composite parent, final List<Component> children, final Options options) {

		int style  = SWT.PUSH;

		Button btn = new Button(parent, style);

		return btn;
	}
}