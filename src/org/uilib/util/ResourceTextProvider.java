package org.uilib.util;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

import java.net.URL;

import java.util.Scanner;

import org.apache.log4j.Logger;

public class ResourceTextProvider implements TextProvider {

	//~ Static fields/initializers -------------------------------------------------------------------------------------

	private static final Logger L = Logger.getLogger(ResourceTextProvider.class);

	//~ Methods --------------------------------------------------------------------------------------------------------

	public ResourceTextProvider(final String langPrefix) {
		L.debug("loading template: " + templateName);

		InputStream in = null;
		try {

			URL url = this.getClass().getResource("/resources/components/" + templateName + ".json");
			if (url == null) {
				return null;
			}
			in = new BufferedInputStream(url.openStream());
			return new Scanner(in, "UTF8").useDelimiter("\\A").next();

		} catch (final IOException e) {}
		finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (final IOException e) {}
		}

		return null;
	}

	@Override
	public String get(final String identifier) {
		L.debug("loading template: " + templateName);

		InputStream in = null;
		try {

			URL url = this.getClass().getResource("/resources/components/" + templateName + ".json");
			if (url == null) {
				return null;
			}
			in = new BufferedInputStream(url.openStream());
			return new Scanner(in, "UTF8").useDelimiter("\\A").next();

		} catch (final IOException e) {}
		finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (final IOException e) {}
		}

		return null;
	}
}