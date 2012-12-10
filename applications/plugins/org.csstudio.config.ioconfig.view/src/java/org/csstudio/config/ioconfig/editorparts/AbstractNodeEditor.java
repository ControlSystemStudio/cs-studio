/*
 * Copyright (c) 2010 Stiftung Deutsches Elektronen-Synchrotron, Member of the Helmholtz
 * Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS. WITHOUT WARRANTY OF ANY
 * KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE IN ANY RESPECT, THE USER ASSUMES
 * THE COST OF ANY NECESSARY SERVICING, REPAIR OR CORRECTION. THIS DISCLAIMER OF WARRANTY
 * CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE. NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER
 * EXCEPT UNDER THIS DISCLAIMER. DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES,
 * ENHANCEMENTS, OR MODIFICATIONS. THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION,
 * MODIFICATION, USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY AT
 * HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
package org.csstudio.config.ioconfig.editorparts;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.csstudio.config.ioconfig.config.view.INodeConfig;
import org.csstudio.config.ioconfig.config.view.helper.ConfigHelper;
import org.csstudio.config.ioconfig.config.view.helper.DocumentationManageView;
import org.csstudio.config.ioconfig.editorinputs.NodeEditorInput;
import org.csstudio.config.ioconfig.model.AbstractNodeSharedImpl;
import org.csstudio.config.ioconfig.model.FacilityDBO;
import org.csstudio.config.ioconfig.model.IDocumentable;
import org.csstudio.config.ioconfig.model.PersistenceException;
import org.csstudio.config.ioconfig.model.pbmodel.GSDFileDBO;
import org.csstudio.config.ioconfig.model.pbmodel.MasterDBO;
import org.csstudio.config.ioconfig.model.pbmodel.SlaveDBO;
import org.csstudio.config.ioconfig.model.tools.UserName;
import org.csstudio.config.ioconfig.view.DeviceDatabaseErrorDialog;
import org.csstudio.config.ioconfig.view.MainView;
import org.csstudio.config.ioconfig.view.ProfiBusTreeView;
import org.csstudio.ui.util.CustomMediaFactory;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.part.EditorPart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The skeletal structure for an Editor to editing a Node.
 *
 * @author hrickens
 * @author $Author: hrickens $
 * @param <T>
 * @since 31.03.2010
 */
public abstract class AbstractNodeEditor<T extends AbstractNodeSharedImpl<?,?>> extends EditorPart implements INodeConfig {

    /**
     *
     * @author hrickens
     * @author $Author: hrickens $
     * @since 03.06.2009
     */
    private final class SaveSelectionListener implements SelectionListener {

        public SaveSelectionListener() {
            // Default Constructor
        }

        @Override
        public void widgetDefaultSelected(@Nonnull final SelectionEvent e) {
            getEditorSite().getActionBars().getGlobalActionHandler("org.eclipse.ui.file.save")
            .run();
            // TODO:need a IProgressMonitor? Yes!
            doSave(null);
        }

        @Override
        public void widgetSelected(@Nonnull final SelectionEvent e) {
            perfromSave();
        }
    }

    /**
     * The warn background color (SWT.COLOR_RED) of the index Spinner.
     */
    protected static final Color WARN_BACKGROUND_COLOR = CustomMediaFactory.getInstance()
    .getColor(CustomMediaFactory.COLOR_RED);

    private static Font _FONT;
    private static GridDataFactory _LABEL_GRID_DATA = GridDataFactory.fillDefaults();
    private static GridDataFactory _TEXT_GRID_DATA = GridDataFactory.fillDefaults().grab(true, false);
    private static final Logger LOG = LoggerFactory.getLogger(AbstractNodeEditor.class);

    private T _node;
    private boolean _new;
    private Composite _parent;
    private Button _cancelButton;
    private Button _saveButton;
    private Spinner _indexSpinner;
    /**
     * The text field for the name of the node.
     */
    private Text _nameText;
    private Text _descText;

    /**
     * The Tabfolder for a config view.
     */
    private TabFolder _tabFolder;
    /**
     * The Tabview to Manage the documentation of Node.
     */
    private DocumentationManageView _documentationManageView;

    private List<GSDFileDBO> _gsdFiles;

    /**
     * A ModifyListener that set the save button enable to store the changes.
     * Works with {@link Text}, {@link Combo} and {@link Spinner}.
     */
    private final ModifyListener _mLSB = new NodeConfigModifyListener<T>(this);

    /**
     * Contain all different events that have change a Value and the status of
     * the change. The status means is the new Value a differnt from the origin
     * Value.
     */
    private final Map<String, Boolean> _saveButtonEvents = new HashMap<String, Boolean>();
    private final Map<HeaderFields, Text> headerFields = new HashMap<HeaderFields, Text>();

    public AbstractNodeEditor() {
        // constructor
    }

    public AbstractNodeEditor(final boolean nevv) {
        // super(parent, SWT.NONE);
        setNew(nevv);
        // ActionFactory.SAVE.create(this.get);
        // setGlobalActionHandler(IWorkbenchActionConstants.,
        // goIntoAction);
        // setBackgroundComposite(headline, node);

    }

    public void cancel() {
        final Text descText = getDescText();
        if (descText != null && descText.getData() != null && descText.getData() instanceof String) {
            setDesc((String) descText.getData());
        }
        final Text headerField = getHeaderField(HeaderFields.KRYK_NO);
        if(headerField!=null) {
            final Object data = headerField.getData();
            String krykNo = "";
            if (data instanceof String) {
                krykNo = (String) data;
            }
            headerField.setText(krykNo);
        }
        if (getNode() != null) {
            getNode().setDirty(false);
        }
    }

    /**
     * (@inheritDoc)
     */
    @Override
    public void createPartControl(@Nonnull final Composite parent) {
        setParent(parent);
        setBackgroundComposite();
        documents();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void dispose() {
        super.dispose();
        setSaveButton(null);
        if (_saveButtonEvents != null) {
            _saveButtonEvents.clear();
        }
        getProfiBusTreeView().removeOpenEditor(this);
    }

    @Override
    public void doSave(@Nullable final IProgressMonitor monitor) {
        // Header
        final Date now = new Date();
        final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
        final Text text = getHeaderField(HeaderFields.VERSION);
        int i = -1;
        try {
            i = Integer.valueOf(text.getText());
        } catch (final NumberFormatException nfe) {
            i = 0;
        }
        getNode().setVersion(i);
        getNode().setUpdatedBy(ConfigHelper.getUserName());
        getNode().setUpdatedOn(now);
        getNode().setDescription(getDesc());
        final Text descText = getDescText();
        if (descText != null) {
            descText.setData(getDesc());
        }

        final Text krykNoField = getHeaderField(HeaderFields.KRYK_NO);
        final String krykNo = krykNoField.getText();
        getNode().setKrykNo(krykNo);
        krykNoField.setData(krykNo);

        // update Header
        getHeaderField(HeaderFields.MODIFIED_ON).setText(df.format(now));
        getHeaderField(HeaderFields.MODIFIED_BY).setText(ConfigHelper.getUserName());
        getHeaderField(HeaderFields.DB_ID).setText("" + getNode().getId());

    }

    /**
     * (@inheritDoc)
     */
    @Override
    public void doSaveAs() {
        // save as not supported
    }

    @Nonnull
    public String getDesc() {
        final Text descText = getDescText();
        return descText == null || descText.getText() == null ? "" : descText.getText();
    }

    /**
     * @return the node which was configured with this View.
     */
    @Override
    @Nonnull
    public IDocumentable getDocumentableObject() {
        return _node;
    }

    @Nonnull
    public Text getHeaderField(@Nonnull final HeaderFields field) {
        return headerFields.get(field);
    }

    /**
     * A ModifyListener that set the save button enable to store the changes.
     * Works wiht {@link Text}, {@link Combo} and {@link Spinner}.
     *
     * @return the ModifyListener.
     */
    @Nonnull
    public final ModifyListener getMLSB() {
        return _mLSB;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public T getNode() {
        return _node;
    }

    /**
     * (@inheritDoc)
     */
    @SuppressWarnings("unchecked")
    @Override
    public void init(@Nonnull final IEditorSite site,
                     @Nonnull final IEditorInput input) throws PartInitException {
        setSite(site);
        setInput(input);
        _node = (T) ((NodeEditorInput) input).getNode();
        setNew( ((NodeEditorInput) input).isNew());
        setPartName(_node.getName());
        getProfiBusTreeView().setOpenEditor(this);
    }

    /**
     * @return only true when the node has on or more changes.
     */
    @Override
    public final boolean isDirty() {
        final boolean dirty = getNode().isDirty();
        return dirty;
    }

    @Override
    public boolean isSaveAsAllowed() {
        return false; // save as not supported
    }

    public void perfromClose() {
        final IViewSite site = getProfiBusTreeView().getSite();
        site.getPage().closeEditor(this, true);
    }

    public void setDesc(@CheckForNull final String desc) {
        final Text descText = getDescText();
        if (descText != null) {
            final String temp = desc != null ? desc : "";
            descText.setText(temp);
        }
    }

    @Override
    public void setFocus() {
        // nothing to do;
    }

    @Nonnull
    public Text setHeaderField(@Nonnull final HeaderFields field, @Nullable final Text text) {
        return headerFields.put(field, text);
    }

    public final void setName(@Nonnull final String name) {
        final TreeItem treeItem = getProfiBusTreeView().getTreeViewer().getTree().getSelection()[0];
        treeItem.setText(name);
        _nameText.setText(name);
    }

    /**
     * Give a change event and his save status.<br>
     * Have one or more events the status unsaved then the Save Button was set
     * to enable otherwise to disable.
     *
     * @param event
     *            the Save Event Id.
     * @param enabled
     *            the Save Enable status for the event.
     */
    @Override
    public final void setSavebuttonEnabled(@Nullable final String event, final boolean enabled) {
        if (event != null) {
            _saveButtonEvents.put(event, enabled);
        }
        final boolean enab = !getNode().isPersistent()
                || _saveButtonEvents.containsValue(Boolean.valueOf(true));
        final Button saveButton = getSaveButton();
        if (saveButton != null) {
            final boolean changed = enab != saveButton.isEnabled();
            getNode().setDirty(enab);
            if (changed) {
                saveButton.setEnabled(enab);
                firePropertyChange(PROP_DIRTY);
            }
        }
    }

    /**
     * Clear the dirty bit, delete all saveButton events and set the Button
     * disable.
     */
    @Override
    public final void setSaveButtonSaved() {
        _saveButtonEvents.clear();
        final Button saveButton = getSaveButton();
        if(saveButton!=null) {
            saveButton.setEnabled(false);
        }
        if (getNode() != null) {
            getNode().setDirty(false);
        }
        firePropertyChange(PROP_DIRTY);
    }

    public final void setSaveButtonSelectionListener(@Nonnull final SelectionListener selectionListener) {
        final Button saveButton = getSaveButton();
        if(saveButton!=null) {
            saveButton.addSelectionListener(selectionListener);
        }
    }

    @Nonnull
    private Group makeBackgroundGroup(final int columnNum) {
        final Group header = new Group(getParent(), SWT.H_SCROLL);
        header.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, columnNum, 1));
        header.setLayout(new GridLayout(7, false));
        header.setText("General Information");
        header.setTabList(new Control[0]);
        return header;
    }

    /**
     * set the documents and Icon tab item.
     */
    protected void documents() {
        final String head = "Documents";
        final TabItem item = new TabItem(getTabFolder(), SWT.NO_SCROLL);
        item.setText(head);
        // TODO: refactor DocumentationManageView
        _documentationManageView = new DocumentationManageView(getTabFolder(), SWT.NONE, this);
        item.setControl(_documentationManageView);
        getTabFolder().addSelectionListener(new SelectionListener() {

            @Override
            public void widgetDefaultSelected(@Nonnull final SelectionEvent e) {
                docTabSelectionAction(e);
            }

            @Override
            public void widgetSelected(@Nonnull final SelectionEvent e) {
                docTabSelectionAction(e);
            }

            private void docTabSelectionAction(@Nonnull final SelectionEvent e) {
                if (e.item.equals(item)) {
                    final DocumentationManageView documentationManageView = getDocumentationManageView();
                    if (documentationManageView != null) {
                        documentationManageView.onActivate();
                    }
                }
            }
        });
    }

    /**
     * @return the cancelButton
     */
    @CheckForNull
    protected final Button getCancelButton() {
        return _cancelButton;
    }

    @CheckForNull
    protected Text getDescText() {
        return _descText;
    }

    /**
     *
     * @return The Documentation Manage View.
     */
    @Nonnull
    protected final DocumentationManageView getDocumentationManageView() {
        if(_documentationManageView == null) {
            documents();
        }
        return _documentationManageView;
    }

    @Nonnull
    protected List<GSDFileDBO> getGsdFiles() {
        if(_gsdFiles==null) {
            _gsdFiles=new ArrayList<GSDFileDBO>();
        }
        return _gsdFiles;
    }

    /**
     * @return the indexSpinner
     */
    @CheckForNull
    protected final Spinner getIndexSpinner() {
        return _indexSpinner;
    }

    /**
     *
     * @return the Node Name description field.
     */
    @CheckForNull
    protected final Text getNameWidget() {
        return _nameText;
    }

    /**
     * @param head
     *            The Tab text.
     * @param rows
     *            row number for the grid.
     * @return The Composite of the generated TabItem.
     */
    @Nonnull
    protected final Composite getNewTabItem(@Nonnull final String head, final int rows) {
        final TabItem item = new TabItem(getTabFolder(), SWT.NONE, 0);
        item.setText(head);
        final Composite comp = new Composite(getTabFolder(), SWT.NONE);
        comp.setLayout(new GridLayout(rows, false));
        item.setControl(comp);

        comp.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
        return comp;
    }

    /**
     * @return the parent
     */
    @CheckForNull
    protected Composite getParent() {
        return _parent;
    }

    /**
     * @return the profiBusTreeView
     */
    @Nonnull
    protected final ProfiBusTreeView getProfiBusTreeView() {
        return ((MainView) getEditorSite().getPage().findView(MainView.ID)).getProfiBusTreeView();
    }

    @CheckForNull
    protected Button getSaveButton() {
        return _saveButton;
    }

    @CheckForNull
    protected Shell getShell() {
        if (this.getEditorSite() != null) {
            return this.getEditorSite().getShell();
        }
        return null;
    }

    /**
     * @return the tabFolder
     */
    @Nonnull
    protected final TabFolder getTabFolder() {
        assert _tabFolder != null : "Tab folder not avaible!";
        return _tabFolder;
    }

    protected boolean isNew() {
        return _new;
    }

    protected void makeDescGroup(@Nonnull final Composite comp, final int hSize) {
        final Group gDesc = new Group(comp, SWT.NONE);
        gDesc.setText("Description: ");
        final GridDataFactory gdf = GridDataFactory.fillDefaults().grab(true, true).span(hSize, 1)
        .minSize(200, 200);
        gDesc.setLayoutData(gdf.create());
        gDesc.setLayout(new GridLayout(2, false));

        final Label shortDescLabel = new Label(gDesc, SWT.NONE);
        shortDescLabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
        shortDescLabel.setText("Short Desc:");

        final Text shortDescText = new Text(gDesc, SWT.BORDER | SWT.SINGLE | SWT.READ_ONLY);
        shortDescText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
        shortDescText.setEditable(false);
        final Text descText = new Text(gDesc, SWT.BORDER | SWT.MULTI);
        descText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
        descText.setEditable(true);
        descText.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(@Nonnull final ModifyEvent e) {
                final String string = getShortDesc(descText.getText());
                shortDescText.setText(string);
            }
        });
        setText(descText, getNode().getDescription(), 255);
        setDescWidget(descText);
        gDesc.setTabList(new Control[] {descText});
    }

    /**
     * Generate a new Node on his parent with a Name, creator and creation date.
     */
    protected boolean newNode() {
        return newNode("");
    }

    @SuppressWarnings("unchecked")
    protected boolean newNode(@Nullable final String nameOffer) {
        final String nodeType = getNode().getClass().getSimpleName();
        final String dialogTitle = "Create new " + nodeType;
        final String dialogMessage = "Enter the name of the "+ nodeType;
        final InputDialog id = new InputDialog(getShell(), dialogTitle, dialogMessage, nameOffer, null);
        id.setBlockOnOpen(true);
        if (id.open() == Window.OK) {
            getNode().setName(id.getValue());
            final String name = getUserName();
            getNode().setCreationData(name, new Date());
            getNode().setVersion(-2);
            id.close();

            final Object obj = ((StructuredSelection) getProfiBusTreeView().getTreeViewer()
                    .getSelection()).getFirstElement();

            if (getNode() instanceof FacilityDBO || obj == null) {
                getProfiBusTreeView().getTreeViewer().setInput(getNode());
            } else if (obj instanceof AbstractNodeSharedImpl) {
                if (getNode().getParent() == null) {
                    try {
                        @SuppressWarnings("rawtypes")
                        final AbstractNodeSharedImpl nodeParent = (AbstractNodeSharedImpl) obj;
                        getNode().moveSortIndex(nodeParent.getfirstFreeStationAddress());
                        nodeParent.addChild(getNode());
                    } catch (final PersistenceException e) {
                        final String msg = "Can't create node! Database error.";
                        LOG.error(msg, e);
                        DeviceDatabaseErrorDialog.open(getShell(), msg, e);
                    }
                }
            }
            return true;
        }
        return false;
    }

    /**
     * @param exception
     *            A exception to show in a Dialog,
     */
    protected void openErrorDialog(@Nonnull final Exception exception) {
        final String message = "The Settings not saved!\n\nDataBase Failure:";
        LOG.error(message, exception);
        DeviceDatabaseErrorDialog.open(getShell(), message, exception);
    }

    /**
     * @param exception
     *            A exception to show in a Dialog,
     */
    protected void openErrorDialog(@Nonnull final Exception exception, @Nullable final ProfiBusTreeView busTreeView) {
        final String message = "The Settings not saved!\n\nDataBase Failure:";
        LOG.error(message, exception);
        DeviceDatabaseErrorDialog.open(getShell(), message, exception, busTreeView);
    }

    protected void perfromSave() {
        final IViewSite site = getProfiBusTreeView().getSite();
        final IHandlerService handlerService = (IHandlerService) site
                .getService(IHandlerService.class);
        try {
            handlerService.executeCommand("org.eclipse.ui.file.save", null);
        } catch (final Exception ex) {
            LOG.error("Can't Save!", ex);
        }
    }

    protected final void selecttTabFolder(final int index) {
        if (_tabFolder != null) {
            _tabFolder.setSelection(index);
        }
    }

    /**
     *
     * This method generate the Background of an NodeConfig this a 3 Parts.<br>
     * 1. The Header with description line the 3 information field for:<br>
     * 1.1. Modified by <br>
     * 1.2. Modified on <br>
     * 1.3. GSD-Version <br>
     * 2. The Body as an empty TabFolder. <br>
     * 3. The Footer with the Save and Cancel Buttons. <br>
     *
     * @param headline
     *            The description line of the Header
     * @param node
     *            The node that was configured.
     */
    protected void setBackgroundComposite() {
        final Composite parent = getParent();
        if(parent == null) {
            return;
        }
        final int columnNum = 5;
        final GridDataFactory labelGridData = GridDataFactory.fillDefaults();
        parent.setLayout(new GridLayout(columnNum, true));
        final Group header = buildBackgroundHeader(parent, columnNum);

        // build Body
        setTabFolder(new TabFolder(parent, SWT.H_SCROLL | SWT.V_SCROLL));
        getTabFolder().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 5, 1));
        getTabFolder().setBackgroundMode(SWT.INHERIT_DEFAULT);

        buildBackgroundFooter(parent, labelGridData);

        header.setTabList(new Control[0]);
    }

    @Nonnull
    private Group buildBackgroundHeader(@Nonnull final Composite parent, final int columnNum) {
        final T node = getNode();
        final Label headlineLabel = new Label(parent, SWT.NONE);
        if (_FONT == null) {
            _FONT = headlineLabel.getFont();
        }
        headlineLabel.setText("Profibus " + node.getClass().getSimpleName() + " Configuration");
        headlineLabel.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, false, columnNum, 1));

        // - Header
        final Group header = makeBackgroundGroup(columnNum);
        // -- Create by
        getNewLabel(header, "Created by:");
        /** The description field with the name of the User that make changes. */
        String temp = "";
        if (isNew()) {
            node.setCreatedBy(getUserName());
        } else if (node.getCreatedBy() != null) {
            temp = node.getCreatedBy();
        }
        final Text creatByText = getNewText(header, temp);
        parent.setData("creatBy", creatByText);
        temp = "";
        // -- Created on
        getNewLabel(header, "Created on:");
        /** The description field with the name of the Date of the last change. */

        Date now = new Date();
        if (isNew()) {
            temp = ConfigHelper.getSimpleDateFormat().format(now);
            node.setCreatedOn(now);
        } else if (node.getCreatedOn() != null) {
            now = node.getCreatedOn();
            temp = now.toString();
        }

        final Text creatOnText = getNewText(header, temp);
        parent.setData("date", now);
        parent.setData("creatOn", creatOnText);
        // -- Version
        getNewLabel(header, "Version:");
        /** The description field with the Version from GSD File. */
        final Text versionText = getNewText(header, node.getVersion() + "");
        setHeaderField(HeaderFields.VERSION, versionText);

        final Label iconButton = new Label(header, SWT.BORDER);
        iconButton.setLayoutData(GridDataFactory.swtDefaults().span(1, 3).minSize(40, 40).create());
        iconButton.setImage(ConfigHelper.getImageFromNode(node, 30, 60));
        iconButton.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseUp(@Nullable final MouseEvent e) {
                final IconChooserDialog chooseIconDialog = new IconChooserDialog(AbstractNodeEditor.this
                                                                                 .getShell());

                chooseIconDialog.setBlockOnOpen(true);
                if (chooseIconDialog.open() == 0) {
                    node.setIcon(chooseIconDialog.getImage());
                }

            }

        });

        getNewLabel(header, "Modified by:");
        /** The description field with the name of the User that make changes. */
        temp = node.getUpdatedBy() != null?node.getUpdatedBy():"";
        final Text modifiedBy = getNewText(header, temp);
        setHeaderField(HeaderFields.MODIFIED_BY, modifiedBy);
        // -- Modifierd on
        getNewLabel(header, "Modified on:");
        /** The description field with the name of the Date of the last change. */
        temp = node.getUpdatedOn() != null?node.getUpdatedOn().toString():"";
        final Text modifiedOn = getNewText(header, temp);
        setHeaderField(HeaderFields.MODIFIED_ON, modifiedOn);

        getNewLabel(header, "DataBase ID:");
        /** The description field with the Version from GSD File. */

        final Text dbIdText = getNewText(header, node.getId() + "");
        setHeaderField(HeaderFields.DB_ID, dbIdText);

        getNewLabel(header, "Kryk No:");
        final Text krykNoText = new Text(header, SWT.SINGLE | SWT.LEAD | SWT.BORDER);
        krykNoText.setLayoutData(_TEXT_GRID_DATA.create());
        krykNoText.setEditable(true);
        setText(krykNoText, node.getKrykNoNH(), 20);
        setHeaderField(HeaderFields.KRYK_NO, krykNoText);

        /**
         * GSD Version. The description field with the Version from GSD File.
         * Only Master and Slave have a GSD File
         */

        if (node instanceof MasterDBO || node instanceof SlaveDBO) {
            getNewLabel(header, "Version from GSD:");
            temp = node.getUpdatedOn() != null?node.getVersion() + "":"";
            final Text version = getNewText(header, temp);
            parent.setData("gsdVersion", version);
        }
        return header;
    }

    @SuppressWarnings({ "unused", "rawtypes", "unchecked" })
    private void buildBackgroundFooter(@Nonnull final Composite parent,
                                       @Nonnull final GridDataFactory labelGridData) {
        new Label(parent, SWT.NONE);
        setSaveButton(new Button(parent, SWT.PUSH));
        final Button saveButton = getSaveButton();
        if (saveButton != null) {
            if (isNew()) {
                saveButton.setText("Create");
            } else {
                saveButton.setText("&Save");
            }
            setSavebuttonEnabled(null, !isNew());
            saveButton.setLayoutData(labelGridData.create());
            setSaveButtonSelectionListener(new SaveSelectionListener());
        }
        new Label(parent, SWT.NONE);
        setCancelButton(new Button(parent, SWT.PUSH));
        final Button cancelButton = getCancelButton();
        if (cancelButton != null) {
            cancelButton.setText("&Cancel");
            cancelButton.setLayoutData(labelGridData.create());
            cancelButton.addSelectionListener(new CancelSelectionListener(this));
        }
        new Label(parent, SWT.NONE);

    }

    /**
     * @param cancelButton
     *            the cancelButton to set
     */
    protected final void setCancelButton(@Nullable final Button cancelButton) {
        _cancelButton = cancelButton;
    }

    protected void setDescText(@Nullable final Text descText) {
        _descText = descText;
    }

    /**
     *
     * @param descText
     *            set the Node Name description field.
     */
    protected final void setDescWidget(@Nonnull final Text descText) {
        descText.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(@Nonnull final KeyEvent e) {
                if (e.keyCode == SWT.CR || e.keyCode == SWT.KEYPAD_CR) {
                    if (e.stateMask != SWT.MOD1) {
                        e.doit = false;
                        final Button saveButton = getSaveButton();
                        if(saveButton!=null) {
                            saveButton.setFocus();
                        }
                    }
                }
            }
        });
        setDescText(descText);
    }

    @CheckForNull
    protected void setGsdFiles(@Nullable final List<GSDFileDBO> gsdFiles) {
        _gsdFiles = gsdFiles;
    }

    /**
     * @param indexSpinner
     *            the indexSpinner to set
     */
    protected final void setIndexSpinner(@Nullable final Spinner indexSpinner) {
        _indexSpinner = indexSpinner;
    }

    /**
     *
     * @param nameText
     *            set the Node Name description field.
     */
    protected final void setNameWidget(@Nonnull final Text nameText) {
        _nameText = nameText;
        _nameText.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(@Nonnull final KeyEvent e) {
                final Text descText = getDescText();
                if ( (e.keyCode == SWT.CR || e.keyCode == SWT.KEYPAD_CR) && descText != null) {
                    descText.setFocus();
                }
            }
        });
    }

    protected void setNew(final boolean nevv) {
        _new = nevv;
    }

    protected void setParent(@Nonnull final Composite parent) {
        _parent = parent;
    }

    protected void setSaveButton(@Nullable final Button saveButton) {
        _saveButton = saveButton;
    }

    /**
     * @param tabFolder
     *            the tabFolder to set
     */
    protected final void setTabFolder(@Nullable final TabFolder tabFolder) {
        _tabFolder = tabFolder;
    }

    /**
     * Save or Update the Node to the Data Base.
     */
    final void save() {
        final ProgressMonitorDialog dia = new ProgressMonitorDialog(getShell());
        dia.open();
        final IProgressMonitor progressMonitor = dia.getProgressMonitor();
        try {
            final T node = getNode();
            progressMonitor.beginTask("Save " + node, 3);
            final AbstractNodeSharedImpl<?,?> parent = (AbstractNodeSharedImpl<?, ?>) node.getParent();
            progressMonitor.worked(1);
            try {
                if (parent == null) {
                    node.localSave();
                } else {
                    parent.localSave();
                }
            } catch (final PersistenceException e) {
                openErrorDialog(e);
            }
            progressMonitor.worked(2);
            setSaveButtonSaved();
            final Button saveButton = getSaveButton();
            if (saveButton != null) {
                saveButton.setText("&Save");
            }

            getHeaderField(HeaderFields.DB_ID).setText(node.getId() + "");
            progressMonitor.worked(3);

            if (isNew() && !node.isRootNode()) {
                getProfiBusTreeView().refresh(parent);
                getProfiBusTreeView().getTreeViewer().expandToLevel(node,
                                                                    AbstractTreeViewer.ALL_LEVELS);
            } else if (isNew() && node.isRootNode()) {
                getProfiBusTreeView().addFacility((FacilityDBO) node);
                getProfiBusTreeView().refresh(node);
                getProfiBusTreeView().refresh();
            } else {
                // refresh the View
                getProfiBusTreeView().refresh(node);
            }
            setNew(false);
        } finally {
            progressMonitor.done();
            dia.close();
        }
    }

    /**
     * Select a text to a {@link Combo} and store the index into Data as origin
     * value.
     *
     * @param comboField
     *            The Combo field to set the index
     * @param value
     *            The value was select.
     */
    final void setCombo(@Nonnull final Combo comboField, @CheckForNull final String value) {
        final String tmp = value == null?"":value;
        final int index = comboField.indexOf(tmp);
        comboField.select(index);
        comboField.setData(index);
        comboField.addModifyListener(getMLSB());
    }

    /**
     * Set a int as text to a {@link Text} and store the text into Data as
     * origin value.
     *
     * @param textField
     *            The Text field to set the text
     * @param textValue
     *            The int was set.
     */
    final void setText(@Nonnull final Text textField, final int textValue, final int limit) {
        setText(textField, Integer.toString(textValue), limit);
    }

    /**
     * Set a text to a {@link Text} and store the text into Data as origin
     * value.
     *
     * @param textField
     *            The Text-field to set the text
     * @param text
     *            The text was set.
     * @param limit
     *            The maximum text length for the Text-field. (Text.LIMIT for
     *            maximum)
     */
    final void setText(@Nonnull final Text textField, @Nullable final String text, final int limit) {
        final String tmp = text==null?"":text;
        textField.setText(tmp);
        textField.setData(tmp);
        textField.setTextLimit(limit);
        textField.setToolTipText("");
        textField.addModifyListener(getMLSB());
    }

    @Nonnull
    public static String getUserName() {
        return UserName.getUserName();
    }

    @Nonnull
    private static Label getNewLabel(@Nonnull final Composite header, @Nonnull final String string) {
        final Label newLabel = new Label(header, SWT.NONE);
        newLabel.setLayoutData(_LABEL_GRID_DATA.create());
        newLabel.setText(string);
        return newLabel;
    }

    @Nonnull
    private static Text getNewText(@Nonnull final Composite header, @Nonnull final String text) {
        final Text newText = new Text(header, SWT.NONE);
        newText.setLayoutData(_TEXT_GRID_DATA.create());
        newText.setEditable(false);
        newText.setText(text);
        return newText;
    }

    protected static void resetSelection(@Nonnull final Combo combo) {
        final Integer index = (Integer) combo.getData();
        if (index != null) {
            combo.select(index);
        }

    }

    protected static void resetString(@Nonnull final Text textField) {
        final String text = (String) textField.getData();
        if (text != null) {
            textField.setText(text);
        }
    }

    @Nonnull
	protected String getShortDesc(@Nonnull final String descText) {
		// guard: do not process null or empty
		if ((descText == null) || (descText.isEmpty())) {
			return "";
		}
		
		final String[] split = descText.split("[\r\n]");
		// take care if splitting fails
		if ((split == null) || (split.length == 0)) {
			return "";
		}
		
		String shortDesc = split[0];
		if (split.length > 0) {
			if (shortDesc.length() > 40) {
				shortDesc = shortDesc.substring(0, 40);
			} else {
				shortDesc = split[0];
			}
		}
		return shortDesc;
	}

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        final T node = getNode();
        sb.append(getNameWidget()).append(" \r\n");
        if(node !=null) {
            sb.append(node).append(" \r\n");
            sb.append("ID: ").append(node.getId()).append(" \r\n");
        }

        if(isDirty()) {
            sb.append("(*)");
        }
        return sb.toString();
    }

}
