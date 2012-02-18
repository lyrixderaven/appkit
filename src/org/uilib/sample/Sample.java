package org.uilib.sample;

import com.google.common.eventbus.Subscribe;

import java.util.Locale;
import java.util.Properties;

import org.apache.log4j.PropertyConfigurator;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.uilib.application.LocalEventContext;
import org.uilib.overlay.Overlay;
import org.uilib.overlay.SpinnerOverlay;
import org.uilib.registry.Texts;
import org.uilib.templating.Component;
import org.uilib.templating.Templating;
import org.uilib.templating.components.DatepickerUI.DateRange;
import org.uilib.templating.components.SearchUI;
import org.uilib.templating.components.TableUI;
import org.uilib.util.SmartExecutor;
import org.uilib.util.prefs.PrefStore;
import org.uilib.widget.util.TableScrollDetector.ScrollEvent;
import org.uilib.widget.util.TableScrollDetector.ScrollListener;
import org.uilib.widget.util.TableUtils;

public final class Sample {

	//~ Static fields/initializers -------------------------------------------------------------------------------------

	private static final Logger L = LoggerFactory.getLogger(Sample.class);

	//~ Instance fields ------------------------------------------------------------------------------------------------

	private Shell shell;
	private Component orders;
	private SmartExecutor executor;
	private Overlay overlay;

	//~ Constructors ---------------------------------------------------------------------------------------------------

	public Sample() {
		/* Log4J Configuration */
		PropertyConfigurator.configure(log4jProperties());

		/* create a shell */
		shell					  = new Shell();
		shell.setLayout(new FillLayout());

		/* create templating and load a template */
		Templating templating = Templating.fromResources();

		/* register the SearchUI component */
		templating.registerType(SearchUI.class, "search");

		/* for catching all local events (see the methods tagged with @Subscribe) */
		LocalEventContext eventContext = new LocalEventContext(this);

		/* create the orderview component with the given eventContext */
		orders = templating.create("orderview");
		orders.initialize(eventContext, shell);

		/* translate component */
		Texts.forComponent("orderview", Locale.ENGLISH).translateComponent(orders);

		/* output naming for debugging purposes */
		L.debug(orders.getNaming().toString());

		/* selects the table */
		Table t = orders.selectUI("orders$table", TableUI.class).getTable();
		t.setHeaderVisible(true);

		/* create columns */
		for (int i = 0; i <= 6; i++) {

			TableColumn c1 = new TableColumn(t, SWT.NONE);
			c1.setText("col " + i);
		}

		/* add items */
		for (int i = 0; i <= 15; i++) {

			TableItem i1 = new TableItem(t, SWT.NONE);
			i1.setText("item " + i);
		}

		PrefStore prefStore = PrefStore.createJavaPrefStore("org/uilib/sample");
		executor = SmartExecutor.create();

		/* divide table equally among columns */
		TableUtils.fillTableWidth(t);

		/* restore and save column-weights and order */
		TableUtils.rememberColumnWeights(prefStore, executor, t, "sample");
		TableUtils.rememberColumnOrder(prefStore, executor, t, "sample");

		/* resize columns proportionally if table is resized */
		TableUtils.autosizeColumns(t);

		/* install a ScrollDetector */
		TableUtils.installScrollListener(
			t,
			new ScrollListener() {
					@Override
					public void scrolled(final ScrollEvent event) {
						L.debug("first vis: {}", event.getFirstVisibleRow());
						L.debug("last vis: {}", event.getLastVisibleRow());
					}
				});

		shell.open();

		while (! shell.isDisposed()) {
			if (! shell.getDisplay().readAndDispatch()) {
				shell.getDisplay().sleep();
			}
		}

		executor.shutdown();
	}

	//~ Methods --------------------------------------------------------------------------------------------------------

	public static void main(final String args[]) {
		new Sample();
	}

	@Subscribe
	public void localEvent(final Object object) {
		L.debug("event: " + object);

		/* display a spinner: unfinished */
		//final Table t = orders.selectUI("orders$table", TableUI.class).getTable();
		if (this.overlay != null) {
			this.overlay.dispose();
		}

		this.overlay = new Overlay(this.executor, (Composite) orders.getControl(), new SpinnerOverlay());
		this.overlay.show();
	}

	@Subscribe
	public void daterangeChange(final DateRange daterange) {
		L.debug("we got a date-range: " + daterange);
	}

	public static Properties log4jProperties() {

		Properties props = new Properties();

		props.setProperty("log4j.rootLogger", "DEBUG,console");
		props.setProperty("log4j.appender.console", "org.apache.log4j.ConsoleAppender");
		props.setProperty("log4j.appender.console.Threshold", "DEBUG");
		props.setProperty("log4j.appender.console.layout", "org.apache.log4j.PatternLayout");
		props.setProperty("log4j.appender.console.layout.ConversionPattern", "%d [%t] %-5p %c - %m%n");

		return props;
	}
}