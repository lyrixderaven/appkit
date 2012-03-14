package org.appkit.templating.components;

import com.google.common.base.Preconditions;

import java.util.Arrays;

import org.appkit.application.EventContext;
import org.appkit.templating.Options;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/** for creating components that use a {@link StackLayout} on a {@link Composite} */
public class StackUI implements ComponentUI {

	//~ Instance fields ------------------------------------------------------------------------------------------------

	private StackLayout stackLayout;
	private Composite comp;

	//~ Methods --------------------------------------------------------------------------------------------------------

	@Override
	public Control initialize(final EventContext app, final Composite parent, final String name, final String type,
							  final Options options) {
		this.stackLayout     = new StackLayout();
		this.comp			 = new Composite(parent, SWT.NONE);
		this.comp.setLayout(this.stackLayout);

		return this.comp;
	}

	public void setTopControl(final Control control) {
		Preconditions.checkArgument(
			Arrays.asList(comp.getChildren()).contains(control),
			"%s is not a child of this stack",
			control);

		this.stackLayout.topControl = control;
		this.comp.layout();
	}
}