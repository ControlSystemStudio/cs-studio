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
/*
 * $Id$
 */
package org.csstudio.config.ioconfig.config.view;

import java.util.Set;

import org.csstudio.config.ioconfig.config.view.helper.ConfigHelper;
import org.csstudio.config.ioconfig.config.view.helper.DocumentationManageView;
import org.csstudio.config.ioconfig.model.Document;
import org.csstudio.config.ioconfig.model.Facility;
import org.csstudio.config.ioconfig.model.Node;
import org.csstudio.config.ioconfig.model.pbmodel.GSDFile;
import org.csstudio.config.ioconfig.view.ProfiBusTreeView;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;

/**
 * @author hrickens
 * @author $Author$
 * @version $Revision$
 * @since 20.03.2008
 */
public class FacilityConfigComposite extends NodeConfig {

    /**
     * The Facility Object.
     */
    private Facility _facility;

    /**
     * The text field for the description of the Facility.
     */
    private Text _descText;

    /**
     * @param parent
     *            The Parent Composite.
     * @param profiBusTreeView
     *            The Tree of all node from the IO Config.
     * @param facility
     *            to Configure. Is NULL create a new one.
     */
    public FacilityConfigComposite(Composite parent, final ProfiBusTreeView profiBusTreeView,
            Facility facility) {
        super(parent, profiBusTreeView, "Facility Configuration", facility, facility == null);
        _facility = facility;

        if (_facility == null) {
            getProfiBusTreeView().getTreeViewer().setSelection(null);
            newNode();
        }
        setSavebuttonEnabled(null, getNode().isPersistent());
        main("Facility");
        documents();
        // _tabFolder.pack();

    }

    /**
     * Generate the Main IOC configuration Tab.
     * 
     * @param head
     *            The headline of the tab.
     */
    private void main(final String head) {
        Composite comp = ConfigHelper.getNewTabItem(head, getTabFolder(), 5);
        comp.setLayout(new GridLayout(4, false));

        Group gName = new Group(comp, SWT.NONE);
        gName.setText("Name");
        gName.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 5, 1));
        gName.setLayout(new GridLayout(3, false));

        Text nameText = new Text(gName, SWT.BORDER | SWT.SINGLE);
        setText(nameText, _facility.getName(), 255);
        nameText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
        setNameWidget(nameText);
        setIndexSpinner(ConfigHelper.getIndexSpinner(gName, _facility, getMLSB(), "Index",
                getProfiBusTreeView()));

        Group gDesc = new Group(comp, SWT.NONE);
        gDesc.setText("Description: ");
        gDesc.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 5, 1));
        gDesc.setLayout(new GridLayout(1, false));
        _descText = new Text(gDesc, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL);
        _descText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 3, 1));
        setText(_descText, _facility.getDescription(), 255);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.csstudio.config.ioconfig.config.view.NodeConfig#fill(org.csstudio
     * .config.ioconfig.model.pbmodel.GSDFile)
     */
    @Override
    public boolean fill(final GSDFile gsdFile) {
        return false;
    }

    /**
     * Store all Data in {@link Facility} DB object.
     */
    public final void store() {
        super.store();
        // Main
        _facility.setName(getNameWidget().getText());
        getNameWidget().setData(getNameWidget().getText());

        _facility.setDescription(_descText.getText());
        _descText.setData(_descText.getText());

        getIndexSpinner().setData(_facility.getSortIndex());

        // Document
        Set<Document> docs = getDocumentationManageView().getDocuments();
        _facility.setDocuments(docs);

        save();
        getProfiBusTreeView().refresh(getNode());
    }

    @Override
    public void cancel() {
        super.cancel();
        if (_facility != null) {
            Object data = getData("version");
            if (data != null) {
                if (data instanceof Text) {
                    Text text = (Text) data;
                    text.setText("");
                }
            }
            _descText.setText((String) _descText.getData());
            getIndexSpinner().setSelection((Short) getIndexSpinner().getData());
            getNameWidget().setText((String) getNameWidget().getData());
        }
        DocumentationManageView dMV = getDocumentationManageView();
        if (dMV != null) {
            dMV.cancel();
        }
        setSaveButtonSaved();
    }

    /**
     * Have no GSD File.
     * 
     * @return null.
     */
    @Override
    public GSDFile getGSDFile() {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.csstudio.config.ioconfig.config.view.NodeConfig#getNode()
     */
    @Override
    public Node getNode() {
        if (_facility == null) {
            _facility = new Facility();
        }
        return _facility;
    }
}
