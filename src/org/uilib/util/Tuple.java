package org.uilib.util;

public final class Tuple<A, B> {

	//~ Instance fields ------------------------------------------------------------------------------------------------

	private final A first;
	private final B second;

	//~ Constructors ---------------------------------------------------------------------------------------------------

	public Tuple(final A first, final B second) {
		this.first	    = first;
		this.second     = second;
	}

	//~ Methods --------------------------------------------------------------------------------------------------------

	public A getFirst() {
		return this.first;
	}

	public B getSecond() {
		return this.second;
	}

	// FIXME: equals
}