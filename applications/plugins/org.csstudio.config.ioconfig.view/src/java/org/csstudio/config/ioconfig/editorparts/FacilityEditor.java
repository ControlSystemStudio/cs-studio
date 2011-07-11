/*
 * Copyright (c) 2010 Stiftung Deutsches Elektronen-Synchrotron,
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
package org.csstudio.config.ioconfig.editorparts;

import java.util.Set;

import javax.annotation.Nonnull;

import org.csstudio.config.ioconfig.config.view.helper.ConfigHelper;
import org.csstudio.config.ioconfig.config.view.helper.DocumentationManageView;
import org.csstudio.config.ioconfig.model.DocumentDBO;
import org.csstudio.config.ioconfig.model.FacilityDBO;
import org.csstudio.config.ioconfig.model.PersistenceException;
import org.csstudio.config.ioconfig.view.DeviceDatabaseErrorDialog;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Editor for {@link FacilityDBO} node's
 *
 * @author hrickens
 * @author $Author: hrickens $
 * @version $Revision: 1.2 $
 * @since 31.03.2010
 */
public class FacilityEditor extends AbstractNodeEditor<FacilityDBO> {
    
    private static final Logger LOG = LoggerFactory.getLogger(FacilityEditor.class);
    public static final String ID = "org.csstudio.config.ioconfig.view.editor.facility";
    
    /**
     * The Facility Object.
     */
    private FacilityDBO _facility;
    
    /**
     *
     * Constructor.
     */
    public FacilityEditor() {
        //  nothing to do.
    }
    
    /**
     * Constructor.
     */
    public FacilityEditor(@Nonnull final Composite parent, final short sortIndex) {
        super(true);
        getProfiBusTreeView().getTreeViewer().setSelection(null);
        newNode();
        try {
            getNode().moveSortIndex(sortIndex);
        } catch (PersistenceException e) {
            LOG.error("Can't create Facility. Database Error.", e);
            DeviceDatabaseErrorDialog.open(null, "Can't create Facility. Database Error.", e);
        }
        buildGui();
    }
    
    /**
     * @param parent
     *            The Parent Composite.
     * @param facility
     *            to Configure. Is NULL create a new one.
     */
    public FacilityEditor(@Nonnull final Composite parent, @Nonnull final FacilityDBO facility) {
        super(facility == null);
        _facility = facility;
        buildGui();
        selecttTabFolder(0);
    }
    
    @Override
    public void createPartControl(@Nonnull final Composite parent) {
        super.createPartControl(parent);
        _facility = (FacilityDBO) getNode();
        buildGui();
        selecttTabFolder(0);
    }
    
    /**
     * (@inheritDoc)
     */
    @Override
    public void doSave(@Nonnull final IProgressMonitor monitor) {
        super.doSave(monitor);
        // Main
        Text nameWidget = getNameWidget();
        if(nameWidget != null) {
            _facility.setName(nameWidget.getText());
            nameWidget.setData(nameWidget.getText());
        }
        
        Spinner indexSpinner = getIndexSpinner();
        if(indexSpinner != null) {
            indexSpinner.setData(_facility.getSortIndex());
        }
        
        // Document
        DocumentationManageView documentationManageView = getDocumentationManageView();
        if(documentationManageView != null) {
            Set<DocumentDBO> docs = documentationManageView.getDocuments();
            _facility.setDocuments(docs);
        }
        
        save();
        //        getProfiBusTreeView().refresh(getNode());
        //        getProfiBusTreeView().refresh();
    }
    
    private void buildGui() {
        setSavebuttonEnabled(null, getNode().isPersistent());
        main("Facility");
        getProfiBusTreeView().refresh(getNode()); // TODO: denke dieser refresh ist Überflüssig
        // _tabFolder.pack();
    }
    
    /**
     * Generate the Main IOC configuration Tab.
     *
     * @param head
     *            The headline of the tab.
     */
    private void main(@Nonnull final String head) {
        Composite comp = ConfigHelper.getNewTabItem(head, getTabFolder(), 5, 300, 260);
        comp.setLayout(new GridLayout(4, false));
        
        Group gName = new Group(comp, SWT.NONE);
        gName.setText("Name");
        gName.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 5, 1));
        gName.setLayout(new GridLayout(3, false));
        
        Text nameText = new Text(gName, SWT.BORDER | SWT.SINGLE);
        setText(nameText, _facility.getName(), 255);
        nameText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
        setNameWidget(nameText);
        setIndexSpinner(ConfigHelper.getIndexSpinner(gName,
                                                     _facility,
                                                     getMLSB(),
                                                     "Index",
                                                     getProfiBusTreeView()));
        
        makeDescGroup(comp, 3);
    }
    
    @Override
    public void cancel() {
        super.cancel();
        if(_facility != null) {
            Object data = null;
            Composite parent = getParent();
            if(parent != null) {
                data = parent.getData("version");
                if(data != null && data instanceof Text) {
                    Text text = (Text) data;
                    text.setText("");
                }
            }
            Spinner indexSpinner = getIndexSpinner();
            if(indexSpinner != null) {
                indexSpinner.setSelection((Short) indexSpinner.getData());
            }
            Text nameWidget = getNameWidget();
            if(nameWidget != null) {
                nameWidget.setText((String) nameWidget.getData());
            }
        }
        cancelDocumentationManageView();
        setSaveButtonSaved();
    }

    private void cancelDocumentationManageView() {
        DocumentationManageView dMV = getDocumentationManageView();
        if(dMV != null) {
            dMV.cancel();
        }
    }
}
