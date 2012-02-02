package org.uilib.templating.components;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Label;

import org.uilib.application.EventContext;
import org.uilib.registry.Texts;
import org.uilib.templating.Options;

public final class DatepickerUI implements ComponentUI {

	//~ Instance fields ------------------------------------------------------------------------------------------------

	private DateTime dt		    = null;
	private DateTime dtFrom     = null;
	private DateTime dtTo	    = null;
	private Label labelFrom     = null;
	private Label labelTo	    = null;
	private Button bEnableFrom  = null;
	private Button bEnableTo    = null;
	private DateRange daterange;

	//~ Methods --------------------------------------------------------------------------------------------------------

	/* convenince function for using DatePicker directly */
	public Control initialize(final EventContext app, final Composite parent, final Options options) {
		return this.initialize(app, parent, null, null, options);
	}

	@Override
	public Control initialize(final EventContext app, final Composite parent, final String name, final String type,
							  final Options options) {

		int style			    = SWT.NONE;
		style |= (options.get("border", false) ? SWT.BORDER : SWT.NONE);

		Composite comp     = new Composite(parent, style);
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
			this.labelFrom.setText(Texts.forComponent("datepicker", Locale.ENGLISH).get("from"));
			this.dtFrom = new DateTime(comp, SWT.DATE | SWT.BORDER | SWT.DROP_DOWN);
			this.dtFrom.setLayoutData(new GridData(SWT.NONE, SWT.CENTER, false, false));
			this.dtFrom.addSelectionListener(
				new SelectionAdapter() {
						@Override
						public void widgetSelected(final SelectionEvent event) {
							setInternalDateRange();
							app.postEvent(daterange);
						}
					});

			this.bEnableFrom = new Button(comp, SWT.CHECK);
			this.bEnableFrom.setLayoutData(new GridData(SWT.NONE, SWT.FILL, false, false));
			this.bEnableFrom.setSelection(true);
			this.bEnableFrom.addSelectionListener(
				new SelectionAdapter() {
						@Override
						public void widgetSelected(final SelectionEvent event) {
							dtFrom.setEnabled(bEnableFrom.getSelection());
							setInternalDateRange();
							app.postEvent(daterange);
						}
					});

			this.labelTo = new Label(comp, SWT.NONE);
			this.labelTo.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
			this.labelTo.setText(Texts.forComponent("datepicker", Locale.ENGLISH).get("to"));
			this.dtTo = new DateTime(comp, SWT.DATE | SWT.BORDER | SWT.DROP_DOWN);
			this.dtTo.setLayoutData(new GridData(SWT.NONE, SWT.CENTER, false, false));
			this.dtTo.addSelectionListener(
				new SelectionAdapter() {
						@Override
						public void widgetSelected(final SelectionEvent event) {
							setInternalDateRange();
							app.postEvent(daterange);
						}
					});
			this.bEnableTo = new Button(comp, SWT.CHECK);
			this.bEnableTo.setLayoutData(new GridData(SWT.NONE, SWT.FILL, false, false));
			this.bEnableTo.setSelection(true);
			this.bEnableTo.addSelectionListener(
				new SelectionAdapter() {
						@Override
						public void widgetSelected(final SelectionEvent event) {
							dtTo.setEnabled(bEnableTo.getSelection());
							setInternalDateRange();
							app.postEvent(daterange);
						}
					});
		}

		this.setInternalDateRange();

		return comp;
	}

	public DateRange getDateRange() {
		return this.daterange;
	}

	private void setInternalDateRange() {

		Date dateFrom = null;
		Date dateTo   = null;

		if (this.dtFrom.getEnabled()) {
			dateFrom = this.constructDate(this.dtFrom);
		}

		if (this.dtTo.getEnabled()) {
			dateTo = this.constructDate(this.dtTo);
		}

		this.daterange = new DateRange(dateFrom, dateTo);
	}

	private Date constructDate(final DateTime dt) {

		Calendar cal = Calendar.getInstance();
		cal.clear();
		cal.set(Calendar.DAY_OF_MONTH, dt.getDay());
		cal.set(Calendar.MONTH, dt.getMonth());
		cal.set(Calendar.YEAR, dt.getYear());

		return cal.getTime();
	}

	//~ Inner Classes --------------------------------------------------------------------------------------------------

	public static final class DateRange {

		private final Date fromDate;
		private final Date toDate;

		public DateRange(final Date fromDate, final Date toDate) {

			Date tempFrom;
			Date tempTo;

			/* swap dates if necessary */
			if ((fromDate != null) && (toDate != null)) {
				if (fromDate.compareTo(toDate) <= 0) {
					tempFrom     = fromDate;
					tempTo		 = toDate;
				} else {
					tempFrom     = toDate;
					tempTo		 = fromDate;
				}
			} else {
				tempFrom     = fromDate;
				tempTo		 = toDate;
			}

			if (tempFrom != null) {
				this.fromDate = new Date(tempFrom.getTime());
			} else {
				this.fromDate = null;
			}

			if (tempTo != null) {
				this.toDate = new Date(tempTo.getTime());
			} else {
				this.toDate = null;
			}
		}

		public Date getFromDate() {
			if (fromDate != null) {
				return new Date(fromDate.getTime());
			} else {
				return null;
			}
		}

		public Date getToDate() {
			if (toDate != null) {
				return new Date(toDate.getTime());
			} else {
				return null;
			}
		}

		@Override
		public String toString() {
			return "[" + ((this.fromDate != null) ? this.fromDate : "") + ".."
				   + ((this.toDate != null) ? this.toDate : "") + "]";
		}
	}
}