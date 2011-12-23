package org.uilib.controllers;

import com.google.common.base.Preconditions;

import java.util.Map.Entry;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;

import org.uilib.Application;
import org.uilib.ComponentController;
import org.uilib.templating.Component;
import org.uilib.templating.components.RadioSetUI;

public class DatepickerControl implements ComponentController {

	//~ Methods --------------------------------------------------------------------------------------------------------

	@Override
	public void init(final Application app, final Component comp) {
		Preconditions.checkArgument(
			comp.getController() instanceof RadioSetUI,
			"invalid controller %s" + comp.getController());

		RadioSetUI radioset = (RadioSetUI) comp.getController();

		for (final Entry<String, Button> choice : radioset.getChoices().entrySet()) {
			choice.getValue().addSelectionListener(
				new SelectionAdapter() {
						@Override
						public void widgetSelected(final SelectionEvent event) {
							app.postEvent(choice.getKey());
						}
					});
		}
	}
}