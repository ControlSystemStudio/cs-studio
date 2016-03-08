/*
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchroton,
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
package org.csstudio.sds.importer.ui.internal.wizards;

import org.eclipse.jface.fieldassist.FieldAssistColors;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * Wizard page for the selection of import source files.
 *
 * @author Alexander Will
 * @version $Revision: 1.1 $
 *
 */
public final class ImportSourceSelectionWizardPage extends WizardPage {
    /**
     * Text element that holds the selcted file's full path.
     */
    private Text _filePath;

    /**
     * Creates a new import source selection wizard page.
     *
     * @param pageName
     *            the name of the page
     */
    public ImportSourceSelectionWizardPage(final String pageName) {
        super(pageName);

        setTitle("Choose the file to import");
        setDescription("Select the file that should be imported.");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void createControl(final Composite parent) {
        Composite control = new Composite(parent, SWT.NONE);
        control.setLayout(LayoutUtil.createGridLayout(1, 0, 5, 0));
        control.setLayoutData(LayoutUtil.createGridDataForFillingCell());

        Label label = new Label(control, SWT.NONE);
        label.setText("Choose file to import:");

        Composite fileSelectionComposite = new Composite(control, SWT.NONE);
        fileSelectionComposite.setLayout(LayoutUtil
                .createGridLayout(2, 0, 0, 5));
        fileSelectionComposite.setLayoutData(LayoutUtil
                .createGridDataForFillingCell());

        _filePath = new Text(fileSelectionComposite, SWT.BORDER);
        _filePath.setBackground(FieldAssistColors
                .getRequiredFieldBackgroundColor(_filePath));
        _filePath.setEditable(false);
        _filePath.setLayoutData(LayoutUtil
                .createGridDataForHorizontalFillingCell());
        _filePath.addModifyListener(new ModifyListener() {
            public void modifyText(final ModifyEvent e) {
                setPageComplete(_filePath.getText() != null
                        && _filePath.getText().length() > 0);
            }
        });

        Button openFileButton = new Button(fileSelectionComposite, SWT.NONE);
        openFileButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseUp(final MouseEvent e) {
                FileDialog fd = new FileDialog(parent.getShell());
                String fileName = fd.open();

                if (fileName != null) {
                    _filePath.setText(fileName);
                }
            }
        });
        openFileButton.setText("...");
        openFileButton.setLayoutData(LayoutUtil.createGridData(30));

        setPageComplete(false);
        setErrorMessage(null);
        setMessage(null);
        setControl(control);
    }

    /**
     * Return the path and the name of the selected file.
     *
     * @return The path and the name of the selected file.
     */
    public String getSelectedFilePath() {
        String result = null;

        if (_filePath != null) {
            result = _filePath.getText();
        }

        return result;
    }
}
