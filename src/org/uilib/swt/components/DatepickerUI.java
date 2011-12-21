package org.uilib.swt.components;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

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
import org.eclipse.swt.widgets.Listener;
import org.uilib.swt.templating.Options;
import org.uilib.util.Texts;

import com.google.common.base.Preconditions;

// FIXME: intern mit komponents rumwerfen?
// FIXME: jodatime?
public final class DatepickerUI implements UIController<DatepickerUI.DateChoice> {

	//~ Methods --------------------------------------------------------------------------------------------------------

	private Listener listener;
	private DateTime dt;
	private Button bEnableFrom;
	private Button bEnableTo;
	private DateTime dtFrom;
	private DateTime dtTo;
	private Composite comp;

	@Override
	public Control initialize(final Composite parent, final Options options) {

		comp = new Composite(parent, SWT.NONE);
		GridLayout gl = new GridLayout(3,false);
		gl.marginHeight = 0;
		gl.marginWidth = 0;
		gl.verticalSpacing = 2;
		comp.setLayout(gl);

		// FIXME: language out of here?
		Texts texts = Texts.defaults("de");

		if (!options.get("range",true)) {
			this.dt = new DateTime(comp,SWT.DATE|SWT.BORDER|SWT.DROP_DOWN);
			this.dt.setLayoutData(new GridData(SWT.NONE,SWT.NONE,false,false,3,1));
			this.dt.addSelectionListener(
					new SelectionAdapter() {
							@Override
							public void widgetSelected(final SelectionEvent event) {
								listener.handleEvent(null);
							}
						});
		} else {
			final Label labelFrom = new Label(comp,SWT.NONE);
			labelFrom.setText(texts.get("datepicker_from"));
			labelFrom.setLayoutData(new GridData(SWT.RIGHT,SWT.CENTER,false,false));
			this.dtFrom = new DateTime(comp,SWT.DATE|SWT.BORDER|SWT.DROP_DOWN);
			this.dtFrom.setLayoutData(new GridData(SWT.NONE,SWT.CENTER,false,false));
			this.dtFrom.addSelectionListener(
					new SelectionAdapter() {
							@Override
							public void widgetSelected(final SelectionEvent event) {
								listener.handleEvent(null);
							}
						});
			this.bEnableFrom = new Button(comp, SWT.CHECK);
			this.bEnableFrom.setLayoutData(new GridData(SWT.NONE,SWT.FILL,false,false));
			this.bEnableFrom.setSelection(true);
			this.bEnableFrom.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent event) {
					dtFrom.setEnabled(bEnableFrom.getSelection());
					listener.handleEvent(null);
				}
			});
			final Label labelTo = new Label(comp,SWT.NONE);
			labelTo.setText(texts.get("datepicker_to"));
			labelTo.setLayoutData(new GridData(SWT.RIGHT,SWT.CENTER,false,false));
			this.dtTo = new DateTime(comp,SWT.DATE|SWT.BORDER|SWT.DROP_DOWN);
			this.dtTo.setLayoutData(new GridData(SWT.NONE,SWT.CENTER,false,false));
			this.dtTo.addSelectionListener(
				new SelectionAdapter() {
						@Override
						public void widgetSelected(final SelectionEvent event) {
							listener.handleEvent(null);
						}
					});
			this.bEnableTo = new Button(comp, SWT.CHECK);
			this.bEnableTo.setLayoutData(new GridData(SWT.NONE,SWT.FILL,false,false));
			this.bEnableTo.setSelection(true);
			this.bEnableTo.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent event) {
					dtTo.setEnabled(bEnableTo.getSelection());
					listener.handleEvent(null);
				}
			});
		}

		return comp;
	}

	public Composite getControl() {
		return this.comp;
	}

	@Override
	public DateChoice getState() {
		Date dateFrom = null;
		Date dateTo = null;

		if (this.bEnableFrom.getSelection()) {
			dateFrom = this.constructDate(this.dtFrom);
		}

		if (this.bEnableTo.getSelection()) {
			dateTo = this.constructDate(this.dtTo);
		}

		if (dateFrom != null && dateTo != null) {
			if (dateFrom.compareTo(dateTo) > 0) {
				Date temp = dateFrom;
				dateFrom = dateTo;
				dateTo = temp;
			}
		}

		return new DateChoice(dateFrom,dateTo);

	}

	private Date constructDate(DateTime dt) {
		Calendar cal = Calendar.getInstance();
		cal.clear();
		cal.set(Calendar.DAY_OF_MONTH, dt.getDay());
		cal.set(Calendar.MONTH, dt.getMonth());
		cal.set(Calendar.YEAR, dt.getYear());

		return cal.getTime();
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

	// FIXME: nullPointer Listener / Status
	public void setStatusQueue(final Listener listener) {
		Preconditions.checkArgument(this.listener == null, "already a listener registered");
		this.listener = listener;
	}

	@SuppressWarnings("serial")
	public static final class DateChoice implements Serializable {
		private final Date fromDate;
		private final Date toDate;

		public DateChoice(final Date fromDate, final Date toDate) {
			this.fromDate = fromDate;
			this.toDate = toDate;
		}

		public Date getFromDate() {
			return fromDate;
		}

		public Date getToDate() {
			return toDate;
		}
	}
}