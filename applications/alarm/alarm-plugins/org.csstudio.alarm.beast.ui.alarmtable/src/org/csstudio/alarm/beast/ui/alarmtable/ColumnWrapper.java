package org.csstudio.alarm.beast.ui.alarmtable;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * <code>ColumnWrapper</code> is a wrapper around {@link ColumnInfo}, which contains also
 * information if the column is visible or not.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public final class ColumnWrapper {

    /**
     * @return a set of new wrappers (all visible) for all available column infos
     */
    public static ColumnWrapper[] getNewWrappers() {
        ColumnInfo[] infos = ColumnInfo.values();
        ColumnWrapper[] wrappers = new ColumnWrapper[infos.length];
        for (int i = 0; i < wrappers.length; i++) {
            wrappers[i] = new ColumnWrapper(infos[i]);
        }
        return wrappers;
    }

    /**
     * Generates a clone of the source. The clone has exactly the same number of elements
     * in the same order and the method guarantees that clone[i].equals(source[i]) but not
     * clone[i] == source[i].
     *
     * @param source wrappers to clone
     * @return cloned array
     */
    public static ColumnWrapper[] getCopy(ColumnWrapper[] source) {
        ColumnWrapper[] w = new ColumnWrapper[source.length];
        for (int i = 0; i < w.length; i++) {
            w[i] = new ColumnWrapper(source[i].info);
            w[i].setVisible(source[i].visible);
        }
        return w;
    }

    /**
     * Converts the array of wrappers into an array of string, where string is the name
     * of the column info. Only the visible wrappers are included.
     *
     * @param columns the source data
     * @return array of visible column info names
     */
    public static String[] toSaveArray(ColumnWrapper[] columns) {
        List<String> list = new ArrayList<>(columns.length);
        for (ColumnWrapper cw : columns) {
            if (cw.isVisible()) {
                list.add(cw.info.name());
            }
        }
        return list.toArray(new String[list.size()]);
    }

    /**
     * Converts the array of column info names to an array of column wrappers. The return array
     * always contains wrappers for all column infos. The wrapper of those infos that are included
     * in the <code>columns</code> parameter are visible, the others are not.
     *
     * @param columns visible column info names
     * @return array of wrappers for all column infos
     */
    public static ColumnWrapper[] fromSaveArray(String[] columns) {
        ColumnWrapper[] wrappers = getNewWrappers();
        List<ColumnWrapper> list = new ArrayList<>(wrappers.length);
        for (String s : columns) {
            ColumnInfo info = ColumnInfo.valueOf(s);
            for (int i = 0; i < wrappers.length; i++) {
                if (wrappers[i] != null && wrappers[i].info.equals(info)) {
                    list.add(wrappers[i]);
                    wrappers[i] = null;
                    break;
                }
            }
        }
        for (ColumnWrapper w : wrappers) {
            if (w != null) {
                list.add(w);
                w.setVisible(false);
            }
        }
        return list.toArray(new ColumnWrapper[list.size()]);
    }

    private final ColumnInfo info;
    private boolean visible = true;

    /**
     * Constructs a new ColumnWrapper for the given info.
     *
     * @param info the column info
     */
    private ColumnWrapper(ColumnInfo info) {
        this.info = info;
    }

    /**
     * Sets this column visible or invisible. The GUI should make the effort to obey this setting.
     *
     * @param visible true if the column should be visible or false if it should be hidden
     */
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    /**
     * @see {@link #setVisible(boolean)}
     * @return the visible flag for this column
     */
    public boolean isVisible() {
        return visible;
    }

    /**
     * @return the column info wrapped into this wrapper
     */
    public ColumnInfo getColumnInfo() {
        return info;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((info == null) ? 0 : info.hashCode());
        result = prime * result + (visible ? 1231 : 1237);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ColumnWrapper other = (ColumnWrapper) obj;
        if (info != other.info)
            return false;
        if (visible != other.visible)
            return false;
        return true;
    }
}
