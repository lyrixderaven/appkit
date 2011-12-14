package org.uilib.swt.templating;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

import java.net.URL;

import java.util.Scanner;

import org.apache.log4j.Logger;

// TODO: simples Caching / allgemeiner StringLoader?
public class ResourceTemplateLoader implements TemplateLoader {

	//~ Static fields/initializers -------------------------------------------------------------------------------------

	private static final Logger L = Logger.getLogger(ResourceTemplateLoader.class);

	//~ Methods --------------------------------------------------------------------------------------------------------

	@Override
	public String getTemplate(final String templateName) {
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