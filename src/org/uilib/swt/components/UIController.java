package org.uilib.swt.components;

import java.io.Serializable;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import org.uilib.swt.templating.Options;

// FIXME: Controller: umbenennen und trennen? UI-Creator?
public interface UIController<T extends Serializable> {

	//~ Methods --------------------------------------------------------------------------------------------------------

	/* FIXME: Controller: welches control zurückgeben? */
	public Control initialize(final Composite parent, final Options options);

	public void setI18nText(final String text);

	public T getState();

	public boolean fillVertByDefault();

	public boolean fillHorizByDefault();
}