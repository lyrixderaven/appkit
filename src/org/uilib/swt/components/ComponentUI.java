package org.uilib.swt.components;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import org.uilib.swt.states.State;
import org.uilib.swt.templating.Options;

public class ComponentUI implements UIController<State> {

	//~ Methods --------------------------------------------------------------------------------------------------------

	@Override
	public Composite initialize(final Composite parent, final Options options) {

		int style = SWT.NONE;
		style |= (options.get("border", false) ? SWT.BORDER : SWT.NONE);

		GridLayout gl = new GridLayout(-1, false);
		gl.marginHeight = options.get("margin-height", gl.marginHeight);
		gl.marginWidth = options.get("margin-width", gl.marginWidth);
		gl.verticalSpacing = options.get("v-spacing", gl.verticalSpacing);
		gl.horizontalSpacing = options.get("h-spacing", gl.horizontalSpacing);

		Composite comp = new Composite(parent, style);
		comp.setLayout(gl);

		return comp;
	}

	@Override
	public State getState() {
		return State.emptyState();
	}

	@Override
	public boolean fillVertByDefault() {
		return true;
	}

	@Override
	public boolean fillHorizByDefault() {
		return true;
	}

	@Override
	public void setI18nText(final String text) {}
}