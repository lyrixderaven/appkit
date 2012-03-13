package org.appkit.registry;

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

import org.appkit.templating.Component;
import org.appkit.templating.components.RadioSetUI;
import org.appkit.util.ParamSupplier;
import org.appkit.util.ResourceStreamSupplier;

/**
 * Loads and stores I18N-Strings and provides method for working with them.
 *
 */
public final class Texts {

	//~ Static fields/initializers -------------------------------------------------------------------------------------

	private static final Logger L = LoggerFactory.getLogger(Texts.class);

	//~ Instance fields ------------------------------------------------------------------------------------------------

	private final ImmutableMap<String, String> texts;

	//~ Constructors ---------------------------------------------------------------------------------------------------

	/**
	 * loads Text by passing the <code>resourceName</code> into the <code>dataSupplier</data>
	 */
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

	/**
	 * loads the default-texts using the default-locale from resources.
	 * Example: on a english-system this would load i18n/en.properties from resources.
	 */
	public static Texts fromResources() {
		return fromResources(Locale.getDefault());
	}

	/**
	 * loads texts using the default-locale for a given component type.
	 * Example: specifying "orderview" on an english system will load components/orderview.en.properties
	 */
	public static Texts forComponent(final String componentType) {
		return forComponent(componentType, Locale.getDefault());
	}

	/**
	 * loads the default-texts using the given locale
	 */
	public static Texts fromResources(final Locale locale) {
		return new Texts(new ResourceStreamSupplier(), "i18n/" + locale.getLanguage() + ".properties");
	}

	/**
	 * loads texts using the given locale for a given component type.
	 */
	public static Texts forComponent(final String componentType, final Locale locale) {
		return new Texts(
			new ResourceStreamSupplier(),
			"components/" + componentType + "." + locale.getLanguage() + ".properties");
	}

	/**
	 * translates a {@link Component} by using all keys of .properties file to
	 * find sub-components. Calls setText() {@link Button}s, {@link Text}s and {@link Label}s.
	 * Other widgets/component are ignored until the implement {@link CustomI18N}.
	 *
	 * @see CustomI18N
	 * @see Component#selectUI(String, Class)
	 */
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

	/**
	 * returns the I18N-string for the given identifier and values
	 *
	 * @return "<missing identifier>" if no string was found
	 */
	public String get(final String identifier, final Object... values) {

		String text = this.texts.get(identifier);

		if (text == null) {
			text = "<missing identifier>";
			L.error("missing identifier: " + identifier);
		}

		return MessageFormat.format(text, values);
	}

	//~ Inner Interfaces -----------------------------------------------------------------------------------------------

	/**
	 * Implement this to make a component translatable. The content of i18nInfo doesn't have to follow a particular format.
	 * It usually is just a string but for {@link RadioSetUI} for example it contains multiple key-value-pairs to
	 * translate all it's options.
	 */
	public static interface CustomI18N {
		public void translate(final String i18nInfo);
	}
}