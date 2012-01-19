package org.uilib.templating.components;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import org.uilib.application.EventContext;
import org.uilib.templating.Options;

public interface ComponentUI {

	//~ Methods --------------------------------------------------------------------------------------------------------

	Control initialize(final EventContext app, final Composite parent, final String name, final String type,
					   final Options options);
}