package org.uilib.templating.components;

import org.eclipse.swt.widgets.Control;

import org.uilib.templating.Options;

public interface LayoutUI extends ComponentUI {

	//~ Methods --------------------------------------------------------------------------------------------------------

	public void layoutChild(final Control child, final Options options);

	public void setVisible(final Control child, final boolean visible);
}