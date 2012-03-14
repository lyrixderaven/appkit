package org.appkit.templating.components;

import org.appkit.application.EventContext;
import org.appkit.templating.Options;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public interface ComponentUI {

	//~ Methods --------------------------------------------------------------------------------------------------------

	/**
	 * creates the widgets
	 */
	Control initialize(final EventContext app, final Composite parent, final String name, final String type,
					   final Options options);
}