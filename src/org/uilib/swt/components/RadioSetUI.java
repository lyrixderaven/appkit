package org.uilib.swt.components;

import com.google.common.base.Preconditions;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import org.uilib.swt.states.StringState;
import org.uilib.swt.templating.Options;

public class RadioSetUI implements UIController<StringState> {

	//~ Instance fields ------------------------------------------------------------------------------------------------

	private SelectionListener listener;
	private StringState state = StringState.empty();

	//~ Methods --------------------------------------------------------------------------------------------------------

	@Override
	public Control initialize(final Composite parent, final Options options) {

		Composite comp = new Composite(parent, SWT.NONE);
		comp.setLayout(new GridLayout(1, false));

		for (final String opt : options.get("choices")) {

			Button btn = new Button(comp, SWT.NONE);
			btn.setText(opt);
			btn.addSelectionListener(
				new SelectionAdapter() {
						@Override
						public void widgetSelected(final SelectionEvent event) {
							state = new StringState(opt);
						}
					});
		}

		return comp;
	}

	public void setStatusQueue(final SelectionListener listener) {
		Preconditions.checkArgument(this.listener == null, "already a listener registered");
		listener.widgetSelected(null);
	}

	@Override
	public StringState getState() {
		return this.state;
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

		// TODO Auto-generated method stub
	}
}