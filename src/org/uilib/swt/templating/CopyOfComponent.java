package org.uilib.swt.templating;

import org.eclipse.swt.widgets.Control;

public class CopyOfComponent<T extends Control> {

	//~ Instance fields ------------------------------------------------------------------------------------------------

	public final String name;
	public final T control;

	//~ Constructors ---------------------------------------------------------------------------------------------------

	public CopyOfComponent(final String name, final T control) {
		this.name		 = name;
		this.control     = control;
	}

	//~ Methods --------------------------------------------------------------------------------------------------------

	public String getName() {
		return name;
	}

	public T getControl() {
		return control;
	}

	public <E extends Control> CopyOfComponent<E> find(final String query, final Class<E> clazz) {
		return null;
	}
}