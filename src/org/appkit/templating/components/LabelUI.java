package org.appkit.templating.components;

import java.util.List;

import org.appkit.application.EventContext;
import org.appkit.registry.Fonts;
import org.appkit.templating.Options;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

/** for creating a component that is a {@link Label} */
public class LabelUI implements ComponentUI {

	//~ Methods --------------------------------------------------------------------------------------------------------

	@Override
	public Control initialize(final EventContext app, final Composite parent, final String name, final String type,
							  final Options options) {

		Label label = new Label(parent, SWT.NONE);
		label.setText("< empty >");

		List<String> fontInfo = options.get("font");
		if (! fontInfo.isEmpty()) {
			if (fontInfo.contains("bold")) {
				Fonts.set(label, Fonts.BOLD);
			}
		}

		return label;
	}
}