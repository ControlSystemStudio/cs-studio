package org.csstudio.alarm.beast.ui.alarmtable.doubleclick;

import org.csstudio.alarm.beast.client.AlarmTreePV;
import org.csstudio.alarm.beast.client.AlarmTreePosition;
import org.csstudio.alarm.beast.client.GDCDataStructure;
import org.csstudio.alarm.beast.ui.actions.GuidanceAction;
import org.csstudio.alarm.beast.ui.alarmtable.DoubleClickHandler;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * 
 * <code>DisplayGuidanceDoubleClickHandler</code> displays the first guidance.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public class DisplayGuidanceDoubleClickHandler implements DoubleClickHandler {

    @Override
    public void activeTableDoubleClicked(AlarmTreePV pv) {
        showGuidance(pv);
    }

    @Override
    public void acknowledgedTableDoubleClicked(AlarmTreePV pv) {
        showGuidance(pv);        
    }

    private void showGuidance(AlarmTreePV pv) {
        GDCDataStructure[] guidances = pv.getGuidance();
        if (guidances != null && guidances.length > 0) { 
            Shell shell = Display.getCurrent().getActiveShell();
            new GuidanceAction(shell, AlarmTreePosition.PV, guidances[0]).run();
        }
    }
    
}
