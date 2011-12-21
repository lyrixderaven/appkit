package org.uilib.swt.components;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import org.uilib.swt.states.State;
import org.uilib.swt.templating.Options;

public class ButtonUI implements UIController<State> {

	//~ Instance fields ------------------------------------------------------------------------------------------------

	private Button btn;
	private String i18nText;

	//~ Methods --------------------------------------------------------------------------------------------------------

	@Override
	public Control initialize(final Composite parent, final Options options) {

		int style = SWT.NONE;

		this.btn = new Button(parent, style);
		this.btn.setText(this.i18nText);

		return this.btn;
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
		this.i18nText = text;
	}
}