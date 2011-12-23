package org.uilib.widgets;

import org.apache.log4j.Logger;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public final class MBox {

	//~ Enumerations ---------------------------------------------------------------------------------------------------

	public enum Icon {ERROR, INFO;
	}

	//~ Static fields/initializers -------------------------------------------------------------------------------------

	@SuppressWarnings("unused")
	private static final Logger L							 = Logger.getLogger(MBox.class);

	//~ Instance fields ------------------------------------------------------------------------------------------------

	private final Shell shell;
	private int answer										 = 0;

	//~ Constructors ---------------------------------------------------------------------------------------------------

	public MBox(final Shell parent, final Icon icon, final String title, final String message, final String... options) {
		this.shell = new Shell(parent, SWT.APPLICATION_MODAL | SWT.DIALOG_TRIM | SWT.SHEET);
		this.shell.setLayout(new GridLayout(2, false));

		Composite compIcon = new Composite(this.shell, SWT.NONE);
		compIcon.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 2));

		GridLayout gl = new GridLayout();
		gl.marginHeight     = 10;
		gl.marginWidth	    = 10;
		compIcon.setLayout(gl);

		Label label = new Label(compIcon, SWT.NONE);
		Image image =
			this.shell.getDisplay().getSystemImage((icon == Icon.ERROR) ? SWT.ICON_ERROR : SWT.ICON_INFORMATION);
		label.setImage(image);

		this.shell.setText(title);

		if (message.length() < 1000) {

			Label lSecMessage = new Label(this.shell, SWT.NONE);
			lSecMessage.setText(message);
			lSecMessage.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, true));
		} else {

			Text tSecMessage = new Text(this.shell, SWT.MULTI | SWT.V_SCROLL | SWT.BORDER);
			tSecMessage.setText(message);
			tSecMessage.setEditable(false);
			tSecMessage.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, true));
			tSecMessage.getVerticalBar().setEnabled(true);
		}

		Composite compButtons = new Composite(this.shell, SWT.NONE);
		compButtons.setLayout(new GridLayout(options.length + 1, false));
		compButtons.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));

		if (options.length != 1) {

			Label spacer = new Label(compButtons, SWT.NONE);
			spacer.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
		}

		int i	   = 0;
		Button btn = null;
		for (final String option : options) {
			btn = new Button(compButtons, SWT.PUSH);
			btn.setText(option);
			btn.setLayoutData(new GridData(SWT.NONE, SWT.NONE, false, false));
			btn.addSelectionListener(new BClicked(i));
			i++;
		}

		/* Center Button if there is just one */
		if (options.length == 1) {
			((GridData) btn.getLayoutData()).horizontalAlignment		   = SWT.CENTER;
			((GridData) btn.getLayoutData()).grabExcessHorizontalSpace     = true;
		}

		/* last Option is default */
		this.answer = i - 1;
		if (btn != null) {
			shell.setDefaultButton(btn);
			btn.setFocus();
		}
		if (message.length() < 1000) {
			this.shell.setSize(shell.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		} else {
			this.shell.setSize(shell.computeSize(800, 400));
		}

		/* Position in the middle of of parent shell */
		Rectangle parentBounds = this.shell.getParent().getBounds();
		Rectangle shellBounds  = shell.getBounds();
		int x				   = parentBounds.x + ((parentBounds.width - shellBounds.width) / 2);
		int y				   = parentBounds.y + ((parentBounds.height - shellBounds.height) / 2);
		this.shell.setLocation(x, y);
	}

	//~ Methods --------------------------------------------------------------------------------------------------------

	public int open() {
		this.shell.open();

		while (! this.shell.isDisposed()) {
			if (! this.shell.getDisplay().readAndDispatch()) {
				this.shell.getDisplay().sleep();
			}
		}

		return this.answer;
	}

	//~ Inner Classes --------------------------------------------------------------------------------------------------

	private final class BClicked extends SelectionAdapter {

		private final int answer;

		public BClicked(final int answer) {
			this.answer = answer;
		}

		@Override
		public void widgetSelected(final SelectionEvent e) {
			MBox.this.answer = this.answer;
			shell.dispose();
		}
	}
}