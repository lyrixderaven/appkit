package org.uilib.widget.util;

import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class FillTableLayout {

	//~ Static fields/initializers -------------------------------------------------------------------------------------

	private static final Logger L = LoggerFactory.getLogger(FillTableLayout.class);

	//~ Constructors ---------------------------------------------------------------------------------------------------

	protected FillTableLayout(final Table table) {

		/* install layout into parentComposite of Table */
		TableColumnLayout layout = new TableColumnLayout();
		table.getParent().setLayout(layout);

		/* size all the columns */
		int weight = 100 / table.getColumnCount();
		L.debug("setting weight to " + weight + " for " + table.getColumnCount() + " columns");

		for (final TableColumn c : table.getColumns()) {
			layout.setColumnData(c, new ColumnWeightData(weight));
		}
	}
}