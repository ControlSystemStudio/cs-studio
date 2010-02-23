package org.csstudio.trends.databrowser.ui;

import org.csstudio.apputil.time.StartEndTimeParser;
import org.csstudio.apputil.ui.time.StartEndDialog;
import org.csstudio.swt.xygraph.undo.OperationsManager;
import org.csstudio.trends.databrowser.model.Model;
import org.csstudio.trends.databrowser.propsheet.ChangeTimerangeCommand;
import org.eclipse.swt.widgets.Shell;

/** Helper for handling start/end time changes
 *  @author Kay Kasemir
 */
public class StartEndTimeAction
{
    /** Prompt user for new start/end time
     *  @param shell Parent shell
     *  @param model Model to change
     *  @param operations_manager Undo/Redo operations manager
     */
    public static void run(final Shell shell, final Model model,
            final OperationsManager operations_manager)
    {
        final String start_time = model.getStartSpecification();
        final String end_time = model.getEndSpecification();
        final StartEndDialog dlg = new StartEndDialog(shell,
                start_time, end_time);
        if (dlg.open() != StartEndDialog.OK)
            return;
        // TODO dlg.isEndNow() doesn't seem to work
        new ChangeTimerangeCommand(model, operations_manager,
                dlg.isEndNow(), dlg.getStartCalendar(), dlg.getEndCalendar());
    }

    /** Change start/end time
     *  @param model Model to change
     *  @param operations_manager Undo/Redo operations manager
     *  @param start_time Desired start time specification
     *  @param end_time .. end time
     *  @throws Exception on error in start/end time
     */
    public static void run(final Model model,
            final OperationsManager operations_manager,
            final String start_time, final String end_time) throws Exception
    {
        final StartEndTimeParser parser =
            new StartEndTimeParser(start_time, end_time);
        new ChangeTimerangeCommand(model, operations_manager, parser.isEndNow(), parser.getStart(), parser.getEnd());
    }
}
