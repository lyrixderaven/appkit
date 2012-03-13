package org.appkit.templating;

import com.google.common.base.CharMatcher;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

import java.util.List;
import java.util.Map;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.appkit.application.EventContext;
import org.appkit.application.FakeEventContext;
import org.appkit.templating.components.ComponentUI;
import org.appkit.templating.components.LayoutUI;
import org.appkit.util.Naming;

/**
 * A Templating-component. Has a name, a type, 0...n child-component, associated {@link Options} and a {@link ComponentUI}
 * that does the actually rendering/displaying job.
 *
 * <ul>
 * <li>For the name only the following characters are valid: a-z,A-Z, '?', '!' and '-'.
 * </ul>
 *
 * Components are selectable via a query-syntax, which works like this: <code>name$type</code>.<br />
 * <br />
 * Examples:
 * <ul>
 * <li> <code>$buttons</code> will return all buttons
 * <li> <code>book-in$button</code> will return all buttons called bookin
 * <li> <code>action.book-in$button</code> will return all buttons called bookin in an composite called action
 * <li> <code>sidebar$label</code> will return all labels in a composite called sidebar
 * </ul>
 * <br />
 * <br />
 * Trying to construct invalid component will throw an IllegalArgumentException.
 */
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

	/**
	 * Creates a component. If children aren't empty the ComponentUI has to be a {@link LayoutUI}.
	 *
	 */
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

		/* 1. this is addressable via name */
		this.naming.register(name, this);

		/* 2. this is addressable as $<type> */
		this.naming.register("$" + type, this);

		/* 3. this is also addressable as <name>$<type> */
		this.naming.register(name + "$" + type, this);

		/* children */
		for (final Component child : this.children) {
			for (final Map.Entry<String, Component> entry : child.getNaming().asMap().entries()) {
				/* 4. copy all namings of children */
				this.naming.register(entry.getKey(), entry.getValue());

				/* 5. add all namings of children reachable via <this-name>.<children-naming> and <this-name>$<type> */
				if (entry.getKey().startsWith("$")) {
					this.naming.register(this.name + entry.getKey(), entry.getValue());
				} else {
					this.naming.register(this.name + "." + entry.getKey(), entry.getValue());
				}
			}
		}

		/* *** build name-map for this component */
		this.naming.seal();
	}

	//~ Methods --------------------------------------------------------------------------------------------------------

	public Naming<Component> getNaming() {
		return this.naming;
	}

	/** returns the Control that was returns by the UI */
	public Control getControl() {
		return this.control;
	}

	/** returns the UI */
	public ComponentUI getUI() {
		return this.ui;
	}

	/** returns the type */
	public String getType() {
		return this.type;
	}

	/** returns the name */
	public String getName() {
		return this.name;
	}

	/** returns the options */
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

	/** select the sub-component (which can be this component itself) by the given query.
	 *
	 * @throws IllegalStateException if Component wasn't initialized yest
	 *
	 */
	public Component select(final String query) {
		Preconditions.checkState(this.control != null, "control wasn't initialized yet");

		return this.naming.select(query, Component.class);
	}

	/**
	 * returns the control of the sub-component selected by the given query cast to the specified class
	 *
	 * @throws IllegalStateException if Component wasn't initialized yet
	 *
	 */
	@SuppressWarnings("unchecked")
	public <E extends Control> E select(final String query, final Class<E> clazz) {
		return (E) this.select(query).getControl();
	}

	/**
	 * returns the UI of the sub-component selected by the given query cast to the specified class
	 *
	 * @throws IllegalStateException if Component wasn't initialized yet
	 *
	 */
	@SuppressWarnings("unchecked")
	public <E extends ComponentUI> E selectUI(final String query, final Class<E> clazz) {
		return (E) this.select(query).getUI();
	}

	/**
	 * Initializes the Component / creates the widgets on the given parent using a {@link FakeEventContext}.
	 *
	 * @throws IllegalStateException if Component wasn't initialized yet
	 *
	 */
	public void initialize(final Composite parent) {
		this.initialize(EventContext.FAKE, parent);
	}

	/**
	 * Initializes the Component / creates the widgets on the given parent using the given EventContext.
	 *
	 * @throws IllegalStateException if Component wasn't initialized yet
	 *
	 */
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

	/**
	 * Sets the visibility of the component selected by the query, by passing it's control
	 * to the {@link LayoutUI} of this component.
	 *
	 * @throws IllegalStateException if this' UI doesn't implement LayoutUI.
	 * @throws IllegalArgumentException if this' control isn't the parent of the control you want to modify
	 *
	 */
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

	/**
	 * @see #setVisible(String, boolean)
	 */
	public void hide(final String query) {
		this.setVisible(query, false);
	}

	/**
	 * @see #setVisible(String, boolean)
	 */
	public void show(final String query) {
		this.setVisible(query, true);
	}
}