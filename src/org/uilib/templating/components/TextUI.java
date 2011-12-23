package org.uilib.templating.components;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;

import org.uilib.templating.Component;
import org.uilib.templating.Options;

public class TextUI implements ComponentUI {

	//~ Methods --------------------------------------------------------------------------------------------------------

	@Override
	public Control initialize(final Composite parent, final List<Component> children, final Options options) {

		int style = SWT.NONE;
		style |= (options.get("border", true) ? SWT.BORDER : SWT.NONE);
		style |= (options.get("search", true) ? SWT.SEARCH : SWT.NONE);
		style |= (options.get("search", true) ? SWT.CANCEL : SWT.NONE);

		Text text = new Text(parent, style);

		return text;
	}
}