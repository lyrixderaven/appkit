package org.uilib.swt.states;

@SuppressWarnings("serial")
public final class StringState extends State {

	//~ Instance fields ------------------------------------------------------------------------------------------------

	private final String str;

	//~ Constructors ---------------------------------------------------------------------------------------------------

	public StringState(final String str) {
		this.str = str;
	}

	//~ Methods --------------------------------------------------------------------------------------------------------

	public String get() {
		return str;
	}

	public static final StringState empty() {
		return new StringState("");
	}
}