package org.csstudio.alarm.beast.ui.alarmtable;

import org.eclipse.jface.action.Action;

/** Action to combine/split the alarm table
 * 
 *  @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 */
class SeparateCombineTablesAction extends Action 
{
    private final boolean combine;
    private final AlarmTableView view;
    
    public SeparateCombineTablesAction(final AlarmTableView view, final boolean combine, final boolean checked)
    {
        super(combine ? Messages.AlarmTableCombined : Messages.AlarmTableSeparate, AS_RADIO_BUTTON);
        this.combine = combine;
        this.view = view;
        setChecked(checked);
    }
    
    @Override
    public void run() 
    {
        view.setCombinedTables(combine);
    }
}