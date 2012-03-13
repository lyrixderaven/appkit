package org.appkit.registry;

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

/** <b>SWT Font cache/registry</b>
 * <br />
 * <br />
 * Creates, assigns and caches {@link Font}s. Fonts can be set on a {@link Control}.
 * Use of the color is deregistered when the control is disposed or manually via the <code>putBack</code> methods.
 * <br />
 * <br />
 * This uses a simple counter to keep of track of usage of Fonts. If the usage drops to 0, the font
 * is disposed.
 */
public final class Fonts {

	//~ Enumerations ---------------------------------------------------------------------------------------------------

	public enum Style {NORMAL, BOLD, HUGEBOLD;
	}

	//~ Static fields/initializers -------------------------------------------------------------------------------------

	private static final Logger L = LoggerFactory.getLogger(Fonts.class);

	/* default font options */
	private static final String defaultFontName;
	private static final int defaultFontStyle;
	private static final int defaultFontHeight;

	/* cache / registry */
	private static final BiMap<Integer, Font> fontCache = HashBiMap.create();
	private static final Multiset<Font> usage		    = HashMultiset.create();

	/* currently installed disposeListeners */
	private static final Map<Control, DisposeListener> disposeListeners = Maps.newHashMap();

	static {
		Preconditions.checkArgument(Display.getCurrent() != null, "can't instantiate Fonts on a non-display thread");
		defaultFontName		  = Display.getCurrent().getSystemFont().getFontData()[0].getName();
		defaultFontStyle	  = Display.getCurrent().getSystemFont().getFontData()[0].getStyle();
		defaultFontHeight     = Display.getCurrent().getSystemFont().getFontData()[0].getHeight();
	}

	//~ Constructors ---------------------------------------------------------------------------------------------------

	private Fonts() {}

	//~ Methods --------------------------------------------------------------------------------------------------------

	/**
	 * sets a font, described by a fontStyle on the control
	 *
	 * @throws IllegalStateException if called from a non-Display thread
	 */
	public static void set(final Control control, final Style fontStyle) {
		Preconditions.checkState(
			Display.getCurrent() != null,
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
			font = new Font(Display.getCurrent(), name, height, style);
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

	/**
	 * deregisters use of the font of a control
	 *
	 * @throws IllegalStateException if control isn't registered
	 */
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