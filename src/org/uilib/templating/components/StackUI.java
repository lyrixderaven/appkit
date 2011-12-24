package org.uilib.templating.components;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import org.uilib.AppContext;
import org.uilib.templating.Component;
import org.uilib.templating.Options;

public class StackUI implements ComponentUI {

	//~ Instance fields ------------------------------------------------------------------------------------------------

	private StackLayout stackLayout;
	private Composite comp;

	//~ Methods --------------------------------------------------------------------------------------------------------

	@Override
	public Control initialize(final AppContext app, final Composite parent, final List<Component> children,
							  final Options options) {
		this.stackLayout     = new StackLayout();
		this.comp			 = new Composite(parent, SWT.NONE);
		this.comp.setLayout(this.stackLayout);

		return this.comp;
	}

	// TODO: Test: PreConditions für falsches parent
	public void setTopControl(final Control control) {
		this.stackLayout.topControl = control;
		this.comp.layout();
	}
}