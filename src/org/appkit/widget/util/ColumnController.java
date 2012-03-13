package org.appkit.widget.util;

import com.google.common.collect.Lists;
import com.google.common.primitives.Ints;

import java.util.List;

import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.widgets.Scrollable;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

abstract class ColumnController {

	//~ Static fields/initializers -------------------------------------------------------------------------------------

	private static final Logger L = LoggerFactory.getLogger(ColumnController.class);
	private static final int BASE = 10000;

	//~ Methods --------------------------------------------------------------------------------------------------------

	abstract int getColumnCount();

	abstract void setWidth(final int column, final int width);

	abstract int getWidth(final int column);

	abstract int getAvailWidth();

	abstract Scrollable getControl();

	abstract void installColumnControlListener(final int column, final ControlListener listener);

	abstract void setColumnOrder(final int order[]);

	abstract int[] getColumnOrder();

	abstract void setColumnsMoveable();

	public final List<Integer> calculateWeights() {
		L.debug("calculating weights");

		List<Integer> weights = Lists.newArrayList();
		int controlWidth	  = this.getAvailWidth();
		for (int i = 0; i < this.getColumnCount(); i++) {

			int columnWidth = this.getWidth(i);
			int weight	    = Ints.saturatedCast(Math.round((double) columnWidth / controlWidth * BASE));

			L.debug("calculated weight {} for column {}", weight, i);
			weights.add(weight);
		}

		return weights;
	}

	public final void setWeights(final List<Integer> weights) {

		int controlWidth = this.getAvailWidth();
		double fraction  = controlWidth / (double) BASE;

		L.debug("sizing columns - control width {}, fraction {}", controlWidth, fraction);

		int sum = 0;
		for (int i = 0; i < this.getColumnCount(); i++) {

			int weight = weights.get(i);
			int width  = Ints.saturatedCast(Math.round(weight * fraction));

			sum		   = sum + width;

			/* correct rounding "errors" */
			if (i == (this.getColumnCount() - 1)) {

				int diff = sum - controlWidth;
				if ((diff > 0) && (diff <= 20)) {
					width = width - diff;
				}
			}

			L.debug("sizing column " + i + " to width {} (weight {})", width, weight);
			this.setWidth(i, width);
		}
	}

	//~ Inner Classes --------------------------------------------------------------------------------------------------

	public static final class TableColumnController extends ColumnController {

		private final Table table;

		public TableColumnController(final Table table) {
			this.table = table;
		}

		@Override
		Scrollable getControl() {
			return this.table;
		}

		@Override
		public void installColumnControlListener(final int column, final ControlListener listener) {
			this.table.getColumn(column).addControlListener(listener);
		}

		@Override
		public int getColumnCount() {
			return this.table.getColumnCount();
		}

		@Override
		public void setWidth(final int column, final int width) {
			this.table.getColumn(column).setWidth(width);
		}

		@Override
		public int getWidth(final int column) {
			return this.table.getColumn(column).getWidth();
		}

		@Override
		public int getAvailWidth() {
			return this.table.getClientArea().width - (this.table.getBorderWidth() * 2);
		}

		@Override
		public void setColumnOrder(final int order[]) {
			this.table.setColumnOrder(order);

		}

		@Override
		public int[] getColumnOrder() {
			return this.table.getColumnOrder();
		}

		@Override
		public void setColumnsMoveable() {
			for (final TableColumn c : this.table.getColumns()) {
				c.setMoveable(true);
			}
		}
	}

	public static final class TreeColumnController extends ColumnController {

		private final Tree tree;

		public TreeColumnController(final Tree tree) {
			this.tree = tree;
		}

		@Override
		Scrollable getControl() {
			return this.tree;
		}

		@Override
		public int getColumnCount() {
			return this.tree.getColumnCount() - (this.tree.getBorderWidth() * 2);
		}

		@Override
		public void setWidth(final int column, final int width) {
			this.tree.getColumn(column).setWidth(width);
		}

		@Override
		public int getWidth(final int column) {
			return this.tree.getColumn(column).getWidth();
		}

		@Override
		public int getAvailWidth() {
			return this.tree.getClientArea().width;
		}

		@Override
		public void installColumnControlListener(final int column, final ControlListener listener) {
			this.tree.getColumn(column).addControlListener(listener);
		}

		@Override
		public void setColumnOrder(final int order[]) {
			this.tree.setColumnOrder(order);
		}

		@Override
		public int[] getColumnOrder() {
			return this.tree.getColumnOrder();
		}

		@Override
		public void setColumnsMoveable() {
			for (final TreeColumn c : this.tree.getColumns()) {
				c.setMoveable(true);
			}
		}
	}
}