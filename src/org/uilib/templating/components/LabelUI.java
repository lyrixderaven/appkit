package org.uilib.templating.components;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

import org.uilib.application.EventContext;
import org.uilib.registry.Fonts;
import org.uilib.templating.Options;

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
				Fonts.set(label, Fonts.Style.BOLD);
			}
		}

		return label;
	}
}