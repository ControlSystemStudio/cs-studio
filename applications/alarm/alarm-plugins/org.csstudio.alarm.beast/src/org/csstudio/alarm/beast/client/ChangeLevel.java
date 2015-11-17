package org.csstudio.alarm.beast.client;

/**
 *
 * <code>ChangeLevel</code> indicates the three possible level of changes, which are the result of applying the
 * alarm to the alarm PV. Either the PV already has that state, so nothing is changed (NONE); the state of
 * the PV has changed (PV), the state of the PV and its parent has changed (PV_AND_PARENT).
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public enum ChangeLevel {
    NONE, PV, PV_AND_PARENT
}
