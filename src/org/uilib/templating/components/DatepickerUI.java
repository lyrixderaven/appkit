package org.uilib.templating.components;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Label;

import org.uilib.templating.Component;
import org.uilib.templating.Options;

public final class DatepickerUI implements ComponentUI {

	//~ Instance fields ------------------------------------------------------------------------------------------------

	private DateTime dt		   = null;
	private DateTime dtFrom    = null;
	private DateTime dtTo	   = null;
	private Label labelFrom    = null;
	private Label labelTo	   = null;
	private Button bEnableFrom = null;
	private Button bEnableTo   = null;

	//~ Methods --------------------------------------------------------------------------------------------------------

	@Override
	public Control initialize(final Composite parent, final List<Component> children, final Options options) {

		Composite comp     = new Composite(parent, SWT.NONE);
		GridLayout gl	   = new GridLayout(3, false);
		gl.marginHeight    = 0;
		gl.marginWidth     = 0;
		gl.verticalSpacing = 2;
		comp.setLayout(gl);

		if (! options.get("range", true)) {
			this.dt = new DateTime(comp, SWT.DATE | SWT.BORDER | SWT.DROP_DOWN);
			this.dt.setLayoutData(new GridData(SWT.NONE, SWT.NONE, false, false, 3, 1));
		} else {
			this.labelFrom = new Label(comp, SWT.NONE);
			this.labelFrom.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
			this.dtFrom = new DateTime(comp, SWT.DATE | SWT.BORDER | SWT.DROP_DOWN);
			this.dtFrom.setLayoutData(new GridData(SWT.NONE, SWT.CENTER, false, false));

			this.bEnableFrom = new Button(comp, SWT.CHECK);
			this.bEnableFrom.setLayoutData(new GridData(SWT.NONE, SWT.FILL, false, false));
			this.bEnableFrom.setSelection(true);

			this.labelTo = new Label(comp, SWT.NONE);
			this.labelTo.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
			this.dtTo = new DateTime(comp, SWT.DATE | SWT.BORDER | SWT.DROP_DOWN);
			this.dtTo.setLayoutData(new GridData(SWT.NONE, SWT.CENTER, false, false));
			this.bEnableTo = new Button(comp, SWT.CHECK);
			this.bEnableTo.setLayoutData(new GridData(SWT.NONE, SWT.FILL, false, false));
			this.bEnableTo.setSelection(true);
		}

		return comp;
	}

	public DateTime getDt() {
		return dt;
	}

	public DateTime getDtFrom() {
		return dtFrom;
	}

	public DateTime getDtTo() {
		return dtTo;
	}

	public Label getLabelFrom() {
		return labelFrom;
	}

	public Label getLabelTo() {
		return labelTo;
	}

	public Button getbEnableFrom() {
		return bEnableFrom;
	}

	public Button getbEnableTo() {
		return bEnableTo;
	}
}