package org.uilib.controllers;

import com.google.common.base.Preconditions;

import java.util.Calendar;
import java.util.Date;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.DateTime;

import org.uilib.Application;
import org.uilib.ComponentController;
import org.uilib.templating.Component;
import org.uilib.templating.components.DatepickerUI;

public class RadioSetControl implements ComponentController {

	//~ Instance fields ------------------------------------------------------------------------------------------------

	private Application app;
	private DatepickerUI datepicker;

	//~ Methods --------------------------------------------------------------------------------------------------------

	@Override
	public void init(final Application app, final Component comp) {
		Preconditions.checkArgument(
			comp.getController() instanceof DatepickerUI,
			"invalid controller %s" + comp.getController());
		this.app		    = app;

		this.datepicker     = (DatepickerUI) comp.getController();

		this.datepicker.getDtFrom().addSelectionListener(
			new SelectionAdapter() {
					@Override
					public void widgetSelected(final SelectionEvent event) {
						fireChange();
					}
				});
		this.datepicker.getDtTo().addSelectionListener(
			new SelectionAdapter() {
					@Override
					public void widgetSelected(final SelectionEvent event) {
						fireChange();
					}
				});

		this.datepicker.getbEnableTo().addSelectionListener(
			new SelectionAdapter() {
					@Override
					public void widgetSelected(final SelectionEvent event) {
						datepicker.getDtTo().setEnabled(datepicker.getbEnableTo().getSelection());
						fireChange();
					}
				});
		this.datepicker.getbEnableFrom().addSelectionListener(
			new SelectionAdapter() {
					@Override
					public void widgetSelected(final SelectionEvent event) {
						datepicker.getDtFrom().setEnabled(datepicker.getbEnableFrom().getSelection());
						fireChange();
					}
				});
	}

	private void fireChange() {

		Date dateFrom = null;
		Date dateTo   = null;

		if (this.datepicker.getDtFrom().getEnabled()) {
			dateFrom = this.constructDate(this.datepicker.getDtFrom());
		}

		if (this.datepicker.getDtFrom().getEnabled()) {
			dateTo = this.constructDate(this.datepicker.getDtTo());
		}

		app.postEvent(new DateRange(dateFrom, dateTo));
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

			/* swap dates if necessary */
			if ((fromDate != null) && (toDate != null)) {
				if (fromDate.compareTo(toDate) <= 0) {
					this.fromDate     = fromDate;
					this.toDate		  = toDate;
				} else {
					this.fromDate     = toDate;
					this.toDate		  = fromDate;
				}
			} else {
				this.fromDate     = toDate;
				this.toDate		  = fromDate;
			}
		}

		public Date getFromDate() {
			return fromDate;
		}

		public Date getToDate() {
			return toDate;
		}
	}
}