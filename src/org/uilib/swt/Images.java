package org.uilib.swt;

import com.google.common.collect.Maps;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

import java.util.Map;

import org.apache.log4j.Logger;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

public final class Images {

	//~ Enumerations ---------------------------------------------------------------------------------------------------

	public enum Type {LOGO, ICON, NEW, OPEN, COPY, DELETE, SAVE, CLOSE, RUN, RESET, ACTION;
	}

	//~ Static fields/initializers -------------------------------------------------------------------------------------

	private static final Logger L		 = Logger.getLogger(Images.class);

	//~ Instance fields ------------------------------------------------------------------------------------------------

	private final Map<String, Image> images = Maps.newHashMap();

	//~ Methods --------------------------------------------------------------------------------------------------------

	public String translate(final Type imgType) {
		switch (imgType) {
			case LOGO:
				return "p4m-logo.png";
			case ICON:
				return "p4m-icon.png";
			case NEW:
				return "New.png";
			case OPEN:
				return "Open.png";
			case COPY:
				return "Copy.png";
			case DELETE:
				return "Delete.png";
			case SAVE:
				return "Save.png";
			case CLOSE:
				return "Close.png";
			case RUN:
				return "Run.png";
			case RESET:
				return "Reset.png";
			case ACTION:
				return "AppleNSActionTemplate.jpg";
			default:
				throw new IllegalStateException();
		}
	}

	public Image load(final Type imgType) {

		String name = this.translate(imgType);
		Image img   = this.images.get(name);
		if (img != null) {
			return img;
		}

		InputStream in = null;
		try {
			in	    = new BufferedInputStream(this.getClass().getResource("/resources/ico/" + name).openStream());
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
	}
}