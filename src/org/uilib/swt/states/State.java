package org.uilib.swt.states;

import java.io.Serializable;

@SuppressWarnings("serial")
public class State implements Serializable {

	//~ Methods --------------------------------------------------------------------------------------------------------

	public static final State emptyState() {
		return new State();
	}
}