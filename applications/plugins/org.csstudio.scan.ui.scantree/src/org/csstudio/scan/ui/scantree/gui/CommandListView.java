package org.csstudio.scan.ui.scantree.gui;

import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.csstudio.scan.command.ScanCommand;
import org.csstudio.scan.ui.scantree.Messages;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSourceAdapter;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.part.ViewPart;

/** Eclipse View that lists all commands.
 *
 *  <p>A "Palette" for dragging commands into a scan editor
 *
 *  @author Kay Kasemir
 */
public class CommandListView extends ViewPart
{
    /** View ID defined in plugin.xml */
    final public static String ID = "org.csstudio.scan.ui.scantree.commandlist"; //$NON-NLS-1$

    private TableViewer table_viewer;

    /** {@inheritDoc} */
    @Override
    public void createPartControl(final Composite parent)
    {
        createGUI(parent);
        addDrag();
    }

    /** Create GUI elements
     *  @param parent
     */
    private void createGUI(final Composite parent)
    {
        parent.setLayout(new FillLayout());

        table_viewer = new TableViewer(parent);
        final Table table = table_viewer.getTable();
        table.setLinesVisible(true);
        table.setToolTipText(Messages.CommandListTT);
        table_viewer.setLabelProvider(new CommandTreeLabelProvider());
        table_viewer.setContentProvider(new ArrayContentProvider());

        try
        {
            table_viewer.setInput(CommandsInfo.getInstance().getCommands());
        }
        catch (Exception ex)
        {
            Logger.getLogger(getClass().getName())
                .log(Level.WARNING, "Cannot obtain list of commands", ex); //$NON-NLS-1$
        }
    }

    /** @return Currently selected scan command or <code>null</code> */
    protected ScanCommand getSelectedCommand()
    {
        final IStructuredSelection sel = (IStructuredSelection)  table_viewer.getSelection();
        if (sel.isEmpty())
            return null;
        return (ScanCommand) sel.getFirstElement();
    }

    /** Add drag support */
    private void addDrag()
    {
        final Transfer[] transfers = new Transfer[]
        {
            ScanCommandTransfer.getInstance()
        };
        table_viewer.addDragSupport(DND.DROP_MOVE | DND.DROP_COPY, transfers, new DragSourceAdapter()
        {
            private ScanCommand command = null;

            @Override
            public void dragStart(final DragSourceEvent event)
            {
                command = getSelectedCommand();
                if (command == null)
                    event.doit = false;
            }

            @Override
            public void dragSetData(final DragSourceEvent event)
            {
                event.data = Arrays.asList(command);
            }
        });
    }

    /** {@inheritDoc} */
    @Override
    public void setFocus()
    {
        // NOP
    }
}
