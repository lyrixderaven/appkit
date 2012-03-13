package org.appkit.templating.components;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import org.appkit.application.EventContext;
import org.appkit.templating.Options;

public interface ComponentUI {

	//~ Methods --------------------------------------------------------------------------------------------------------

	/**
	 * creates the widgets
	 */
	Control initialize(final EventContext app, final Composite parent, final String name, final String type,
					   final Options options);
}