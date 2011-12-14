package org.uilib.swt.templating.components;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Table;

import org.uilib.swt.templating.Options;

// TODO: Templating: Table immer einpacken für Memory
public class TableUI implements UIController {

	//~ Methods --------------------------------------------------------------------------------------------------------

	@Override
	public Control initialize(final Composite parent, final Options options) {

		int style = SWT.NONE;
		style |= (options.get("border", true) ? SWT.BORDER : SWT.NONE);
		style |= (options.get("virtual", true) ? SWT.VIRTUAL : SWT.NONE);
		style |= (options.get("fullselect", false) ? SWT.FULL_SELECTION : SWT.NONE);

		return new Table(parent, style);
	}
}