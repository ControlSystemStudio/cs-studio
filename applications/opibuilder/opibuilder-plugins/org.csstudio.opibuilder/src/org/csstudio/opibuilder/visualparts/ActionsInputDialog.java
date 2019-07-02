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

package org.csstudio.opibuilder.visualparts;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.csstudio.opibuilder.OPIBuilderPlugin;
import org.csstudio.opibuilder.preferences.PreferencesHelper;
import org.csstudio.opibuilder.properties.AbstractWidgetProperty;
import org.csstudio.opibuilder.widgetActions.AbstractWidgetAction;
import org.csstudio.opibuilder.widgetActions.ActionsInput;
import org.csstudio.opibuilder.widgetActions.WidgetActionFactory;
import org.csstudio.opibuilder.widgetActions.WidgetActionFactory.ActionType;
import org.csstudio.ui.util.CustomMediaFactory;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.model.BaseWorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;

/**The dialog to configure actions input.
 * @author Xihui Chen,  Kai Meyer (part of the code is copied from SDS)
 *
 */
public class ActionsInputDialog extends HelpTrayDialog {

    private Action addAction;
    private Action copyAction;
    private Action removeAction;
    private Action moveUpAction;
    private Action moveDownAction;

    private TableViewer actionsViewer;

    private TableViewer propertiesViewer;

    private LinkedList<AbstractWidgetAction> actionsList;
    private boolean hookedUpFirstActionToWidget;
    private boolean hookedUpAllActionsToWidget;

    private boolean showHookOption = true;
    private ActionsInput actionsInput;
    private String title;
    private Button hookFirstCheckBox;

    public ActionsInputDialog(Shell parentShell,
            ActionsInput actionsInput, String dialogTitle, boolean showHookOption) {
        super(parentShell);
        setShellStyle(getShellStyle() | SWT.RESIZE);
        this.actionsInput = actionsInput.getCopy();
        this.actionsList = this.actionsInput.getActionsList();
        hookedUpFirstActionToWidget = actionsInput.isFirstActionHookedUpToWidget();
        hookedUpAllActionsToWidget = actionsInput.isHookUpAllActionsToWidget();
        title = dialogTitle;
        this.showHookOption = showHookOption;
    }

    public ActionsInput getOutput() {
        ActionsInput actionsInput = new ActionsInput(actionsList);
        actionsInput.setHookUpFirstActionToWidget(hookedUpFirstActionToWidget);
        actionsInput.setHookUpAllActionsToWidget(hookedUpAllActionsToWidget);
        return actionsInput;
    }

    @Override
    protected void okPressed() {
        propertiesViewer.getTable().forceFocus();
        super.okPressed();
    }

    @Override
    protected String getHelpResourcePath() {
        return "/" + OPIBuilderPlugin.PLUGIN_ID + "/html/Actions.html"; //$NON-NLS-1$; //$NON-NLS-2$
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void configureShell(final Shell shell) {
        super.configureShell(shell);
        if (title != null) {
            shell.setText(title);
        }
    }

    /**
     * Creates a label with the given text.
     *
     * @param parent
     *            The parent for the label
     * @param text
     *            The text for the label
     */
    private void createLabel(final Composite parent, final String text) {
        Label label = new Label(parent, SWT.WRAP);
        label.setText(text);
        label.setLayoutData(new GridData(SWT.FILL, SWT.BOTTOM, false,
                false, 2, 1));
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        final Composite parent_Composite = (Composite) super.createDialogArea(parent);

        final Composite mainComposite = new Composite(parent_Composite, SWT.None);
        mainComposite.setLayout(new GridLayout(2, false));
        GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
        gridData.heightHint = 250;
        mainComposite.setLayoutData(gridData);
        final Composite leftComposite = new Composite(mainComposite, SWT.None);
        leftComposite.setLayout(new GridLayout(1, false));
        GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
        gd.widthHint = 250;
        leftComposite.setLayoutData(gd);
        createLabel(leftComposite, "Actions:");

        Composite toolBarComposite = new Composite(leftComposite, SWT.BORDER);
        GridLayout gridLayout = new GridLayout(1, false);
        gridLayout.marginLeft = 0;
        gridLayout.marginRight = 0;
        gridLayout.marginBottom = 0;
        gridLayout.marginTop = 0;
        gridLayout.marginHeight = 0;
        gridLayout.marginWidth = 0;
        toolBarComposite.setLayout(gridLayout);
        gd = new GridData(SWT.FILL, SWT.FILL, true, true);
        toolBarComposite.setLayoutData(gd);

        ToolBarManager toolbarManager = new ToolBarManager(SWT.FLAT);
        ToolBar toolBar = toolbarManager.createControl(toolBarComposite);
        GridData grid = new GridData();
        grid.horizontalAlignment = GridData.FILL;
        grid.verticalAlignment = GridData.BEGINNING;
        toolBar.setLayoutData(grid);
        createActions();
        toolbarManager.add(addAction);
        toolbarManager.add(copyAction);
        toolbarManager.add(removeAction);
        toolbarManager.add(moveUpAction);
        toolbarManager.add(moveDownAction);

        toolbarManager.update(true);

        actionsViewer = createActionsTableViewer(toolBarComposite);
        actionsViewer.setInput(actionsList);

        Composite rightComposite = new Composite(mainComposite, SWT.NONE);
        rightComposite.setLayout(new GridLayout(1, false));
        gd = new GridData(SWT.FILL, SWT.FILL, true, true);
        gd.widthHint = 350;
        rightComposite.setLayoutData(gd);
        this.createLabel(rightComposite, "Properties:");

        propertiesViewer = createPropertiesViewer(rightComposite);
        Composite bottomComposite = new Composite(mainComposite, SWT.NONE);
        bottomComposite.setLayout(new GridLayout(1, false));
        bottomComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));
        if(showHookOption){
            hookFirstCheckBox = new Button(bottomComposite, SWT.CHECK);
            hookFirstCheckBox.setSelection(hookedUpFirstActionToWidget);
            hookFirstCheckBox.setText("Hook the first action to the mouse click event on widget.");
            hookFirstCheckBox.setEnabled(!hookedUpAllActionsToWidget);
            hookFirstCheckBox.addSelectionListener(new SelectionAdapter(){
                @Override
                public void widgetSelected(SelectionEvent e) {
                    hookedUpFirstActionToWidget = hookFirstCheckBox.getSelection();
                }
            });
        }

        final Button hookAllCheckBox = new Button(bottomComposite, SWT.CHECK);
        hookAllCheckBox.setSelection(hookedUpAllActionsToWidget);
        hookAllCheckBox.setText("Hook all actions to the mouse click event on widget.");
        hookAllCheckBox.addSelectionListener(new SelectionAdapter(){
            @Override
            public void widgetSelected(SelectionEvent e) {
                hookedUpAllActionsToWidget = hookAllCheckBox.getSelection();
                if(hookFirstCheckBox != null)
                    hookFirstCheckBox.setEnabled(!hookedUpAllActionsToWidget);
            }
        });

        if(actionsList.size() > 0){
            refreshActionsViewer(actionsList.get(0));
        }

        return parent_Composite;


    }

    private TableViewer createPropertiesViewer(Composite parent) {
        TableViewer viewer = new TableViewer(parent, SWT.V_SCROLL
                | SWT.H_SCROLL | SWT.BORDER | SWT.FULL_SELECTION);
        viewer.getTable().setLinesVisible(true);
        viewer.getTable().setHeaderVisible(true);
        TableViewerColumn tvColumn = new TableViewerColumn(viewer, SWT.NONE);
        tvColumn.getColumn().setText("Property");
        tvColumn.getColumn().setMoveable(false);
        tvColumn.getColumn().setWidth(100);
        tvColumn = new TableViewerColumn(viewer, SWT.NONE);
        tvColumn.getColumn().setText("Value");
        tvColumn.getColumn().setMoveable(false);
        tvColumn.getColumn().setWidth(300);
        EditingSupport editingSupport = new PropertiesEditingSupport(viewer,
                viewer.getTable());
        tvColumn.setEditingSupport(editingSupport);


        viewer.setContentProvider(new WidgetPropertiesContentProvider());
        viewer.setLabelProvider(new PropertiesLabelProvider());
        viewer.getTable().setLayoutData(
                new GridData(SWT.FILL, SWT.FILL, true, true));
        viewer.getTable().setEnabled(false);
        return viewer;
    }

    /**
     * Refreshes the enabled-state of the actions.
     */
    private void refreshGUIOnSelection() {

        IStructuredSelection selection = (IStructuredSelection) actionsViewer
                .getSelection();
        if (!selection.isEmpty()
                && selection.getFirstElement() instanceof AbstractWidgetAction) {
            removeAction.setEnabled(true);
            moveUpAction.setEnabled(true);
            moveDownAction.setEnabled(true);
            copyAction.setEnabled(true);
            propertiesViewer.setInput(((AbstractWidgetAction) selection
                    .getFirstElement()).getAllProperties());
            propertiesViewer.getTable().setEnabled(true);
        } else {
            removeAction.setEnabled(false);
            moveUpAction.setEnabled(false);
            moveDownAction.setEnabled(false);
            propertiesViewer.getTable().setEnabled(false);
            copyAction.setEnabled(false);
        }
    }


    private void refreshActionsViewer(AbstractWidgetAction widgetAction){
        actionsViewer.refresh();
        if(widgetAction == null)
            actionsViewer.setSelection(StructuredSelection.EMPTY);
        else {
            actionsViewer.setSelection(new StructuredSelection(widgetAction));
        }
    }


    /**
     * Creates and configures a {@link TableViewer}.
     *
     * @param parent
     *            The parent for the table
     * @return The {@link TableViewer}
     */
    private TableViewer createActionsTableViewer(final Composite parent) {
        TableViewer viewer = new TableViewer(parent, SWT.V_SCROLL
                | SWT.H_SCROLL | SWT.BORDER | SWT.SINGLE);
        viewer.setContentProvider(new BaseWorkbenchContentProvider() {
            @SuppressWarnings("unchecked")
            @Override
            public Object[] getElements(final Object element) {
                return (((List<AbstractWidgetAction>)element).toArray());
            }
        });
        viewer.setLabelProvider(new WorkbenchLabelProvider(){
            @Override
            protected String decorateText(String input, Object element) {
                return input + "(index: " + actionsList.indexOf(element) + ")";
            }
        });
        viewer.addSelectionChangedListener(new ISelectionChangedListener() {
            public void selectionChanged(final SelectionChangedEvent event) {
                refreshGUIOnSelection();
            }
        });
        viewer.getTable().setLayoutData(
                new GridData(SWT.FILL, SWT.FILL, true, true));
        return viewer;
    }


    /**
     * Creates the popup-menu for adding a {@link AbstractWidgetActionModel}.
     *
     * @param control
     *            The {@link Control} for the menu
     * @param withRemoveAction
     *            Indicates if an action to remove a {@link AbstractWidgetActionModel}
     *            should be added
     * @return The resulting menu
     */
    private Menu createMenu(final Control control,
            final boolean withRemoveAction) {
        MenuManager listMenu = new MenuManager();
        for (ActionType type : ActionType.values()) {
            if(type.equals(ActionType.OPEN_PHOEBUS)) {
                if(PreferencesHelper.isPhoebusIntegrated()) {
                    listMenu.add(new MenuAction(type));
                }
            } else {
                listMenu.add(new MenuAction(type));
            }
        }
        if (withRemoveAction) {
            listMenu.add(new Separator());
            listMenu.add(removeAction);
        }
        return listMenu.createContextMenu(control);
    }

    /**
     * Creates the actions.
     */
    private void createActions() {
        addAction = new Action("Add") {
            @Override
            public void run() {
            }
        };
        addAction.setMenuCreator(new IMenuCreator() {

            private Menu menu;

            @Override
            public void dispose() {
                if (menu != null) {
                    menu.dispose();
                    menu = null;
                }
            }

            @Override
            public Menu getMenu(final Control parent) {
                if (menu != null) {
                    menu.dispose();
                }
                menu = createMenu(parent, false);
                return menu;
            }

            @Override
            public Menu getMenu(final Menu parent) {
                return null;
            }

        });

        addAction.setToolTipText("Add an action");
        addAction.setImageDescriptor(CustomMediaFactory.getInstance()
                .getImageDescriptorFromPlugin(OPIBuilderPlugin.PLUGIN_ID,
                        "icons/add.gif"));

        copyAction = new Action() {
            @Override
            public void run() {
                IStructuredSelection selection = (IStructuredSelection) actionsViewer
                        .getSelection();
                if (!selection.isEmpty()
                        && selection.getFirstElement() instanceof AbstractWidgetAction) {
                    AbstractWidgetAction newAction =
                        ((AbstractWidgetAction)selection.getFirstElement()).getCopy();
                    actionsInput.addAction(newAction);
                    actionsViewer.setSelection(new StructuredSelection(newAction));
                    refreshActionsViewer(newAction);
                }
            }
        };
        copyAction.setText("Copy Action");
        copyAction
                .setToolTipText("Copy the selected action");
        copyAction.setImageDescriptor(CustomMediaFactory.getInstance()
                .getImageDescriptorFromPlugin(OPIBuilderPlugin.PLUGIN_ID,
                        "icons/copy.gif"));
        copyAction.setEnabled(false);


        removeAction = new Action() {
            @Override
            public void run() {
                IStructuredSelection selection = (IStructuredSelection) actionsViewer
                        .getSelection();
                if (!selection.isEmpty()
                        && selection.getFirstElement() instanceof AbstractWidgetAction) {
                    actionsList.remove((AbstractWidgetAction)selection.getFirstElement());
                    refreshActionsViewer(null);
                    this.setEnabled(false);
                }
            }
        };
        removeAction.setText("Remove Action");
        removeAction
                .setToolTipText("Remove the selected action from the list");
        removeAction.setImageDescriptor(CustomMediaFactory.getInstance()
                .getImageDescriptorFromPlugin(OPIBuilderPlugin.PLUGIN_ID,
                        "icons/delete.gif"));
        removeAction.setEnabled(false);

        moveUpAction = new Action() {
            @Override
            public void run() {
                IStructuredSelection selection = (IStructuredSelection) actionsViewer
                        .getSelection();
                if (!selection.isEmpty()
                        && selection.getFirstElement() instanceof AbstractWidgetAction) {
                    AbstractWidgetAction widgetAction = (AbstractWidgetAction) selection
                            .getFirstElement();
                    int i = actionsList.indexOf(widgetAction);
                    if(i>0){
                        actionsList.remove(widgetAction);
                        actionsList.add(i-1, widgetAction);
                        refreshActionsViewer(widgetAction);
                    }
                }
            }
        };
        moveUpAction.setText("Move Up Action");
        moveUpAction.setToolTipText("Move up the selected action");
        moveUpAction.setImageDescriptor(CustomMediaFactory.getInstance()
                .getImageDescriptorFromPlugin(OPIBuilderPlugin.PLUGIN_ID,
                        "icons/search_prev.gif"));
        moveUpAction.setEnabled(false);

        moveDownAction = new Action() {
            @Override
            public void run() {
                IStructuredSelection selection = (IStructuredSelection) actionsViewer
                        .getSelection();
                if (!selection.isEmpty()
                        && selection.getFirstElement() instanceof AbstractWidgetAction) {
                    AbstractWidgetAction widgetAction = (AbstractWidgetAction) selection
                            .getFirstElement();
                    int i = actionsList.indexOf(widgetAction);
                    if(i<actionsList.size()-1){
                        actionsList.remove(widgetAction);
                        actionsList.add(i+1, widgetAction);
                        refreshActionsViewer(widgetAction);
                    }
                }
            }
        };
        moveDownAction.setText("Move Down Action");
        moveDownAction.setToolTipText("Move down the selected action");
        moveDownAction.setImageDescriptor(CustomMediaFactory.getInstance()
                .getImageDescriptorFromPlugin(OPIBuilderPlugin.PLUGIN_ID,
                        "icons/search_next.gif"));
        moveDownAction.setEnabled(false);
    }

    /**
     * An {@link Action}, which adds a new {@link AbstractWidgetAction} of the
     * given {@link ActionType}.
     *
     * @author Xihui Chen
     *
     */
    private final class MenuAction extends Action {
        /**
         * The {@link ActionType}.
         */
        private ActionType type;

        /**
         * Constructor.
         *
         * @param type
         *            The {@link ActionType} for the action.
         */
        public MenuAction(final ActionType type) {
            this.type = type;
            this.setText("Add " + type.getDescription());
            this.setImageDescriptor(type.getIconImage());

        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void run() {
            AbstractWidgetAction widgetAction = WidgetActionFactory.createWidgetAction(type);
            if(widgetAction != null){
                actionsInput.addAction(widgetAction);
                refreshActionsViewer(widgetAction);
            }

        }
    }


    final static class WidgetPropertiesContentProvider extends
            ArrayContentProvider {
        @Override
        public Object[] getElements(Object inputElement) {
            if (inputElement instanceof AbstractWidgetProperty[]) {
                AbstractWidgetProperty[] oldProperties = (AbstractWidgetProperty[]) inputElement;
                List<AbstractWidgetProperty> newPropertiesList = new ArrayList<AbstractWidgetProperty>();
                for (AbstractWidgetProperty property : oldProperties) {
                    if (property.isVisibleInPropSheet())
                        newPropertiesList.add(property);
                }

                return newPropertiesList.toArray();
            }
            return super.getElements(inputElement);
        }
    }



}
