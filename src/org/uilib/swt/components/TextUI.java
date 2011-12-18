package org.uilib.swt.components;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;

import org.uilib.swt.states.StringState;
import org.uilib.swt.templating.Options;

public class TextUI implements UIController<StringState> {

	//~ Instance fields ------------------------------------------------------------------------------------------------

	private Text text = null;

	//~ Methods --------------------------------------------------------------------------------------------------------

	@Override
	public Control initialize(final Composite parent, final Options options) {

		int style = SWT.NONE;
		style |= (options.get("border", true) ? SWT.BORDER : SWT.NONE);

		this.text = new Text(parent, style);

		return this.text;
	}

	@Override
	public StringState getState() {
		return new StringState(this.text.getText());
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
		this.text.setMessage(text);
	}
}