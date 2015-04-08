package org.csstudio.alarm.beast.ui.alarmtable.doubleclick;

import org.csstudio.alarm.beast.client.AlarmTreePV;
import org.csstudio.alarm.beast.client.AlarmTreePosition;
import org.csstudio.alarm.beast.client.GDCDataStructure;
import org.csstudio.alarm.beast.ui.actions.RelatedDisplayAction;
import org.csstudio.alarm.beast.ui.alarmtable.DoubleClickHandler;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * 
 * <code>OpenDisplayDoubleClickHandler</code> shows the first related display of the double clicked item.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public class OpenDisplayDoubleClickHandler implements DoubleClickHandler {

    
    @Override
    public void acknowledgedTableDoubleClicked(AlarmTreePV pv) {
        openDisplay(pv);
    }
    
    @Override
    public void activeTableDoubleClicked(AlarmTreePV pv) {
        openDisplay(pv);   
    }
    
    private void openDisplay(AlarmTreePV pv) {
        GDCDataStructure[] displays = pv.getDisplays();
        if (displays != null && displays.length > 0) { 
            Shell shell = Display.getCurrent().getActiveShell();
            new RelatedDisplayAction(shell, AlarmTreePosition.PV, displays[0]).run();
        }
    }
    
}
