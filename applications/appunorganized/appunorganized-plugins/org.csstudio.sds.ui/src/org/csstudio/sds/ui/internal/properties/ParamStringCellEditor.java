/*
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron,
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.csstudio.sds.model.WidgetProperty;
import org.csstudio.sds.ui.SdsUiPlugin;
import org.csstudio.sds.util.TextDnDUtil;
import org.csstudio.sds.util.TooltipResolver;
import org.csstudio.ui.util.CustomMediaFactory;
import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;

/**
 * A table cell editor for values of a multiple line String.
 *
 * @author Kai Meyer
 */
public final class ParamStringCellEditor extends AbstractDialogCellEditor {

    /**
     * The current String value.
     */
    private String _value;
    /**
     * The names of the properties and their categories.
     */
    private Map<String, WidgetProperty> _properties;

    /**
     * Creates a new string cell editor parented under the given control. The
     * cell editor value is a String.
     *
     * @param parent
     *            The parent table.
     * @param properties
     *               the names of the properties and their categories
     */
    public ParamStringCellEditor(final Composite parent, final Map<String, WidgetProperty> properties) {
        super(parent, "Parameterized Text");
        _properties = properties;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void openDialog(final Shell parentShell, final String dialogTitle) {
        MultipleLineInputDialog dialog = new MultipleLineInputDialog(parentShell,dialogTitle,"Type in the text", _value);
        if (dialog.open()==Window.OK) {
            _value = dialog.getText();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean shouldFireChanges() {
        return _value != null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Object doGetValue() {
        return _value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doSetFocus() {
        // Ignore
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doSetValue(final Object value) {
        Assert.isTrue(value instanceof String);
        this._value = (String) value;
    }

    /**
     * This class represents a TextCellEditor, which can handle multiple line text.
     *
     * @author Kai Meyer
     */
    private final class MultipleLineInputDialog extends Dialog {
        /**
         * The title of the dialog.
         */
        private String _title;
        /**
         * The message to display, or <code>null</code> if none.
         */
        private String _message;
        /**
         * The input value; the empty string by default.
         */
        private String _value = "";//$NON-NLS-1$
        /**
         * Input text widget.
         */
        private Text _text;
        /**
         * The Action to add a new property-tag.
         */
        private Action _addAction;
        /**
         * The menu for the add-action.
         */
        private Menu _actionMenu;

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
         * @param initialValue
         *            the initial input value, or <code>null</code> if none
         *            (equivalent to the empty string)
         */
        public MultipleLineInputDialog(final Shell parentShell, final String dialogTitle,
                final String dialogMessage, final String initialValue) {
            super(parentShell);
            this.setShellStyle(SWT.MODELESS | SWT.CLOSE | SWT.MAX | SWT.TITLE
                    | SWT.BORDER | SWT.RESIZE);
            _title = dialogTitle;
            _message = dialogMessage;
            if (initialValue == null) {
                _value = "";//$NON-NLS-1$
            } else {
//                _value = TooltipResolver.resolveToValue(initialValue, _properties);
                _value = initialValue;
            }
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
            Composite composite = (Composite) super.createDialogArea(parent);
            if (_message != null) {
                Label label = new Label(composite, SWT.WRAP);
                label.setText(_message);
                GridData data = new GridData(GridData.GRAB_HORIZONTAL
                        | GridData.GRAB_VERTICAL | GridData.HORIZONTAL_ALIGN_FILL
                        | GridData.VERTICAL_ALIGN_CENTER);
                data.widthHint = convertHorizontalDLUsToPixels(IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH);
                label.setLayoutData(data);
            }
            this.createActions();
            _actionMenu = this.createMenu(composite);
            Composite toolBarComposite = new Composite(composite,SWT.BORDER);
            GridLayout gridLayout = new GridLayout(1,false);
            gridLayout.marginLeft = 0;
            gridLayout.marginRight = 0;
            gridLayout.marginBottom = 0;
            gridLayout.marginTop = 0;
            gridLayout.marginHeight = 0;
            gridLayout.marginWidth = 0;
            toolBarComposite.setLayout(gridLayout);
            GridData gridData = new GridData(SWT.FILL,SWT.FILL,true,true);
            toolBarComposite.setLayoutData(gridData);

            ToolBarManager toolbarManager = new ToolBarManager(SWT.FLAT);
            ToolBar toolBar = toolbarManager.createControl(toolBarComposite);
            GridData grid = new GridData();
            grid.horizontalAlignment = GridData.FILL;
            grid.verticalAlignment = GridData.BEGINNING;
            toolBar.setLayoutData(grid);


            toolbarManager.add(_addAction);
            toolbarManager.update(true);
            _text = new Text(toolBarComposite, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
            gridData = new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL);
            gridData.heightHint = 100;
            _text.setLayoutData(gridData);
            _text.setText(_value);
            _text.setFocus();
            _text.addKeyListener(new KeyAdapter() {
                /**
                 * The key code for Return.
                 */
                private static final int RETURN = 13;

                @Override
                public void keyPressed(final KeyEvent e) {
                    if (e.keyCode == SWT.ESC) {
                        buttonPressed(IDialogConstants.CANCEL_ID);
                    } else if (e.keyCode==RETURN && e.stateMask==SWT.MOD1) {
                        buttonPressed(IDialogConstants.OK_ID);
                    }
                }
            });

            TextDnDUtil.addDnDSupport(_text);
            return composite;
        }

        /**
         * Creates the actions.
         */
        private void createActions() {
            _addAction = new Action("Add") {
                @Override
                public void run() {
                    _actionMenu.setVisible(true);
                    while(!_actionMenu.isDisposed() && _actionMenu.isVisible()){
                        if(!Display.getCurrent().readAndDispatch()){
                            Display.getCurrent().sleep();
                        }
                    }
                    _actionMenu.setVisible(false);
                }
            };
            _addAction.setToolTipText("Adds a property tag on the current position of the cursor");
            _addAction.setImageDescriptor(CustomMediaFactory.getInstance()
                    .getImageDescriptorFromPlugin(SdsUiPlugin.PLUGIN_ID,
                    "icons/add.gif"));
        }

        /**
         * Creates the popup-menu for adding a property-tag.
         * @param control The {@link Control} for the menu
         * @return The resulting menu
         */
        private Menu createMenu(final Control control) {
            MenuManager listMenu = new MenuManager();
            // get all categories
            List<String> categoryNames = new ArrayList<String>();
            String[] propertyNames = _properties.keySet().toArray(new String[0]);
            Arrays.sort(propertyNames);
            for (String propertyName : propertyNames) {
                String categoryName = _properties.get(propertyName).getCategory().name();
                if (!categoryNames.contains(categoryName)) {
                    categoryNames.add(categoryName);
                }
            }
            String[] categories = categoryNames.toArray(new String[categoryNames.size()]);
            // sort categories
            Arrays.sort(categories);
            // create submenus for the categories
            for (String category : categories) {
                MenuManager categoryMenu = new MenuManager(category,category);
                listMenu.add(categoryMenu);
            }
            // fill submenus with entries
            for (String key : _properties.keySet()) {
                WidgetProperty p = _properties.get(key);
                String categoryName = p.getCategory().name();
                MenuManager subMenu = (MenuManager) listMenu.find(categoryName);
                subMenu.add(new ParamAction(key, p.getDescription()));
            }
            return listMenu.createContextMenu(control);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected void buttonPressed(final int buttonId) {
            if (buttonId == IDialogConstants.OK_ID) {
                _value = _text.getText();
            } else {
                _value = null;
            }
            super.buttonPressed(buttonId);
        }

         /**
         * Returns the string typed into this dialog.
         *
         * @return the input string
         */
        public String getText() {
            return _value;
        }

        /**
         * An {@link Action} which adds a parameter on the current position of the cursor.
         * @author Kai Meyer
         *
         */
        private final class ParamAction extends Action {

            /**
             * The name of the property.
             */
            private String _tooltipVariable;

            private String _description;

            /**
             * Constructor.
             * @param tooltipVariable The name of the property
             */
            public ParamAction(final String tooltipVariable, String description) {
                _tooltipVariable = tooltipVariable;
                this.setText("Add '"+description+"'-Tag");
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public void run() {
                String text = _text.getText();
                int caretPosition = _text.getCaretPosition();
                int textLength = _text.getText().length();
                if (caretPosition<textLength) {
                    String prefix = text.substring(0,caretPosition);
                    String postfix = text.substring(caretPosition, text.length());
                    _text.setText(prefix+TooltipResolver.START_SEPARATOR+_tooltipVariable+TooltipResolver.END_SEPARATOR+postfix);
                } else {
                    _text.setText(text+TooltipResolver.START_SEPARATOR+_tooltipVariable+TooltipResolver.END_SEPARATOR);
                }
            }
        }

    }

}
