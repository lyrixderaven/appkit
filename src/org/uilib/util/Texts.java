package org.uilib.util;

import com.google.common.collect.Maps;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

import java.text.MessageFormat;

import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;

public class Texts {

	//~ Static fields/initializers -------------------------------------------------------------------------------------

	private static final Texts INSTANCE = new Texts();
	private static final Logger L	    = Logger.getLogger(Texts.class);

	//~ Instance fields ------------------------------------------------------------------------------------------------

	private final Map<String, String> msgs = Maps.newHashMap();

	//~ Methods --------------------------------------------------------------------------------------------------------

	public static Texts instance() {
		return INSTANCE;
	}

	public void loadLanguage(final String code) {
		try {

			String resource = "/resources/i18n/lang-" + code + ".properties";

			L.debug("Loading lang ressource: " + resource);

			Properties lang = new Properties();
			InputStream in  = null;
			try {
				in = new BufferedInputStream(this.getClass().getResource(resource).openStream());
				lang.load(in);
			} finally {
				try {
					if (in != null) {
						in.close();
					}
				} catch (final IOException e) {
					L.error(e.getMessage(), e);
				}
			}

			this.msgs.clear();
			for (final String property : lang.stringPropertyNames()) {

				String msgIdentifier = property;
				String msg			 = lang.getProperty(property);
				this.msgs.put(msgIdentifier, msg);
			}
		} catch (final IOException e) {
			L.error(e.getMessage(), e);
		}
	}

	public String msg(final String identifier) {

		String msg = this.msgs.get(identifier);
		if (msg == null) {
			L.error("Message '" + identifier + "' not found");
			msg = "Message not found!";
		}

		return msg;
	}

	public String msg(final String identifier, final Object... values) {
		return MessageFormat.format(this.msg(identifier), values);
	}
}