package org.uilib.swt.components;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.uilib.swt.Fonts;
import org.uilib.swt.states.State;
import org.uilib.swt.templating.Options;

public class LabelUI implements UIController<State> {

	private Label label;
	private String i18nText;

	@Override
	public Control initialize(final Composite parent, final Options options) {
		this.label = new Label(parent, SWT.NONE);

		this.label.setText(this.i18nText);

		List<String> fontInfo = options.get("font");
		if (!fontInfo.isEmpty()) {

			if (fontInfo.contains("bold"))
				this.label.setFont((new Fonts()).create(Fonts.Style.BOLD, 0));
		}


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
		this.i18nText = text;
	}
}