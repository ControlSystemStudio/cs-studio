package org.csstudio.opibuilder.editparts;

import org.eclipse.draw2d.IFigure;
import org.epics.vtype.AlarmSeverity;

/**
 * Interface for listening to changes of alarm severity.
 * @author Takashi Nakamoto
 */
public interface AlarmSeverityListener {
    /**
     * This method is called when an alarm severity of the subjected PV is changed.
     * @param severity New severity.
     * @param figure Figure related to the subjected PV.
     * @return True if some actions are performed.
     */
    public boolean severityChanged(final AlarmSeverity severity, final IFigure figure);
}
