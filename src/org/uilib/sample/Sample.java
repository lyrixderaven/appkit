package org.uilib.sample;

import com.google.common.eventbus.Subscribe;

import java.util.Locale;
import java.util.Properties;

import org.apache.log4j.PropertyConfigurator;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.uilib.application.LocalEventContext;
import org.uilib.overlay.Overlay;
import org.uilib.registry.Texts;
import org.uilib.templating.Component;
import org.uilib.templating.Templating;
import org.uilib.templating.components.DatepickerUI.DateRange;
import org.uilib.templating.components.SearchUI;
import org.uilib.templating.components.TableUI;
import org.uilib.util.LoggingRunnable;
import org.uilib.util.SmartExecutor;
import org.uilib.util.prefs.PrefStore;
import org.uilib.widget.util.TableUtils;

public final class Sample {

	//~ Static fields/initializers -------------------------------------------------------------------------------------

	private static final Logger L = LoggerFactory.getLogger(Sample.class);

	//~ Instance fields ------------------------------------------------------------------------------------------------

	private Shell shell;
	private Component orders;
	private SmartExecutor executor;

	//~ Constructors ---------------------------------------------------------------------------------------------------

	public Sample() {
		/* Log4J Configuration */
		PropertyConfigurator.configure(log4jProperties());

		/* create a shell */
		shell					  = new Shell();
		shell.setLayout(new FillLayout());

		/* create templating and load a template */
		Templating templating = Templating.fromResources();
		templating.registerType(SearchUI.class, "search");

		/* instantiate a component with simple event-context */
		LocalEventContext eventContext = new LocalEventContext(this);

		orders = templating.create("orderview");
		orders.initialize(eventContext, shell);

		/* translate component */
		Texts.forComponent("orderview", Locale.ENGLISH).translateComponent(orders);

		/* output naming for debugging purposes */
		L.debug(orders.getNaming().toString());

		/* test */
		Table t = orders.selectUI("orders.$table", TableUI.class).getTable();
		t.setHeaderVisible(true);

		TableColumn c1 = new TableColumn(t, SWT.NONE);
		c1.setText("eins");

		TableColumn c2 = new TableColumn(t, SWT.NONE);
		c2.setText("zwei");

		TableColumn c3 = new TableColumn(t, SWT.NONE);
		c3.setText("drei");

		TableItem item = new TableItem(t, SWT.NONE);
		item.setText("asdasdas");

		TableUtils.autosizeColumns(t);

		PrefStore prefStore    = PrefStore.createJavaPrefStore("org/uilib/sample");
		executor = SmartExecutor.create();
		TableUtils.rememberColumnSizes(prefStore, executor, t, "test");
		TableUtils.rememberColumnOrder(prefStore, executor, t, "test");

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

		Table t    = orders.selectUI("orders.$table", TableUI.class).getTable();
		final Overlay ov = new Overlay(t);

		ov.show();
		executor.execute(new LoggingRunnable() {

			@Override
			public void runChecked() {

			}

		});

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