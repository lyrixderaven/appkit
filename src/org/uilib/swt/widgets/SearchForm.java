package org.uilib.swt.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import org.uilib.util.Texts;

public class SearchForm extends Composite {

	//~ Instance fields ------------------------------------------------------------------------------------------------

	private final Text text;
	private final Texts texts;

	//~ Constructors ---------------------------------------------------------------------------------------------------

	public SearchForm(final Composite parent, final int style, final Texts texts) {
		super(parent, style);

		this.texts = texts;

		boolean delButtonNeeded = true;
		if (System.getProperty("os.name").equals("Mac OS X")) {
			delButtonNeeded = false;
		}

		this.setLayout(new GridLayout((delButtonNeeded ? 3 : 2), false));
		((GridLayout) this.getLayout()).horizontalSpacing = -1;

		Label label = new Label(this, SWT.NONE);
		label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		label.setText(" x ");

		if (delButtonNeeded) {

			Button bDelete = new Button(this, SWT.PUSH);
			bDelete.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
			bDelete.setText(this.texts.get("misc_searchdelete"));
			bDelete.addSelectionListener(new BDeleteClicked());
		}

		this.text = new Text(this, SWT.SEARCH | SWT.CANCEL);
		this.text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		this.text.addFocusListener(new FocusChanged());
	}

	//~ Methods --------------------------------------------------------------------------------------------------------

	public Text getTextWidget() {
		return this.text;
	}

	public void clear() {
		this.text.setText("");
	}

	//~ Inner Classes --------------------------------------------------------------------------------------------------

	private final class FocusChanged implements FocusListener {
		@Override
		public void focusGained(final FocusEvent e) {
			text.selectAll();
		}

		@Override
		public void focusLost(final FocusEvent e) {}
	}

	private class BDeleteClicked extends SelectionAdapter {
		@Override
		public void widgetSelected(final SelectionEvent event) {
			clear();
			text.setFocus();
		}
	}
}