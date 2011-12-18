package org.uilib.swt.widgets;

import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

import org.uilib.util.Texts;

// TODO: convert widgets to components
public class SaveFileDialog {

	//~ Instance fields ------------------------------------------------------------------------------------------------

	private final FileDialog dlg;
	private final Texts texts;

	//~ Constructors ---------------------------------------------------------------------------------------------------

	public SaveFileDialog(final Shell shell, final Texts texts) {
		this.dlg	   = new FileDialog(shell, SWT.SAVE);
		this.texts     = texts;
	}

	//~ Methods --------------------------------------------------------------------------------------------------------

	public String open() {

		// We store the selected file name in fileName
		String fileName = null;

		// The user has finished when one of the
		// following happens:
		// 1) The user dismisses the dialog by pressing Cancel
		// 2) The selected file name does not exist
		// 3) The user agrees to overwrite existing file
		boolean done = false;

		while (! done) {
			// Open the File Dialog
			fileName = dlg.open();
			if (fileName == null) {
				// User has cancelled, so quit and return
				done = true;
			} else {

				// User has selected a file; see if it already exists
				File file = new File(fileName);
				if (file.exists()) {

					// The file already exists; asks for confirmation
					MessageBox mb = new MessageBox(dlg.getParent(), SWT.ICON_WARNING | SWT.YES | SWT.NO);
					mb.setMessage(this.texts.get("dialog_filereplace", fileName));

					// If they click Yes, we're done and we drop out. If
					// they click No, we redisplay the File Dialog
					done = mb.open() == SWT.YES;
				} else {
					// File does not exist, so drop out
					done = true;
				}
			}
		}
		return fileName;
	}

	public String getFileName() {
		return dlg.getFileName();
	}

	public String[] getFileNames() {
		return dlg.getFileNames();
	}

	public String[] getFilterExtensions() {
		return dlg.getFilterExtensions();
	}

	public String[] getFilterNames() {
		return dlg.getFilterNames();
	}

	public String getFilterPath() {
		return dlg.getFilterPath();
	}

	public void setFileName(final String string) {
		dlg.setFileName(string);
	}

	public void setFilterExtensions(final String extensions[]) {
		dlg.setFilterExtensions(extensions);
	}

	public void setFilterNames(final String names[]) {
		dlg.setFilterNames(names);
	}

	public void setFilterPath(final String string) {
		dlg.setFilterPath(string);
	}

	public Shell getParent() {
		return dlg.getParent();
	}

	public int getStyle() {
		return dlg.getStyle();
	}

	public String getText() {
		return dlg.getText();
	}

	public void setText(final String string) {
		dlg.setText(string);
	}
}