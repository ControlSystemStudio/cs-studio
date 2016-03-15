package org.csstudio.dct.ui.editor;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.dct.model.IPrototype;
import org.csstudio.dct.model.commands.AddParameterCommand;
import org.csstudio.dct.model.commands.ChangeParameterValueCommand;
import org.csstudio.dct.model.commands.RemoveParameterCommand;
import org.csstudio.dct.model.internal.Parameter;
import org.csstudio.dct.ui.Activator;
import org.csstudio.dct.ui.editor.tables.ConvenienceTableWrapper;
import org.csstudio.dct.ui.editor.tables.ITableRow;
import org.csstudio.ui.util.CustomMediaFactory;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ExpandBar;
import org.eclipse.swt.widgets.ExpandItem;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.PlatformUI;
import org.csstudio.domain.common.LayoutUtil;

/**
 * Editing component for {@link IPrototype}.
 *
 * @author Sven Wende
 *
 */
public final class PrototypeForm extends AbstractPropertyContainerForm<IPrototype> {
    private ParameterClipboard parameterClipboard;
    private ConvenienceTableWrapper parameterTable;
    private ParameterAddAction parameterAddAction;
    private ParameterRemoveAction parameterRemoveAction;
    private Button copyButton;
    private Button pasteWithValuesButton;
    private Button pasteWithoutValuesButton;

    /**
     * Constructor.
     *
     * @param editor
     *            the editor instance
     */
    public PrototypeForm(DctEditor editor) {
        super(editor);
        parameterClipboard = new ParameterClipboard();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doCreateControl(ExpandBar bar, final CommandStack commandStack) {
        super.doCreateControl(bar, commandStack);

        // create field table
        Composite composite = new Composite(bar, SWT.NONE);
        composite.setLayout(LayoutUtil.createGridLayout(1, 5, 8, 8));

        parameterTable = WidgetUtil.create3ColumnTable(composite, commandStack);
        parameterTable.getViewer().getControl().setLayoutData(LayoutUtil.createGridDataForHorizontalFillingCell(200));

        // .. add/remove buttons for properties
        Composite buttons = new Composite(composite, SWT.None);
        buttons.setLayout(new FillLayout());

        Button addButton = new Button(buttons, SWT.FLAT);
        addButton.setImage(CustomMediaFactory.getInstance().getImageFromPlugin(Activator.PLUGIN_ID, "icons/add.jpg"));
        addButton.setToolTipText("Add New Parameter");
        addButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseDown(MouseEvent e) {
                addParameter();
            }
        });

        final Button removeButton = new Button(buttons, SWT.FLAT);
        removeButton.setEnabled(false);
        removeButton.setImage(CustomMediaFactory.getInstance().getImageFromPlugin(Activator.PLUGIN_ID, "icons/delete.png"));
        removeButton.setToolTipText("Remove  Selected Parameter");
        removeButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseDown(MouseEvent e) {
                removeParameter();
            }
        });

        copyButton = new Button(buttons, SWT.FLAT);
        copyButton.setEnabled(true);
        copyButton.setImage(CustomMediaFactory.getInstance().getImageFromPlugin(Activator.PLUGIN_ID, "icons/copy.gif"));
        copyButton.setToolTipText("Copy Parameters");
        copyButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseDown(MouseEvent e) {
                parameterClipboard.setContent(getInput().getParameters());
                updatePasteButtons();
            }
        });

        pasteWithValuesButton = new Button(buttons, SWT.FLAT);
        pasteWithValuesButton.setEnabled(false);
        pasteWithValuesButton.setImage(CustomMediaFactory.getInstance().getImageFromPlugin(Activator.PLUGIN_ID, "icons/paste.gif"));
        pasteWithValuesButton.setToolTipText("Paste Parameters");
        pasteWithValuesButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseDown(MouseEvent e) {
                List<Parameter> parameters = parameterClipboard.getContent();

                CompoundCommand chain = new CompoundCommand("Paste Parameters");

                for (Parameter p : parameters) {
                    if (getInput().hasParameter(p.getName())) {
                        chain.add(new ChangeParameterValueCommand(getInput(), p.getName(), p.getDefaultValue()));
                    } else {
                        chain.add(new AddParameterCommand(getInput(), p.clone()));
                    }
                }

                if (!chain.isEmpty()) {
                    getCommandStack().execute(chain);
                }

            }
        });

        pasteWithoutValuesButton = new Button(buttons, SWT.FLAT);
        pasteWithoutValuesButton.setEnabled(false);
        pasteWithoutValuesButton.setImage(CustomMediaFactory.getInstance().getImageFromPlugin(Activator.PLUGIN_ID, "icons/paste.gif"));
        pasteWithoutValuesButton.setToolTipText("Paste Parameters (Without Default Values)");
        pasteWithoutValuesButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseDown(MouseEvent e) {
                List<Parameter> parameters = parameterClipboard.getContent();

                CompoundCommand chain = new CompoundCommand("Paste Parameters");

                for (Parameter p : parameters) {
                    if (!getInput().hasParameter(p.getName())) {

                        chain.add(new AddParameterCommand(getInput(), new Parameter(p.getName(), null)));
                    }
                }

                if (!chain.isEmpty()) {
                    getCommandStack().execute(chain);
                }
            }
        });

        parameterTable.getViewer().addSelectionChangedListener(new ISelectionChangedListener() {
            public void selectionChanged(SelectionChangedEvent event) {
                removeButton.setEnabled(!event.getSelection().isEmpty());
            }
        });

        ExpandItem expandItem = new ExpandItem(bar, SWT.NONE);
        expandItem.setText("Parameter");
        expandItem.setExpanded(true);
        expandItem.setHeight(270);
        expandItem.setControl(composite);
        expandItem.setImage(CustomMediaFactory.getInstance().getImageFromPlugin(Activator.PLUGIN_ID, "icons/tab_parameter.png"));

        // ... popup menu
        TableViewer viewer = parameterTable.getViewer();
        MenuManager popupMenu = new MenuManager();
        parameterAddAction = new ParameterAddAction(this);
        popupMenu.add(parameterAddAction);
        parameterRemoveAction = new ParameterRemoveAction(this);
        popupMenu.add(parameterRemoveAction);
        Menu menu = popupMenu.createContextMenu(viewer.getControl());
        viewer.getControl().setMenu(menu);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doSetInput(IPrototype prototype) {
        super.doSetInput(prototype);

        // prepare input for parameter table
        List<ITableRow> rowsForParameters = new ArrayList<ITableRow>();
        for (Parameter p : prototype.getParameters()) {
            rowsForParameters.add(new ParameterTableRowAdapter(p));
        }
        parameterTable.setInput(rowsForParameters);

        // update buttons states
        copyButton.setEnabled(!rowsForParameters.isEmpty());
        updatePasteButtons();
    }

    private void updatePasteButtons() {
        pasteWithValuesButton.setEnabled(!parameterClipboard.isEmpty());
        pasteWithoutValuesButton.setEnabled(!parameterClipboard.isEmpty());
    }

    /**
     * Returns the currently selected parameter.
     *
     * @return the currently selected parameter or null
     */
    public Parameter getSelectedParameter() {
        Parameter result = null;

        IStructuredSelection sel = parameterTable != null ? (IStructuredSelection) parameterTable.getViewer().getSelection() : null;

        if (sel != null && sel.getFirstElement() != null) {
            ParameterTableRowAdapter adapter = (ParameterTableRowAdapter) sel.getFirstElement();
            result = adapter.getDelegate();
        }

        return result;
    }

    /**
     * Exposes the table viewer for parameters.
     *
     * @return the table viewer for parameters
     */
    TableViewer getParameterTableViewer() {
        return parameterTable.getViewer();
    }

    /**
     *{@inheritDoc}
     */
    @Override
    protected String doGetFormLabel(IPrototype input) {
        return "Prototype";
    }

    /**
     *
     *{@inheritDoc}
     */
    @Override
    protected void doAddCommonRows(List<ITableRow> rows, IPrototype prototype) {
    }

    /**
     *
     *{@inheritDoc}
     */
    @Override
    protected String doGetAdditionalBreadcrumbLinks(IPrototype prototype) {
        return null;
    }

    /**
     * Adds a parameter to the parameter table.
     */
    void removeParameter() {
        // .. get the selected parameter
        IStructuredSelection sel = (IStructuredSelection) parameterTable.getViewer().getSelection();
        assert !sel.isEmpty();
        ParameterTableRowAdapter row = (ParameterTableRowAdapter) sel.getFirstElement();
        Parameter parameter = row.getDelegate();

        // .. remove the parameter
        Command cmd = new RemoveParameterCommand(getInput(), parameter);
        getCommandStack().execute(cmd);

        // .. clear selection
        parameterTable.getViewer().setSelection(null);
    }

    /**
     * Removes the currently selected parameter from the parameter table.
     */
    @SuppressWarnings("unchecked")
    void addParameter() {
        InputDialog dialog = new InputDialog(PlatformUI.getWorkbench().getDisplay().getActiveShell(), "Enter Property Name",
                "Please enter a name for the new property:", "", new IInputValidator() {
                    @Override
                    public String isValid(String newText) {
                        String error = null;

                        if (newText == null || newText.length() == 0) {
                            error = "Name cannot be empty.";
                        }
                        if (getInput().hasParameter(newText)) {
                            error = "Parameter already exists.";
                        }

                        return error;
                    }
                });

        if (dialog.open() == InputDialog.OK) {
            // .. add the parameter
            Parameter parameter = new Parameter(dialog.getValue(), "");
            Command cmd = new AddParameterCommand(getInput(), parameter);
            getCommandStack().execute(cmd);

            // .. activate the cell editor for the new row
            List<ParameterTableRowAdapter> rows = (List<ParameterTableRowAdapter>) parameterTable.getViewer().getInput();

            ParameterTableRowAdapter insertedRow = null;

            for (ParameterTableRowAdapter r : rows) {
                if (parameter.equals(r.getDelegate())) {
                    insertedRow = r;
                }
            }

            if (insertedRow != null) {
                parameterTable.getViewer().editElement(insertedRow, 1);
            }

        }
    }

}
