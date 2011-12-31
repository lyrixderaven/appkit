package org.uilib.util;

import com.google.common.collect.Maps;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

public final class Images {

	//~ Static fields/initializers -------------------------------------------------------------------------------------

	private static final Logger L = LoggerFactory.getLogger(Images.class);

	//~ Instance fields ------------------------------------------------------------------------------------------------

	private final Map<String, Image> images = Maps.newHashMap();

	//~ Methods --------------------------------------------------------------------------------------------------------

	public Image load(final Translatable imgType) {

		String name = "/resources/images/" + imgType.translate();
		Image img   = this.images.get(name);
		if (img != null) {
			return img;
		}

		InputStream in = null;
		try {
			in	    = new BufferedInputStream(this.getClass().getResource(name).openStream());
			img     = new Image(Display.getDefault(), in);
			this.images.put(name, img);
		} catch (final IOException e) {
			L.error(e.getMessage(), e);
		} finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (final IOException e) {
				L.error(e.getMessage(), e);
			}
		}

		return img;
	}

	public void dispose() {
		for (final Image image : this.images.values()) {
			image.dispose();
		}
		this.images.clear();
	}
}