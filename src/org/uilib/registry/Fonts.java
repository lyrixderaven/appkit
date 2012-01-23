package org.uilib.registry;

import com.google.common.base.Joiner;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Fonts {

	//~ Enumerations ---------------------------------------------------------------------------------------------------

	public enum Style {NORMAL, BOLD, HUGEBOLD;
	}

	//~ Static fields/initializers -------------------------------------------------------------------------------------

	private static final Logger L					   = LoggerFactory.getLogger(Fonts.class);
	private static final String defaultFontName		   =
		Display.getDefault().getSystemFont().getFontData()[0].getName();
	private static final int defaultFontStyle		   =
		Display.getDefault().getSystemFont().getFontData()[0].getStyle();
	private static final int defaultFontHeight		   =
		Display.getDefault().getSystemFont().getFontData()[0].getHeight();
	private static final BiMap<Integer, Font> registry = HashBiMap.create();
	private static final Multiset<Font> usage		   = HashMultiset.create();

	//~ Constructors ---------------------------------------------------------------------------------------------------

	private Fonts() {}

	//~ Methods --------------------------------------------------------------------------------------------------------

	public static void set(final Control control, final Style fontStyle) {
		Preconditions.checkState(
			Display.getDefault().getThread() == Thread.currentThread(),
			"Fonts is to be used from the display-thread exclusively!");
		L.debug("setting font for {}", control);

		String name = defaultFontName;
		int height  = defaultFontHeight;
		int style   = defaultFontStyle;

		switch (fontStyle) {
			case HUGEBOLD:
				style = SWT.BOLD;
				height = height + 4;
				break;
			case BOLD:
				style = SWT.BOLD;
				break;
			case NORMAL:
				break;
			default:
				throw new IllegalStateException("unknown font-style " + fontStyle);
		}

		int hash	    = Objects.hashCode(height, style, name);
		final Font font;
		if (registry.containsKey(hash)) {
			font	    = registry.get(hash);
		} else {
			font = new Font(Display.getDefault(), name, height, style);
			L.debug("creating font {}: {}", Joiner.on("-").join(name, height, fontStyle), font);
			registry.put(hash, font);
		}

		/* register usage */
		usage.setCount(font, usage.count(font) + 1);
		L.debug("usage of {} now {}", font, usage.count(font));

		control.setFont(font);
		control.addDisposeListener(
			new DisposeListener() {
					@Override
					public void widgetDisposed(final DisposeEvent event) {
						/* deregister usage */
						usage.setCount(font, usage.count(font) - 1);
						L.debug("usage of {} now {}", font, usage.count(font));

						if (! usage.contains(font)) {
							L.debug("disposing {}", font);

							/* remove first, font appears to change identity after disposal */
							registry.inverse().remove(font);
							font.dispose();
						}
					}
				});
	}
}