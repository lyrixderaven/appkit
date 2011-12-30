package org.uilib.util;

import com.google.common.collect.ImmutableMap;

import java.io.IOException;
import java.io.StringReader;

import java.text.MessageFormat;

import java.util.Map.Entry;
import java.util.Properties;

import org.apache.log4j.Logger;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import org.uilib.templating.Component;

// FIXME: Texts: getSystemDefault Lang
// FIXME: Texts werden z.B. mit jedem DatePicker angelegt, globals Caching irgendwie? Interface?
public class Texts {

	//~ Static fields/initializers -------------------------------------------------------------------------------------

	private static final Logger L = Logger.getLogger(Texts.class);

	//~ Instance fields ------------------------------------------------------------------------------------------------

	private final ImmutableMap<String, String> texts;

	//~ Constructors ---------------------------------------------------------------------------------------------------

	public Texts(final StringSupplier supplier, final String resourceName) {

		ImmutableMap.Builder<String, String> map = ImmutableMap.builder();

		try {
			L.debug("loading language out of ressource: " + resourceName);

			Properties i18n = new Properties();
			i18n.load(new StringReader(supplier.get(resourceName)));

			for (final String property : i18n.stringPropertyNames()) {

				String msgIdentifier = property;
				String msg			 = i18n.getProperty(property);
				L.debug(msgIdentifier + " -> " + msg);
				map.put(msgIdentifier, msg);
			}
		} catch (final IOException e) {
			L.error(e.getMessage(), e);
		}

		this.texts = map.build();
	}

	//~ Methods --------------------------------------------------------------------------------------------------------

	public static Texts fromResources(final String lang) {
		return new Texts(new ResourceToStringSupplier(), "i18n/" + lang + ".properties");
	}

	public static Texts forComponent(final String componentType, final String lang) {
		return new Texts(new ResourceToStringSupplier(), "components/" + componentType + "." + lang + ".properties");
	}

	public void translateComponent(final Component component) {
		for (final Entry<String, String> msg : this.texts.entrySet()) {

			Component sub = component.select(msg.getKey());

			if (sub.getUI() instanceof CustomI18N) {

				/* custom translation */
				((CustomI18N) sub.getUI()).translate(msg.getValue());

			} else {

				/* standard widgets */
				Control c = sub.getControl();
				if (c instanceof Button) {
					((Button) c).setText(msg.getValue());
				} else if (c instanceof Label) {
					((Label) c).setText(msg.getValue());
				} else if (c instanceof Text) {
					((Text) c).setText(msg.getValue());
				} else {
					L.error("don't know how to translate widget: " + sub);
				}

				c.getParent().layout();
			}
		}
	}

	public String get(final String identifier, final Object... values) {

		String text = this.texts.get(identifier);

		if (text == null) {
			text = "<missing identifier>";
			L.error("missing identifier: " + identifier);
		}

		return MessageFormat.format(text, values);
	}

	//~ Inner Interfaces -----------------------------------------------------------------------------------------------

	public static interface CustomI18N {
		public void translate(final String i18nInfo);
	}
}