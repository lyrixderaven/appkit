package org.uilib.util;

import java.text.MessageFormat;

import org.apache.log4j.Logger;

// TODO ?: DefaultTexts für SearchForm etc.?
// TODO ?: Texts: "context", damit man nicht immer alles ausschreiben muss?
public class Texts {

	@SuppressWarnings("unused")
	private static final Logger L	    = Logger.getLogger(Texts.class);
	private final TextProvider provider;
	
	//~ Instance fields ------------------------------------------------------------------------------------------------

	public static Texts create(final String langId) {
		return new Texts(new ResourceTextProvider(langId));
	}
	
	public Texts(TextProvider provider) {
		this.provider = provider;
	}

	public String get(final String identifier, final Object... values) {
		return MessageFormat.format(this.provider.get(identifier), values);
	}
}