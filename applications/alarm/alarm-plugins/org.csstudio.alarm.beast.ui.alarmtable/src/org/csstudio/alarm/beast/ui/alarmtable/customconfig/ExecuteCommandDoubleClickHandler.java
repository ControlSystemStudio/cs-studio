package org.csstudio.alarm.beast.ui.alarmtable.customconfig;

import org.csstudio.alarm.beast.client.AlarmTreePV;
import org.csstudio.alarm.beast.client.AlarmTreePosition;
import org.csstudio.alarm.beast.client.GDCDataStructure;
import org.csstudio.alarm.beast.ui.actions.CommandAction;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * 
 * <code>ExecuteCommandDoubleClickHandler</code> executes the first related command.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public class ExecuteCommandDoubleClickHandler implements DoubleClickHandler {

    @Override
    public void acknowledgedTableDoubleClicked(AlarmTreePV pv) {
        executeCommand(pv);
        
    }
    @Override
    public void activeTableDoubleClicked(AlarmTreePV pv) {
        executeCommand(pv);        
    }
    
    private void executeCommand(AlarmTreePV pv) {
        GDCDataStructure[] commands = pv.getCommands();
        if (commands != null && commands.length > 0) { 
            Shell shell = Display.getCurrent().getActiveShell();
            new CommandAction(shell, AlarmTreePosition.PV, commands[0]).run();
        }
    }
}
