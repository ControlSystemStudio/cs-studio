package org.csstudio.alarm.beast.ui.alarmtable;

import org.eclipse.jface.action.Action;

/** Action to combine/split the alarm table
 * 
 *  @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 */
class GroupUngroupAction extends Action 
{
    private final boolean group;
    private final AlarmTableView view;
    
    public GroupUngroupAction(final AlarmTableView view, final boolean group, final boolean checked)
    {
        super(group ? Messages.AlarmTableSeparate : Messages.AlarmTableCombined, AS_RADIO_BUTTON);
        this.group = group;
        this.view = view;
        setChecked(checked);
    }
    
    @Override
    public void run() 
    {
        view.group(group);
    }
}