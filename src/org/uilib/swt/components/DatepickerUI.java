package org.uilib.swt.components;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

import org.uilib.swt.states.State;
import org.uilib.swt.templating.Options;

public class DatepickerUI implements UIController<State> {

	//~ Methods --------------------------------------------------------------------------------------------------------

	@Override
	public Control initialize(final Composite parent, final Options options) {

		Label label = new Label(parent, SWT.NONE);
		label.setText("datepicker");

		return label;
	}

	@Override
	public State getState() {
		return State.emptyState();
	}

	@Override
	public boolean fillVertByDefault() {
		return false;
	}

	@Override
	public boolean fillHorizByDefault() {
		return false;
	}

	@Override
	public void setI18nText(final String text) {}
}