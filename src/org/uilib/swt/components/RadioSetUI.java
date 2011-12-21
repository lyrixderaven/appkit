package org.uilib.swt.components;

import java.util.List;

import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Listener;

import org.uilib.swt.states.StringState;
import org.uilib.swt.templating.Options;

public class RadioSetUI implements UIController<StringState> {

	//~ Instance fields ------------------------------------------------------------------------------------------------

	private Listener listener;
	private StringState state = StringState.empty();
	private List<String> i18nTexts = null;

	//~ Methods --------------------------------------------------------------------------------------------------------

	@Override
	public Control initialize(final Composite parent, final Options options) {

		Composite comp = new Composite(parent, SWT.NONE);
		GridLayout gl = new GridLayout(1,false);
		gl.marginHeight = 0;
		gl.marginWidth = 0;
		gl.horizontalSpacing = 0;
		gl.verticalSpacing = 0;
		comp.setLayout(gl);

		List<String> choices = options.get("choices");
		if (this.i18nTexts != null)
			Preconditions.checkState(this.i18nTexts.size() == choices.size(),"number of buttons %s must equals number of translations %s", choices.size(), this.i18nTexts.size());

		int i=0;
		for (final String opt : options.get("choices")) {

			Button btn = new Button(comp, SWT.RADIO);

			if (this.i18nTexts != null)
				btn.setText(this.i18nTexts.get(i));

			/* select first */
			if (i == 0) {
				btn.setSelection(true);
				state = new StringState(opt);
			}
			i++;

			btn.addSelectionListener(
				new SelectionAdapter() {
						@Override
						public void widgetSelected(final SelectionEvent event) {
							state = new StringState(opt);
							listener.handleEvent(null);
						}
					});
		}

		return comp;
	}

	public void setStatusQueue(final Listener listener) {
		Preconditions.checkArgument(this.listener == null, "already a listener registered");
		this.listener = listener;
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
		this.i18nTexts = Lists.newArrayList(Splitter.on("/").split(text));
	}
}