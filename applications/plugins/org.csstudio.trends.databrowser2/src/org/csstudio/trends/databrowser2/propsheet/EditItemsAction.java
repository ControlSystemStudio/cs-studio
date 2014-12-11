package org.csstudio.trends.databrowser2.propsheet;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.csstudio.swt.rtplot.undo.UndoableActionManager;
import org.csstudio.trends.databrowser2.Activator;
import org.csstudio.trends.databrowser2.Messages;
import org.csstudio.trends.databrowser2.model.Model;
import org.csstudio.trends.databrowser2.model.ModelItem;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Shell;

/** Context menu action that edits selected items at once with one dialog.
 *  @author Takashi Nakamoto
 */
@SuppressWarnings("nls")
public class EditItemsAction extends Action
{
    final private UndoableActionManager operations_manager;
    final private Shell shell;
    final private TableViewer trace_table;
    final private Model model;

    /** Initialize
     *  @param operations_manager OperationsManager where command will be reg'ed
     *  @param shell Parent shell for dialog
     *  @param trace_table Table of ModelItems
     *  @param model Model
     */
    public EditItemsAction(final UndoableActionManager operations_manager,
    		final Shell shell,
            final TableViewer trace_table,
            final Model model)
    {
        super(Messages.EditItems, Activator.getDefault().getImageDescriptor("icons/edit.png"));
        this.operations_manager = operations_manager;
        this.shell = shell;
        this.trace_table = trace_table;
        this.model = model;

        // Only enabled when one or more items are selected.
        final ISelectionChangedListener selection_listener = new ISelectionChangedListener()
        {
            @Override
            public void selectionChanged(final SelectionChangedEvent event)
            {
                setEnabled(!trace_table.getSelection().isEmpty());
            }
        };
        trace_table.addSelectionChangedListener(selection_listener);
        selection_listener.selectionChanged(null);
    }

    @Override
    public void run()
    {
        // Get selected objects from table, turn into ModelItem array
        final List<ModelItem> items = new ArrayList<>();
        final Iterator<?> selected = ((IStructuredSelection)trace_table.getSelection()).iterator();
        while (selected.hasNext())
            items.add((ModelItem) selected.next());

        // Show edit dialog.
        EditItemsDialog dialog = new EditItemsDialog(shell, items, model);
        if (dialog.open() == IDialogConstants.OK_ID)
            // Edit PV via undo-able command.
            new EditItemsCommand(operations_manager, items, dialog.getResult());
    }
}
