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

import java.util.List;

import org.csstudio.dct.metamodel.IRecordDefinition;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.IOpenListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.OpenEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.csstudio.domain.common.LayoutUtil;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;

/**
 * Selection dialog for record types. Only one record type can be chosen at a
 * time.
 *
 * @author Sven Wende
 *
 */
public final class RecordDialog extends Dialog {

    private TreeViewer viewer;
    private IRecordDefinition selection;
    private List<IRecordDefinition> recordDefinitions;

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
     * @param recordDefinitions
     *            the record definitions
     */
    public RecordDialog(final Shell parentShell, final List<IRecordDefinition> recordDefinitions) {
        super(parentShell);
        this.setShellStyle(SWT.MODELESS | SWT.CLOSE | SWT.MAX | SWT.TITLE | SWT.BORDER | SWT.RESIZE);
        this.recordDefinitions = recordDefinitions;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void configureShell(final Shell shell) {
        super.configureShell(shell);
        shell.setText("Record Types");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Control createDialogArea(final Composite parent) {
        Composite composite = (Composite) super.createDialogArea(parent);

        composite.setLayout(new GridLayout(1, false));
        Label label = new Label(composite, SWT.WRAP);
        label.setText("Please select the record type:");
        GridData data = new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_CENTER);
        data.horizontalSpan = 2;
        data.widthHint = convertHorizontalDLUsToPixels(IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH);
        label.setLayoutData(data);

        viewer = new TreeViewer(composite);
        viewer.getControl().setLayoutData(LayoutUtil.createGridDataForFillingCell(200, 400));
        viewer.setLabelProvider(new WorkbenchLabelProvider());
        viewer.setContentProvider(new WorkbenchContentProvider() {
            @Override
            public Object[] getElements(Object element) {
                return (Object[]) element;
            }
        });
        viewer.setSorter(new ViewerSorter());
        viewer.setInput(recordDefinitions.toArray());

        viewer.addOpenListener(new IOpenListener() {
            public void open(OpenEvent event) {
                okPressed();
            }
        });
        return composite;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void okPressed() {
        selection = (IRecordDefinition) ((IStructuredSelection) viewer.getSelection()).getFirstElement();
        super.okPressed();
    }

    /**
     * Returns the selected record definition.
     *
     * @return the selected record definition
     */
    public IRecordDefinition getSelection() {
        return selection;
    }

}
