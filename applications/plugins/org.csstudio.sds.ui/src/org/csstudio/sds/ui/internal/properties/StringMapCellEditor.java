/*
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchroton,
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
package org.csstudio.sds.ui.internal.properties;

import java.util.HashMap;
import java.util.Map;

import org.csstudio.dal.ui.dnd.rfc.IProcessVariableAdressReceiver;
import org.csstudio.dal.ui.dnd.rfc.ProcessVariableExchangeUtil;
import org.csstudio.platform.model.pvs.IProcessVariableAddress;
import org.csstudio.sds.ui.SdsUiPlugin;
import org.csstudio.sds.util.TextDnDUtil;
import org.csstudio.ui.util.CustomMediaFactory;
import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;

/**
 * A table cell editor for values of type Map(String, String).
 *
 * @author Kai Meyer
 */
public final class StringMapCellEditor extends AbstractDialogCellEditor {

    /**
     * The minimum count of entries in the list.
     */
    public static final int MINIMUM_ENTRY_COUNT = 0;

    /**
     * A default key for the Map.
     */
    private static final String DEFAULT_KEY = "channel";

    /**
     * The current map.
     */
    private Map<String, String> _map;
    /**
     * A copy of the original map.
     */
    private Map<String, String> _originalMap;

    /**
     * Creates a new string cell editor parented under the given control. The
     * cell editor value is a Map of Strings.
     *
     * @param parent
     *            The parent table.
     * @param title
     *               The title for this CellEditor
     */
    public StringMapCellEditor(final Composite parent, final String title) {
        super(parent, title);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void openDialog(final Shell parentShell, final String dialogTitle) {
        final MapInputDialog dialog = new MapInputDialog(parentShell,dialogTitle,"Add, edit or remove the values");
        if (dialog.open()==Window.CANCEL) {
            _map = _originalMap;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean shouldFireChanges() {
        return _map != null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Object doGetValue() {
        return _map;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    protected void doSetValue(final Object value) {
        Assert.isTrue(value instanceof Map);
        _map = new HashMap<String, String>();
        _originalMap = (Map<String, String>) value;
        for (final String key : _originalMap.keySet()) {
            _map.put(key, _originalMap.get(key));
        }
    }

    /**
     * This class represents a Dialog to add, edit and remove the entries of a Map.
     *
     * @author Kai Meyer
     */
    private final class MapInputDialog extends Dialog {
        /**
         * The title of the dialog.
         */
        private final String _title;
        /**
         * The message to display, or <code>null</code> if none.
         */
        private final String _message;
        /**
         * The List-Widget.
         */
        private ListViewer _viewer;
        /**
         * Adds new entries to the List.
         */
        private Action _addAction;
        /**
         * Edits the selected entry.
         */
        private Action _editAction;
        /**
         * Removes the selected entries from the List.
         */
        private Action _removeAction;
        /**
         * The entry dialog of this MapInputDialog.
         */
        private MapEntryDialog _dialog = null;

        /**
         * Creates an input dialog with OK and Cancel buttons. Note that the dialog
         * will have no visual representation (no widgets) until it is told to open.
         * <p>
         * Note that the <code>open</code> method blocks for input dialogs.
         * </p>
         *
         * @param parentShell
         *            the parent shell, or <code>null</code> to create a top-level
         *            shell
         * @param dialogTitle
         *            the dialog title, or <code>null</code> if none
         * @param dialogMessage
         *            the dialog message, or <code>null</code> if none
         */
        public MapInputDialog(final Shell parentShell, final String dialogTitle,
                final String dialogMessage) {
            super(parentShell);
            this.setShellStyle(SWT.MODELESS | SWT.CLOSE | SWT.MAX | SWT.TITLE
                    | SWT.BORDER | SWT.RESIZE);
            _title = dialogTitle;
            _message = dialogMessage;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected void configureShell(final Shell shell) {
            super.configureShell(shell);
            if (_title != null) {
                shell.setText(_title);
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected Control createDialogArea(final Composite parent) {
            final Composite composite = (Composite) super.createDialogArea(parent);
            composite.setLayout(new GridLayout(2, false));

            if (_message != null) {
                final Label label = new Label(composite, SWT.WRAP);
                label.setText(_message);
                final GridData data = new GridData(GridData.GRAB_HORIZONTAL
                        | GridData.GRAB_VERTICAL | GridData.HORIZONTAL_ALIGN_FILL
                        | GridData.VERTICAL_ALIGN_CENTER);
                data.horizontalSpan = 2;
                data.widthHint = convertHorizontalDLUsToPixels(IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH);
                label.setLayoutData(data);
                //label.setFont(parent.getFont());
            }

            final Composite toolBarComposite = new Composite(composite,SWT.BORDER);
            final GridLayout gridLayout = new GridLayout(1,false);
            gridLayout.marginLeft = 0;
            gridLayout.marginRight = 0;
            gridLayout.marginBottom = 0;
            gridLayout.marginTop = 0;
            gridLayout.marginHeight = 0;
            gridLayout.marginWidth = 0;
            toolBarComposite.setLayout(gridLayout);
            final GridData grid = new GridData(SWT.FILL,SWT.FILL,true,true);
            toolBarComposite.setLayoutData(grid);

            final ToolBarManager toolbarManager = new ToolBarManager(SWT.FLAT);
            final ToolBar toolBar = toolbarManager.createControl(toolBarComposite);
            final GridData gid = new GridData();
            gid.horizontalAlignment = GridData.FILL;
            gid.verticalAlignment = GridData.BEGINNING;
            toolBar.setLayoutData(gid);

            this.createActions(toolbarManager);
            _viewer = this.createListViewer(toolBarComposite);
            this.hookPopupMenu(_viewer);
            this.hookDoubleClick(_viewer);

            //DialogFontUtil.setDialogFont(composite);
            //applyDialogFont(composite);
            return composite;
        }

        /**
         * Creates Actions and adds them to the given {@link ToolBarManager}.
         * @param manager
         *             The ToolBarManager, which should contain the actions
         */
        private void createActions(final ToolBarManager manager) {
            _addAction = new Action() {
                @Override
                public void run() {
                    openMapDialog(null, null, true);
                    refreshAction();
                }
            };
            _addAction.setText("Add "+_title);
            _addAction.setToolTipText("Adds a new "+_title+" to the list");
            _addAction.setImageDescriptor(CustomMediaFactory.getInstance()
                    .getImageDescriptorFromPlugin(SdsUiPlugin.PLUGIN_ID,
                    "icons/add.gif"));
            manager.add(_addAction);
            _editAction = new Action() {
                @Override
                public void run() {
                    final String key = (String) ((IStructuredSelection)_viewer.getSelection()).getFirstElement();
                    openMapDialog(key, _map.get(key), false);
                    refreshAction();
                    setFocus();
                }
            };
            _editAction.setText("Edit "+_title);
            _editAction.setToolTipText("Edits the selected "+_title);
            _editAction.setImageDescriptor(CustomMediaFactory.getInstance()
                    .getImageDescriptorFromPlugin(SdsUiPlugin.PLUGIN_ID,
                    "icons/edit.gif"));
            _editAction.setEnabled(false);
            manager.add(_editAction);
            _removeAction = new Action() {
                @Override
                public void run() {
                    removeMapEntry();
                    refreshAction();
                }
            };
            _removeAction.setText("Remove "+_title);
            _removeAction.setToolTipText("Removes the selected "+_title+" from the list");
            _removeAction.setImageDescriptor(CustomMediaFactory.getInstance()
                    .getImageDescriptorFromPlugin(SdsUiPlugin.PLUGIN_ID,
                    "icons/delete.gif"));
            _removeAction.setEnabled(false);
            manager.add(_removeAction);
            manager.update(true);
        }

        /**
         * Creates the viewer for the List.
         * @param parent
         *                 The parent composite for the viewer
         * @return ListViewer
         *                 The ListViewer
         */
        private ListViewer createListViewer(final Composite parent) {
            final ListViewer viewer = new ListViewer(parent);
            viewer.setContentProvider(new ArrayContentProvider());
            viewer.setLabelProvider(new LabelProvider() {
                @Override
                public String getText(final Object element) {
                    return element.toString()+": "+_map.get(element);
                }
            });
            viewer.setInput(_map.keySet().toArray(new String[_map.keySet().size()]));
            final GridData gridData = new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL);
            gridData.verticalSpan = 6;
            gridData.heightHint = 150;
            viewer.getList().setLayoutData(gridData);
            viewer.getList().addSelectionListener(new SelectionAdapter() {
                /**
                 * {@inheritDoc}
                 */
                @Override
                public void widgetSelected(final SelectionEvent e) {
                    _editAction.setEnabled(viewer.getList().getSelectionIndices().length==1);
                    refreshAction();
                }
            });
            viewer.getList().setFocus();

            // DnD
            ProcessVariableExchangeUtil.addProcessVariableAddressDropSupport(viewer.getControl(), DND.DROP_COPY | DND.DROP_MOVE, new IProcessVariableAdressReceiver(){
                @Override
                public void receive(final IProcessVariableAddress[] pvs,
                        final DropTargetEvent event) {
                    if(pvs.length>0) {
                        openMapDialog(DEFAULT_KEY, pvs[0].getFullName(), true);
                    }
                }
            });

            return viewer;
        }

        /**
         * Adds a Popup menu to the given ListViewer.
         * @param viewer
         *             The ListViewer
         */
        private void hookPopupMenu(final ListViewer viewer) {
            final MenuManager popupMenu = new MenuManager();
            popupMenu.add(_addAction);
            popupMenu.add(_editAction);
            popupMenu.add(new Separator());
            popupMenu.add(_removeAction);
            final Menu menu = popupMenu.createContextMenu(viewer.getList());
            viewer.getList().setMenu(menu);
        }

        /**
         * Adds doubleclick support to the given ListViewer.
         * @param viewer
         *             The Listviewer
         */
        private void hookDoubleClick(final ListViewer viewer) {
            viewer.getControl().addMouseListener(new MouseAdapter() {
                @Override
                public void mouseDoubleClick(final MouseEvent e) {
                    if (_viewer.getList().getSelectionCount()==1) {
                        _editAction.run();
                    } else {
                        _addAction.run();
                    }
                }
            });
        }

        /**
         * Opens a Dialog for adding a new Point.
         * @param initKey
         *                 The initial key for the Dialog
         * @param initValue
         *                 The initial value for the Dialog
         * @param isNew
         *                 True, if the entry is new, false otherwise
         */
        private void openMapDialog(final String initKey, final String initValue, final boolean isNew) {
            int index = _viewer.getList().getItemCount();
            final int[] selectedIndices = _viewer.getList().getSelectionIndices();
            if (selectedIndices.length>0) {
                index = selectedIndices[0];
            }
            try {
                if (_dialog!=null) {
                    _dialog.close();
                }
                _dialog = new MapEntryDialog(this.getParentShell(),"Alias", null, initKey, initValue, isNew);
                this.getButton(IDialogConstants.CANCEL_ID).setEnabled(false);
                this.getButton(IDialogConstants.OK_ID).setEnabled(false);
                if (_dialog.open()==Window.OK) {
                    if (!_viewer.getList().isDisposed()) {
                        _viewer.setInput(_map.keySet());
                        _viewer.refresh();
                    }
                }
                _dialog = null;

                if (!_viewer.getList().isDisposed()) {
                    this.getButton(IDialogConstants.CANCEL_ID).setEnabled(true);
                    this.getButton(IDialogConstants.OK_ID).setEnabled(true);
                    _viewer.getList().setSelection(index);
                }
                getShell().setFocus();
            } catch (final Exception e) {
                e.printStackTrace();
            }
        }

        /**
         * Removes the current selected map entry from the List.
         */
        private void removeMapEntry() {
            if (_viewer.getList().getSelectionIndices().length>0) {
                for (final Object o : ((IStructuredSelection)_viewer.getSelection()).toArray()) {
                    final String key = (String) o;
                    _map.remove(key);
                }
            }
            _viewer.setInput(_map.keySet());
            _viewer.refresh();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean close() {
            if (_dialog!=null) {
                _dialog.close();
            }
            return super.close();
        }

        /**
         * Enables or disables the RemoveButton.
         */
        private void refreshAction() {
            if (_viewer.getList().getItemCount()>MINIMUM_ENTRY_COUNT) {
                _removeAction.setEnabled(_viewer.getList().getSelectionIndices().length>0);
            } else {
                _removeAction.setEnabled(false);
            }
            _editAction.setEnabled(_viewer.getList().getSelectionCount()==1);
        }

    }

    /**
     * This class represents a Dialog for editing a map entry.
     * @author Kai Meyer
     */
    private final class MapEntryDialog extends Dialog {
        /**
         * The title of the dialog.
         */
        private final String _title;
        /**
         * The message to display, or <code>null</code> if none.
         */
        private final String _message;
        /**
         * The key for the map; name of the entry.
         */
        private final String _key;
        /**
         * The value for the map; value of the entry.
         */
        private final String _value;
        /**
         * The Text for the name of the entry.
         */
        private Text _nameText;
        /**
         * The Text for the value of the entry.
         */
        private Text _valueText;

        /**
         * A boolean, which indicates if the entry is new.
         */
        private final boolean _isNew;

        /**
         * Creates an input dialog with OK and Cancel buttons. Note that the dialog
         * will have no visual representation (no widgets) until it is told to open.
         * <p>
         * Note that the <code>open</code> method blocks for input dialogs.
         * </p>
         *
         * @param parentShell
         *            the parent shell, or <code>null</code> to create a top-level
         *            shell
         * @param dialogTitle
         *            the dialog title, or <code>null</code> if none
         * @param dialogMessage
         *            the dialog message, or <code>null</code> if none
         * @param initialKey
         *            the initial input key, or <code>null</code> if none
         * @param initialValue
         *            the initial input value, or <code>null</code> if none
         * @param isNew
         *               true, if the entry is new, false otherwise
         */
        public MapEntryDialog(final Shell parentShell, final String dialogTitle,
                final String dialogMessage, final String initialKey, final String initialValue,
                final boolean isNew) {
            super(parentShell);
            this.setShellStyle(SWT.MODELESS | SWT.CLOSE | SWT.MAX | SWT.TITLE
                    | SWT.BORDER | SWT.RESIZE);
            _title = dialogTitle;
            _message = dialogMessage;
            _key = initialKey;
            _value = initialValue;
            _isNew = isNew;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected void configureShell(final Shell shell) {
            super.configureShell(shell);
            if (_title != null) {
                shell.setText(_title);
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected Control createDialogArea(final Composite parent) {
            final Composite composite = (Composite) super.createDialogArea(parent);
            composite.setLayout(new GridLayout(2, false));
            if (_message != null) {
                final Label label = new Label(composite, SWT.WRAP);
                label.setText(_message);
                final GridData data = new GridData(GridData.GRAB_HORIZONTAL
                        | GridData.GRAB_VERTICAL | GridData.HORIZONTAL_ALIGN_FILL
                        | GridData.VERTICAL_ALIGN_CENTER);
                data.horizontalSpan = 2;
                data.widthHint = convertHorizontalDLUsToPixels(IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH);
                label.setLayoutData(data);
                //label.setFont(parent.getFont());
            }
            _nameText = this.createTextEntry(composite, "Name:", _key);
            _valueText = this.createTextEntry(composite, "Value:", _value);

            //DialogFontUtil.setDialogFont(composite);
            //applyDialogFont(composite);
            return composite;
        }

        /**
         * Creates a Label and a Text.
         * @param parent
         *                 The parent composite for the Widgets
         * @param labelTitle
         *                 The title for the Label
         * @param textValue
         *                 The initial value for the Text
         * @return Text
         *                 The Text-Widget
         */
        private Text createTextEntry(final Composite parent, final String labelTitle, final String textValue) {
            final Label label = new Label(parent, SWT.NONE);
            label.setText(labelTitle);
            final Text text = new Text(parent, SWT.MULTI | SWT.BORDER);
            final GridData gd = new GridData(SWT.FILL, SWT.FILL, true, false);
            gd.widthHint = 280;
            text.setLayoutData(gd);
            if (textValue==null) {
                text.setText("");
            } else {
                text.setText(textValue);
            }

            ProcessVariableExchangeUtil.addProcessVariableAddressDropSupport(text, DND.DROP_COPY | DND.DROP_MOVE, new IProcessVariableAdressReceiver(){
                @Override
                public void receive(final IProcessVariableAddress[] pvs, final DropTargetEvent event) {
                    text.setText(pvs[0].getRawName());
                }
            });

            TextDnDUtil.addDragSupport(text);

            text.addKeyListener(new KeyListener() {
                @Override
                public void keyPressed(final KeyEvent e) {
                    if (e.keyCode==13) {
                        getButton(IDialogConstants.OK_ID).setFocus();
                        okPressed();
                    }
                }

                @Override
                public void keyReleased(final KeyEvent e) {
                }
            });
            return text;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected void okPressed() {
            this.getButton(IDialogConstants.OK_ID).setFocus();
            if (_nameText.getText()!=null && _nameText.getText().trim().length()>0) {
                if (_key!=null && _map.containsKey(_key) && !_isNew) {
                    _map.remove(_key);
                }
                _map.put(_nameText.getText(), _valueText.getText());
            }
            super.okPressed();
        }
    }

}
