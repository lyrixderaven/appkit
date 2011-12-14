package org.uilib.util;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.google.common.collect.Maps;

public class ResourceTextProvider implements TextProvider {

	//~ Static fields/initializers -------------------------------------------------------------------------------------

	private static final Logger L = Logger.getLogger(ResourceTextProvider.class);
	private final Map<String, String> msgs = Maps.newHashMap();
	
	//~ Methods --------------------------------------------------------------------------------------------------------

	public ResourceTextProvider(final String langId) {
		try {

			String resource = "/resources/i18n/lang-" + langId + ".properties";

			L.debug("loading language out of ressource: " + resource);

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

	@Override
	public String get(final String identifier) {
		return this.msgs.get(identifier);
	}
}