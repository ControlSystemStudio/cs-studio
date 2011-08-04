/*
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 * Copyright (c) 2010 Stiftung Deutsches Elektronen-Synchrotron,
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
package org.csstudio.config.ioconfig.editorparts;

import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.csstudio.config.ioconfig.config.view.helper.ConfigHelper;
import org.csstudio.config.ioconfig.config.view.helper.DocumentationManageView;
import org.csstudio.config.ioconfig.model.DocumentDBO;
import org.csstudio.config.ioconfig.model.IocDBO;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.Text;

/**
 * Editor for {@link IocDBO} node's
 *
 * @author hrickens
 * @author $Author: hrickens $
 * @version $Revision: 1.2 $
 * @since 21.05.2010
 */
public class IocEditor extends AbstractNodeEditor<IocDBO> {
    
    public static final String ID = "org.csstudio.config.ioconfig.view.editor.ioc";
    
    /**
     * The IOC Object.
     */
    private IocDBO _ioc;
    
    /**
     * Constructor.
     */
    public IocEditor() {
        // nothing to do.
    }
    
    @Override
    public void cancel() {
        super.cancel();
        if (_ioc != null) {
            final Text text = getHeaderField(HeaderFields.VERSION);
            if (text != null) {
                text.setText("");
            }
            final Spinner indexSpinner = getIndexSpinner();
            if(indexSpinner!=null) {
                indexSpinner.setSelection((Short) indexSpinner.getData());
            }
            final Text nameWidget = getNameWidget();
            if(nameWidget!=null) {
                nameWidget.setText((String) nameWidget.getData());
            }
        }
        final DocumentationManageView dMV = getDocumentationManageView();
        if (dMV != null) {
            dMV.cancel();
        }
        setSaveButtonSaved();
    }
    
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void createPartControl(@Nonnull final Composite parent) {
        super.createPartControl(parent);
        _ioc = getNode();
        main("IOC");
        selecttTabFolder(0);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void doSave(@Nullable final IProgressMonitor monitor) {
        super.doSave(monitor);
        // Main
        final Text nameWidget = getNameWidget();
        if (nameWidget != null) {
            _ioc.setName(nameWidget.getText());
            nameWidget.setData(nameWidget.getText());
        }
        
        final Spinner indexSpinner = getIndexSpinner();
        if(indexSpinner!=null) {
            indexSpinner.setData(_ioc.getSortIndex());
        }
        
        // Document
        final DocumentationManageView documentationManageView = getDocumentationManageView();
        if(documentationManageView!=null) {
            final Set<DocumentDBO> docs = documentationManageView.getDocuments();
            _ioc.setDocuments(docs);
        }
        
        save();
    }
    
    /**
     * Generate the Main IOC configuration Tab.
     *
     * @param head
     *            The headline of the tab.
     */
    private void main(@Nonnull final String head) {
        final TabFolder tabFolder = getTabFolder();
        if (tabFolder != null) {
            final Composite comp = ConfigHelper.getNewTabItem(head, tabFolder,
                                                              5, 300, 260);
            comp.setLayout(new GridLayout(4, false));
            
            final Group gName = new Group(comp, SWT.NONE);
            gName.setText("Name");
            gName.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false,
                                             5, 1));
            gName.setLayout(new GridLayout(3, false));
            
            final Text nameText = new Text(gName, SWT.BORDER | SWT.SINGLE);
            setText(nameText, _ioc.getName(), 255);
            nameText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
                                                false, 1, 1));
            setNameWidget(nameText);
            
            final Spinner indexSpinner = ConfigHelper.getIndexSpinner(gName, _ioc,
                                                                      getMLSB(), "Index", getProfiBusTreeView());
            setIndexSpinner(indexSpinner);
            indexSpinner.setMaximum(
                                    _ioc.getParent().getChildren().size() - 1);
            
            makeDescGroup(comp, 3);
        }
    }
}
