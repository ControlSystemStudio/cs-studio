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

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Formatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.csstudio.config.ioconfig.config.view.INodeConfig;
import org.csstudio.config.ioconfig.config.view.helper.ConfigHelper;
import org.csstudio.config.ioconfig.config.view.helper.DocumentationManageView;
import org.csstudio.config.ioconfig.config.view.helper.GSDLabelProvider;
import org.csstudio.config.ioconfig.config.view.helper.IconManageView;
import org.csstudio.config.ioconfig.config.view.helper.ShowFileSelectionListener;
import org.csstudio.config.ioconfig.editorinputs.NodeEditorInput;
import org.csstudio.config.ioconfig.model.Facility;
import org.csstudio.config.ioconfig.model.GSDFileTypes;
import org.csstudio.config.ioconfig.model.IDocumentable;
import org.csstudio.config.ioconfig.model.Node;
import org.csstudio.config.ioconfig.model.NodeImage;
import org.csstudio.config.ioconfig.model.PersistenceException;
import org.csstudio.config.ioconfig.model.Repository;
import org.csstudio.config.ioconfig.model.pbmodel.GSDFile;
import org.csstudio.config.ioconfig.model.pbmodel.Master;
import org.csstudio.config.ioconfig.model.pbmodel.Slave;
import org.csstudio.config.ioconfig.view.MainView;
import org.csstudio.config.ioconfig.view.ProfiBusTreeView;
import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.platform.security.SecurityFacade;
import org.csstudio.platform.security.User;
import org.csstudio.platform.ui.util.CustomMediaFactory;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
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
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.part.EditorPart;

/**
 * TODO (hrickens) :
 *
 * @author hrickens
 * @author $Author: hrickens $
 * @version $Revision: 1.2 $
 * @since 31.03.2010
 */
public abstract class AbstractNodeEditor extends EditorPart implements INodeConfig {
    /**
     * The warn background color (SWT.COLOR_RED) of the index Spinner.
     */
    protected static final Color WARN_BACKGROUND_COLOR = CustomMediaFactory.getInstance()
            .getColor(CustomMediaFactory.COLOR_RED);

    private static Font _FONT;

    private static GridDataFactory _LABEL_GRID_DATA = GridDataFactory.fillDefaults();

    private static GridDataFactory _TEXT_GRID_DATA = GridDataFactory.fillDefaults().grab(true,
                                                                                         false);

    /**
     * A ModifyListener that set the save button enable to store the changes. Works wiht
     * {@link Text}, {@link Combo} and {@link Spinner}.
     */
    private final ModifyListener _mLSB = new NodeConfigModifyListener();

    /**
     * Contain all different events that have change a Value and the status of the change. The
     * status means is the new Value a differnt from the origin Value.
     */
    private final HashMap<String, Boolean> _saveButtonEvents = new HashMap<String, Boolean>();

    private final Map<HeaderFields, Text> headerFields = new HashMap<HeaderFields, Text>();

    /**
     * The Button to cancel Changes.
     */
    private Button _cancelButton;

    private Text _descText;

    /**
     * The Tabview to Manage the documentation of Node.
     */
    private DocumentationManageView _documentationManageView;

    private GSDFile _gsdFile;

    private List<GSDFile> _gsdFiles;

    /**
     * The Spinner for the channel index.
     */
    private Spinner _indexSpinner;

    /**
     * The text field for the name of the node.
     */
    private Text _nameText;

    /**
     * if true an new SubNet a created also modified _slave.
     */
    private boolean _new = false;

    private Node _node;

    private Composite _parent;

    /**
     * The Button to save Changes.
     */
    private Button _saveButton;

    /**
     * The Tabfolder for a config view.
     */
    private TabFolder _tabFolder;

    public AbstractNodeEditor(final boolean nevv) {
        //        super(parent, SWT.NONE);
        _new = nevv;
        //        ActionFactory.SAVE.create(this.get);
        //        setGlobalActionHandler(IWorkbenchActionConstants.,
        //                                           goIntoAction);
        //        setBackgroundComposite(headline, node);

    }

    public AbstractNodeEditor() {
        // TODO Auto-generated constructor stub
    }

    /**
     * Fill the View whit data from GSDFile.
     *
     * @param gsdFile
     *            the GSDFile whit the data.
     * @return true when set the data ok.
     */
    public abstract boolean fill(@Nullable GSDFile gsdFile);

    // ---------------------------------------
    // Node Editor View

    public void cancel() {
        if (getDescWidget() != null) {
            setDesc((String) getDescWidget().getData());
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
        _parent = parent;
        setBackgroundComposite();
        if (getNode().needGSDFile() != GSDFileTypes.NONE) {
            makeGSDFileChooser(getTabFolder(), "GSD File List");
        }
        documents();
    }

    @Override
    public void doSave(@Nullable final IProgressMonitor monitor) {
        // Header
        Date now = new Date();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
        Text text = getHeaderField(HeaderFields.VERSION);
        int i = -1;
        try {
            i = Integer.valueOf(text.getText());
        } catch (NumberFormatException nfe) {
            i = 0;
        }
        getNode().setVersion(i);

        getNode().setUpdatedBy(ConfigHelper.getUserName());
        getNode().setUpdatedOn(now);

        getNode().setDescription(getDesc());
        if (getDescWidget() != null) {
            getDescWidget().setData(getDesc());
        }

        // update Header
        getHeaderField(HeaderFields.MODIFIED_ON).setText(df.format(now));
        getHeaderField(HeaderFields.MODIFIED_BY).setText(ConfigHelper.getUserName());
        // df = null;
        // now = null;
    }

    /**
     * (@inheritDoc)
     */
    @Override
    public void doSaveAs() {
        //   save as not supported
    }

    // ---------------------------------------
    // Node Editor View

    /**
     *
     * @return the node which was configured with this View.
     */
    @Nonnull
    public IDocumentable getDocumentableObject() {
        return _node;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Node getNode() {
        return _node;
    }

    // ---------------------------------------
    // Node Editor View

    /**
     * (@inheritDoc)
     */
    @Override
    public void init(@Nonnull final IEditorSite site, @Nonnull final IEditorInput input) throws PartInitException {
        setSite(site);
        setInput(input);
        _node = ((NodeEditorInput) input).getNode();
        _new = ((NodeEditorInput) input).isNew();
        setPartName(_node.getName());
        getProfiBusTreeView().setOpenEditor(this);
    }

    /**
     *
     * @param tabFolder
     *            The Tab Folder to add the Tab Item.
     * @param head
     *            Headline for the Tab.
     * @return Tab Item Composite.
     */
    @Nonnull
    public Composite makeGSDFileChooser(@Nonnull final TabFolder tabFolder,@Nonnull  final String head) {
        int columnNum = 7;
        final Composite comp = ConfigHelper.getNewTabItem(head, tabFolder, columnNum, 520, 200);

        final Text selectedText = createSelectionArea(columnNum, comp);

        final TableViewer gsdFileTableViewer = createChooserArea(columnNum, comp);

        createButtonArea(tabFolder, comp, selectedText, gsdFileTableViewer);

        createGSDFileActions(gsdFileTableViewer);

        return comp;

    }

    private void createButtonArea(@Nonnull final TabFolder tabFolder,
                           @Nonnull final Composite comp,
                           @Nonnull final Text selectedText,
                           @Nonnull final TableViewer gsdFileTableViewer) {
        new Label(comp, SWT.NONE);
        final Button fileSelect = new Button(comp, SWT.PUSH);
        fileSelect.setText("Select");
        fileSelect.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
        fileSelect.addSelectionListener(new GSDFileSelectionListener(gsdFileTableViewer, selectedText));
        new Label(comp, SWT.NONE);
        new Label(comp, SWT.NONE);
        final Button fileAdd = new Button(comp, SWT.PUSH);
        fileAdd.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
        fileAdd.setText("Add File");
        fileAdd.addSelectionListener(new GSDFileAddListener(tabFolder, gsdFileTableViewer, comp));
        final Button fileRemove = new Button(comp, SWT.PUSH);
        fileRemove.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
        fileRemove.setText("Remove File");
        fileRemove.addSelectionListener(new GSDFileRemoveListener(gsdFileTableViewer));

        gsdFileTableViewer.addSelectionChangedListener(new GSDFileChangeListener(fileSelect));

        new Label(comp, SWT.NONE);
    }

    /**
     * @param columnNum
     * @param comp
     * @return
     */
    @Nonnull
    private TableViewer createChooserArea(final int columnNum,@Nonnull final Composite comp) {
        final Group gAvailable = new Group(comp, SWT.NONE);
        gAvailable.setText("Available GSD File:");
        gAvailable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, columnNum, 1));
        gAvailable.setLayout(new GridLayout(1, false));

        final TableColumnLayout tableColumnLayout = new TableColumnLayout();
        final Composite tableComposite = new Composite(gAvailable, SWT.BORDER);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(tableComposite);
        tableComposite.setLayout(tableColumnLayout);

        final TableViewer gsdFileTableViewer = new TableViewer(tableComposite, SWT.H_SCROLL | SWT.V_SCROLL
                | SWT.MULTI | SWT.FULL_SELECTION);
        gsdFileTableViewer.setContentProvider(new ArrayContentProvider());
        gsdFileTableViewer.setSorter(new ViewerSorterExtension());
        gsdFileTableViewer.setLabelProvider(new GSDLabelProvider(getNode().needGSDFile()));
        gsdFileTableViewer.getTable().setHeaderVisible(false);
        gsdFileTableViewer.getTable().setLinesVisible(false);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(gsdFileTableViewer.getTable());

        _gsdFiles = Repository.load(GSDFile.class);
        if (_gsdFiles == null) {
            _gsdFiles = new ArrayList<GSDFile>();
        } else if (!_gsdFiles.isEmpty()) {
            gsdFileTableViewer.setInput(_gsdFiles.toArray(new GSDFile[_gsdFiles.size()]));
        }
        return gsdFileTableViewer;
    }

    @Nonnull
    private Text createSelectionArea(final int columnNum,@Nonnull final Composite comp) {
        final Text tSelected;
        Group gSelected = new Group(comp, SWT.NONE);
        gSelected.setText("Selected GSD File:");
        gSelected.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, columnNum, 1));
        gSelected.setLayout(new GridLayout(1, false));

        tSelected = new Text(gSelected, SWT.SINGLE | SWT.BORDER);
        tSelected.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
        if (getGSDFile() != null) {
            _gsdFile = getGSDFile();
            //            fill(_gsdFile);
            tSelected.setText(_gsdFile.getName());
        }
        return tSelected;
    }

    // ---------------------------------------
    // Node Editor View

    public void setDesc(@Nonnull final String desc) {
        _descText.setText(desc);
    }

    @Nonnull
    public String getDesc() {
        if ( (_descText == null) || (_descText.getText() == null)) {
            return "";
        }
        return _descText.getText();
    }

    /**
         *
         * {@inheritDoc}
         */
    @Override
    public void dispose() {
        super.dispose();
        _saveButton = null;
        if (_saveButtonEvents != null) {
            _saveButtonEvents.clear();

        }
        //        if (_FONT != null) {
        //            _FONT.dispose();
        //        }
        //        if (_FONT_BOLD != null) {
        //            _FONT_BOLD.dispose();
        //        }
    }

    @Nonnull
    public Text getHeaderField(@Nonnull final HeaderFields field) {
        return headerFields.get(field);
    }

    @Nonnull
    public Text setHeaderField(@Nonnull final HeaderFields field, final Text text) {
        return headerFields.put(field, text);
    }

    public void perfromClose() {
        //TODO: Schliesst den Momentan angezeigen Editor und nicht den Editor der das Command ausführt
        IViewSite site = getProfiBusTreeView().getSite();
        IHandlerService handlerService = (IHandlerService) site.getService(IHandlerService.class);
        try {
            setFocus();
            handlerService.executeCommand("org.eclipse.ui.file.close", null);
        } catch (Exception ex) {
            CentralLogger.getInstance().error(this, ex.getMessage());
        }
    }

    // ---------------------------------------
    // Node Editor View

    public final void setName(@Nonnull final String name) {
        TreeItem treeItem = getProfiBusTreeView().getTreeViewer().getTree().getSelection()[0];
        treeItem.setText(name);
        _nameText.setText(name);
    }

    /**
     * A ModifyListener that set the save button enable to store the changes. Works wiht
     * {@link Text}, {@link Combo} and {@link Spinner}.
     *
     * @return the ModifyListener.
     */
    @Nonnull
    public final ModifyListener getMLSB() {
        return _mLSB;
    }

    // ---------------------------------------
    // Node Editor View

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

    @Override
    public boolean isSaveAsAllowed() {
        return false; //   save as not supported
    }

    /**
     * Give a change event and his save status.<br>
     * Have one or more events the status unsaved then the Save Button was set to enable otherwise
     * to disable.
     *
     * @param event
     *            the Save Event Id.
     * @param enabled
     *            the Save Enable status for the event.
     */
    public final void setSavebuttonEnabled(@Nullable final String event, final boolean enabled) {
        if (event != null) {
            _saveButtonEvents.put(event, enabled);
        }
        boolean enab = !getNode().isPersistent()
                || _saveButtonEvents.containsValue(Boolean.valueOf(true));
        boolean changed = enab != _saveButton.isEnabled();
        _saveButton.setEnabled(enab);
        getNode().setDirty(enab);
        if (changed) {
            firePropertyChange(IEditorPart.PROP_DIRTY);
        }
    }

    /**
     * Clear the dirty bit, delete all saveButton events and set the Button disable.
     */
    public final void setSaveButtonSaved() {
        _saveButtonEvents.clear();
        _saveButton.setEnabled(false);
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
        _saveButton.addSelectionListener(selectionListener);
    }

    // ---------------------------------------
    // Node Editor View

    /**
     *
     * @return The Documentation Manage View.
     */
    final DocumentationManageView getDocumentationManageView() {
        return _documentationManageView;
    }

    /**
     * Select a text to a {@link Combo} and store the index into Data as origin value.
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
        int index = comboField.indexOf(value);
        comboField.select(index);
        comboField.setData(index);
        comboField.addModifyListener(getMLSB());
    }

    /**
     * Save or Update the Node to the Data Base.
     */
    final void save() {
        try {
            if (getNode().getParent() == null) {
                getNode().localSave();
            } else {
                getNode().getParent().localSave();
            }
        } catch (PersistenceException e) {
            openErrorDialog(e);
        }

        setSaveButtonSaved();
        _saveButton.setText("&Save");

        if (_new && !getNode().isRootNode()) {
            getProfiBusTreeView().refresh(getNode().getParent());
        } else if (_new && getNode().isRootNode()) {
            getProfiBusTreeView().addFacility(getNode());
            getProfiBusTreeView().refresh(getNode());
            getProfiBusTreeView().refresh();
        } else {
            // refresh the View
            getProfiBusTreeView().refresh(getNode());
        }
        _new = false;

    }

    /**
     * Set a int as text to a {@link Text} and store the text into Data as origin value.
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
     * Set a text to a {@link Text} and store the text into Data as origin value.
     *
     * @param textField
     *            The Text-field to set the text
     * @param text
     *            The text was set.
     * @param limit
     *            The maximum text length for the Text-field. (Text.LIMIT for maximum)
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

    /**
     *
     * @return the GSD File of this Node.
     */
    @CheckForNull
    protected GSDFile getGSDFile() {
        return null;
    }

    /**
     * @param exception
     *            A exception to show in a Dialog,
     */
    protected void openErrorDialog(@Nonnull final Exception exception) {
        CentralLogger.getInstance().error(this, exception);
        Formatter f = new Formatter();
        String message = exception.getLocalizedMessage();
        if (message == null) {
            message = exception.getMessage();
            if (message == null) {
                message = "Unknown Exception";
            }
        }

        f.format("The Settings not saved!\n\nDataBase Failure:\n%s", message);

        ErrorDialog errorDialog = new ErrorDialog(Display.getCurrent().getActiveShell(),
                                                  "Data Base Error",
                                                  f.toString(),
                                                  Status.CANCEL_STATUS,
                                                  0);
        errorDialog.open();
        f.close();
    }

    /**
     * Generate a new Node on his parent with a Name, creator and creation date.
     */
    protected boolean newNode() {
        return newNode("");
    }

    protected boolean newNode(@Nullable final String nameOffer) {

        String nodeType = getNode().getClass().getSimpleName();

        InputDialog id = new InputDialog(getShell(),
                                         "Create new " + nodeType,
                                         "Enter the name of the " + nodeType,
                                         nameOffer,
                                         null);
        id.setBlockOnOpen(true);
        if (id.open() == Dialog.OK) {
            getNode().setName(id.getValue());
            User user = SecurityFacade.getInstance().getCurrentUser();
            String name = "Unknown";
            if (user != null) {
                name = user.getUsername();
            }

            getNode().setCreatedBy(name);
            getNode().setCreatedOn(new Date());
            getNode().setVersion(-2);
            id.close();

            Object obj = ((StructuredSelection) getProfiBusTreeView().getTreeViewer()
                    .getSelection()).getFirstElement();

            if ( (getNode() instanceof Facility) || (obj == null)) {
                getProfiBusTreeView().getTreeViewer().setInput(getNode());
                // TODO neue facility erstellen und speichern..
            } else if (obj instanceof Node) {
                if (getNode().getParent() == null) {
                    Node nodeParent = (Node) obj;

                    getNode().moveSortIndex(nodeParent
                            .getfirstFreeStationAddress(Node.MAX_STATION_ADDRESS));
                    nodeParent.addChild(getNode());
                }
            }
            return true;
        }
        return false;
    }

    protected void makeDescGroup(@Nonnull final Composite comp, final int hSize) {
        Group gDesc = new Group(comp, SWT.NONE);
        gDesc.setText("Description: ");
        GridDataFactory gdf = GridDataFactory.fillDefaults().grab(true, true).span(hSize, 1)
                .minSize(200, 200);
        gDesc.setLayoutData(gdf.create());
        gDesc.setLayout(new GridLayout(2, false));

        Label shortDescLabel = new Label(gDesc, SWT.NONE);
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
                String text = descText.getText();
                String string = "";
                if (text != null) {
                    String[] split = text.split("[\r\n]");
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
        gDesc.setTabList(new Control[] {descText });
    }

    /**
     * @return the parent
     */
    protected Composite getParent() {
        return _parent;
    }

    protected void perfromSave() {
        IViewSite site = getProfiBusTreeView().getSite();
        IHandlerService handlerService = (IHandlerService) site.getService(IHandlerService.class);
        try {
            handlerService.executeCommand("org.eclipse.ui.file.save", null);
        } catch (Exception ex) {
            CentralLogger.getInstance().error(this, ex.getMessage());
        }
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
        TabItem item = new TabItem(getTabFolder(), SWT.NONE, 0);
        item.setText(head);
        Composite comp = new Composite(getTabFolder(), SWT.NONE);
        comp.setLayout(new GridLayout(rows, false));
        item.setControl(comp);

        comp.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
        return comp;
    }

    /**
     *
     * @return the Node Name description field.
     */
    protected final Text getNameWidget() {
        return _nameText;
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
                if ( (e.keyCode == SWT.CR) || (e.keyCode == SWT.KEYPAD_CR)) {
                    _descText.setFocus();
                }
            }
        });
    }

    /**
     *
     * @return the Node Name description field.
     */
    protected final Text getDescWidget() {
        return _descText;
    }

    /**
     *
     * @param descText
     *            set the Node Name description field.
     */
    protected final void setDescWidget(@Nonnull final Text descText) {
        _descText = descText;
        _descText.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(@Nonnull final KeyEvent e) {
                if ( (e.keyCode == SWT.CR) || (e.keyCode == SWT.KEYPAD_CR)) {
                    if (e.stateMask != SWT.MOD1) {
                        e.doit = false;
                        _saveButton.setFocus();
                    }
                }
            }
        });
    }

    /**
     * @return the indexSpinner
     */
    protected final Spinner getIndexSpinner() {
        return _indexSpinner;
    }

    /**
     * @param indexSpinner
     *            the indexSpinner to set
     */
    protected final void setIndexSpinner(final Spinner indexSpinner) {
        _indexSpinner = indexSpinner;
    }

    /**
     * @param tabFolder
     *            the tabFolder to set
     */
    protected final void setTabFolder(final TabFolder tabFolder) {
        _tabFolder = tabFolder;
    }

    /**
     * @return the tabFolder
     */
    protected final TabFolder getTabFolder() {
        return _tabFolder;
    }

    /**
     * @param cancelButton
     *            the cancelButton to set
     */
    protected final void setCancelButton(final Button cancelButton) {
        _cancelButton = cancelButton;
    }

    /**
     * @return the cancelButton
     */
    protected final Button getCancelButton() {
        return _cancelButton;
    }

    /**
     * @return the profiBusTreeView
     */
    @Nonnull
    protected final ProfiBusTreeView getProfiBusTreeView() {
        return ((MainView) getEditorSite().getPage().findView(MainView.ID)).getProfiBusTreeView();
    }

    @CheckForNull
    private Shell getShell() {
        if (this.getEditorSite() != null) {
            return this.getEditorSite().getShell();
        }
        return null;
    }

    /**
     *
     * This method generate the Background of an NodeConfig this a 3 Parts. 1. The Header with
     * description line the 3 information field for: 1.1. Modified by 1.2. Modified on 1.3.
     * GSD-Version 2. The Body as an empty TabFolder. 3. The Footer with the Save and Cancel
     * Buttons.
     *
     * @param headline
     *            The description line of the Header
     * @param node
     *            The node that was configured.
     */
    private void setBackgroundComposite() {
        int columnNum = 5;
        final Node node = getNode();
        getParent().setLayout(new GridLayout(columnNum, true));
        Label headlineLabel = new Label(getParent(), SWT.NONE);
        if (_FONT == null) {
            _FONT = headlineLabel.getFont();
        }
        //        if (_fontBold == null) {
        //            FontData fontData = _font.getFontData()[0];
        //            fontData.setStyle(SWT.BOLD);
        //            fontData.setHeight(fontData.getHeight() + 2);
        //            _fontBold = CustomMediaFactory.getInstance().getFont(fontData);
        //        }
        //        headlineLabel.setFont(_fontBold);
        headlineLabel.setText("Profibus " + node.getClass().getSimpleName() + " Configuration");
        headlineLabel.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, false, columnNum, 1));

        GridDataFactory labelGridData = GridDataFactory.fillDefaults();
        // - Header
        Group header = makeBackgroundGroup(columnNum);
        // -- Create by
        getNewLabel(header, "Created by:");
        /** The description field with the name of the User that make changes. */
        String temp = "";
        if (_new) {
            SecurityFacade securityFacade = SecurityFacade.getInstance();
            if ( (securityFacade != null) && (securityFacade.getCurrentUser() != null)
                    && (securityFacade.getCurrentUser().getUsername() != null)) {
                temp = securityFacade.getCurrentUser().getUsername();

            }
        } else if ( (node != null) && (node.getCreatedBy() != null)) {
            temp = node.getCreatedBy();
        }
        final Text creatByText = getNewText(header, temp);
        getParent().setData("creatBy", creatByText);
        temp = "";
        // -- Created on
        getNewLabel(header, "Created on:");
        /** The description field with the name of the Date of the last change. */

        Date now = new Date();
        if (_new) {
            temp = ConfigHelper.getSimpleDateFormat().format(now);
        } else if ( (node != null) && (node.getCreatedOn() != null)) {
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
            public void mouseUp(final MouseEvent e) {
                // Shell shell = new Shell(NodeConfig.this.getDisplay().getActiveShell(),
                // SWT.RESIZE);
                // IconChooserDialog chooseIconDialog = new IconChooserDialog(shell, node);
                IconChooserDialog chooseIconDialog = new IconChooserDialog(AbstractNodeEditor.this
                        .getShell(), node);

                chooseIconDialog.setBlockOnOpen(true);
                if ( (chooseIconDialog.open() == 0) && (node != null)) {
                    node.setIcon(chooseIconDialog.getImage());
                }

            }

        });

        getNewLabel(header, "Modified by:");
        /** The description field with the name of the User that make changes. */
        temp = "";
        if ( (node != null) && (node.getUpdatedBy() != null)) {
            temp = node.getUpdatedBy();
        }
        final Text modifiedBy = getNewText(header, temp);
        setHeaderField(HeaderFields.MODIFIED_BY, modifiedBy);
        temp = "";
        // -- Modifierd on
        getNewLabel(header, "Modified on:");
        /** The description field with the name of the Date of the last change. */
        if ( (node != null) && (node.getUpdatedOn() != null)) {
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
        getNewText(header, temp);

        /**
         * GSD Version. The description field with the Version from GSD File. Only Master and Slave
         * have a GSD File
         */

        if ( (node instanceof Master) || (node instanceof Slave)) {
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
        _saveButton = new Button(getParent(), SWT.PUSH);
        if (_new) {
            _saveButton.setText("Create");
        } else {
            _saveButton.setText("&Save");
        }
        _saveButton.setLayoutData(labelGridData.create());
        setSaveButtonSelectionListener(new SaveSelectionListener());
        new Label(getParent(), SWT.NONE);
        setCancelButton(new Button(getParent(), SWT.PUSH));
        getCancelButton().setText("&Cancel");
        getCancelButton().setLayoutData(labelGridData.create());
        getCancelButton().addSelectionListener(new CancelSelectionListener());
        new Label(getParent(), SWT.NONE);
        header.setTabList(new Control[0]);
    }

    /**
     *
     */
    private static void createGSDFileActions(final TableViewer viewer) {
        Menu menu = new Menu(viewer.getControl());
        MenuItem showItem = new MenuItem(menu, SWT.PUSH);
        showItem.addSelectionListener(new ShowFileSelectionListener(viewer));
        showItem.setText("&Show");
        showItem.setImage(PlatformUI.getWorkbench().getSharedImages()
                .getImage(ISharedImages.IMG_OBJ_FOLDER));
        //       MenuItem saveAsItem = new MenuItem(menu, SWT.PUSH);
        //       saveAsItem.addSelectionListener(new SaveAsSelectionListener(viewer));
        //       saveAsItem.setText("&Show");
        //       saveAsItem.setImage(PlatformUI.getWorkbench().getSharedImages()
        //                         .getImage(ISharedImages.IMG_ETOOL_SAVEAS_EDIT));
        viewer.getTable().setMenu(menu);
    }

    @Nonnull
    private static Label getNewLabel(@Nonnull final Composite header, @Nonnull final String string) {
        Label newLabel = new Label(header, SWT.NONE);
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

    @Nonnull
    private Group makeBackgroundGroup(final int columnNum) {
        Group header = new Group(getParent(), SWT.H_SCROLL);
        header.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, columnNum, 1));
        header.setLayout(new GridLayout(7, false));
        header.setText("General Information");
        header.setTabList(new Control[0]);
        return header;
    }

    /**
     * set the documents and Icon tab item.
     */
    private void documents() {
        String head = "Documents";
        final TabItem item = new TabItem(getTabFolder(), SWT.NO_SCROLL);
        item.setText(head);
        //TODO: refactor DocumentationManageView
        _documentationManageView = new DocumentationManageView(getTabFolder(), SWT.NONE, this);
        item.setControl(_documentationManageView);
        getTabFolder().addSelectionListener(new SelectionListener() {

            public void widgetDefaultSelected(@Nonnull final SelectionEvent e) {
                docTabSelectionAction(e);
            }

            public void widgetSelected(@Nonnull final SelectionEvent e) {
                docTabSelectionAction(e);
            }

            private void docTabSelectionAction(@Nonnull final SelectionEvent e) {
                if (e.item.equals(item)) {
                    _documentationManageView.onActivate();
                }
            }

        });

    }

    /**
     * TODO (hrickens) :
     *
     * @author hrickens
     * @author $Author: hrickens $
     * @version $Revision: 1.2 $
     * @since 14.06.2010
     */
    private final class GSDFileChangeListener implements
            ISelectionChangedListener {
        private final Button _fileSelect;

        /**
         * Constructor.
         * @param fileSelect
         */
        private GSDFileChangeListener(final Button fileSelect) {
            _fileSelect = fileSelect;
        }

        public void selectionChanged(final SelectionChangedEvent event) {
            StructuredSelection selection = (StructuredSelection) event.getSelection();
            if ( (selection == null) || selection.isEmpty()) {
                _fileSelect.setEnabled(false);
                return;
            }
            GSDFile file = (GSDFile) selection.getFirstElement();
            _fileSelect.setEnabled( (getNode().needGSDFile() == GSDFileTypes.Master) == file
                    .isMasterNonHN());
        }
    }

    /**
     * TODO (hrickens) :
     *
     * @author hrickens
     * @author $Author: hrickens $
     * @version $Revision: 1.2 $
     * @since 14.06.2010
     */
    private final class GSDFileRemoveListener implements SelectionListener {
        private final TableViewer _tableViewer;

        /**
         * Constructor.
         * @param tableViewer
         */
        private GSDFileRemoveListener(final TableViewer tableViewer) {
            _tableViewer = tableViewer;
        }

        public void widgetDefaultSelected(final SelectionEvent e) {
        }

        public void widgetSelected(final SelectionEvent e) {
            StructuredSelection selection = (StructuredSelection) _tableViewer.getSelection();
            GSDFile removeFile = (GSDFile) selection.getFirstElement();

            if (MessageDialog.openQuestion(getShell(),
                                           "Lösche Datei aus der Datenbank",
                                           "Sind sie sicher das sie die Datei "
                                                   + removeFile.getName() + " löschen möchten")) {
                Repository.removeGSDFiles(removeFile);
                _gsdFiles.remove(removeFile);
                _tableViewer.setInput(_gsdFiles);
            }

        }
    }

    /**
     * TODO (hrickens) :
     *
     * @author hrickens
     * @author $Author: hrickens $
     * @version $Revision: 1.2 $
     * @since 14.06.2010
     */
    private final class GSDFileAddListener implements SelectionListener {
        private final TabFolder _tabFolder;
        private final TableViewer _tableViewer;
        private final Composite _comp;

        /**
         * Constructor.
         * @param tabFolder
         * @param tableViewer
         * @param comp
         */
        private GSDFileAddListener(final TabFolder tabFolder,
                                                final TableViewer tableViewer,
                                                final Composite comp) {
            _tabFolder = tabFolder;
            _tableViewer = tableViewer;
            _comp = comp;
        }

        public void widgetDefaultSelected(final SelectionEvent e) {
            doFileAdd();
        }

        public void widgetSelected(final SelectionEvent e) {
            doFileAdd();
        }

        private void doFileAdd() {
            FileDialog fd = new FileDialog(_comp.getShell(), SWT.MULTI);
            fd.setFilterExtensions(new String[] {"*.gsd;*.gsg", "*.gs?" });
            fd.setFilterNames(new String[] {"GS(GER)", "GS(ALL)" });
            fd.setFilterPath("Z:\\Boeckmann\\GSD_Dateien\\");
            if (fd.open() != null) {
                File path = new File(fd.getFilterPath());
                for (String fileName : fd.getFileNames()) {
                    if (fileNotContain(fileName)) {
                        String text = ConfigHelper.file2String(new File(path, fileName));
                        File file = new File(path, fileName);
                        GSDFile gsdFile = new GSDFile(file.getName(), text.toString());
                        _gsdFiles.add(gsdFile);
                        _tableViewer.setInput(_gsdFiles);
                        Repository.save(gsdFile);
                    } else {
                        MessageDialog.openInformation(_tabFolder.getShell(),
                                                      "Double GSD File",
                                                      "File is already in the DB");
                    }
                }
            }
        }

        private boolean fileNotContain(final String fileName) {
            boolean add = true;
            if ( (_gsdFiles != null) && !_gsdFiles.isEmpty()) {
                for (GSDFile file : _gsdFiles) {
                    add = !file.getName().equals(fileName);
                    if (!add) {
                        break;
                    }
                }
            }
            return add;
        }
    }

    /**
     * TODO (hrickens) :
     *
     * @author hrickens
     * @author $Author: hrickens $
     * @version $Revision: 1.2 $
     * @since 14.06.2010
     */
    private final class GSDFileSelectionListener implements SelectionListener {
        private final TableViewer _tableViewer;
        private final Text _tSelected;

        /**
         * Constructor.
         * @param tableViewer
         * @param tSelected
         */
        private GSDFileSelectionListener(final TableViewer tableViewer, final Text tSelected) {
            _tableViewer = tableViewer;
            _tSelected = tSelected;
        }

        public void widgetDefaultSelected(final SelectionEvent e) {
            doFileAdd();
        }

        public void widgetSelected(final SelectionEvent e) {
            doFileAdd();
        }

        private void doFileAdd() {
            _gsdFile = (GSDFile) ((StructuredSelection) _tableViewer.getSelection())
                    .getFirstElement();
            if (fill(_gsdFile)) {
                _tSelected.setText(_gsdFile.getName());
                setSavebuttonEnabled("GSDFile", true);
            }
        }
    }

    /**
     * TODO (hrickens) :
     *
     * @author hrickens
     * @author $Author: hrickens $
     * @version $Revision: 1.2 $
     * @since 14.06.2010
     */
    private final class ViewerSorterExtension extends ViewerSorter {
        @Override
        public int compare(final Viewer viewer, final Object e1, final Object e2) {
            if ( (e1 instanceof GSDFile) && (e2 instanceof GSDFile)) {
                GSDFile file1 = (GSDFile) e1;
                GSDFile file2 = (GSDFile) e2;

                // sort wrong files to back.
                if (! (file1.isMasterNonHN() || file1.isSlaveNonHN())
                        && (file2.isMasterNonHN() || file2.isSlaveNonHN())) {
                    return -1;
                } else if ( (file1.isMasterNonHN() || file1.isSlaveNonHN())
                        && ! (file2.isMasterNonHN() || file2.isSlaveNonHN())) {
                    return 1;
                }

                // if master -> master file to top
                switch (getNode().needGSDFile()) {
                    case Master:
                        if (file1.isMasterNonHN() && !file2.isMasterNonHN()) {
                            return -1;
                        } else if (!file1.isMasterNonHN() && file2.isMasterNonHN()) {
                            return 1;
                        }
                        break;
                    case Slave:
                        // if slave -> slave file to top
                        if (file1.isSlaveNonHN() && !file2.isSlaveNonHN()) {
                            return -1;
                        } else if (!file1.isSlaveNonHN() && file2.isSlaveNonHN()) {
                            return 1;
                        }
                        break;
                    default:
                        // do nothing
                }
                return file1.getName().compareToIgnoreCase(file2.getName());
            }
            return super.compare(viewer, e1, e2);
        }
    }

    /**
     *
     * TODO: make a Dialog to select a Icon for this node.
     *
     * @author hrickens
     * @author $Author: hrickens $
     * @version $Revision: 1.2 $
     * @since 21.05.2010
     */
    private final class IconChooserDialog extends Dialog {
        private final Node _node;
        /*
         * (non-Javadoc)
         *
         * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
         */
        private IconManageView _iconManageView;

        private IconChooserDialog(@Nonnull final Shell parentShell, @Nonnull final Node node) {
            super(parentShell);
            this.setShellStyle(SWT.RESIZE | SWT.BORDER | SWT.CLOSE | SWT.MIN | SWT.MAX | SWT.TITLE
                    | SWT.ON_TOP | // SWT.TOOL| SWT.SHEET|
                    SWT.PRIMARY_MODAL);
            _node = node;
        }

        @Override
        @Nonnull
        protected Control createDialogArea(@Nonnull final Composite parent) {
            Composite createDialogArea = (Composite) super.createDialogArea(parent);
            createDialogArea.setLayout(GridLayoutFactory.fillDefaults().create());
            _iconManageView = new IconManageView(createDialogArea, SWT.NONE, _node);
            return _iconManageView;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected void configureShell(@Nonnull final Shell shell) {
            super.configureShell(shell);
            shell.setText("Icon chooser");
            shell.setMaximized(true);
        }

        @CheckForNull
        // TODO: prüfen ob hier wirklich null kommen kann.
        public NodeImage getImage() {
            return _iconManageView.getSelectedImage();
        }
    }

    /**
     *
     * @author hrickens
     * @author $Author: hrickens $
     * @version $Revision: 1.2 $
     * @since 03.06.2009
     */
    private final class SaveSelectionListener implements SelectionListener {
        public void widgetDefaultSelected(@Nonnull final SelectionEvent e) {
            getEditorSite().getActionBars().getGlobalActionHandler("org.eclipse.ui.file.save")
                    .run();
            // TODO:need a IProgressMonitor?
            doSave(null);
        }

        public void widgetSelected(@Nonnull final SelectionEvent e) {
            perfromSave();
        }
    }

    /**
     *
     * @author hrickens
     * @author $Author: hrickens $
     * @version $Revision: 1.2 $
     * @since 03.06.2009
     */
    private final class CancelSelectionListener implements SelectionListener {
        public void widgetDefaultSelected(@Nonnull final SelectionEvent e) {
            doCancle();
        }

        public void widgetSelected(@Nonnull final SelectionEvent e) {
            doCancle();
        }

        /**
         *
         */
        private void doCancle() {
            if (getNode().isPersistent()) {
                cancel();
                getDocumentationManageView().cancel();
                setSaveButtonSaved();
            } else {
                boolean openQuestion = MessageDialog.openQuestion(getShell(),
                                                                  "Cancel",
                                                                  "You dispose this "
                                                                          + getNode().getClass()
                                                                                  .getSimpleName()
                                                                          + "?");
                if (openQuestion) {
                    setSaveButtonSaved();
                    getProfiBusTreeView().getTreeViewer().setSelection(getProfiBusTreeView()
                            .getTreeViewer().getSelection());
                } else {
                    // TODO: do nothing or cancel?
                }
            }
        }
    }

    /**
     * A ModifyListener that set the save button enable to store the changes. Works with
     * {@link Text}, {@link Combo} and {@link Spinner}.
     *
     * @author hrickens
     * @author $Author: hrickens $
     * @version $Revision: 1.2 $
     * @since 03.06.2009
     */
    private final class NodeConfigModifyListener implements ModifyListener {
        public void modifyText(@Nonnull final ModifyEvent e) {
            if (e.widget instanceof Text) {
                Text text = (Text) e.widget;
                setSavebuttonEnabled("ModifyListenerText:" + text.hashCode(), !text.getText()
                        .equals(text.getData()));
            } else if (e.widget instanceof Combo) {
                Combo combo = (Combo) e.widget;
                if (combo.getData() instanceof Integer) {
                    Integer value = (Integer) combo.getData();
                    if (value == null) {
                        setSavebuttonEnabled("ModifyListenerCombo" + combo.hashCode(), false);
                    } else {
                        setSavebuttonEnabled("ModifyListenerCombo" + combo.hashCode(),
                                             value != combo.getSelectionIndex());
                    }
                }
            } else if (e.widget instanceof Spinner) {
                Spinner spinner = (Spinner) e.widget;
                try {
                    setSavebuttonEnabled("ModifyListenerCombo" + spinner.hashCode(),
                                         ((Short) spinner.getData()) != spinner.getSelection());
                } catch (ClassCastException cce) {
                    CentralLogger.getInstance().error(this, spinner.toString(), cce);
                }
            }
        }
    }

}
