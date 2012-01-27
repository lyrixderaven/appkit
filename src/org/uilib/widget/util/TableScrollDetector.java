package org.uilib.widget.util;

import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Table;

public final class TableScrollDetector implements PaintListener {

	//~ Instance fields ------------------------------------------------------------------------------------------------

	private final Table table;
	private final ScrollListener listener;
	private int firstVisible = 0;
	private int lastVisible  = 0;

	//~ Constructors ---------------------------------------------------------------------------------------------------

	protected TableScrollDetector(final Table table, final ScrollListener listener) {
		this.table		  = table;
		this.listener     = listener;
		this.table.addPaintListener(this);
	}

	//~ Methods --------------------------------------------------------------------------------------------------------

	@Override
	public void paintControl(final PaintEvent event) {

		Rectangle rect   = table.getClientArea();
		int itemHeight   = table.getItemHeight();
		int headerHeight = table.getHeaderHeight();
		int visibleCount = ((rect.height - headerHeight + itemHeight) - 1) / itemHeight;

		int totalRows    = table.getItemCount();
		int newFirstVis  = table.getTopIndex();
		int newLastVis   = (newFirstVis + visibleCount) - 1;

		if ((newFirstVis != firstVisible) || (newLastVis != lastVisible)) {
			this.listener.scrolled(new ScrollEvent(totalRows, newFirstVis, newLastVis));
		}

		this.firstVisible     = newFirstVis;
		this.lastVisible	  = newLastVis;
	}

	//~ Inner Interfaces -----------------------------------------------------------------------------------------------

	public interface ScrollListener {
		public void scrolled(final ScrollEvent event);
	}

	//~ Inner Classes --------------------------------------------------------------------------------------------------

	public static final class ScrollEvent {

		private final int totalRows;
		private final int firstVisibleRow;
		private final int lastVisibleRow;

		public ScrollEvent(final int totalRows, final int firstVisibleRow, final int lastVisibleRow) {
			this.totalRows			 = totalRows;
			this.firstVisibleRow     = firstVisibleRow;
			this.lastVisibleRow		 = lastVisibleRow;
		}

		public int getFirstVisibleRow() {
			return firstVisibleRow;
		}

		public int getLastVisibleRow() {
			return lastVisibleRow;
		}

		public boolean isFirstRowVisible() {
			return (firstVisibleRow == 0);
		}

		public boolean isLastRowVisible() {
			return ((lastVisibleRow + 1) == totalRows);
		}
	}
}