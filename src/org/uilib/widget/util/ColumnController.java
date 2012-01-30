package org.uilib.widget.util;

import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;

public interface ColumnController {

	//~ Methods --------------------------------------------------------------------------------------------------------

	int getColumnCount();

	void setWidth(final int column, final int width);

	int getWidth(final int column);

	int getAvailWidth();

	void installControlListener(final ControlListener listener);

	void installColumnControlListener(final int column, final ControlListener listener);

	void setColumnOrder(final int order[]);

	int[] getColumnOrder();

	void setColumnsMoveable();

	//~ Inner Classes --------------------------------------------------------------------------------------------------

	public static final class TableColumnController implements ColumnController {

		private final Table table;

		public TableColumnController(final Table table) {
			this.table = table;
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
			return this.table.getClientArea().width;
		}

		@Override
		public void installControlListener(final ControlListener listener) {
			this.table.addControlListener(listener);
		}

		@Override
		public void installColumnControlListener(final int column, final ControlListener listener) {
			this.table.getColumn(column).addControlListener(listener);
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

	public static final class TreeColumnController implements ColumnController {

		private final Tree tree;

		public TreeColumnController(final Tree tree) {
			this.tree = tree;
		}

		@Override
		public int getColumnCount() {
			return this.tree.getColumnCount();
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
		public void installControlListener(final ControlListener listener) {
			this.tree.addControlListener(listener);
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