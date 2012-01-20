package org.uilib.templating;

import com.google.common.base.CharMatcher;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMultimap;

import java.util.List;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.uilib.application.EventContext;
import org.uilib.templating.components.ComponentUI;
import org.uilib.templating.components.LayoutUI;

// FIXME: Component: use Naming?
public final class Component {

	//~ Static fields/initializers -------------------------------------------------------------------------------------

	@SuppressWarnings("unused")
	private static final Logger L							 = LoggerFactory.getLogger(Component.class);

	//~ Instance fields ------------------------------------------------------------------------------------------------

	private final String name;
	private final String type;
	private final ImmutableList<Component> children;
	private final ImmutableMultimap<String, Component> nameMap;
	private final ComponentUI ui;
	private final Options options;
	private Control control;

	//~ Constructors ---------------------------------------------------------------------------------------------------

	public Component(final String name, final String type, final List<Component> children,
					 final ComponentUI controller, final Options options) {
		this.name											 = name;

		/* check arguments */
		if (this.name != null) {

			CharMatcher nameFilter = CharMatcher.inRange('a', 'z').or(CharMatcher.anyOf("?-"));
			Preconditions.checkArgument(nameFilter.matchesAllOf(name), "only a-z, '-' and '?' allowed in name");
		}
		Preconditions.checkNotNull(type);
		Preconditions.checkNotNull(children);
		Preconditions.checkNotNull(controller);
		Preconditions.checkNotNull(options);
		Preconditions.checkArgument(
			children.isEmpty() || controller instanceof LayoutUI,
			"need a LayoutUI since component has children");

		/* initialize */
		this.type		  = type;
		this.children     = ImmutableList.copyOf(children);
		this.ui			  = controller;
		this.options	  = options;

		/* *** build name-map for this component */
		ImmutableMultimap.Builder<String, Component> map = ImmutableMultimap.builder();

		/* 1. we are definitely addressable as $<type> */
		map.put("$" + type, this);

		/* 2. if we have a name we are also addressable as <name> and as <name>$<type> */
		if (this.name != null) {
			map.put(name, this);
			map.put(name + "$" + type, this);
		}

		/* 3. put all name-mappings of children widgets into map */
		for (final Component child : this.children) {
			map.putAll(child.getNameMap());
		}

		/* 4. if we have a name, all naming in children are addressable as <this.name>.<naming> */
		if (this.name != null) {
			for (final Component child : this.children) {
				for (final String key : child.getNameMap().keySet()) {
					if (key.startsWith("$")) {
						map.putAll(this.name + key, child.getNameMap().get(key));
					} else {
						map.putAll(this.name + "." + key, child.getNameMap().get(key));
					}
				}
			}
		}
		/* *** build name-map for this component */
		this.nameMap = map.build();
	}

	//~ Methods --------------------------------------------------------------------------------------------------------

	public ImmutableMultimap<String, Component> getNameMap() {
		return this.nameMap;
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
			s += (" '" + name + "' ");
		}

		return s;
	}

	@SuppressWarnings("unchecked")
	public <E extends Control> E select(final String query, final Class<E> clazz) {
		Preconditions.checkState(this.control != null, "control wasn't initialized yet");

		ImmutableCollection<Component> components = this.nameMap.get(query);

		Preconditions.checkState(
			components.size() == 1,
			"found " + components.size() + " controls for '" + query + "'");

		return (E) components.iterator().next().getControl();
	}

	public Component select(final String query) {
		// FIXME: RFC: does it make sense to select before initialization?
		Preconditions.checkState(this.control != null, "control wasn't initialized yet");

		ImmutableCollection<Component> components = this.nameMap.get(query);

		Preconditions.checkState(
			components.size() == 1,
			"found " + components.size() + " controls for '" + query + "'");

		return components.iterator().next();
	}

	@SuppressWarnings("unchecked")
	public <E extends ComponentUI> E selectUI(final String query, final Class<E> clazz) {
		Preconditions.checkState(this.control != null, "control wasn't initialized yet");

		ImmutableCollection<Component> components = this.nameMap.get(query);

		Preconditions.checkState(
			components.size() == 1,
			"found " + components.size() + " controls for '" + query + "'");

		return (E) components.iterator().next().getUI();
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
			"you have to call setVisible on the component of the layout containing the widget you want to change");

		((LayoutUI) this.ui).setVisible(child, visible);
	}

	public void hide(final String query) {
		this.setVisible(query, false);
	}

	public void show(final String query) {
		this.setVisible(query, true);
	}
}