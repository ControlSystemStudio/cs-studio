package org.csstudio.dct.ui.editor;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.dct.model.IPropertyContainer;
import org.csstudio.dct.model.commands.AddPropertyCommand;
import org.csstudio.dct.model.commands.RemovePropertyCommand;
import org.csstudio.dct.ui.Activator;
import org.csstudio.dct.ui.editor.tables.ConvenienceTableWrapper;
import org.csstudio.dct.ui.editor.tables.ITableRow;
import org.csstudio.ui.util.CustomMediaFactory;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CommandStack;
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
 * Abstract base class for forms that edit model elements which contain
 * properties and therefore implement {@link IPropertyContainer}.
 *
 * @author Sven Wende
 *
 * @param <E>
 *            the type of element that is edited with a form
 */
public abstract class AbstractPropertyContainerForm<E extends IPropertyContainer> extends AbstractForm<E> {

    private ConvenienceTableWrapper propertyTable;

    /**
     * Constructor.
     *
     * @param editor
     *            a DCT editor instance
     */
    public AbstractPropertyContainerForm(DctEditor editor) {
        super(editor);
    }

    /**
     * Returns the currently selected property.
     *
     * @return the selected property or null
     */
    public final String getSelectedProperty() {
        String result = null;

        IStructuredSelection sel = propertyTable != null ? (IStructuredSelection) propertyTable.getViewer().getSelection() : null;

        if (sel != null && sel.getFirstElement() != null) {
            PropertyTableRowAdapter adapter = (PropertyTableRowAdapter) sel.getFirstElement();
            result = adapter.getPropertyKey();
        }

        return result;
    }

    /**
     *{@inheritDoc}
     */
    @Override
    protected void doCreateControl(ExpandBar bar, final CommandStack commandStack) {
        // .. input table for properties
        Composite composite = new Composite(bar, SWT.NONE);
        composite.setLayout(LayoutUtil.createGridLayout(1, 5, 8, 8));

        propertyTable = WidgetUtil.create3ColumnTable(composite, commandStack);
        propertyTable.getViewer().getControl().setLayoutData(LayoutUtil.createGridDataForHorizontalFillingCell(200));

        // .. add/remove buttons for properties
        Composite buttons = new Composite(composite, SWT.None);
        buttons.setLayout(new FillLayout());

        Button addButton = new Button(buttons, SWT.FLAT);
        addButton.setText("Add");
        addButton.addMouseListener(new MouseAdapter() {
            @SuppressWarnings("unchecked")
            @Override
            public void mouseDown(MouseEvent e) {
                InputDialog dialog = new InputDialog(PlatformUI.getWorkbench().getDisplay().getActiveShell(), "Enter Property Name",
                        "Please enter a name for the new property:", "", new IInputValidator() {
                            @Override
                            public String isValid(String newText) {
                                String error = null;

                                if (newText == null || newText.length() == 0) {
                                    error = "Name cannot be empty.";
                                }
                                if (getInput().getProperty(newText) != null) {
                                    error = "Property already exists.";
                                }

                                return error;
                            }
                        });

                if (dialog.open() == InputDialog.OK) {
                    String name = dialog.getValue();

                    // .. insert the property
                    Command cmd = new AddPropertyCommand(getInput(), name);
                    commandStack.execute(cmd);

                    // .. activate the cell editor for the new row
                    TableViewer viewer = propertyTable.getViewer();
                    List<PropertyTableRowAdapter> rows = (List<PropertyTableRowAdapter>) viewer.getInput();

                    PropertyTableRowAdapter insertedRow = null;

                    for (PropertyTableRowAdapter r : rows) {
                        if (name.equals(r.getPropertyKey())) {
                            insertedRow = r;
                        }
                    }

                    if (insertedRow != null) {
                        viewer.editElement(insertedRow, 1);
                    }

                }

            }
        });

        final Button removeButton = new Button(buttons, SWT.FLAT);
        removeButton.setEnabled(false);
        removeButton.setText("Remove");
        removeButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseDown(MouseEvent e) {
                // .. get the selected property
                IStructuredSelection sel = (IStructuredSelection) propertyTable.getViewer().getSelection();
                assert !sel.isEmpty();
                PropertyTableRowAdapter row = (PropertyTableRowAdapter) sel.getFirstElement();
                String key = row.getPropertyKey();

                // .. remove the property
                Command cmd = new RemovePropertyCommand(getInput(), key);
                commandStack.execute(cmd);

                // .. clear selection
                propertyTable.getViewer().setSelection(null);
            }
        });

        propertyTable.getViewer().addSelectionChangedListener(new ISelectionChangedListener() {
            public void selectionChanged(SelectionChangedEvent event) {
                removeButton.setEnabled(!event.getSelection().isEmpty());
            }
        });

        // .. the expand item
        ExpandItem expandItem = new ExpandItem(bar, SWT.NONE);
        expandItem.setText("Properties");
        expandItem.setHeight(270);
        expandItem.setControl(composite);
        expandItem.setImage(CustomMediaFactory.getInstance().getImageFromPlugin(Activator.PLUGIN_ID, "icons/tab_properties.png"));

        // ... popup menu for properties
        TableViewer viewer = propertyTable.getViewer();
        MenuManager popupMenu = new MenuManager();
        PropertyAddAction propertyAddAction = new PropertyAddAction(this);
        popupMenu.add(propertyAddAction);
        PropertyRemoveAction propertyRemoveAction = new PropertyRemoveAction(this);
        popupMenu.add(propertyRemoveAction);
        Menu menu = popupMenu.createContextMenu(viewer.getControl());
        viewer.getControl().setMenu(menu);
    }

    /**
     *{@inheritDoc}
     */
    @Override
    protected void doSetInput(E input) {
        // .. prepare input for property table
        if (input instanceof IPropertyContainer) {
            IPropertyContainer container = (IPropertyContainer) input;
            List<ITableRow> rowsForProperties = new ArrayList<ITableRow>();

            for (String key : container.getFinalProperties().keySet()) {
                rowsForProperties.add(new PropertyTableRowAdapter(container, key));
            }
            propertyTable.setInput(rowsForProperties);
        }
    };

}
