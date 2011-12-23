package org.uilib.templating.components;

import java.util.List;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import org.uilib.templating.Component;
import org.uilib.templating.Options;

public interface ComponentUI {

	//~ Methods --------------------------------------------------------------------------------------------------------

	public Control initialize(final Composite parent, final List<Component> children, final Options options);
}