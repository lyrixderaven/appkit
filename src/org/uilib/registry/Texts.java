package org.uilib.registry;

import com.google.common.collect.ImmutableMap;

import java.io.IOException;
import java.io.InputStream;

import java.text.MessageFormat;

import java.util.Locale;
import java.util.Map.Entry;
import java.util.Properties;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.uilib.templating.Component;
import org.uilib.util.ParamSupplier;
import org.uilib.util.ResourceStreamSupplier;

public class Texts {

	//~ Static fields/initializers -------------------------------------------------------------------------------------

	private static final Logger L = LoggerFactory.getLogger(Texts.class);

	//~ Instance fields ------------------------------------------------------------------------------------------------

	private final ImmutableMap<String, String> texts;

	//~ Constructors ---------------------------------------------------------------------------------------------------

	public Texts(final ParamSupplier<String, InputStream> dataSupplier, final String resourceName) {

		ImmutableMap.Builder<String, String> map = ImmutableMap.builder();

		try {
			L.debug("loading language out of ressource: " + resourceName);

			Properties i18n = new Properties();
			InputStream in  = dataSupplier.get(resourceName);

			i18n.load(in);
			in.close();

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

	public static Texts fromResources() {
		return fromResources(Locale.getDefault());
	}

	public static Texts forComponent(final String componentType) {
		return forComponent(componentType, Locale.getDefault());
	}

	public static Texts fromResources(final Locale locale) {
		return new Texts(new ResourceStreamSupplier(), "i18n/" + locale.getLanguage() + ".properties");
	}

	public static Texts forComponent(final String componentType, final Locale locale) {
		return new Texts(
			new ResourceStreamSupplier(),
			"components/" + componentType + "." + locale.getLanguage() + ".properties");
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