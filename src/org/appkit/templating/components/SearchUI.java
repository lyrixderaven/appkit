package org.appkit.templating.components;

import java.util.Locale;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.appkit.application.EventContext;
import org.appkit.registry.Texts;
import org.appkit.templating.Component;
import org.appkit.templating.Options;
import org.appkit.templating.Templating;
import org.appkit.templating.components.ComponentUI;

/** for creating a component that aims to be a search-field */
public final class SearchUI implements ComponentUI {

	//~ Static fields/initializers -------------------------------------------------------------------------------------

	private static final Logger L = LoggerFactory.getLogger(SearchUI.class);

	//~ Instance fields ------------------------------------------------------------------------------------------------

	private Text text;

	//~ Methods --------------------------------------------------------------------------------------------------------

	@Override
	public Control initialize(final EventContext context, final Composite parent, final String name, final String type,
							  final Options options) {

		final Component search    = Templating.fromResources().create("search");
		search.initialize(parent);
		L.debug(search.getNaming().toString());

		search.select("clear", Button.class).addSelectionListener(
			new SelectionAdapter() {
					@Override
					public void widgetSelected(final SelectionEvent e) {
						search.select("$text", Text.class).setText("");
						search.select("$text", Text.class).setFocus();
					}
				});

		// hide button on cocoa
		if (SWT.getPlatform().equals("cocoa")) {
			search.hide("clear");
		}

		// i18n translation
		Texts.forComponent("search", Locale.ENGLISH).translateComponent(search);

		this.text = search.select("$text", Text.class);

		return search.getControl();
	}

	public Text getText() {
		return this.text;
	}
}