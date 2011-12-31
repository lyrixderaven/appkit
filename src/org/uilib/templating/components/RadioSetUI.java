package org.uilib.templating.components;

import com.google.common.base.Splitter;
import com.google.common.collect.Maps;

import java.util.Iterator;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import org.uilib.EventContext;
import org.uilib.templating.Options;
import org.uilib.util.Texts.CustomI18N;

public class RadioSetUI implements ComponentUI, CustomI18N {

	//~ Static fields/initializers -------------------------------------------------------------------------------------

	private static final Logger L = LoggerFactory.getLogger(RadioSetUI.class);

	//~ Instance fields ------------------------------------------------------------------------------------------------

	private Map<String, Button> choices = Maps.newHashMap();
	private Composite comp			    = null;
	private String selection		    = "";

	//~ Methods --------------------------------------------------------------------------------------------------------

	/* convenince function for using DatePicker directly */
	public Control initialize(final EventContext app, final Composite parent, final Options options) {
		return this.initialize(app, parent, null, null, options);
	}

	@Override
	public Control initialize(final EventContext app, final Composite parent, final String name, final String type,
							  final Options options) {

		int style = SWT.NONE;
		style |= (options.get("border", false) ? SWT.BORDER : SWT.NONE);

		this.comp = new Composite(parent, style);

		GridLayout gl = new GridLayout(1, false);
		gl.marginHeight			 = 0;
		gl.marginWidth			 = 0;
		gl.horizontalSpacing     = 0;
		gl.verticalSpacing		 = 0;
		this.comp.setLayout(gl);

		int i = 0;
		for (final String choice : options.get("choices")) {

			Button btn = new Button(this.comp, SWT.RADIO);
			this.choices.put(choice, btn);
			btn.addSelectionListener(
				new SelectionAdapter() {
						@Override
						public void widgetSelected(final SelectionEvent event) {
							selection = choice;
							app.postEvent(selection);
						}
					});

			/* if it's the first, select it */
			if (i == 0) {
				btn.setSelection(true);
				this.selection = choice;
			}
			i++;
		}

		return comp;
	}

	public String getSelection() {
		return this.selection;
	}

	@Override
	public void translate(final String i18nInfo) {
		for (final String choice : Splitter.on("/").split(i18nInfo)) {

			Iterator<String> i = Splitter.on(":").trimResults().split(choice).iterator();
			String code		   = i.next();
			String text		   = i.next();
			L.debug("i18n text for option '" + code + "' is '" + text + "'");
			this.choices.get(code).setText(text);
		}

		this.comp.layout();
	}
}