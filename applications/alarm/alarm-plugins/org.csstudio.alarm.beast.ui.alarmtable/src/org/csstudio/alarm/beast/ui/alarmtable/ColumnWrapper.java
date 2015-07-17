package org.csstudio.alarm.beast.ui.alarmtable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.eclipse.ui.IMemento;

/**
 *
 * <code>ColumnWrapper</code> is a wrapper around {@link ColumnInfo}, which contains also information if the column is
 * visible or not, its width and weight.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public final class ColumnWrapper {

    /** The memento tag name of the visible property */
    private static final String M_VISIBLE = "visible"; //$NON-NLS-1$
    /** The memento tag name of the key by which the columns or ordered */
    private static final String M_ORDER_KEY = "orderKey"; //$NON-NLS-1$
    /** The memento tag name of the minimum width of the column */
    private static final String M_MIN_WIDTH = "minWidth"; //$NON-NLS-1$
    /** The memento tag name of the weight of the column */
    private static final String M_WEIGHT = "weight"; //$NON-NLS-1$
    /** The memento tag name of the visible name of the column */
    private static final String M_NAME = "name"; //$NON-NLS-1$

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
     * Generates a clone of the source. The clone has exactly the same number of elements in the same order and the
     * method guarantees that clone[i].equals(source[i]) but not clone[i] == source[i].
     *
     * @param source wrappers to clone
     * @return cloned array
     */
    public static ColumnWrapper[] getCopy(ColumnWrapper[] source) {
        ColumnWrapper[] w = new ColumnWrapper[source.length];
        for (int i = 0; i < w.length; i++) {
            w[i] = new ColumnWrapper(source[i].info);
            w[i].setVisible(source[i].visible);
            w[i].name = source[i].name;
            w[i].minWidth = source[i].minWidth;
            w[i].weight = source[i].weight;
        }
        return w;
    }

    /**
     * Converts the array of wrappers into an array of string, where string is the name of the column info,
     * its width and weight. Only the visible wrappers are included.
     *
     * @param columns the source data
     * @return array of visible column strings
     */
    public static String[] toSaveArray(ColumnWrapper[] columns) {
        List<String> list = new ArrayList<>(columns.length);
        for (ColumnWrapper cw : columns) {
            if (cw.isVisible()) {
                StringBuilder sb = new StringBuilder(25);
                sb.append(cw.info.name()).append(',').append(cw.minWidth).append(',').append(cw.weight);
                list.add(sb.toString());
            }
        }
        return list.toArray(new String[list.size()]);
    }

    /**
     * Converts the array of column info names to an array of column wrappers. The return array always contains wrappers
     * for all column infos. The wrapper of those infos that are included in the <code>columns</code> parameter are
     * visible, the others are not.
     *
     * @param columns visible column info names
     * @return array of wrappers for all column infos
     */
    public static ColumnWrapper[] fromSaveArray(String[] columns) {
        ColumnWrapper[] wrappers = getNewWrappers();
        List<ColumnWrapper> list = new ArrayList<>(wrappers.length);
        for (String s : columns) {
            String[] col = s.split("\\,", -1); //$NON-NLS-1$
            String id = null;
            String name = null;
            int minWidth = -1;
            int weight = -1;
            if (col.length > 2) {
                id = col[0];
                minWidth = Integer.parseInt(col[1]);
                weight = Integer.parseInt(col[2]);
                if (col.length > 3) {
                    name = col[3];
                }
            }
            ColumnInfo info = ColumnInfo.valueOf(id);
            for (int i = 0; i < wrappers.length; i++) {
                if (wrappers[i] != null && wrappers[i].info.equals(info)) {
                    list.add(wrappers[i]);
                    wrappers[i].weight = weight;
                    wrappers[i].minWidth = minWidth;
                    wrappers[i].name = name;
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

    /**
     * Restore the columns from the memento. The columns are expected to be children of the given memento.
     *
     * @param memento the source
     * @return columns restored from the memento with all parameters properly set
     */
    public static ColumnWrapper[] restoreColumns(IMemento memento) {
        if (memento == null) {
            return ColumnWrapper.fromSaveArray(Preferences.getColumns());
        }
        ColumnInfo[] infos = ColumnInfo.values();
        ColumnWrapper[] wrappers = new ColumnWrapper[infos.length];
        for (ColumnInfo ci : ColumnInfo.values()) {
            IMemento m = memento.getChild(ci.name());
            if (m == null) {
                for (int i = wrappers.length - 1; i >= -1; i--) {
                    if (wrappers[i] == null) {
                        wrappers[i] = new ColumnWrapper(ci, false);
                        break;
                    }
                }
            } else {
                Boolean visible = m.getBoolean(M_VISIBLE);
                Integer order = m.getInteger(M_ORDER_KEY);
                ColumnWrapper w = null;
                if (wrappers[order] == null) {
                    wrappers[order] = new ColumnWrapper(ci, visible);
                    w = wrappers[order];
                } else {
                    for (int i = 0; i < wrappers.length; i++) {
                        if (wrappers[i] == null) {
                            wrappers[i] = new ColumnWrapper(ci, visible);
                            w = wrappers[i];
                            break;
                        }
                    }
                }

                Integer minWidth = m.getInteger(M_MIN_WIDTH);
                Integer weight = m.getInteger(M_WEIGHT);
                if (w != null) {
                    w.name = m.getString(M_NAME);
                    w.minWidth = minWidth == null ? Integer.valueOf(-1) : minWidth;
                    w.weight = weight == null ? Integer.valueOf(-1) : weight;
                }
            }
        }
        return wrappers;
    }

    /**
     * Save the column info into the given memento. The columns are stored as children of the memento, one child per
     * column. Each child contains information required to restore the current visuble state of the column.
     *
     * @param memento the destination memento
     * @param columns the column to store
     */
    public static void saveColumns(IMemento memento, ColumnWrapper[] columns) {
        if (memento == null) {
            return;
        }

        for (int i = 0; i < columns.length; i++) {
            IMemento m = memento.createChild(columns[i].getColumnInfo().name());
            m.putBoolean(M_VISIBLE, columns[i].isVisible());
            m.putInteger(M_ORDER_KEY, i);
            m.putInteger(M_WEIGHT, columns[i].weight);
            m.putInteger(M_MIN_WIDTH, columns[i].minWidth);
            m.putString(M_NAME, columns[i].name);
        }
    }

    private final ColumnInfo info;
    private boolean visible = true;
    private String name;
    private int minWidth = -1;
    private int weight = -1;

    /**
     * Constructs a new ColumnWrapper for the given info.
     *
     * @param info the column info
     */
    private ColumnWrapper(ColumnInfo info) {
        this.info = info;
    }

    /**
     * Constructs a new ColumnWrapper for the given info.
     *
     * @param info the column info
     * @param visible the default visible property value
     */
    private ColumnWrapper(ColumnInfo info, boolean visible) {
        this.info = info;
        setVisible(visible);
    }

    /**
     * Constructs a new ColumnWrapper for the given parameters.
     *
     * @param info the info
     * @param visible true if the column should be visible or false otherwise
     * @param minWidth minimum width
     * @param weight columns resize weight
     */
    public ColumnWrapper(ColumnInfo info, boolean visible, int minWidth, int weight) {
        this.info = info;
        setWeight(weight);
        setVisible(visible);
        setMinWidth(minWidth);
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

    /**
     * @return the visible name of the column
     */
    public String getName() {
        return name == null ? info.getTitle() : name;
    }

    /**
     * @return resize weight of the column
     */
    public int getWeight() {
        return weight < 0 ? info.getWeight() : weight;
    }

    /**
     * Set a new resize weight for the column.
     *
     * @param weight the resize weight
     */
    public void setWeight(int weight) {
        this.weight = weight;
    }

    /**
     * @return minimum width of the column
     */
    public int getMinWidth() {
        return minWidth <= 0 ? info.getMinWidth() : minWidth;
    }

    /**
     * Set the new minimum width for this column.
     *
     * @param minWidth the new minimum width
     */
    public void setMinWidth(int minWidth) {
        this.minWidth = minWidth;
    }

    @Override
    public String toString() {
        return info.toString();
    }

    @Override
    public int hashCode() {
        return Objects.hash(info, name, visible, weight, minWidth);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj == null) {
            return false;
        } else if (getClass() != obj.getClass()) {
            return false;
        }
        ColumnWrapper other = (ColumnWrapper) obj;
        return visible == other.visible && weight == other.weight && minWidth == other.minWidth && info == other.info
                && Objects.equals(name, other.name);
    }
}
