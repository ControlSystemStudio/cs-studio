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
package org.csstudio.utility.documentviewer;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.csstudio.config.ioconfig.model.DocumentDBO;
import org.csstudio.config.ioconfig.model.INode;
import org.csstudio.config.ioconfig.model.INodeWithPrototype;
import org.csstudio.config.ioconfig.model.PersistenceException;
import org.csstudio.config.ioconfig.model.service.IProcessVariable2IONameService;
import org.csstudio.config.ioconfig.model.service.NodeNotFoundException;
import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.platform.model.IProcessVariable;
import org.csstudio.servicelocator.ServiceLocator;
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
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
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
public class DocumentContent {

    protected static final Logger LOG = CentralLogger.getInstance()
            .getLogger(DocumentContent.class);

    private ListViewer _pvList;

    private Collection<IProcessVariable> _processVariables = new HashSet<IProcessVariable>();

    /**
     * A List with all Process Variables to show the Documents
     */
    private TableViewer _foundsDocumentsTable;

    private MessageArea _messageArea;

    /**
     * @param parent
     */
    public void createPVList(final Composite parent) {
        final Composite pvListMain = new Composite(parent, SWT.NONE);
        pvListMain.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true));
        pvListMain.setLayout(new GridLayout(2, false));

        final Text text = new Text(pvListMain, SWT.SINGLE | SWT.LEAD | SWT.BORDER);
        text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        text.setText("");

        final Button button = new Button(pvListMain, SWT.PUSH);
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
                getProcessVariables().add(new ProcessVariable(text.getText()));
                update();
            }

        });
        createMenu(_pvList);
    }

    public void createDocumentView(final Composite parent) {
        _foundsDocumentsTable = DocumentTableBuilder.createDocumentTable(parent);
        DocumentTableBuilder.makeMenus(_foundsDocumentsTable);
    }

    /**
     * @param processVariables
     */
    public void setProcessVariables(final IProcessVariable[] processVariables) {
        _processVariables = new HashSet<IProcessVariable>(Arrays.asList(processVariables));

    }

    protected Collection<IProcessVariable> getProcessVariables() {
        return _processVariables;
    }

    protected TableViewer getFoundsDocumentsTable() {
        return _foundsDocumentsTable;
    }

    protected MessageArea getMessageArea() {
        return _messageArea;
    }

    /**
     * Creation of the message area. This must be called by each subclass in createPartControl.
     *
     * @param parent
     */
    public void createMessageArea(final Composite parent) {
        _messageArea = new MessageArea(parent);
    }

    /**
     * @throws PersistenceException
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
        final Menu menu = new Menu(viewer.getControl());
        final MenuItem showItem = new MenuItem(menu, SWT.PUSH);
        showItem.addSelectionListener(new RemoveChannelListener(viewer));
        showItem.setText("&Remove");
        showItem.setImage(PlatformUI.getWorkbench().getSharedImages()
                .getImage(ISharedImages.IMG_ELCL_REMOVE));

        final MenuItem saveItem = new MenuItem(menu, SWT.PUSH);
        saveItem.addSelectionListener(new RemoveAllChannelsListener(viewer));
        saveItem.setText("Remove &All");
        saveItem.setImage(PlatformUI.getWorkbench().getSharedImages()
                .getImage(ISharedImages.IMG_ELCL_REMOVEALL));

        viewer.getList().setMenu(menu);
    }

    /**
     * @param processVariables
     * @throws PersistenceException
     */
    protected void callIoNameService(final Collection<IProcessVariable> processVariables) {
        final Thread thread = new Thread() {
            /**
             * {@inheritDoc}
             */
            @Override
            public void run() {
                final IProcessVariable2IONameService pv2IOName = ServiceLocator.getService(IProcessVariable2IONameService.class);
                final Collection<String> pcNames = new HashSet<String>(processVariables.size());
                for (final IProcessVariable iProcessVariable : processVariables) {
                    pcNames.add(iProcessVariable.getName());
                }
                Collection<INode> nodes;
                try {
                    nodes = pv2IOName.getNodes(pcNames).values();
                    final Collection<HierarchyDocument> hierarchyDocuments = new HashSet<HierarchyDocument>();
                    addNodes(hierarchyDocuments, nodes);
                    getFoundsDocumentsTable().getTable().setEnabled(true);
                    getFoundsDocumentsTable().setInput(hierarchyDocuments);
                    getMessageArea().hide();
                } catch (final NodeNotFoundException nnfe) {
                    getFoundsDocumentsTable().getTable().setEnabled(true);
                    switch (nnfe.getState()) {
                        case DCT:
                            getFoundsDocumentsTable().setInput(new String[] { nnfe
                                                                       .getLocalizedMessage() });
                            break;
                        case DeviceDB:
                            getFoundsDocumentsTable().setInput(new String[] { nnfe
                                                                       .getLocalizedMessage() });
                            break;
                    }
                    getMessageArea().hide();
                } catch (final PersistenceException e) {
                    LOG.error(e);
                    getMessageArea().showMessage(SWT.ERROR, "Device Database Error",
                                                 e.getLocalizedMessage());
                    getMessageArea().show();
                    getFoundsDocumentsTable()
                            .setInput(new String[] { "Datenbank nicht erreichbar!" });
                    getFoundsDocumentsTable().getTable().setEnabled(false);
                }
            }

        };
        Display.getCurrent().asyncExec(thread);
    }

    /**
     * @param all
     * @param nodes
     */
    protected void addNodes(final Collection<HierarchyDocument> all, final Collection<INode> nodes) {
        for (final INode iNode : nodes) {
            addDocuments(all, iNode);
            final INode parent = iNode.getParentAsINode();
            addParent(all, parent);
        }
    }

    /**
     * @param all
     * @param iNode
     */
    private void addDocuments(final Collection<HierarchyDocument> all, final INode iNode) {
        final Set<DocumentDBO> documents = iNode.getDocuments();
        if(iNode instanceof INodeWithPrototype) {
            final INodeWithPrototype new_name = (INodeWithPrototype) iNode;
            documents.addAll(new_name.getPrototypeDocuments());
        }
        for (final DocumentDBO document : documents) {
            all.add(new HierarchyDocument(iNode, document));
        }
    }

    /**
     * @param all
     * @param parent
     */
    private void addParent(final Collection<HierarchyDocument> all, final INode parent) {
        if (parent != null) {
            addDocuments(all, parent);
            addParent(all, parent.getParentAsINode());
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
            setProcessVariables();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void widgetSelected(final SelectionEvent e) {
            setProcessVariables();
        }

        /**
         *
         */
        private void setProcessVariables() {
            final Collection<IProcessVariable> processVariables = getProcessVariables();
            processVariables.clear();
            _viewer.setInput(processVariables);
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
        private void remove() {
            final StructuredSelection selection = (StructuredSelection) _viewer.getSelection();
            final List<?> list = selection.toList();
            final Collection<IProcessVariable> processVariables = getProcessVariables();
            processVariables.removeAll(list);
            _viewer.setInput(processVariables);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void widgetSelected(final SelectionEvent e) {
            remove();
        }

    }

    /**
     * Encapsulation of the message area. It is located below the tree view.<br>
     * FIXME (hrickens) This is a copy of the inner class of the AlarmTreeView.
     */
    private static final class MessageArea {
        /**
         * The message area which can display error messages inside the view part.
         */
        private final Composite _messageAreaComposite;

        /**
         * The icon displayed in the message area.
         */
        private final Label _messageAreaIcon;

        /**
         * The message displayed in the message area.
         */
        private final Label _messageAreaMessage;

        /**
         * The description displayed in the message area.
         */
        private final Label _messageAreaDescription;

        public MessageArea(final Composite parent) {
            _messageAreaComposite = new Composite(parent, SWT.NONE);
            final GridData messageAreaLayoutData = new GridData(SWT.FILL, SWT.FILL, true, false, 3,
                                                                1);
            messageAreaLayoutData.exclude = true;
            _messageAreaComposite.setVisible(false);
            _messageAreaComposite.setLayoutData(messageAreaLayoutData);
            _messageAreaComposite.setLayout(new GridLayout(2, false));

            _messageAreaIcon = new Label(_messageAreaComposite, SWT.NONE);
            _messageAreaIcon.setLayoutData(new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false,
                                                        1, 2));
            _messageAreaIcon.setImage(Display.getCurrent().getSystemImage(SWT.ICON_WARNING));

            _messageAreaMessage = new Label(_messageAreaComposite, SWT.WRAP);
            _messageAreaMessage.setText("Test3");
            // Be careful if changing the GridData below! The label will not wrap
            // correctly for some settings.
            _messageAreaMessage.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));

            _messageAreaDescription = new Label(_messageAreaComposite, SWT.WRAP);
            _messageAreaDescription.setText("Test4");
            // Be careful if changing the GridData below! The label will not wrap
            // correctly for some settings.
            _messageAreaDescription
                    .setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
        }

        /**
         * Sets the message displayed in the message area of this view part.
         *
         * @param icon the icon to be displayed next to the message. Must be one of
         *            <code>SWT.ICON_ERROR</code>, <code>SWT.ICON_INFORMATION</code>,
         *            <code>SWT.ICON_WARNING</code>, <code>SWT.ICON_QUESTION</code>.
         * @param message the message.
         * @param description a descriptive text.
         */
        public void showMessage(final int icon, final String message, final String description) {
            _messageAreaIcon.setImage(Display.getCurrent().getSystemImage(icon));
            _messageAreaMessage.setText(message);
            _messageAreaDescription.setText(description);
            _messageAreaComposite.layout();

            show();
        }

        public void show() {
            _messageAreaComposite.setVisible(true);
            ((GridData) _messageAreaComposite.getLayoutData()).exclude = false;
            _messageAreaComposite.getParent().layout();
        }

        /**
         * Hides the message displayed in this view part.
         */
        public void hide() {
            _messageAreaComposite.setVisible(false);
            ((GridData) _messageAreaComposite.getLayoutData()).exclude = true;
            _messageAreaComposite.getParent().layout();
        }
    }
}
