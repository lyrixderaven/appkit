package org.uilib.swt.components;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Table;

import org.uilib.swt.states.State;
import org.uilib.swt.templating.Options;

public class TableUI implements UIController<State> {

	//~ Methods --------------------------------------------------------------------------------------------------------

	@Override
	public Control initialize(final Composite parent, final Options options) {

		/* table is only child so that ColumnWeightLayout workds */
		Composite comp = new Composite(parent, SWT.NONE);

		int style	   = SWT.NONE;
		style |= (options.get("border", true) ? SWT.BORDER : SWT.NONE);
		style |= (options.get("virtual", true) ? SWT.VIRTUAL : SWT.NONE);
		style |= (options.get("fullselect", false) ? SWT.FULL_SELECTION : SWT.NONE);

		return new Table(comp, style);
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