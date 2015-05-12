package org.csstudio.sds.ui.internal.editor.newproperties.table;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Composite;

/**
 * Represents a table row being used in a {@link ConvenienceTableWrapper}. This
 * interface allows for an easy adaption of domain objects to be displayed and
 * edited in a table.
 *
 * All aspects of the table representation (colors, fonts etc.) and the editing
 * behaviour (editing allowed, celleditor) can be controlled via this interface.
 *
 * @author Sven Wende
 *
 */
public interface ITableRow extends Comparable<ITableRow> {

    /**
     * Returns the value which is displayed in the specified column.
     *
     * @param column
     *            the column index
     * @return the value for the specified column
     */
    String getDisplayValue(int column);

    /**
     * Returns the editing value which is displayed in the specified column when
     * it switches to edit mode.
     *
     * @param column
     *            the column index
     *
     * @return the editing value for the specified column
     */
    String getEditingValue(int column);

    /**
     * Returns true, if the value in the specified column can be edited.
     *
     * @param column
     *            the column index
     * @return true, if the value in the specified column can be edited
     */
    boolean canModify(int column);

    /**
     * Returns the cell editor for the specified column.
     *
     * @param column
     *            the column index
     * @param parent
     *            a parent composite
     * @return the cell editor for the specified column
     */
    CellEditor getCellEditor(int column, Composite parent);

    /**
     * This method is called, when the value in the specified column was edited.
     *
     * @param column
     *            the column index
     * @param value
     *            the new value
     */
    void setValue(int column, Object value);

    /**
     * Returns the background color for the specified column.
     *
     * @param column
     *            the column index
     * @return the background color for the specified column
     */
    RGB getBackgroundColor(int column);

    /**
     * Returns the foreground color for the specified column.
     *
     * @param column
     *            the column index
     * @return the foreground color for the specified column
     */
    RGB getForegroundColor(int column);

    /**
     * Returns the font for the specified column.
     *
     * @param column
     *            the column index
     * @return the font for the specified column
     */
    Font getFont(int column);

    /**
     * Returns the image for the specified column.
     *
     * @param column
     *            the column index
     * @return the image for the specified column
     */
    Image getImage(int column);

    /**
     * Returns a tool tip for this row.
     *
     * @return a tool tip
     */
    String getTooltip();

}
