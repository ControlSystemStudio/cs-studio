/*
 * Copyright (c) 2007 Stiftung Deutsches Elektronen-Synchrotron,
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS.
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE.
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS,
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION,
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */

package org.csstudio.dct.ui.editor.outline.internal;

import org.csstudio.dct.model.IFolder;
import org.csstudio.dct.model.IProject;
import org.csstudio.dct.model.IPrototype;
import org.csstudio.platform.ui.util.LayoutUtil;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;

public final class PrototypeSelectionDialog extends Dialog {
	private String message;
	
	private IPrototype selection;
	
	private IProject project;
	
	private TreeViewer treeViewer;
	
	/**
	 * Creates an input dialog with OK and Cancel buttons. Note that the dialog
	 * will have no visual representation (no widgets) until it is told to open.
	 * <p>
	 * Note that the <code>open</code> method blocks for input dialogs.
	 * </p>
	 * 
	 * @param parentShell
	 *            the parent shell, or <code>null</code> to create a top-level
	 *            shell
	 * @param dialogMessage
	 *            the dialog message, or <code>null</code> if none
	 * @param fileExtensions
	 *            the file extensions of files to show in the dialog. Use an
	 *            empty array or <code>null</code> to show only containers
	 *            (folders).
	 */
	public PrototypeSelectionDialog(final Shell parentShell,
			final String dialogMessage, final IProject project) {
		super(parentShell);
		this.setShellStyle(SWT.MODELESS | SWT.CLOSE | SWT.MAX | SWT.TITLE
				| SWT.BORDER | SWT.RESIZE);
		message = dialogMessage;
		this.project = project;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void configureShell(final Shell shell) {
		super.configureShell(shell);
		shell.setText("Prototypes");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Control createDialogArea(final Composite parent) {
		Composite composite = (Composite) super.createDialogArea(parent);
		composite.setLayout(new GridLayout(1, false));
		if (message != null) {
			Label label = new Label(composite, SWT.WRAP);
			label.setText(message);
			GridData data = new GridData(GridData.GRAB_HORIZONTAL
					| GridData.HORIZONTAL_ALIGN_FILL
					| GridData.VERTICAL_ALIGN_CENTER);
			data.horizontalSpan = 2;
			data.widthHint = convertHorizontalDLUsToPixels(IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH);
			label.setLayoutData(data);
		}
		
		
		treeViewer = new TreeViewer(composite);
		treeViewer.getTree().setLayoutData(LayoutUtil.createGridDataForFillingCell(200,400));
		treeViewer.setLabelProvider(new WorkbenchLabelProvider());
		treeViewer.setContentProvider(new WorkbenchContentProvider());
		treeViewer.setAutoExpandLevel(4);
		treeViewer.addFilter(new ViewerFilter(){
			@Override
			public boolean select(Viewer viewer, Object parentElement, Object element) {
				return element instanceof IPrototype || element instanceof IFolder;
			}
		});
		treeViewer.setInput(project);
		return composite;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void okPressed() {
		selection = (IPrototype) ((IStructuredSelection)treeViewer.getSelection()).getFirstElement();
		super.okPressed();
	}
	
	public IPrototype getSelection() {
		return selection;
	}

}
