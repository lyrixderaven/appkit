package org.uilib.swt.templating.components;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import org.uilib.swt.templating.Options;

// FIXME: Templating: umbenennen? UI-Creator?
public interface UIController {

	//~ Methods --------------------------------------------------------------------------------------------------------

	public Control initialize(final Composite parent, final Options options);
}