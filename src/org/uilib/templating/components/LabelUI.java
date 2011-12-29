package org.uilib.templating.components;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

import org.uilib.EventContext;
import org.uilib.templating.Options;
import org.uilib.util.Fonts;

public class LabelUI implements ComponentUI {

	//~ Methods --------------------------------------------------------------------------------------------------------

	@Override
	public Control initialize(final EventContext app, final Composite parent, final String name, final String type,
							  final Options options) {

		Label label			  = new Label(parent, SWT.NONE);

		List<String> fontInfo = options.get("font");
		if (! fontInfo.isEmpty()) {
			if (fontInfo.contains("bold")) {
				// FIXME: FontCache in Application oder so?
				label.setFont((new Fonts()).create(Fonts.Style.BOLD, 0));
			}
		}

		return label;
	}
}