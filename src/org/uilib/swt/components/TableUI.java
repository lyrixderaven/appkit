package org.uilib.swt.components;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Table;

import org.uilib.swt.states.State;
import org.uilib.swt.templating.Options;

public class TableUI implements UIController<State> {

	//~ Methods --------------------------------------------------------------------------------------------------------

	private Table table;

	@Override
	public Control initialize(final Composite parent, final Options options) {

		/* table is only child so that ColumnWeightLayout works */
		Composite comp = new Composite(parent, SWT.NONE);
		comp.setLayout(new FillLayout());

		int style	   = SWT.NONE;
		style |= (options.get("border", true) ? SWT.BORDER : SWT.NONE);
		style |= (options.get("virtual", true) ? SWT.VIRTUAL : SWT.NONE);
		style |= (options.get("fullselect", true) ? SWT.FULL_SELECTION : SWT.NONE);
		style |= (options.get("check", false) ? SWT.CHECK : SWT.NONE);

		this.table = new Table(comp,style);
		this.table.setLinesVisible((options.get("lines", true)));

		return comp;
	}

	public Table getTable() {
		return this.table;
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