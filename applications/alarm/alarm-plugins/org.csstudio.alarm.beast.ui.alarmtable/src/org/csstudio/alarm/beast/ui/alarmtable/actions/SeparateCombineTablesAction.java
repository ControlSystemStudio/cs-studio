package org.csstudio.alarm.beast.ui.alarmtable.actions;

import org.csstudio.alarm.beast.ui.alarmtable.AlarmTableView;
import org.csstudio.alarm.beast.ui.alarmtable.Messages;
import org.eclipse.jface.action.Action;

/**
 * <code>SeparateCombineTablesAction</code> combines or splits the alarm table. When the table is
 * split acknowledged alarms are shown in a separated table.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 */
public class SeparateCombineTablesAction extends Action {
    private final boolean combine;
    private final AlarmTableView view;

    /**
     * Construct a new action.
     *
     * @param view the view which is being configured
     * @param combine true if this action combines the tables or false if it separates them
     * @param checked the default state of the action
     */
    public SeparateCombineTablesAction(final AlarmTableView view, final boolean combine, final boolean checked) {
        super(combine ? Messages.AlarmTableCombined : Messages.AlarmTableSeparate, AS_RADIO_BUTTON);
        this.combine = combine;
        this.view = view;
        setChecked(checked);
    }

    @Override
    public void run() {
        view.setCombinedTables(combine);
    }
}