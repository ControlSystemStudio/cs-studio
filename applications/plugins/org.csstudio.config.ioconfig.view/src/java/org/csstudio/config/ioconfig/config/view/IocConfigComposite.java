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
 * $Id: IocConfigComposite.java,v 1.6 2010/08/20 13:33:00 hrickens Exp $
 */
package org.csstudio.config.ioconfig.config.view;

import java.util.Set;

import org.csstudio.config.ioconfig.config.view.helper.ConfigHelper;
import org.csstudio.config.ioconfig.config.view.helper.DocumentationManageView;
import org.csstudio.config.ioconfig.model.DocumentDBO;
import org.csstudio.config.ioconfig.model.FacilityDBO;
import org.csstudio.config.ioconfig.model.IDocumentable;
import org.csstudio.config.ioconfig.model.IocDBO;
import org.csstudio.config.ioconfig.model.AbstractNodeDBO;
import org.csstudio.config.ioconfig.model.pbmodel.GSDFileDBO;
import org.csstudio.config.ioconfig.view.ProfiBusTreeView;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;

/**
 * @author hrickens
 * @author $Author: hrickens $
 * @version $Revision: 1.6 $
 * @since 20.03.2008
 */
public class IocConfigComposite extends AbstractNodeConfig {

    /**
     * The IOC Object.
     */
    private IocDBO _ioc;

    /**
     * @param parent
     *            The Parent Composite.
     * @param profiBusTreeView
     *            The Tree of all node from the IO Config.
     * @param ioc
     *            to Configure. Is NULL create a new one.
     */
    public IocConfigComposite(final Composite parent, final ProfiBusTreeView profiBusTreeView,
            final IocDBO ioc) {
        super(parent, profiBusTreeView, "IOC Configuration", ioc, ioc == null);
        _ioc = ioc;

        if (_ioc == null) {
            getNode();
            newNode();
        }
        setSavebuttonEnabled(null, getNode().isPersistent());
        main("IOC");
        documents();
    }

    /**
     * Generate the Main IOC configuration Tab.
     *
     * @param head
     *            The headline of the tab.
     */
    private void main(final String head) {
        Composite comp = ConfigHelper.getNewTabItem(head, getTabFolder(), 5,300,260);
        comp.setLayout(new GridLayout(4, false));

        Group gName = new Group(comp, SWT.NONE);
        gName.setText("Name");
        gName.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 5, 1));
        gName.setLayout(new GridLayout(3, false));

        Text nameText = new Text(gName, SWT.BORDER | SWT.SINGLE);
        setText(nameText, _ioc.getName(), 255);
        nameText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
        setNameWidget(nameText);

        setIndexSpinner(ConfigHelper.getIndexSpinner(gName, _ioc, getMLSB(), "Index",
                getProfiBusTreeView()));
        getIndexSpinner().setMaximum(_ioc.getParent().getChildren().size() - 1);

        makeDescGroup(comp,3);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.csstudio.config.ioconfig.config.view.NodeConfig#fill(org.csstudio
     * .config.ioconfig.model .pbmodel.GSDFile)
     */
    @Override
    public boolean fill(final GSDFileDBO gsdFile) {
        return false;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.csstudio.config.ioconfig.config.view.NodeConfig#getGSDFile()
     */
    @Override
    public GSDFileDBO getGSDFile() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final IDocumentable getDocumentableObject() {
        return getNode();
    }

    public final AbstractNodeDBO getNode() {
        if (_ioc == null) {
            StructuredSelection selection = (StructuredSelection) getProfiBusTreeView().getTreeViewer()
                    .getSelection();
            if (selection.getFirstElement() instanceof FacilityDBO) {
                FacilityDBO facility = (FacilityDBO) selection.getFirstElement();
                _ioc = new IocDBO(facility);
            }
        }
        return _ioc;
    }

    /**
     * Store all Data in {@link IocDBO} DB object.
     */
    public final void store() {
        super.store();
        // Main
        _ioc.setName(getNameWidget().getText());
        getNameWidget().setData(getNameWidget().getText());

        getIndexSpinner().setData(_ioc.getSortIndex());

        // Document
        Set<DocumentDBO> docs = getDocumentationManageView().getDocuments();
        _ioc.setDocuments(docs);

        save();
    }

    @Override
    public void cancel() {
        super.cancel();
        if (_ioc != null) {
            Object data = getData("version");
            if (data != null) {
                if (data instanceof Text) {
                    Text text = (Text) data;
                    text.setText("");
                }
            }
            getIndexSpinner().setSelection((Short) getIndexSpinner().getData());
            getNameWidget().setText((String) getNameWidget().getData());
        }
        DocumentationManageView dMV = getDocumentationManageView();
        if (dMV != null) {
            dMV.cancel();
        }
        setSaveButtonSaved();
    }

}
