package org.uilib.swt.templating.components;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import org.uilib.swt.templating.Options;

public class ComponentUI implements UIController {

	//~ Methods --------------------------------------------------------------------------------------------------------

	@Override
	public Composite initialize(final Composite parent, final Options options) {

		int style = SWT.NONE;

		if (options.get("border", false)) {
			style = SWT.BORDER;
		}

		return new Composite(parent, style);
	}
}