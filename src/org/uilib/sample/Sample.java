package org.uilib.sample;

import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;

import java.util.Collections;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.PropertyConfigurator;

import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Shell;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.uilib.templating.Component;
import org.uilib.templating.Templating;
import org.uilib.util.Texts;

public final class Sample {

	//~ Static fields/initializers -------------------------------------------------------------------------------------

	private static final Logger L = LoggerFactory.getLogger(Sample.class);

	//~ Methods --------------------------------------------------------------------------------------------------------

	public static void main(final String args[]) {
		/* Log4J Configuration */
		PropertyConfigurator.configure(log4jProperties());

		Shell shell = new Shell();
		shell.setLayout(new FillLayout());

		Templating tl    = Templating.fromResources();
		Component orders = tl.create("orderview");
		orders.initialize(shell);

		Texts.forComponent("orderview", "de").translateComponent(orders);

		List<String> keys = Lists.newArrayList(orders.getNameMap().keySet());
		Collections.sort(keys, Ordering.natural());
		for (final String k : keys) {
			L.debug(k);
			for (final Component c : orders.getNameMap().get(k)) {
				L.debug("\t" + c.getType());
			}
		}

		shell.open();

		while (! shell.isDisposed()) {
			if (! shell.getDisplay().readAndDispatch()) {
				shell.getDisplay().sleep();
			}
		}
	}

	public static Properties log4jProperties() {

		Properties props = new Properties();

		props.setProperty("log4j.rootLogger", "DEBUG,console");
		props.setProperty("log4j.appender.console", "org.apache.log4j.ConsoleAppender");
		props.setProperty("log4j.appender.console.Threshold", "DEBUG");
		props.setProperty("log4j.appender.console.layout", "org.apache.log4j.PatternLayout");
		props.setProperty("log4j.appender.console.layout.ConversionPattern", "%d [%t] %-5p %c - %m%n");
		props.setProperty("log4j.appender.internal", "com.partner4media.tc.util.InternalLog$Logger");
		props.setProperty("log4j.appender.internal.Threshold", "DEBUG");
		props.setProperty("log4j.appender.file", "org.apache.log4j.FileAppender");
		props.setProperty("log4j.appender.file.File", "${java.io.tmpdir}/p4m-crash.log");
		props.setProperty("log4j.appender.file.layout", "org.apache.log4j.PatternLayout");
		props.setProperty("log4j.appender.file.layout.ConversionPattern", "%d [%t] %-5p %c - %m%n");
		props.setProperty("log4j.appender.file.Threshold", "ERROR");

		return props;
	}
}