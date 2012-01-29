package org.uilib.templating;

import com.google.common.base.CharMatcher;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

import java.util.List;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.uilib.application.EventContext;
import org.uilib.templating.components.ComponentUI;
import org.uilib.templating.components.LayoutUI;
import org.uilib.util.Naming;

public final class Component {

	//~ Static fields/initializers -------------------------------------------------------------------------------------

	@SuppressWarnings("unused")
	private static final Logger L							 = LoggerFactory.getLogger(Component.class);

	//~ Instance fields ------------------------------------------------------------------------------------------------

	private final CharMatcher nameFilter =
		CharMatcher.inRange('a', 'z').or(CharMatcher.inRange('0', '9')).or(CharMatcher.anyOf("?!-"));

	/* data */
	private final String name;
	private final String type;
	private final ImmutableList<Component> children;
	private final ComponentUI ui;
	private final Options options;

	/* naming */
	private final Naming<Component> naming = Naming.create();

	/* is set after initialization */
	private Control control;

	//~ Constructors ---------------------------------------------------------------------------------------------------

	public Component(final String name, final String type, final List<Component> children, final ComponentUI ui,
					 final Options options) {
		/* check arguments */
		Preconditions.checkNotNull(name);
		Preconditions.checkNotNull(type);
		Preconditions.checkNotNull(children);
		Preconditions.checkNotNull(ui);
		Preconditions.checkNotNull(options);

		Preconditions.checkArgument(
			children.isEmpty() || ui instanceof LayoutUI,
			"ui needs to implement LayoutUI since component has children");

		Preconditions.checkArgument(
			this.nameFilter.matchesAllOf(name),
			"'%s' didn't satisfy name-filter (%s)",
			name,
			nameFilter);

		/* initialize */
		this.name						   = name;
		this.type						   = type;
		this.children					   = ImmutableList.copyOf(children);
		this.ui							   = ui;
		this.options					   = options;

		/* 1. this is addressable as $<type> */
		this.naming.register("$" + type, this);

		/* 2. this is also addressable as <name>$<type> */
		this.naming.register(name + "$" + type, this);

		/* 3. add all namings of children */
		for (final Component child : this.children) {
			this.naming.register(child.getNaming());
		}

		/* 4. add all namings of children reachable via <name>.<children-naming> and <name>$<type> */
		for (final Component child : this.children) {
			this.naming.register(this.name + ".", child.getNaming());
		}

		/* *** build name-map for this component */
		this.naming.seal();
	}

	//~ Methods --------------------------------------------------------------------------------------------------------

	public Naming<Component> getNaming() {
		return this.naming;
	}

	public Control getControl() {
		return this.control;
	}

	public ComponentUI getUI() {
		return this.ui;
	}

	public String getType() {
		return this.type;
	}

	public String getName() {
		return this.name;
	}

	public Options getOptions() {
		return this.options;
	}

	@Override
	public String toString() {

		String s = "(" + this.type + ")";
		if (name != null) {
			s += (" '" + name + "'");
		}

		return s;
	}

	public Component select(final String query) {
		Preconditions.checkState(this.control != null, "control wasn't initialized yet");

		return this.naming.select(query, Component.class);
	}

	@SuppressWarnings("unchecked")
	public <E extends Control> E select(final String query, final Class<E> clazz) {
		return (E) this.select(query).getControl();
	}

	@SuppressWarnings("unchecked")
	public <E extends ComponentUI> E selectUI(final String query, final Class<E> clazz) {
		return (E) this.select(query).getUI();
	}

	public void initialize(final Composite parent) {
		this.initialize(EventContext.FAKE, parent);
	}

	public void initialize(final EventContext app, final Composite parent) {
		Preconditions.checkArgument(this.control == null, "control wasn't null -> double initialization");

		this.control = this.ui.initialize(app, parent, this.name, this.type, this.options);

		Preconditions.checkArgument(this.control != null, "control wasn't initialized, it's still null");

		if (this.children.isEmpty()) {
			return;
		}

		Preconditions.checkState(
			this.control instanceof Composite,
			"initializer returned %s instead of a composite",
			this.control);

		/* initialize children */
		for (final Component child : this.children) {
			child.initialize(app, (Composite) this.control);
		}

		/* layout children */
		for (final Component child : this.children) {
			((LayoutUI) this.ui).layoutChild(child.getControl(), child.getOptions());
		}
	}

	public void setVisible(final String query, final boolean visible) {
		Preconditions.checkArgument(
			this.ui instanceof LayoutUI,
			"can't call show(), underlying ui doesn't implement layout functions");

		Control child = this.select(query, Control.class);

		Preconditions.checkArgument(
			child.getParent() == this.control,
			"you have to call setVisible on layout-component containing the widget you want to change");

		((LayoutUI) this.ui).setVisible(child, visible);
	}

	public void hide(final String query) {
		this.setVisible(query, false);
	}

	public void show(final String query) {
		this.setVisible(query, true);
	}
}