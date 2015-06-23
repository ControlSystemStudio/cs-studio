package org.csstudio.sds.ui.internal.editor.newproperties.table;

/**
 * Describes a single column for a {@link ConvenienceTableWrapper}.
 *
 * @author Sven Wende
 *
 */
public final class ColumnConfig {
    private String id;
    private String title;
    private int weight;
    private int minimumWidth;
    private boolean resizable;

    /**
     * Constructor.
     *
     * @param id
     *            an unique ID
     * @param title
     *            a title
     * @param minimumWidth
     *            the column width
     */
    public ColumnConfig(String id, String title, int minimumWidth, int weight, boolean resizable) {
        this.id = id;
        this.title = title;
        this.minimumWidth = minimumWidth;
        this.weight = weight;
        this.resizable = resizable;
    }

    /**
     * Returns an unique id for the column.
     *
     * @return an unique id
     */
    public String getId() {
        return id;
    }

    /**
     * Returns a title for the column.
     *
     * @return a title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Returns the minimum column width.
     *
     * @return the column width
     */
    public int getMinimumWidth() {
        return minimumWidth;
    }

    /**
     * Returns the column weight.
     *
     * @return the column weight
     */

    public int getWeight() {
        return weight;
    }

    /**
     * Returns true, if the column is resizable.
     *
     * @return true for resizable columns
     */
    public boolean isResizable() {
        return resizable;
    }

}
