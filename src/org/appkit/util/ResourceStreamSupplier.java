package org.appkit.util;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * a ParamSupplier which returns an InputStream by loading from resources
 *
 */
public class ResourceStreamSupplier implements ParamSupplier<String, InputStream> {

	//~ Static fields/initializers -------------------------------------------------------------------------------------

	@SuppressWarnings("unused")
	private static final Logger L = LoggerFactory.getLogger(ResourceStreamSupplier.class);

	//~ Methods --------------------------------------------------------------------------------------------------------

	public static ResourceStreamSupplier create() {
		return new ResourceStreamSupplier();
	}

	/** returns an InputStream for the resource */
	@Override
	public InputStream get(final String resource) {

		String fullName = "/resources/" + resource;

		URL url		    = ResourceStreamSupplier.class.getResource(fullName);
		if (url == null) {
			return null;
		}

		try {
			return new BufferedInputStream(url.openStream());
		} catch (final IOException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}
}