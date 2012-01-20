package org.uilib.templating.components;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.uilib.application.EventContext;
import org.uilib.templating.Options;

public class ComponentGridUI implements LayoutUI {

	//~ Static fields/initializers -------------------------------------------------------------------------------------

	private static final Logger L = LoggerFactory.getLogger(ComponentGridUI.class);

	//~ Instance fields ------------------------------------------------------------------------------------------------

	private Composite comp		    = null;
	private boolean variableColumns = false;

	//~ Methods --------------------------------------------------------------------------------------------------------

	@Override
	public Composite initialize(final EventContext app, final Composite parent, final String name, final String type,
								final Options options) {

		int style = SWT.NONE;
		style |= (options.get("border", false) ? SWT.BORDER : SWT.NONE);

		/* create composite */
		GridLayout gl = new GridLayout(-1, false);
		gl.marginHeight			 = options.get("margin-height", gl.marginHeight);
		gl.marginWidth			 = options.get("margin-width", gl.marginWidth);
		gl.verticalSpacing		 = options.get("v-spacing", gl.verticalSpacing);
		gl.horizontalSpacing     = options.get("h-spacing", gl.horizontalSpacing);

		/* layout columns */
		String columns = options.get("columns", "1");
		if (columns.equals("variable")) {
			this.variableColumns     = true;
			gl.numColumns			 = 0;
		} else {
			gl.numColumns = Integer.valueOf(columns);
		}

		this.comp = new Composite(parent, style);
		this.comp.setLayout(gl);

		return this.comp;
	}

	@Override
	public void layoutChild(final Control child, final Options options) {

		/* create GridData for positioning */
		GridData gd = this.genGridData(options);
		child.setLayoutData(gd);

		L.debug(child.toString() + ", " + gd);

		if (this.variableColumns) {

			int columns = ((GridLayout) this.comp.getLayout()).numColumns;
			((GridLayout) this.comp.getLayout()).numColumns = columns + 1;
		}
	}

	@Override
	public void setVisible(final Control child, final boolean visible) {
		child.setVisible(visible);
		((GridData) child.getLayoutData()).exclude = ! visible;
		this.comp.layout();
	}

	private GridData genGridData(final Options options) {

		GridData gd = new GridData();

		gd.grabExcessHorizontalSpace     = options.get("grow", "").contains("-");
		gd.horizontalIndent				 = options.get("h-indent", 0);
		gd.horizontalSpan				 = options.get("h-span", 1);

		// FIXME: RFC: Preconditions which options are allowed? MigLayout instead?
		gd.widthHint	  = options.get("width", SWT.DEFAULT);
		gd.heightHint     = options.get("height", SWT.DEFAULT);

		String hAlign     = options.get("h-align", "");
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

		gd.grabExcessVerticalSpace     = options.get("grow", "").contains("|");
		gd.verticalIndent			   = options.get("v-indent", 0);
		gd.verticalSpan				   = options.get("v-span", 1);

		String vAlign				   = options.get("v-align", "");
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