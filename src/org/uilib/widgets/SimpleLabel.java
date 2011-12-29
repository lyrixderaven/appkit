package org.uilib.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

// FIXME: use global fonts
public class SimpleLabel extends Composite {

	//~ Enumerations ---------------------------------------------------------------------------------------------------

	public enum LabelStyle {NORMAL, BOLD, BIGBOLD, HUGEBOLD;
	}

	//~ Instance fields ------------------------------------------------------------------------------------------------

	private Label label;
	private Font font;

	//~ Constructors ---------------------------------------------------------------------------------------------------

	public SimpleLabel(final Composite parent, final int swtStyle) {
		this(parent, swtStyle, LabelStyle.NORMAL);
	}

	public SimpleLabel(final Composite parent, final int swtStyle, final LabelStyle style) {
		super(parent, SWT.NONE);

		this.setLayout(new FillLayout());
		this.label = new Label(this, swtStyle);

		/* Defaults */
		String defaultFontName = label.getFont().getFontData()[0].getName();
		int defaultFontHeight  = label.getFont().getFontData()[0].getHeight();
		int defaultFontStyle   = label.getFont().getFontData()[0].getStyle();

		String fontName		   = defaultFontName;
		int fontHeight		   = defaultFontHeight;
		int fontStyle		   = defaultFontStyle;

		switch (style) {
			case BOLD:
				fontHeight = defaultFontHeight;
				fontStyle = SWT.BOLD;
				break;
			case BIGBOLD:
				fontHeight = defaultFontHeight + 2;
				fontStyle = SWT.BOLD;
				break;
			case HUGEBOLD:
				fontHeight = defaultFontHeight + 4;
				fontStyle = SWT.BOLD;
				break;
			case NORMAL:default:
				fontHeight = defaultFontHeight;
				fontStyle = defaultFontStyle;
				break;
		}

		this.font = new Font(this.getDisplay(), fontName, fontHeight, fontStyle);
		this.label.setFont(font);

		this.addDisposeListener(
			new DisposeListener() {
					@Override
					public void widgetDisposed(final DisposeEvent e) {
						font.dispose();
					}
				});
	}

	//~ Methods --------------------------------------------------------------------------------------------------------

	public void setText(final String text) {
		this.label.setText(text);
	}

	public String getText() {
		return this.label.getText();
	}
}