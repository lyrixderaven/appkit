package org.uilib.swt.components;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import org.uilib.swt.states.State;
import org.uilib.swt.templating.Options;

public class ComponentUI implements UIController<State> {

	//~ Methods --------------------------------------------------------------------------------------------------------

	@Override
	public Composite initialize(final Composite parent, final Options options) {

		int style = SWT.NONE;
		style |= (options.get("border", true) ? SWT.BORDER : SWT.NONE);

		return new Composite(parent, style);
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