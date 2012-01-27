package org.uilib.registry;

import com.google.common.base.Joiner;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Maps;
import com.google.common.collect.Multiset;

import java.util.Map;

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

	private static final Logger L = LoggerFactory.getLogger(Fonts.class);

	/* default font options */
	private static final String defaultFontName = Display.getDefault().getSystemFont().getFontData()[0].getName();
	private static final int defaultFontStyle   = Display.getDefault().getSystemFont().getFontData()[0].getStyle();
	private static final int defaultFontHeight  = Display.getDefault().getSystemFont().getFontData()[0].getHeight();

	/* cache / registry */
	private static final BiMap<Integer, Font> fontCache				    = HashBiMap.create();
	private static final Multiset<Font> usage						    = HashMultiset.create();
	private static final Map<Control, DisposeListener> disposeListeners = Maps.newHashMap();

	//~ Constructors ---------------------------------------------------------------------------------------------------

	private Fonts() {}

	//~ Methods --------------------------------------------------------------------------------------------------------

	public static void set(final Control control, final Style fontStyle) {
		Preconditions.checkState(
			Display.getDefault().getThread() == Thread.currentThread(),
			"Fonts is to be used from the display-thread exclusively!");

		/* if we already set a font on this control, remove it */
		if (disposeListeners.containsKey(control)) {
			putBack(control);
		}

		/* load / create font */
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

		L.debug("setting font {} on {}", Joiner.on("-").join(name, height, fontStyle), control);

		/* set font */
		int hash	    = Objects.hashCode(height, style, name);
		final Font font;
		if (fontCache.containsKey(hash)) {
			font	    = fontCache.get(hash);
		} else {
			font = new Font(Display.getDefault(), name, height, style);
			L.debug("created font: {}", font);
			fontCache.put(hash, font);
		}

		/* increase usage-counter */
		usage.setCount(font, usage.count(font) + 1);
		L.debug("usage of {} now {}", font, usage.count(font));

		/* set font and add the disposer */
		control.setFont(font);

		DisposeListener listener = new FontDisposeListener();
		disposeListeners.put(control, listener);
		control.addDisposeListener(listener);
	}

	public static void putBack(final Control control) {
		Preconditions.checkState(disposeListeners.containsKey(control), "control {} not registered", control);

		/* remove control out of registry and remove listener */
		control.removeDisposeListener(disposeListeners.remove(control));

		/* get the font */
		Font font = control.getFont();

		/* decrease usage-counter */
		usage.setCount(font, usage.count(font) - 1);
		L.debug("usage of {} now {}", font, usage.count(font));

		/* if usage is 0 dispose it */
		if (! usage.contains(font)) {
			L.debug("disposing {}", font);
			fontCache.inverse().remove(font);
			font.dispose();
		}
	}

	//~ Inner Classes --------------------------------------------------------------------------------------------------

	private static final class FontDisposeListener implements DisposeListener {
		@Override
		public void widgetDisposed(final DisposeEvent event) {
			putBack((Control) event.widget);
		}
	}
}