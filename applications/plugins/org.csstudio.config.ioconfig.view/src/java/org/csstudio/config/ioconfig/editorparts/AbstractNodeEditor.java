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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.csstudio.auth.security.SecurityFacade;
import org.csstudio.auth.security.User;
import org.csstudio.config.ioconfig.config.view.INodeConfig;
import org.csstudio.config.ioconfig.config.view.helper.ConfigHelper;
import org.csstudio.config.ioconfig.config.view.helper.DocumentationManageView;
import org.csstudio.config.ioconfig.editorinputs.NodeEditorInput;
import org.csstudio.config.ioconfig.model.AbstractNodeDBO;
import org.csstudio.config.ioconfig.model.FacilityDBO;
import org.csstudio.config.ioconfig.model.IDocumentable;
import org.csstudio.config.ioconfig.model.PersistenceException;
import org.csstudio.config.ioconfig.model.pbmodel.GSDFileDBO;
import org.csstudio.config.ioconfig.model.pbmodel.MasterDBO;
import org.csstudio.config.ioconfig.model.pbmodel.SlaveDBO;
import org.csstudio.config.ioconfig.view.DeviceDatabaseErrorDialog;
import org.csstudio.config.ioconfig.view.MainView;
import org.csstudio.config.ioconfig.view.ProfiBusTreeView;
import org.csstudio.platform.ui.util.CustomMediaFactory;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
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
import org.eclipse.ui.IEditorPart;
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
 * @since 31.03.2010
 */
public abstract class AbstractNodeEditor extends EditorPart implements INodeConfig {
    
    /**
     * 
     * @author hrickens
     * @author $Author: hrickens $
     * @since 03.06.2009
     */
    private final class CancelSelectionListener implements SelectionListener {
        
        public CancelSelectionListener() {
            // Default Constructor
        }
        
        /**
         *
         */
        private void doCancle() {
            // if (getNode().isPersistent()) {
            if (!isNew()) {
                cancel();
                DocumentationManageView documentationManageView = getDocumentationManageView();
                if (documentationManageView != null) {
                    documentationManageView.cancel();
                }
                setSaveButtonSaved();
            } else {
                AbstractNodeDBO<?,?> node = getNode();
                final boolean openQuestion = MessageDialog
                        .openQuestion(getShell(), "Cancel", "You dispose this "
                                + node.getClass().getSimpleName() + "?");
                if (openQuestion) {
                    setSaveButtonSaved();
                    // hrickens (01.10.2010): Beim Cancel einer neuen Facility
                    // macht nur Perfrom close Sinn.
                    AbstractNodeDBO parent = node.getParent();
                    if (parent != null) {
                        parent.removeChild(node);
                    }
                    perfromClose();
                }
            }
        }
        
        @Override
        public void widgetDefaultSelected(@Nonnull final SelectionEvent e) {
            doCancle();
        }
        
        @Override
        public void widgetSelected(@Nonnull final SelectionEvent e) {
            doCancle();
        }
    }
    
    /**
     * A ModifyListener that set the save button enable to store the changes.
     * Works with {@link Text}, {@link Combo} and {@link Spinner}.
     * 
     * @author hrickens
     * @author $Author: hrickens $
     * @since 03.06.2009
     */
    private final class NodeConfigModifyListener implements ModifyListener {
        
        public NodeConfigModifyListener() {
            // Default Constructor.
        }
        
        @Override
        public void modifyText(@Nonnull final ModifyEvent e) {
            if (e.widget instanceof Text) {
                final Text text = (Text) e.widget;
                setSavebuttonEnabled("ModifyListenerText:" + text.hashCode(), !text.getText()
                        .equals(text.getData()));
            } else if (e.widget instanceof Combo) {
                final Combo combo = (Combo) e.widget;
                if (combo.getData() instanceof Integer) {
                    final Integer value = (Integer) combo.getData();
                    if (value == null) {
                        setSavebuttonEnabled("ModifyListenerCombo" + combo.hashCode(), false);
                    } else {
                        setSavebuttonEnabled("ModifyListenerCombo" + combo.hashCode(),
                                             value != combo.getSelectionIndex());
                    }
                }
            } else if (e.widget instanceof Spinner) {
                final Spinner spinner = (Spinner) e.widget;
                try {
                    setSavebuttonEnabled("ModifyListenerCombo" + spinner.hashCode(),
                                         (Short) spinner.getData() != spinner.getSelection());
                } catch (final ClassCastException cce) {
                    LOG.error(spinner.toString(), cce);
                }
            }
        }
    }
    
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
    
    private static Font _FONT;
    
    private static GridDataFactory _LABEL_GRID_DATA = GridDataFactory.fillDefaults();
    
    private static GridDataFactory _TEXT_GRID_DATA = GridDataFactory.fillDefaults().grab(true,
                                                                                         false);

    private static final Logger LOG = LoggerFactory.getLogger(AbstractNodeEditor.class);
    
    /**
    /**
     * The warn background color (SWT.COLOR_RED) of the index Spinner.
     */
    protected static final Color WARN_BACKGROUND_COLOR = CustomMediaFactory.getInstance()
            .getColor(CustomMediaFactory.COLOR_RED);
    
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
    
    protected static void resetSelection(@Nonnull Combo combo) {
        Integer index = (Integer) combo.getData();
        if (index != null) {
            combo.select(index);
        }
        
    }
    
    protected static void resetString(@Nonnull Text textField) {
        String text = (String) textField.getData();
        if (text != null) {
            textField.setText(text);
        }
    }
    
    /**
     * The Button to cancel Changes.
     */
    private Button _cancelButton;
    
    private Text _descText;
    
    /**
     * The Tabview to Manage the documentation of Node.
     */
    private DocumentationManageView _documentationManageView;
    
//    private GSDFileDBO _gsdFile;
    
    private List<GSDFileDBO> _gsdFiles;
    
    // ---------------------------------------
    // Node Editor View
    
    /**
     * The Spinner for the channel index.
     */
    private Spinner _indexSpinner;
    
    /**
     * A ModifyListener that set the save button enable to store the changes.
     * Works with {@link Text}, {@link Combo} and {@link Spinner}.
     */
    private final ModifyListener _mLSB = new NodeConfigModifyListener();
    
    /**
     * The text field for the name of the node.
     */
    private Text _nameText;
    
    /**
     * if true an new SubNet a created also modified _slave.
     */
    private boolean _new = false;
    
    // ---------------------------------------
    // Node Editor View
    
    private AbstractNodeDBO _node;
    
    private Composite _parent;
    
    // ---------------------------------------
    // Node Editor View
    
    /**
     * The Button to save Changes.
     */
    private Button _saveButton;
    
    /**
     * Contain all different events that have change a Value and the status of
     * the change. The status means is the new Value a differnt from the origin
     * Value.
     */
    private final HashMap<String, Boolean> _saveButtonEvents = new HashMap<String, Boolean>();
    
    /**
     * The Tabfolder for a config view.
     */
    private TabFolder _tabFolder;
    
    private final Map<HeaderFields, Text> headerFields = new HashMap<HeaderFields, Text>();
    
    public AbstractNodeEditor() {
        // constructor
    }
    
    // ---------------------------------------
    // Node Editor View
    
    public AbstractNodeEditor(final boolean nevv) {
        // super(parent, SWT.NONE);
        setNew(nevv);
        // ActionFactory.SAVE.create(this.get);
        // setGlobalActionHandler(IWorkbenchActionConstants.,
        // goIntoAction);
        // setBackgroundComposite(headline, node);
        
    }
    
    public void cancel() {
        Text descText = getDescText();
        if (descText != null && descText.getData() != null && descText.getData() instanceof String) {
            setDesc((String) descText.getData());
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
     * 
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
            
            private void docTabSelectionAction(@Nonnull final SelectionEvent e) {
                if (e.item.equals(item)) {
                    DocumentationManageView documentationManageView = getDocumentationManageView();
                    if (documentationManageView != null) {
                        documentationManageView.onActivate();
                    }
                }
            }
            
            @Override
            public void widgetDefaultSelected(@Nonnull final SelectionEvent e) {
                docTabSelectionAction(e);
            }
            
            @Override
            public void widgetSelected(@Nonnull final SelectionEvent e) {
                docTabSelectionAction(e);
            }
            
        });
        
    }
    
    // ---------------------------------------
    // Node Editor View
    
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
        Text descText = getDescText();
        if (descText != null) {
            descText.setData(getDesc());
        }
        
        // update Header
        getHeaderField(HeaderFields.MODIFIED_ON).setText(df.format(now));
        getHeaderField(HeaderFields.MODIFIED_BY).setText(ConfigHelper.getUserName());
        getHeaderField(HeaderFields.DB_ID).setText("" + getNode().getId());
        
        // df = null;
        // now = null;
    }
    
    /**
     * (@inheritDoc)
     */
    @Override
    public void doSaveAs() {
        // save as not supported
    }
    
    /**
     * @return the cancelButton
     */
    @CheckForNull
    protected final Button getCancelButton() {
        return _cancelButton;
    }
    
    @Nonnull
    public String getDesc() {
        Text descText = getDescText();
        if (descText == null || descText.getText() == null) {
            return "";
        }
        return descText.getText();
    }
    
    // ---------------------------------------
    // Node Editor View
    
    @CheckForNull
    protected Text getDescText() {
        return _descText;
    }
    
    /**
     * 
     * @return the node which was configured with this View.
     */
    @Override
    @Nonnull
    public IDocumentable getDocumentableObject() {
        return _node;
    }
    
    /**
     * 
     * @return The Documentation Manage View.
     */
    @CheckForNull
    protected final DocumentationManageView getDocumentationManageView() {
        return _documentationManageView;
    }
    
    @CheckForNull
    protected List<GSDFileDBO> getGsdFiles() {
        return _gsdFiles;
    }
    
    @Nonnull
    public Text getHeaderField(@Nonnull final HeaderFields field) {
        return headerFields.get(field);
    }
    
    /**
     * @return the indexSpinner
     */
    @CheckForNull
    protected final Spinner getIndexSpinner() {
        return _indexSpinner;
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
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public AbstractNodeDBO getNode() {
        return _node;
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
    
    /**
     * @return
     */
    @Nonnull
    private String getUserName() {
        final User user = SecurityFacade.getInstance().getCurrentUser();
        String name = "Unknown";
        if (user != null) {
            name = user.getUsername();
        }
        return name;
    }
    
    /**
     * (@inheritDoc)
     */
    @Override
    public void init(@Nonnull final IEditorSite site, 
                     @Nonnull final IEditorInput input) throws PartInitException {
        setSite(site);
        setInput(input);
        _node = ((NodeEditorInput) input).getNode();
        setNew( ((NodeEditorInput) input).isNew());
        setPartName(_node.getName());
        getProfiBusTreeView().setOpenEditor(this);
    }
    
    /**
     * 
     * @return only true when the node has on or more changes.
     */
    @Override
    public final boolean isDirty() {
        if (getNode() != null) {
            return getNode().isDirty();
        }
        return true;
    }
    
    protected boolean isNew() {
        return _new;
    }
    
    @Override
    public boolean isSaveAsAllowed() {
        return false; // save as not supported
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
                final String text = descText.getText();
                String string = "";
                if (text != null) {
                    final String[] split = text.split("[\r\n]");
                    if (split.length > 0) {
                        if (string.length() > 40) {
                            string = string.substring(0, 40);
                        } else {
                            string = split[0];
                        }
                    }
                }
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
    
    protected boolean newNode(@Nullable final String nameOffer) {
        
        final String nodeType = getNode().getClass().getSimpleName();
        
        final InputDialog id = new InputDialog(getShell(),
                                               "Create new " + nodeType,
                                               "Enter the name of the " + nodeType,
                                               nameOffer,
                                               null);
        id.setBlockOnOpen(true);
        try {
            if (id.open() == Window.OK) {
                getNode().setName(id.getValue());
                String name = getUserName();
                getNode().setCreatedBy(name);
                getNode().setCreatedOn(new Date());
                getNode().setVersion(-2);
                id.close();
                
                final Object obj = ((StructuredSelection) getProfiBusTreeView().getTreeViewer()
                        .getSelection()).getFirstElement();
                
                if (getNode() instanceof FacilityDBO || obj == null) {
                    getProfiBusTreeView().getTreeViewer().setInput(getNode());
                } else if (obj instanceof AbstractNodeDBO) {
                    if (getNode().getParent() == null) {
                        final AbstractNodeDBO nodeParent = (AbstractNodeDBO) obj;
                        
                        getNode()
                                .moveSortIndex(nodeParent.getfirstFreeStationAddress(AbstractNodeDBO.MAX_STATION_ADDRESS));
                        nodeParent.addChild(getNode());
                    }
                }
                return true;
            }
        } catch (PersistenceException e) {
            LOG.error("Can't create node! Database error.",e);
            DeviceDatabaseErrorDialog.open(null, "Can't create node! Database error.", e);
        }
        return false;
    }
    
    /**
     * @param exception
     *            A exception to show in a Dialog,
     */
    protected void openErrorDialog(@Nonnull final Exception exception) {
        LOG.error("The Settings not saved!\n\nDataBase Failure:", exception);
        DeviceDatabaseErrorDialog.open(null,
                                       "The Settings not saved!\n\nDataBase Failure:",
                                       exception);
    }
    
    public void perfromClose() {
        final IViewSite site = getProfiBusTreeView().getSite();
        site.getPage().closeEditor(this, false);
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
    
    /**
     * Save or Update the Node to the Data Base.
     */
    final void save() {
        ProgressMonitorDialog dia = new ProgressMonitorDialog(getShell());
        dia.open();
        IProgressMonitor progressMonitor = dia.getProgressMonitor();
        try {
            progressMonitor.beginTask("Save " + getNode(), 3);
            AbstractNodeDBO parent = getNode().getParent();
            progressMonitor.worked(1);
            try {
                if (parent == null) {
                    getNode().localSave();
                } else {
                    parent.localSave();
                }
            } catch (final PersistenceException e) {
                openErrorDialog(e);
            }
            progressMonitor.worked(2);
            setSaveButtonSaved();
            Button saveButton = getSaveButton();
            if (saveButton != null) {
                saveButton.setText("&Save");
            }
            
            getHeaderField(HeaderFields.DB_ID).setText(getNode().getId() + "");
            progressMonitor.worked(3);
            
            if (isNew() && !getNode().isRootNode()) {
                getProfiBusTreeView().refresh(parent);
                getProfiBusTreeView().getTreeViewer().expandToLevel(getNode(),
                                                                    AbstractTreeViewer.ALL_LEVELS);
            } else if (isNew() && getNode().isRootNode()) {
                getProfiBusTreeView().addFacility(getNode());
                getProfiBusTreeView().refresh(getNode());
                getProfiBusTreeView().refresh();
                
            } else {
                // refresh the View
                getProfiBusTreeView().refresh(getNode());
            }
            setNew(false);
        } finally {
            progressMonitor.done();
            dia.close();
        }
    }
    
    protected final void selecttTabFolder(int index) {
        if (_tabFolder != null) {
            _tabFolder.setSelection(index);
        }
    }
    
    /**
     * 
     * This method generate the Background of an NodeConfig this a 3 Parts. 1.
     * The Header with description line the 3 information field for: 1.1.
     * Modified by 1.2. Modified on 1.3. GSD-Version 2. The Body as an empty
     * TabFolder. 3. The Footer with the Save and Cancel Buttons.
     * 
     * @param headline
     *            The description line of the Header
     * @param node
     *            The node that was configured.
     */
    @SuppressWarnings("unused")
    protected void setBackgroundComposite() {
        final int columnNum = 5;
        final AbstractNodeDBO node = getNode();
        if (node == null) {
            return;
        }
        getParent().setLayout(new GridLayout(columnNum, true));
        final Label headlineLabel = new Label(getParent(), SWT.NONE);
        if (_FONT == null) {
            _FONT = headlineLabel.getFont();
        }
        headlineLabel.setText("Profibus " + node.getClass().getSimpleName() + " Configuration");
        headlineLabel.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, false, columnNum, 1));
        
        final GridDataFactory labelGridData = GridDataFactory.fillDefaults();
        // - Header
        final Group header = makeBackgroundGroup(columnNum);
        // -- Create by
        getNewLabel(header, "Created by:");
        /** The description field with the name of the User that make changes. */
        String temp = "";
        if (isNew()) {
            final SecurityFacade securityFacade = SecurityFacade.getInstance();
            if (securityFacade != null && securityFacade.getCurrentUser() != null
                    && securityFacade.getCurrentUser().getUsername() != null) {
                temp = securityFacade.getCurrentUser().getUsername();
                node.setCreatedBy(temp);
                
            }
        } else if (node != null && node.getCreatedBy() != null) {
            temp = node.getCreatedBy();
        }
        final Text creatByText = getNewText(header, temp);
        getParent().setData("creatBy", creatByText);
        temp = "";
        // -- Created on
        getNewLabel(header, "Created on:");
        /** The description field with the name of the Date of the last change. */
        
        Date now = new Date();
        if (isNew()) {
            temp = ConfigHelper.getSimpleDateFormat().format(now);
            node.setCreatedOn(now);
        } else if (node != null && node.getCreatedOn() != null) {
            now = node.getCreatedOn();
            temp = now.toString();
        }
        
        final Text creatOnText = getNewText(header, temp);
        getParent().setData("date", now);
        getParent().setData("creatOn", creatOnText);
        temp = "";
        // -- Version
        getNewLabel(header, "Version:");
        /** The description field with the Version from GSD File. */
        if (node != null) {
            temp = node.getVersion() + "";
        }
        final Text versionText = getNewText(header, temp);
        setHeaderField(HeaderFields.VERSION, versionText);
        
        final Label iconButton = new Label(header, SWT.BORDER);
        iconButton.setLayoutData(GridDataFactory.swtDefaults().span(1, 3).minSize(40, 40).create());
        iconButton.setImage(ConfigHelper.getImageFromNode(node, 30, 60));
        iconButton.addMouseListener(new MouseAdapter() {
            
            @Override
            public void mouseUp(@Nullable final MouseEvent e) {
                final IconChooserDialog chooseIconDialog = new IconChooserDialog(AbstractNodeEditor.this
                                                                                         .getShell(),
                                                                                 node);
                
                chooseIconDialog.setBlockOnOpen(true);
                if (chooseIconDialog.open() == 0 && node != null) {
                    node.setIcon(chooseIconDialog.getImage());
                }
                
            }
            
        });
        
        getNewLabel(header, "Modified by:");
        /** The description field with the name of the User that make changes. */
        temp = "";
        if (node != null && node.getUpdatedBy() != null) {
            temp = node.getUpdatedBy();
        }
        final Text modifiedBy = getNewText(header, temp);
        setHeaderField(HeaderFields.MODIFIED_BY, modifiedBy);
        temp = "";
        // -- Modifierd on
        getNewLabel(header, "Modified on:");
        /** The description field with the name of the Date of the last change. */
        if (node != null && node.getUpdatedOn() != null) {
            temp = node.getUpdatedOn().toString();
        }
        final Text modifiedOn = getNewText(header, temp);
        setHeaderField(HeaderFields.MODIFIED_ON, modifiedOn);
        temp = "";
        
        getNewLabel(header, "DataBase ID:");
        /** The description field with the Version from GSD File. */
        if (node != null) {
            temp = node.getId() + "";
        }
        
        final Text dbIdText = getNewText(header, temp);
        setHeaderField(HeaderFields.DB_ID, dbIdText);
        
        /**
         * GSD Version. The description field with the Version from GSD File.
         * Only Master and Slave have a GSD File
         */
        
        if (node instanceof MasterDBO || node instanceof SlaveDBO) {
            getNewLabel(header, "Version from GSD:");
            
            if (node.getUpdatedOn() != null) {
                // TODO: GSD Versionsnummer setzen.
                temp = node.getVersion() + "";
            }
            final Text version = getNewText(header, temp);
            getParent().setData("gsdVersion", version);
        }
        
        setTabFolder(new TabFolder(getParent(), SWT.H_SCROLL | SWT.V_SCROLL));
        getTabFolder().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 5, 1));
        getTabFolder().setBackgroundMode(SWT.INHERIT_DEFAULT);
        
        // -Footer
        new Label(getParent(), SWT.NONE);
        setSaveButton(new Button(getParent(), SWT.PUSH));
        Button saveButton = getSaveButton();
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
        new Label(getParent(), SWT.NONE);
        setCancelButton(new Button(getParent(), SWT.PUSH));
        Button cancelButton = getCancelButton();
        if (cancelButton != null) {
            cancelButton.setText("&Cancel");
            cancelButton.setLayoutData(labelGridData.create());
            cancelButton.addSelectionListener(new CancelSelectionListener());
        }
        new Label(getParent(), SWT.NONE);
        header.setTabList(new Control[0]);
    }
    
    /**
     * @param cancelButton
     *            the cancelButton to set
     */
    protected final void setCancelButton(@Nullable final Button cancelButton) {
        _cancelButton = cancelButton;
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
    final void setCombo(@Nonnull final Combo comboField, @Nullable final String value) {
        String tmp = value;
        if (tmp == null) {
            tmp = "";
        }
        final int index = comboField.indexOf(value);
        comboField.select(index);
        comboField.setData(index);
        comboField.addModifyListener(getMLSB());
    }
    
    public void setDesc(@CheckForNull final String desc) {
        String temp = desc != null ? desc : "";
        Text descText = getDescText();
        if (descText != null) {
            descText.setText(temp);
        }
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
        setDescText(descText);
        getDescText().addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(@Nonnull final KeyEvent e) {
                if (e.keyCode == SWT.CR || e.keyCode == SWT.KEYPAD_CR) {
                    if (e.stateMask != SWT.MOD1) {
                        e.doit = false;
                        getSaveButton().setFocus();
                    }
                }
            }
        });
    }
    
    @Override
    public void setFocus() {
        // nothing to do;
    }
    
//    protected void setGsdFile(@Nullable GSDFileDBO gsdFile) {
//        _gsdFile = gsdFile;
//    }
    
    @CheckForNull
    protected void setGsdFiles(@Nullable final List<GSDFileDBO> gsdFiles) {
        _gsdFiles = gsdFiles;
    }
    
    @Nonnull
    public Text setHeaderField(@Nonnull final HeaderFields field, @Nullable final Text text) {
        return headerFields.put(field, text);
    }
    
    /**
     * @param indexSpinner
     *            the indexSpinner to set
     */
    protected final void setIndexSpinner(@Nullable final Spinner indexSpinner) {
        _indexSpinner = indexSpinner;
    }
    
    public final void setName(@Nonnull final String name) {
        final TreeItem treeItem = getProfiBusTreeView().getTreeViewer().getTree().getSelection()[0];
        treeItem.setText(name);
        _nameText.setText(name);
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
                Text descText = getDescText();
                if ( (e.keyCode == SWT.CR || e.keyCode == SWT.KEYPAD_CR) && (descText != null)) {
                    descText.setFocus();
                }
            }
        });
    }
    
    protected void setNew(boolean nevv) {
        _new = nevv;
    }
    
    protected void setParent(@Nonnull Composite parent) {
        _parent = parent;
    }
    
    protected void setSaveButton(@Nullable final Button saveButton) {
        _saveButton = saveButton;
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
        final boolean changed = enab != getSaveButton().isEnabled();
        getSaveButton().setEnabled(enab);
        getNode().setDirty(enab);
        if (changed) {
            firePropertyChange(IEditorPart.PROP_DIRTY);
        }
    }
    
    /**
     * Clear the dirty bit, delete all saveButton events and set the Button
     * disable.
     */
    @Override
    public final void setSaveButtonSaved() {
        _saveButtonEvents.clear();
        getSaveButton().setEnabled(false);
        if (getNode() != null) {
            getNode().setDirty(false);
        }
        firePropertyChange(PROP_DIRTY);
    }
    
    /**
     * 
     * @param selectionListener
     */
    public final void setSaveButtonSelectionListener(@Nonnull final SelectionListener selectionListener) {
        getSaveButton().addSelectionListener(selectionListener);
    }
    
    /**
     * @param tabFolder
     *            the tabFolder to set
     */
    protected final void setTabFolder(@Nullable final TabFolder tabFolder) {
        _tabFolder = tabFolder;
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
        String tmp = text;
        if (tmp == null) {
            tmp = "";
        }
        textField.setText(tmp);
        textField.setData(tmp);
        textField.setTextLimit(limit);
        // Formatter f = new Formatter();
        // f.format("The maximum text length is %s character", limit);
        textField.setToolTipText("");
        textField.addModifyListener(getMLSB());
    }
}
