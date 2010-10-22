/*
		* Copyright (c) 2010 Stiftung Deutsches Elektronen-Synchrotron,
		* Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
		*
		* THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS.
		* WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT
		NOT LIMITED
		* TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE
		AND
		* NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS
		BE LIABLE
		* FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF
		CONTRACT,
		* TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
		SOFTWARE OR
		* THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE
		DEFECTIVE
		* IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING,
		REPAIR OR
		* CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART
		OF THIS LICENSE.
		* NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS
		DISCLAIMER.
		* DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES,
		ENHANCEMENTS,
		* OR MODIFICATIONS.
		* THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION,
		MODIFICATION,
		* USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE
		DISTRIBUTION OF THIS
		* PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU
		MAY FIND A COPY
		* AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
		*/
package org.csstudio.utility.documentviewer;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.csstudio.config.ioconfig.model.IDocument;
import org.csstudio.config.ioconfig.model.INode;
import org.csstudio.config.ioconfig.model.service.ProcessVariable2IONameImplemation;
import org.csstudio.platform.model.IProcessVariable;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

/**
 * TODO (hrickens) :
 *
 * @author hrickens
 * @author $Author: hrickens $
 * @version $Revision: 1.1 $
 * @since 17.08.2010
 */
public class DocumentContend {

    private ListViewer _pvList;

    private Collection<IProcessVariable> _processVariables = new HashSet<IProcessVariable>();

    /**
     * A List with all Process Variables to show the Documents
     */
    private TableViewer _crateDocumentTable;

    /**
     * @param parent
     */
    public void createPVList(final Composite parent) {
        Composite pvListMain = new Composite(parent, SWT.NONE);
        pvListMain.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true));
        pvListMain.setLayout(new GridLayout(2, false));

        final Text text = new Text(pvListMain, SWT.SINGLE | SWT.LEAD | SWT.BORDER);
        text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        text.setText("");

        Button button = new Button(pvListMain, SWT.PUSH);
        button.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false));
        button.setText("Add");

        _pvList = new ListViewer(pvListMain);
        _pvList.getList().setLayoutData(GridDataFactory.fillDefaults().grab(false, true).span(2, 1)
                .create());
        _pvList.setLabelProvider(new IProcessVariableLabelProvider());
        _pvList.setContentProvider(new ArrayContentProvider());

        button.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(final SelectionEvent e) {
                _processVariables.add(new ProcessVariable(text.getText()));
                _pvList.setInput(_processVariables);
                callIoNameService(_processVariables);
            }

        });
        createMenu(_pvList);
    }

    public void createDocumentView(final Composite parent) {
        //      _documentTree = new TreeViewer(parent);
        _crateDocumentTable = DocumentTableBuilder.crateDocumentTable(parent);
        DocumentTableBuilder.makeMenus(_crateDocumentTable);
    }

    /**
     * @param processVariables
     */
    public void setProcessVariables(final IProcessVariable[] processVariables) {
        _processVariables = new HashSet<IProcessVariable>(Arrays.asList(processVariables));

    }

    /**
     *
     */
    public void update() {
      _pvList.setInput(_processVariables);
      callIoNameService(_processVariables);
    }

    /**
     * @param pvList
     */
    private void createMenu(final ListViewer viewer) {
        Menu menu = new Menu(viewer.getControl());
        MenuItem showItem = new MenuItem(menu, SWT.PUSH);
        showItem.addSelectionListener(new RemoveChannelListener(viewer));
        showItem.setText("&Remove");
        showItem.setImage(PlatformUI.getWorkbench().getSharedImages()
                .getImage(ISharedImages.IMG_ELCL_REMOVE));

        MenuItem saveItem = new MenuItem(menu, SWT.PUSH);
        saveItem.addSelectionListener(new RemoveAllChannelsListener(viewer));
        saveItem.setText("Remove &All");
        saveItem.setImage(PlatformUI.getWorkbench().getSharedImages()
                .getImage(ISharedImages.IMG_ELCL_REMOVEALL));

        viewer.getList().setMenu(menu);
    }

    /**
     * @param processVariables
     */
    private void callIoNameService(final Collection<IProcessVariable> processVariables) {
        ProcessVariable2IONameImplemation pv2IOName = new ProcessVariable2IONameImplemation();
        Collection<String> pcNames = new HashSet<String>(processVariables.size());
        for (IProcessVariable iProcessVariable : processVariables) {
            pcNames.add(iProcessVariable.getName());
        }
        Collection<INode> nodes = pv2IOName.getNodes(pcNames).values();
        Collection<HierarchyDocument> hierarchyDocuments = new HashSet<HierarchyDocument>();

        addNodes(hierarchyDocuments, nodes);
        _crateDocumentTable.setInput(hierarchyDocuments);
    }

    /**
     * @param all
     * @param nodes
     */
    private void addNodes(final Collection<HierarchyDocument> all, final Collection<INode> nodes) {
        for (INode iNode : nodes) {
            addDocuments(all, iNode);
            INode parent = iNode.getParent();
            addParent(all, parent);
        }
    }

    /**
     * @param all
     * @param iNode
     */
    private void addDocuments(final Collection<HierarchyDocument> all, final INode iNode) {
        Set documents = iNode.getDocuments();
        for (Object object : documents) {
            IDocument d = (IDocument) object;
            all.add(new HierarchyDocument(iNode, d));
        }
    }

    /**
     * @param all
     * @param parent
     */
    private void addParent(final Collection<HierarchyDocument> all, final INode parent) {
        if(parent!=null) {
            addDocuments(all, parent);
            addParent(all, parent.getParent());
        }
    }



    private class RemoveAllChannelsListener implements SelectionListener {

        private final ListViewer _viewer;

        /**
         * Constructor.
         */
        public RemoveAllChannelsListener(final ListViewer viewer) {
            _viewer = viewer;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void widgetDefaultSelected(final SelectionEvent e) {
            _processVariables.clear();
            _viewer.setInput(_processVariables);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void widgetSelected(final SelectionEvent e) {
            _processVariables.clear();
            _viewer.setInput(_processVariables);
        }

    }

    private class RemoveChannelListener implements SelectionListener {

        private final ListViewer _viewer;

        /**
         * Constructor.
         */
        public RemoveChannelListener(final ListViewer viewer) {
            _viewer = viewer;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void widgetDefaultSelected(final SelectionEvent e) {
            remove();
        }

        /**
         *
         */
        @SuppressWarnings("unchecked")
        private void remove() {
            StructuredSelection selection = (StructuredSelection) _viewer.getSelection();
            List list = selection.toList();
            _processVariables.removeAll(list);
            _viewer.setInput(_processVariables);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void widgetSelected(final SelectionEvent e) {
            remove();
        }

    }
}
