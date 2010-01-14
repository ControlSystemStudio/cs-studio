/*
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchrotron,
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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Formatter;
import java.util.HashMap;

import org.csstudio.config.ioconfig.config.view.helper.ConfigHelper;
import org.csstudio.config.ioconfig.config.view.helper.DocumentationManageView;
import org.csstudio.config.ioconfig.config.view.helper.IconManageView;
import org.csstudio.config.ioconfig.model.Facility;
import org.csstudio.config.ioconfig.model.Node;
import org.csstudio.config.ioconfig.model.NodeImage;
import org.csstudio.config.ioconfig.model.PersistenceException;
import org.csstudio.config.ioconfig.model.pbmodel.GSDFile;
import org.csstudio.config.ioconfig.model.pbmodel.Master;
import org.csstudio.config.ioconfig.model.pbmodel.Slave;
import org.csstudio.config.ioconfig.view.ProfiBusTreeView;
import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.platform.security.SecurityFacade;
import org.csstudio.platform.security.User;
import org.csstudio.platform.ui.util.CustomMediaFactory;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.StructuredSelection;
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
import org.eclipse.swt.graphics.FontData;
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

/**
 * @author hrickens
 * @author $Author$
 * @version $Revision$
 * @since 23.07.2007
 */
public abstract class NodeConfig extends Composite {

    private final class IconChooserDialog extends Dialog {
        private final Node _node;
        /*
         * (non-Javadoc)
         * 
         * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
         */
        private IconManageView _iconManageView;

        private IconChooserDialog(Shell parentShell, Node node) {
            super(parentShell);
            this.setShellStyle(SWT.RESIZE | SWT.BORDER | SWT.CLOSE | SWT.MIN | SWT.MAX | SWT.TITLE
                    | SWT.ON_TOP | // SWT.TOOL| SWT.SHEET|
                    SWT.PRIMARY_MODAL);
            _node = node;
        }

        @Override
        protected Control createDialogArea(Composite parent) {
            Composite createDialogArea = (Composite) super.createDialogArea(parent);
            createDialogArea.setLayout(GridLayoutFactory.fillDefaults().create());
            _iconManageView = new IconManageView(createDialogArea, SWT.NONE, _node);
            return _iconManageView;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected void configureShell(final Shell shell) {
            super.configureShell(shell);
            shell.setText("Icon chooser");
            shell.setMaximized(true);
        }

        public NodeImage getImage() {
            return _iconManageView.getSelectedImage();
        }
    }

    /**
     * 
     * @author hrickens
     * @author $Author$
     * @version $Revision$
     * @since 03.06.2009
     */
    private final class SaveSelectionListener implements SelectionListener {
        public void widgetDefaultSelected(final SelectionEvent e) {
            store();
        }

        public void widgetSelected(final SelectionEvent e) {
            store();
        }
    }

    /**
     * 
     * @author hrickens
     * @author $Author$
     * @version $Revision$
     * @since 03.06.2009
     */
    private final class CancelSelectionListener implements SelectionListener {
        public void widgetDefaultSelected(final SelectionEvent e) {
        }

        public void widgetSelected(final SelectionEvent e) {
            if (getNode().isPersistent()) {
                cancel();
                getDocumentationManageView().cancel();
                setSaveButtonSaved();
            } else {
                boolean openQuestion = MessageDialog.openQuestion(getShell(), "Cancel",
                        "You dispose this " + getNode().getClass().getSimpleName() + "?");
                if (openQuestion) {
                    setSaveButtonSaved();
                    getProfiBusTreeView().getTreeViewer().setSelection(
                            getProfiBusTreeView().getTreeViewer().getSelection());
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
     * @author $Author$
     * @version $Revision$
     * @since 03.06.2009
     */
    private final class NodeConfigModifyListener implements ModifyListener {
        public void modifyText(final ModifyEvent e) {
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

    /**
     * The Button to save Changes.
     */
    private Button _saveButton;

    /**
     * if true an new SubNet a created also modified _slave.
     */
    private boolean _new = false;

    /**
     * A ModifyListener that set the save button enable to store the changes. Works wiht
     * {@link Text}, {@link Combo} and {@link Spinner}.
     */
    private final ModifyListener _mLSB = new NodeConfigModifyListener();

    /**
     * Contain all different events that have change a Value and the status of the change. The
     * status means is the new Value a differnt from the origin Value.
     */
    private HashMap<String, Boolean> _saveButtonEvents = new HashMap<String, Boolean>();

    /**
     * The Tabview to Manage the documentation of Node.
     */
    private DocumentationManageView _documentationManageView;

    /**
     * The text field for the name of the node.
     */
    private Text _nameText;
    /**
     * The Spinner for the channel index.
     */
    private Spinner _indexSpinner;
    /**
     * The Button to cancel Changes.
     */
    private Button _cancelButton;
    /**
     * The Tabfolder for a config view.
     */
    private TabFolder _tabFolder;
    /**
     * The warn background color (SWT.COLOR_RED) of the index Spinner.
     */
    protected final static Color WARN_BACKGROUND_COLOR = CustomMediaFactory.getInstance().getColor(
            CustomMediaFactory.COLOR_RED);
    /**
     * The TreeViewer.
     */
    private ProfiBusTreeView _profiBusTreeView;

    private Text _descText;

    private static Font _font;

    private static Font _fontBold;

    private static GridDataFactory _labelGridData = GridDataFactory.fillDefaults();

    private static GridDataFactory _textGridData = GridDataFactory.fillDefaults().grab(true, false);

    /**
     * {@inheritDoc}
     * 
     * @param parent
     *            The parent Composite.
     * @param headline
     *            The Headline of this Config View.
     * @param node
     * @param nevv
     *            It is true generate a new Node otherwise configured a available Node.
     * 
     */
    public NodeConfig(final Composite parent, final ProfiBusTreeView profiBusTreeView,
            final String headline, final Node node, final boolean nevv) {
        super(parent, SWT.NONE);
        setProfiBusTreeView(profiBusTreeView);
        _new = nevv;
        setBackgroundComposite(headline, node);
    }

    /**
     * Fill the View whit data from GSDFile.
     * 
     * @param gsdFile
     *            the GSDFile whit the data.
     * @return true when set the data ok.
     */
    public abstract boolean fill(GSDFile gsdFile);

    /**
     * 
     * @return the GSD File of this Node.
     */
    public abstract GSDFile getGSDFile();

    /**
     * 
     * @return the node which was configured with this View.
     */
    public abstract Node getNode();

    /**
     * Store the Header Data in {@link Node} DB object.
     */
    public void store() {
        // Header
        Date now = new Date();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
        Text text = (Text) this.getData("version");
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
        ((Text) this.getData("modifiedOn")).setText(df.format(now));
        ((Text) this.getData("modifiedBy")).setText(ConfigHelper.getUserName());
        // df = null;
        // now = null;

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
    public final void setSavebuttonEnabled(final String event, final boolean enabled) {
        if (event != null) {
            _saveButtonEvents.put(event, enabled);
        }
        boolean enab = !getNode().isPersistent()
                || _saveButtonEvents.containsValue(new Boolean(true));
        _saveButton.setEnabled(enab);
        getNode().setDirty(enab);
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
    }

    /**
     * 
     * @param selectionListener
     */
    public final void setSaveButtonSelectionListener(final SelectionListener selectionListener) {
        _saveButton.addSelectionListener(selectionListener);
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
    private void setBackgroundComposite(final String headline, final Node node) {
        int columnNum = 5;
        this.setLayout(new GridLayout(columnNum, true));
        Label headlineLabel = new Label(this, SWT.NONE);
        if (_font == null) {
            _font = headlineLabel.getFont();
        }
        if (_fontBold == null) {
            FontData fontData = _font.getFontData()[0];
            fontData.setStyle(SWT.BOLD);
            fontData.setHeight(fontData.getHeight() + 2);
            _fontBold = CustomMediaFactory.getInstance().getFont(fontData);
        }
        headlineLabel.setFont(_fontBold);
        headlineLabel.setText(headline);
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
            if (securityFacade != null && securityFacade.getCurrentUser() != null
                    && securityFacade.getCurrentUser().getUsername() != null) {
                temp = securityFacade.getCurrentUser().getUsername();

            }
        } else if (node != null && node.getCreatedBy() != null) {
            temp = node.getCreatedBy();
        }
        final Text creatByText = getNewText(header, temp);
        this.setData("creatBy", creatByText);
        temp = "";
        // -- Created on
        getNewLabel(header, "Created on:");
        /** The description field with the name of the Date of the last change. */

        Date now = new Date();
        if (_new) {
            temp = ConfigHelper.getSimpleDateFormat().format(now);
        } else if (node != null && node.getCreatedOn() != null) {
            now = node.getCreatedOn();
            temp = now.toString();
        }

        final Text creatOnText = getNewText(header, temp);
        this.setData("date", now);
        this.setData("creatOn", creatOnText);
        temp = "";
        // -- Version
        getNewLabel(header, "Version:");
        /** The description field with the Version from GSD File. */
        if (node != null) {
            temp = node.getVersion() + "";
        }
        final Text versionText = getNewText(header, temp);
        this.setData("version", versionText); // -- Modifierd by

        final Label iconButton = new Label(header, SWT.BORDER);
        iconButton.setLayoutData(GridDataFactory.swtDefaults().span(1, 3).minSize(40, 40).create());
        iconButton.setImage(ConfigHelper.getImageFromNode(node, 30, 60));
        iconButton.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseUp(MouseEvent e) {
                // Shell shell = new Shell(NodeConfig.this.getDisplay().getActiveShell(),
                // SWT.RESIZE);
                // IconChooserDialog chooseIconDialog = new IconChooserDialog(shell, node);
                IconChooserDialog chooseIconDialog = new IconChooserDialog(NodeConfig.this
                        .getDisplay().getActiveShell(), node);

                chooseIconDialog.setBlockOnOpen(true);
                if (chooseIconDialog.open() == 0) {
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
        this.setData("modifiedBy", modifiedBy);
        temp = "";
        // -- Modifierd on
        getNewLabel(header, "Modified on:");
        /** The description field with the name of the Date of the last change. */
        if (node != null && node.getUpdatedOn() != null) {
            temp = node.getUpdatedOn().toString();
        }
        final Text modifiedOn = getNewText(header, temp);
        this.setData("modifiedOn", modifiedOn);
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

        if (node instanceof Master || node instanceof Slave) {
            getNewLabel(header, "Version from GSD:");

            if (node != null && node.getUpdatedOn() != null) {
                // TODO: GSD Versionsnummer setzen.
                temp = node.getVersion() + "";
            }
            final Text version = getNewText(header, temp);
            this.setData("gsdVersion", version);
        }

        setTabFolder(new TabFolder(this, SWT.H_SCROLL | SWT.V_SCROLL));
        getTabFolder().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 5, 1));
        getTabFolder().setBackgroundMode(SWT.INHERIT_DEFAULT);

        // -Footer
        new Label(this, SWT.NONE);
        _saveButton = new Button(this, SWT.PUSH);
        if (_new) {
            _saveButton.setText("Create");
        } else {
            _saveButton.setText("&Save");
        }
        _saveButton.setLayoutData(labelGridData.create());
        setSaveButtonSelectionListener(new SaveSelectionListener());
        new Label(this, SWT.NONE);
        setCancelButton(new Button(this, SWT.PUSH));
        getCancelButton().setText("&Cancel");
        getCancelButton().setLayoutData(labelGridData.create());
        getCancelButton().addSelectionListener(new CancelSelectionListener());
        new Label(this, SWT.NONE);
        header.setTabList(new Control[0]);
    }

    private static Label getNewLabel(Group header, String string) {
        Label newLabel = new Label(header, SWT.NONE);
        newLabel.setLayoutData(_labelGridData.create());
        newLabel.setText(string);
        return newLabel;
    }

    private static Text getNewText(Group header, String text) {
        final Text newText = new Text(header, SWT.NONE);
        newText.setLayoutData(_textGridData.create());
        newText.setEditable(false);
        newText.setText(text);
        return newText;
    }

    private Group makeBackgroundGroup(int columnNum) {
        Group header = new Group(this, SWT.H_SCROLL);
        header.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, columnNum, 1));
        header.setLayout(new GridLayout(7, false));
        header.setText("General Information");
        header.setTabList(new Control[0]);
        return header;
    }

    /**
     * Set a int as text to a {@link Text} and store the text into Data as origin value.
     * 
     * @param textField
     *            The Text field to set the text
     * @param textValue
     *            The int was set.
     */
    final void setText(final Text textField, final int textValue, final int limit) {
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
    final void setText(final Text textField, final String text, final int limit) {
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
     * Select a text to a {@link Combo} and store the index into Data as origin value.
     * 
     * @param comboField
     *            The Combo field to set the index
     * @param value
     *            The value was select.
     */
    final void setCombo(final Combo comboField, final String value) {
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
        } else {
            // refresh the View
            getProfiBusTreeView().refresh(getNode());
        }
        _new = false;
    }

    /**
     * @param exception
     *            A exception to show in a Dialog,
     */
    protected void openErrorDialog(final Exception exception) {
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
                "Data Base Error", f.toString(), Status.CANCEL_STATUS, 0);
        errorDialog.open();
        f.close();
    }

    /**
     * set the documents and Icon tab item.
     */
    final void documents() {
        String head = "Documents";
        final TabItem item = new TabItem(getTabFolder(), SWT.NO_SCROLL);
        item.setText(head);
        _documentationManageView = new DocumentationManageView(getTabFolder(), SWT.NONE, this);
        item.setControl(_documentationManageView);
        getTabFolder().addSelectionListener(new SelectionListener() {

            public void widgetDefaultSelected(SelectionEvent e) {
                docTabSelectionAction(e);
            }

            public void widgetSelected(SelectionEvent e) {
                docTabSelectionAction(e);
            }

            private void docTabSelectionAction(SelectionEvent e) {
                if (e.item.equals(item)) {
                    _documentationManageView.onActivate();
                }
            }

        });

    }

    /**
     * @param head
     *            The Tab text.
     * @param rows
     *            row number for the grid.
     * @return The Composite of the generated TabItem.
     */
    protected final Composite getNewTabItem(final String head, final int rows) {
        TabItem item = new TabItem(getTabFolder(), SWT.NONE);
        item.setText(head);
        Composite comp = new Composite(getTabFolder(), SWT.NONE);
        comp.setLayout(new GridLayout(rows, false));
        item.setControl(comp);

        comp.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
        return comp;
    }

    /**
     * 
     * @return The Documentation Manage View.
     */
    final DocumentationManageView getDocumentationManageView() {
        return _documentationManageView;
    }

    /**
     * A ModifyListener that set the save button enable to store the changes. Works wiht
     * {@link Text}, {@link Combo} and {@link Spinner}.
     * 
     * @return the ModifyListener.
     */
    public final ModifyListener getMLSB() {
        return _mLSB;
    }

    /**
     * 
     * @return only true when the node has on or more changes.
     */
    public final boolean isDirty() {
        if (_saveButton != null) {
            return _saveButton.getEnabled();
        }
        return false;
    }

    public void cancel() {
        if (getDescWidget() != null) {
            setDesc((String) getDescWidget().getData());
        }
        if (getNode() != null) {
            getNode().setDirty(false);
        }
    }

    public final void setName(String name) {
        TreeItem treeItem = getProfiBusTreeView().getTreeViewer().getTree().getSelection()[0];
        treeItem.setText(name);
        _nameText.setText(name);
    }

    /**
     * Generate a new Node on his parent with a Name, creator and creation date.
     */
    protected boolean newNode() {
        return newNode("");
    }
    protected boolean newNode(String nameOffer) {

        String nodeType = getNode().getClass().getSimpleName();

        InputDialog id = new InputDialog(getShell(), "Create new " + nodeType,
                "Enter the name of the " + nodeType, nameOffer, null);
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

            if (getNode() instanceof Facility || obj == null) {
                getProfiBusTreeView().getTreeViewer().setInput(getNode());
                // TODO neue facility erstellen und speichern..
            } else if (obj instanceof Node) {
                if (getNode().getParent() == null) {
                    Node nodeParent = (Node) obj;

                    getNode().moveSortIndex(
                            nodeParent.getfirstFreeStationAddress(Node.MAX_STATION_ADDRESS));
                    nodeParent.addChild(getNode());
                }
            }
            return true;
        }
        return false;
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
    protected final void setNameWidget(Text nameText) {
        _nameText = nameText;
        _nameText.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.keyCode == SWT.CR || e.keyCode == SWT.KEYPAD_CR) {
                    _descText.setFocus();
                }
            }
        });
    }

    public void setDesc(String desc) {
        _descText.setText(desc);
    }

    public String getDesc() {
        if (_descText == null || _descText.getText() == null) {
            return "";
        }
        return _descText.getText();
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
    protected final void setDescWidget(Text descText) {
        _descText = descText;
        _descText.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.keyCode == SWT.CR || e.keyCode == SWT.KEYPAD_CR) {
                    if (e.stateMask != SWT.MOD1) {
                        e.doit = false;
                        _saveButton.setFocus();
                    }
                }
            }
        });
    }

    protected void makeDescGroup(Composite comp, int hSize) {
        Group gDesc = new Group(comp, SWT.NONE);
        gDesc.setText("Description: ");
        GridDataFactory gdf = GridDataFactory.fillDefaults().grab(true, true).span(hSize, 1)
                .minSize(200, 200);
        gDesc.setLayoutData(gdf.create());
        gDesc.setLayout(new GridLayout(2, false));

        Label shortDescLabel = new Label(gDesc, SWT.NONE);
        shortDescLabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
        shortDescLabel.setText("Short Desc:");
        
        final Text shortDescText = new Text(gDesc, SWT.BORDER | SWT.SINGLE|SWT.READ_ONLY);
        shortDescText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
        shortDescText.setEditable(false);
        final Text descText = new Text(gDesc, SWT.BORDER | SWT.MULTI);
        descText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
        descText.setEditable(true);
        descText.addModifyListener(new ModifyListener() {
            
            @Override
            public void modifyText(ModifyEvent e) {
                String text = descText.getText();
                String string = "";
                if(text!=null) {
                    String[] split = text.split("[\r\n]");
                    if(split.length>0) {
                        if(string.length()>40) {
                            string = string.substring(0,40);
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
        if (_font != null) {
            _font.dispose();
        }
        if (_fontBold != null) {
            _fontBold.dispose();
        }
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
    protected final void setIndexSpinner(Spinner indexSpinner) {
        _indexSpinner = indexSpinner;
    }

    /**
     * @param tabFolder
     *            the tabFolder to set
     */
    protected final void setTabFolder(TabFolder tabFolder) {
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
    protected final void setCancelButton(Button cancelButton) {
        _cancelButton = cancelButton;
    }

    /**
     * @return the cancelButton
     */
    protected final Button getCancelButton() {
        return _cancelButton;
    }

    /**
     * @param profiBusTreeView
     *            the profiBusTreeView to set
     */
    protected final void setProfiBusTreeView(ProfiBusTreeView profiBusTreeView) {
        _profiBusTreeView = profiBusTreeView;
    }

    /**
     * @return the profiBusTreeView
     */
    protected final ProfiBusTreeView getProfiBusTreeView() {
        return _profiBusTreeView;
    }
}
