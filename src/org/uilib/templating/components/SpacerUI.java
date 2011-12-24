package org.uilib.templating.components;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

import org.uilib.AppContext;
import org.uilib.templating.Component;
import org.uilib.templating.Options;

public class SpacerUI implements ComponentUI {

	//~ Methods --------------------------------------------------------------------------------------------------------

	@Override
	public Control initialize(final AppContext app, final Composite parent, final List<Component> children,
							  final Options options) {
		return new Label(parent, SWT.NONE);
	}
}