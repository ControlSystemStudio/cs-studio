/**
 * 
 */
package org.csstudio.utility.channel.actions;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.CheckedTreeSelectionDialog;

/**
 * @author shroffk
 * 
 */
public class DisplayTreeDialog extends TitleAreaDialog {

	private ILabelProvider labelProvider;
	private ITreeContentProvider contentProvider;
	private Object root = null;
	private String title;
	private String message;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * @param parentShell
	 */
	public DisplayTreeDialog(Shell parentShell, ILabelProvider labelProvider,
			ITreeContentProvider contentProvider) {
		super(parentShell);
		this.labelProvider = labelProvider;
		this.contentProvider = contentProvider;
	}

	/**
	 * Creates the dialog's contents
	 * 
	 * @param parent
	 *            the parent composite
	 * @return Control
	 */
	protected Control createContents(Composite parent) {
		Control contents = super.createContents(parent);

		// Set the title
		super.setTitle(title);

		// Set the message
		setMessage(message, IMessageProvider.INFORMATION);

		// Set the image
		// if (image != null) setTitleImage(image);

		return contents;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
//		Composite comp = (Composite) super.createDialogArea(parent);
		TreeViewer v = new TreeViewer(parent, SWT.FULL_SELECTION | SWT.NONE);
		v.getTree().setLayoutData(
				new GridData(GridData.FILL_BOTH | GridData.GRAB_HORIZONTAL
						| GridData.GRAB_VERTICAL));
		v.setLabelProvider(labelProvider);
		v.setContentProvider(contentProvider);
		v.setInput(root);

		return parent;
	}

	public void setInput(Object input) {
		this.root = input;
	}

}
