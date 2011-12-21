package org.uilib.swt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Display;

// TODO: caching von Fonts
public final class Fonts {

	public enum Style {NORMAL, BOLD}

	private final Font defaultFont = Display.getDefault().getSystemFont();

	public Font create(Style style, int sizeDiff) {
		/* Defaults */
		String defaultFontName = this.defaultFont.getFontData()[0].getName();
		int defaultFontHeight  = this.defaultFont.getFontData()[0].getHeight();
		int defaultFontStyle   = this.defaultFont.getFontData()[0].getStyle();

		String fontName		   = defaultFontName;
		int fontHeight		   = defaultFontHeight + sizeDiff;
		int fontStyle;

		switch (style) {
			case BOLD:
				fontStyle = SWT.BOLD;
				break;
			case NORMAL:
				fontStyle = defaultFontStyle;
				break;
			default:
				throw new IllegalStateException("unknown font-style " + style);
		}

		return new Font(Display.getDefault(), fontName, fontHeight, fontStyle);
	}
}
