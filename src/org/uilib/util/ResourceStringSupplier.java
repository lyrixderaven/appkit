package org.uilib.util;

import com.google.common.collect.Maps;

import java.io.IOException;
import java.io.InputStream;

import java.util.Map;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * a ParamSupplier which returns Strings loaded from resources
 *
 */
public class ResourceStringSupplier implements ParamSupplier<String, String> {

	//~ Static fields/initializers -------------------------------------------------------------------------------------

	@SuppressWarnings("unused")
	private static final Logger L							 = LoggerFactory.getLogger(ResourceStringSupplier.class);
	private static final ResourceStringSupplier INSTANCE     = new ResourceStringSupplier();

	//~ Instance fields ------------------------------------------------------------------------------------------------

	private final Map<String, String> cache = Maps.newHashMap();

	//~ Constructors ---------------------------------------------------------------------------------------------------

	private ResourceStringSupplier() {}

	//~ Methods --------------------------------------------------------------------------------------------------------

	public static ResourceStringSupplier instance() {
		return INSTANCE;
	}

	@Override
	public String get(final String resource) {

		String fullName = "/resources/" + resource;

		if (this.cache.containsKey(fullName)) {
			return this.cache.get(fullName);
		}

		/* read string from stream */
		InputStream in = ResourceStreamSupplier.create().get(resource);
		String s	   = new Scanner(in, "UTF8").useDelimiter("\\A").next();
		try {
			in.close();
		} catch (final IOException e) {
			throw new RuntimeException(e.getMessage(), e);
		}

		/* save in cache */
		this.cache.put(fullName, s);

		return s;
	}
}