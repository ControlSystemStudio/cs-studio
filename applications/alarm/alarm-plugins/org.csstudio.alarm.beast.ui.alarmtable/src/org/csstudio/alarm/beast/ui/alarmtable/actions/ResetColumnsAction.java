package org.csstudio.alarm.beast.ui.alarmtable.actions;

import org.csstudio.alarm.beast.ui.alarmtable.AlarmTableView;
import org.csstudio.alarm.beast.ui.alarmtable.ColumnWrapper;
import org.csstudio.alarm.beast.ui.alarmtable.Messages;
import org.csstudio.alarm.beast.ui.alarmtable.Preferences;
import org.eclipse.jface.action.Action;

/**
 * <code>ResetColumnsAction</code> resets the columns configuration to default, as defined in the
 * preferences.ini
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public class ResetColumnsAction extends Action {
    private final AlarmTableView view;

    /**
     * Constructs a new action that acts on the given alarm table view.
     *
     * @param view the view to configure its columns
     */
    public ResetColumnsAction(final AlarmTableView view) {
        super(Messages.ResetColumns);
        this.view = view;
    }

    @Override
    public void run() {
        ColumnWrapper[] columns = ColumnWrapper.fromSaveArray(Preferences.getColumns());
        view.setColumns(columns);
    }
}
