 package org.uilib.swt.templating;

import com.google.common.base.CharMatcher;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMultimap;

import java.util.List;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import org.uilib.swt.components.UIController;
import org.uilib.util.Texts;

public final class Component {

	//~ Static fields/initializers -------------------------------------------------------------------------------------

	private static final Logger L							 = Logger.getLogger(Component.class);

	//~ Instance fields ------------------------------------------------------------------------------------------------

	private final String name;
	private final String type;
	private final ImmutableList<Component> children;
	private final ImmutableMultimap<String, Component> nameMap;
	private final UIController<?> controller;
	private final Options options;
	private Control control;

	//~ Constructors ---------------------------------------------------------------------------------------------------

	// TODO: Component: builder syntax?
	// FIXME: warning if options ends with "
	public Component(final String name, final String type, final List<Component> children,
					 final UIController<?> controller, final Options options) {
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

		/* initialize */
		this.type		    = type;
		this.children	    = ImmutableList.copyOf(children);
		this.controller     = controller;
		this.options	    = options;

		/* build name-map for this component */
		ImmutableMultimap.Builder<String, Component> map = ImmutableMultimap.builder();

		/* 1. we are definitely addressable as $<type> */
		map.put("$" + type, this);
		if (this.name != null) {

			/* 2. if we have a name we are also addressable as <name> and as <name>$<type> */
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
					if (key.startsWith("$"))
						map.putAll(this.name + key, child.getNameMap().get(key));
					else {
						map.putAll(this.name + "." + key, child.getNameMap().get(key));
					}
				}
			}
		}

		this.nameMap = map.build();
	}

	//~ Methods --------------------------------------------------------------------------------------------------------

	public void i18nTranslate(final Texts texts) {
		for (final Entry<String, String> text : texts.getMap().entrySet()) {

			/* search for the component and set the translation */
			UIController<?> controller = this.controller(text.getKey(), UIController.class);
			controller.setI18nText(text.getValue());
		}
	}

	public ImmutableMultimap<String, Component> getNameMap() {
		return this.nameMap;
	}

	public Control getControl() {
		return this.control;
	}

	public UIController<?> getController() {
		return this.controller;
	}

	public String getType() {
		return this.type;
	}

	public String getName() {
		return this.name;
	}

	public ImmutableList<Component> getChildren() {
		return this.children;
	}

	public Options getOptions() {
		return this.options;
	}

	public void setVisible(final boolean visible) {
		this.control.setVisible(visible);
		((GridData) this.control.getLayoutData()).exclude = ! visible;

		this.control.getParent().layout();
	}

	public Component component(final String query) {
		ImmutableCollection<Component> components = this.nameMap.get(query);

		Preconditions.checkState(
			components.size() == 1,
			"found " + components.size() + " controls for '" + query + "'");

		return components.iterator().next();
	}

	@SuppressWarnings("unchecked")
	public <E extends Control> E control(final String query, final Class<E> clazz) {

		ImmutableCollection<Component> components = this.nameMap.get(query);

		Preconditions.checkState(
			components.size() == 1,
			"found " + components.size() + " controls for '" + query + "'");

		return (E) components.iterator().next().getControl();
	}

	@SuppressWarnings("unchecked")
	public <E extends UIController<?>> E controller(final String query, final Class<E> clazz) {

		ImmutableCollection<Component> components = this.nameMap.get(query);

		Preconditions.checkState(
			components.size() == 1,
			"found " + components.size() + " controls for '" + query + "'");

		return (E) components.iterator().next().getController();
	}

	public void initialize(final Composite parent) {
		Preconditions.checkArgument(this.control == null, "control wasn't null -> double initialization");

		this.control = this.controller.initialize(parent, this.options);

		Preconditions.checkState(
			this.children.isEmpty() || (this.control instanceof Composite),
			"compnent has children, but ui-controller didn't return a composite");
		if (this.children.isEmpty()) {
			return;
		}

		/* create children */
		Composite comp = (Composite) this.control;
		for (final Component child : this.children) {
			child.initialize(comp);

			/* create GridData for positioning */
			GridData gd = this.genGridData(child);
			child.getControl().setLayoutData(gd);

			L.debug(child.toString() + ", " + gd);
		}

		/* layout columns */
		String columns = this.options.get("columns", "1");
		if (columns.equals("variable")) {
			((GridLayout) comp.getLayout()).numColumns = this.children.size();
		} else {
			((GridLayout) comp.getLayout()).numColumns = Integer.valueOf(columns);
		}
	}

	private GridData genGridData(Component child) {

		Options cOptions = child.getOptions();

		GridData gd = new GridData();

		gd.grabExcessHorizontalSpace     = cOptions.get("grow", "").contains("-");
		gd.horizontalIndent				 = cOptions.get("h-indent", 0);
		gd.horizontalSpan				 = cOptions.get("h-span", 1);

		String hAlign					 = cOptions.get("h-align", "");
		if (hAlign.contains("center")) {
			gd.horizontalAlignment = SWT.CENTER;
		} else if (hAlign.contains("left")) {
			gd.horizontalAlignment = SWT.LEFT;
		} else if (hAlign.contains("right")) {
			gd.horizontalAlignment = SWT.RIGHT;
		} else if (hAlign.contains("fill")) {
			gd.horizontalAlignment = SWT.FILL;
		} else {
			gd.horizontalAlignment = (child.getController().fillHorizByDefault() ? SWT.FILL : SWT.NONE);
		}

		gd.grabExcessVerticalSpace     = cOptions.get("grow", "").contains("|");
		gd.verticalIndent			   = cOptions.get("v-indent", 0);
		gd.verticalSpan				   = cOptions.get("v-span", 1);

		String vAlign				   = cOptions.get("v-align", "");
		if (vAlign.contains("center")) {
			gd.verticalAlignment = SWT.CENTER;
		} else if (vAlign.contains("top")) {
			gd.verticalAlignment = SWT.TOP;
		} else if (vAlign.contains("bottom")) {
			gd.verticalAlignment = SWT.BOTTOM;
		} else if (vAlign.contains("fill")) {
			gd.verticalAlignment = SWT.FILL;
		} else {
			gd.verticalAlignment = (child.getController().fillVertByDefault() ? SWT.FILL : SWT.NONE);
		}

		return gd;
	}

	@Override
	public String toString() {
		String s = "(" + this.type + ")";
		if (name != null)
			s += " '" + name + "' ";

		return s;
	}
}