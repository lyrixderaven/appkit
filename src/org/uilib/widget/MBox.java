package org.uilib.widget;

import java.util.Arrays;
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

/**
 * A more sophisticated MessageBox.
 */
public final class MBox {

	//~ Enumerations ---------------------------------------------------------------------------------------------------

	public enum Type {ERROR, INFO, WARNING, QUESTION;
	}

	//~ Static fields/initializers -------------------------------------------------------------------------------------

	@SuppressWarnings("unused")
	private static final Logger L							 = LoggerFactory.getLogger(MBox.class);

	//~ Instance fields ------------------------------------------------------------------------------------------------

	private final Shell shell;
	private int answer										 = 0;
	private ImmutableList<String> options;

	//~ Constructors ---------------------------------------------------------------------------------------------------

	public MBox(final Shell parentShell, final Type type, final String title, final String message, final int def,
				final String... optionArray) {

		this.options = ImmutableList.copyOf(Arrays.asList(optionArray));
		this.answer = def;

		Preconditions.checkArgument(this.options.size() > 0, "empy options");
		Preconditions.checkArgument(def >= 0 && def < this.options.size(), "%s options but default %s specified", this.options.size(), def);

		this.shell = new Shell(parentShell, SWT.APPLICATION_MODAL | SWT.DIALOG_TRIM | SWT.SHEET);
		this.shell.setLayout(new GridLayout(2, false));

		Composite compIcon = new Composite(this.shell, SWT.NONE);
		compIcon.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 2));

		GridLayout gl = new GridLayout();
		gl.marginHeight     = 10;
		gl.marginWidth	    = 10;
		compIcon.setLayout(gl);

		Label label = new Label(compIcon, SWT.NONE);

		int systemImage;
		switch (type) {
			case ERROR:
				systemImage = SWT.ICON_ERROR;
				break;
			case WARNING:
				systemImage = SWT.ICON_WARNING;
				break;
			case QUESTION:
				systemImage = SWT.ICON_QUESTION;
				break;
			default:
				systemImage = SWT.ICON_INFORMATION;
		}
		Image image =
			this.shell.getDisplay().getSystemImage(systemImage);
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
		compButtons.setLayout(new GridLayout(options.size() + 1, false));
		compButtons.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));

		if (options.size() != 1) {

			Label spacer = new Label(compButtons, SWT.NONE);
			spacer.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
		}

		int i	   = 0;
		Button btn = null;
		Button defBtn = null;
		for (final String option : options) {
			btn = new Button(compButtons, SWT.PUSH);
			btn.setText(option);
			btn.setLayoutData(new GridData(SWT.NONE, SWT.NONE, false, false));
			btn.addSelectionListener(new BClicked(i));

			if (i == answer) {
				defBtn = btn;
			}

			i++;
		}

		shell.setDefaultButton(defBtn);
		defBtn.setFocus();

		/* Center Button if there is just one */
		if (options.size() == 1) {
			((GridData) btn.getLayoutData()).horizontalAlignment		   = SWT.CENTER;
			((GridData) btn.getLayoutData()).grabExcessHorizontalSpace     = true;
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

	public void open() {
		this.openReturningInt();
	}

	public int openReturningInt() {
		this.shell.open();

		while (! this.shell.isDisposed()) {
			if (! this.shell.getDisplay().readAndDispatch()) {
				this.shell.getDisplay().sleep();
			}
		}

		return this.answer;
	}

	public String openReturningString() {
		return this.options.get(this.openReturningInt());
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