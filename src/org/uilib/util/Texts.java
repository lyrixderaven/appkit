package org.uilib.util;

import com.google.common.collect.ImmutableMap;

import java.io.IOException;
import java.io.StringReader;

import java.text.MessageFormat;

import java.util.Properties;

import org.apache.log4j.Logger;

public class Texts {

	//~ Static fields/initializers -------------------------------------------------------------------------------------

	private static final Logger L = Logger.getLogger(Texts.class);

	//~ Instance fields ------------------------------------------------------------------------------------------------

	private final ImmutableMap<String, String> texts;

	//~ Constructors ---------------------------------------------------------------------------------------------------

	private Texts(final StringSupplier supplier, final String resourceName) {

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

	// TODO: Performance: darf nicht immer dasselbe zurückgeben, andere statics
	public static Texts defaults(final String lang) {
		return new Texts(new ResourceToStringSupplier(), "i18n/" + lang + ".properties");
	}

	public static Texts forComponent(final String componentType, final String lang) {
		return new Texts(new ResourceToStringSupplier(), "components/" + componentType + "." + lang + ".properties");
	}

	public String get(final String identifier, final Object... values) {

		String text = this.texts.get(identifier);

		if (text == null) {
			text = "<missing identifier>";
			L.error("missing identifier: " + identifier);
		}

		return MessageFormat.format(text, values);
	}

	public ImmutableMap<String, String> getMap() {
		return this.texts;
	}
}