package org.uilib.templating.components;

import java.util.List;

import org.apache.log4j.Logger;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import org.uilib.AppContext;
import org.uilib.templating.Component;
import org.uilib.templating.Options;

public class ComponentGridUI implements ComponentUI {

	//~ Static fields/initializers -------------------------------------------------------------------------------------

	private static final Logger L = Logger.getLogger(ComponentGridUI.class);

	//~ Methods --------------------------------------------------------------------------------------------------------

	@Override
	public Composite initialize(final AppContext app, final Composite parent, final List<Component> children,
								final Options options) {

		int style = SWT.NONE;
		style |= (options.get("border", false) ? SWT.BORDER : SWT.NONE);

		/* create composite */
		GridLayout gl = new GridLayout(-1, false);
		gl.marginHeight			 = options.get("margin-height", gl.marginHeight);
		gl.marginWidth			 = options.get("margin-width", gl.marginWidth);
		gl.verticalSpacing		 = options.get("v-spacing", gl.verticalSpacing);
		gl.horizontalSpacing     = options.get("h-spacing", gl.horizontalSpacing);

		Composite comp			 = new Composite(parent, style);
		comp.setLayout(gl);

		/* create children */
		for (final Component child : children) {
			child.initialize(comp);

			/* create GridData for positioning */
			GridData gd = this.genGridData(child);
			child.getControl().setLayoutData(gd);

			L.debug(child.toString() + ", " + gd);
		}

		/* layout columns */
		String columns = options.get("columns", "1");
		if (columns.equals("variable")) {
			((GridLayout) comp.getLayout()).numColumns = children.size();
		} else {
			((GridLayout) comp.getLayout()).numColumns = Integer.valueOf(columns);
		}

		return comp;
	}

	private GridData genGridData(final Component child) {

		Options cOptions			 = child.getOptions();

		GridData gd					 = new GridData();

		gd.grabExcessHorizontalSpace = cOptions.get("grow", "").contains("-");
		gd.horizontalIndent			 = cOptions.get("h-indent", 0);
		gd.horizontalSpan			 = cOptions.get("h-span", 1);

		String hAlign				 = cOptions.get("h-align", "");
		if (hAlign.contains("center")) {
			gd.horizontalAlignment = SWT.CENTER;
		} else if (hAlign.contains("left")) {
			gd.horizontalAlignment = SWT.LEFT;
		} else if (hAlign.contains("right")) {
			gd.horizontalAlignment = SWT.RIGHT;
		} else if (hAlign.contains("fill")) {
			gd.horizontalAlignment = SWT.FILL;
		} else {
			gd.horizontalAlignment = SWT.NONE;
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
			gd.verticalAlignment = SWT.NONE;
		}

		return gd;
	}
}