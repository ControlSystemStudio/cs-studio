package org.csstudio.platform.internal.simpledal.dal;

import org.csstudio.dal.DynamicValueCondition;
import org.csstudio.dal.DynamicValueProperty;
import org.csstudio.dal.DynamicValueState;
import org.csstudio.dal.context.ConnectionEvent;
import org.csstudio.dal.context.LinkAdapter;

/**
 * Utility methods for accessing characteristics and dealing with DAL
 * properties.
 *
 * @author Sven Wende
 *
 */
public class EpicsUtil {

    /**
     * Waits until DAL property is connected or timeout has elapsed
     *
     * @param property
     *            the DAL property
     * @param timeout
     *            the timeout to wait
     * @return <code>true</code> if property was connected
     */
    public static boolean waitTillConnected(DynamicValueProperty property, long timeout) {
        if (property == null) {
            return false;
        }
        if (property.isConnected()) {
            return true;
        }
        if (property.isConnectionFailed()) {
            return false;
        }

        LinkAdapter link = new LinkAdapter() {
            @Override
            public synchronized void connected(ConnectionEvent e) {
                notifyAll();
            }

            @Override
            public synchronized void connectionFailed(ConnectionEvent e) {
                notifyAll();
            }
        };

        synchronized (link) {
            if (property.isConnected()) {
                return true;
            }
            else if (property.isConnectionFailed()) {
                return false;
            } else {
                property.addLinkListener(link);

                try {
                    link.wait(timeout);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                property.removeLinkListener(link);
            }


        }

        return property.isConnected();

    }

    /**
     * Returns EPICS favored status string for DAL condition.
     *
     * @param cond
     *            DAL condition
     * @return EPICS favored status string for DAL condition
     */
    public static final String extratStatus(DynamicValueCondition cond) {
        if (cond == null || cond.getDescription() == null) {
            return "N/A";
        }
        return cond.getDescription();
    }

    /**
     * Converts DAL condition to EPICS favored severity string.
     *
     * @param condition
     *            DAL condition
     * @return EPICS favored severity string
     */
    public static final String toEPICSFlavorSeverity(DynamicValueCondition condition) {
        if (condition.isNormal()) {
            return DynamicValueState.NORMAL.toString();
        }
        if (condition.isWarning()) {
            return DynamicValueState.WARNING.toString();
        }
        if (condition.isAlarm()) {
            return DynamicValueState.ALARM.toString();
        }
        if (condition.isError()) {
            return DynamicValueState.ERROR.toString();
        }
        return "UNKNOWN";
    }

}
