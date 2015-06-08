package org.csstudio.alarm.beast.ui.alarmtable;

/**
 *
 * <code>FilterType</code> defines possible filters applied on the table. The table can either show the root selected in
 * the alarm tree ({@link #TREE}), it can show all alarms of another root ({@link #ROOT}), or alarms that belong to a
 * specific item ({@link #ITEM}).
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public enum FilterType {
    TREE, ROOT, ITEM
}
