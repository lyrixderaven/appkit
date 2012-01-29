package org.uilib.sample;

import com.google.common.eventbus.Subscribe;

import java.util.Locale;
import java.util.Properties;

import org.apache.log4j.PropertyConfigurator;

import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Shell;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.uilib.application.LocalEventContext;
import org.uilib.registry.Texts;
import org.uilib.templating.Component;
import org.uilib.templating.Templating;
import org.uilib.templating.components.DatepickerUI.DateRange;
import org.uilib.templating.components.SearchUI;

public final class Sample {

	//~ Static fields/initializers -------------------------------------------------------------------------------------

	private static final Logger L = LoggerFactory.getLogger(Sample.class);

	//~ Constructors ---------------------------------------------------------------------------------------------------

	public Sample() {
		/* Log4J Configuration */
		PropertyConfigurator.configure(log4jProperties());

		/* create a shell */
		Shell shell = new Shell();
		shell.setLayout(new FillLayout());

		/* create templating and load a template */
		Templating templating = Templating.fromResources();
		templating.registerType(SearchUI.class, "search");

		/* instantiate a component with simple event-context */
		LocalEventContext eventContext = new LocalEventContext(this);

		Component orders			   = templating.create("orderview");
		orders.initialize(eventContext, shell);

		/* translate component */
		Texts.forComponent("orderview", Locale.ENGLISH).translateComponent(orders);

		/* output naming for debugging purposes */
		L.debug(orders.getNaming().toString());

		shell.open();

		while (! shell.isDisposed()) {
			if (! shell.getDisplay().readAndDispatch()) {
				shell.getDisplay().sleep();
			}
		}
	}

	//~ Methods --------------------------------------------------------------------------------------------------------

	public static void main(final String args[]) {
		new Sample();
	}

	@Subscribe
	public void localEvent(final Object object) {
		L.debug("event: " + object);
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