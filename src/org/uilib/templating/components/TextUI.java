package org.uilib.templating.components;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;

import org.uilib.EventContext;
import org.uilib.templating.Options;

public class TextUI implements ComponentUI {

	//~ Methods --------------------------------------------------------------------------------------------------------

	@Override
	public Control initialize(final EventContext app, final Composite parent, final String name, final String type,
							  final Options options) {

		int style = SWT.NONE;
		style |= (options.get("border", true) ? SWT.BORDER : SWT.NONE);
		style |= (options.get("search", false) ? SWT.SEARCH : SWT.NONE);
		style |= (options.get("search", false) ? SWT.CANCEL : SWT.NONE);
		style |= (options.get("password", false) ? SWT.PASSWORD : SWT.NONE);

		return new Text(parent, style);
	}
}