package org.uilib.swt.components;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

import org.uilib.swt.states.State;
import org.uilib.swt.templating.Options;

public class LabelUI implements UIController<State> {

	//~ Instance fields ------------------------------------------------------------------------------------------------

	private Label label;

	//~ Methods --------------------------------------------------------------------------------------------------------

	@Override
	public Control initialize(final Composite parent, final Options options) {
		this.label = new Label(parent, SWT.NONE);

		this.label.setText("test");

		return this.label;
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
	public void setI18nText(final String text) {
		this.label.setText(text);
	}
}