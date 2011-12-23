package org.uilib.templating.components;

import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import org.uilib.templating.Component;
import org.uilib.templating.Options;

public class RadioSetUI implements ComponentUI {

	//~ Instance fields ------------------------------------------------------------------------------------------------

	private Map<String, Button> choices = Maps.newHashMap();

	//~ Methods --------------------------------------------------------------------------------------------------------

	@Override
	public Control initialize(final Composite parent, final List<Component> children, final Options options) {

		Composite comp		 = new Composite(parent, SWT.NONE);
		GridLayout gl		 = new GridLayout(1, false);
		gl.marginHeight		 = 0;
		gl.marginWidth		 = 0;
		gl.horizontalSpacing = 0;
		gl.verticalSpacing   = 0;
		comp.setLayout(gl);

		int i = 0;
		for (final String opt : options.get("choices")) {

			Button btn = new Button(comp, SWT.RADIO);
			this.choices.put(opt, btn);

			/* if it's the first, select it */
			if (i == 0) {
				btn.setSelection(true);
			}
			i++;
		}

		return comp;
	}

	public Map<String, Button> getChoices() {
		return this.choices;
	}
}