package org.csstudio.dct.ui.editor;

import org.csstudio.dct.ui.editor.tables.ITableRow;
import org.csstudio.domain.common.strings.StringUtil;
import org.csstudio.ui.util.CustomMediaFactory;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Composite;

/**
 * Base class for all table row adapters that are used to adapt domain objects
 * for a ConvenienceTableWrapper with the following column layout:
 *
 * <ul>
 * <li>1. column = Description</li>
 * <li>2. column = Value</li>
 * <li>3. column = Error (optional)</li>
 * </ul>
 *
 * @author Sven Wende
 *
 * @param <E>
 *            the type of the domain object
 */
public abstract class AbstractTableRowAdapter<E> implements ITableRow {
    public static final String DEFAULT_FONT = "Arial";
    public static final int DEFAULT_FONT_SIZE = 9;

    private E delegate;
    private String error;

    /**
     * Constructor.
     *
     * @param delegate
     *            the delegate
     */
    public AbstractTableRowAdapter(E delegate) {
        assert delegate != null;
        this.delegate = delegate;
    }

    /**
     * Sets an error.
     *
     * @param error
     *            the error
     */
    public final void setError(String error) {
        this.error = error;
    }

    /**
     * Returns the domain object.
     *
     * @return the domain object
     */
    public final E getDelegate() {
        return delegate;
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public final String getTooltip() {
        return doGetKeyDescription(delegate);
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public final String getDisplayValue(int column) {
        String result = null;

        switch (column) {
        case 0:
            result = doGetKeyDescription(delegate);
            break;
        case 1:
            result = doGetValueForDisplay(delegate);
            break;
        case 2:
            result = error;
            break;
        default:
            break;
        }

        return result;
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public final String getEditingValue(int column) {
        String result = null;

        switch (column) {
        case 0:
            result = doGetKey(delegate);
            break;
        case 1:
            result = doGetValue(delegate);
            break;
        case 2:
            result = null;
            break;
        default:
            break;
        }

        return result;
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public final void setValue(int column, Object value, CommandStack commandStack) {
        Command cmd = null;
        switch (column) {
        case 0:
            cmd = doSetKey(delegate, value);
            break;
        case 1:
            cmd = doSetValue(delegate, value);
            break;
        default:
            break;
        }

        if (cmd != null) {
            commandStack.execute(cmd);
        }
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public final RGB getBackgroundColor(int column) {
        RGB result = null;
        if (!hasError()) {
            result = doGetBackgroundColorForValue(delegate);
        } else {
            switch (column) {
            case 0:
                result = doGetBackgroundColorForKey(delegate);
                break;
            case 1:
                result = doGetBackgroundColorForValue(delegate);
                break;
            case 2:
                result = doGetBackgroundColorForError(delegate);
                break;
            default:
                break;
            }
        }

        return result;
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public final RGB getForegroundColor(int column) {
        RGB result = null;
        if (!hasError()) {
            result = doGetForegroundColorForValue(delegate);
        } else {
            switch (column) {
            case 0:
                result = doGetForegroundColorForKey(delegate);
                break;
            case 1:
                result = doGetForegroundColorForValue(delegate);
                break;
            case 2:
                result = doGetForegroundColorForError(delegate);
                break;
            default:
                break;
            }
        }

        return result;
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public final Font getFont(int column) {
        Font result = null;
        if (hasError()) {
            result = doGetFontForError(delegate);
        } else {
            switch (column) {
            case 0:
                result = doGetFontForKey(delegate);
                break;
            case 1:
                result = doGetFontForValue(delegate);
                break;
            case 2:
                result = doGetFontForError(delegate);
                break;
            default:
                break;
            }
        }

        return result;
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public final boolean canModify(int column) {
        boolean result = false;

        switch (column) {
        case 0:
            result = doCanModifyKey(delegate);
            break;
        case 1:
            result = doCanModifyValue(delegate);
            break;
        case 2:
            result = false;
            break;
        default:
            break;
        }

        return result;
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public final Image getImage(int column) {
        return doGetImage(delegate, column);
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public final CellEditor getCellEditor(int column, Composite parent) {
        CellEditor editor = column == 1 ? doGetValueCellEditor(delegate, parent) : null;

        if(editor!=null && editor.getControl()!=null) {
            editor.getControl().setFont(getFont(column));
        }

        return editor;
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public final int compareTo(ITableRow row) {
        return doCompareTo(row);
    }

    /**
     * Returns a cell editor for value column. Returns a {@link TextCellEditor}
     * by default. Subclasses may override.
     *
     * @param delegate
     *            the domain object
     * @param parent
     *            a parent composite
     * @return a cell editor for value column
     */
    protected CellEditor doGetValueCellEditor(E delegate, Composite parent) {
        return new TextCellEditor(parent);
    }

    /**
     * Returns the background color for the key column for the specified domain
     * object. Default is null. Subclasses may override.
     *
     * @param delegate
     *            the domain object
     * @return the background color for the key column
     */
    protected RGB doGetBackgroundColorForKey(E delegate) {
        return null;
    }

    /**
     * Returns the background color for the value column for the specified
     * domain object. Default is null. Subclasses may override.
     *
     * @param delegate
     *            the domain object
     * @return the background color for the value column
     */
    protected RGB doGetBackgroundColorForValue(E delegate) {
        return null;
    }

    /**
     * Returns the background color for the error column for the specified
     * domain object. Default is null. Subclasses may override.
     *
     * @param delegate
     *            the domain object
     * @return the background color for the error column
     */
    protected RGB doGetBackgroundColorForError(E delegate) {
        return new RGB(255, 0, 0);
    }

    /**
     * Returns the font for the key column for the specified domain object.
     * Default is null. Subclasses may override.
     *
     * @param delegate
     *            the domain object
     * @return the font for the key column
     */
    protected Font doGetFontForKey(E delegate) {
        return CustomMediaFactory.getInstance().getFont(DEFAULT_FONT, DEFAULT_FONT_SIZE, SWT.NORMAL);
    }

    /**
     * Returns the font for the value column for the specified domain object.
     * Default is null. Subclasses may override.
     *
     * @param delegate
     *            the domain object
     * @return the font for the value column
     */
    protected Font doGetFontForValue(E delegate) {
        return CustomMediaFactory.getInstance().getFont(DEFAULT_FONT, DEFAULT_FONT_SIZE, SWT.NORMAL);
    }

    /**
     * Returns the font for the error column for the specified domain object.
     * Default is null. Subclasses may override.
     *
     * @param delegate
     *            the domain object
     * @return the font for the error column
     */
    protected Font doGetFontForError(E delegate) {
        return CustomMediaFactory.getInstance().getFont(DEFAULT_FONT, DEFAULT_FONT_SIZE, SWT.NORMAL);
    }

    /**
     * Returns the foreground color for the key column for the specified domain
     * object. Default is null. Subclasses may override.
     *
     * @param delegate
     *            the domain object
     * @return the foreground color for the key column
     */
    protected RGB doGetForegroundColorForKey(E delegate) {
        return null;
    }

    /**
     * Returns the foreground color for the value column for the specified
     * domain object. Default is null. Subclasses may override.
     *
     * @param delegate
     *            the domain object
     * @return the foreground color for the value column
     */
    protected RGB doGetForegroundColorForValue(E delegate) {
        return doCanModifyValue(delegate) ? ColorSettings.MODIFYABLE : ColorSettings.UNMODIFYABLE;
    }

    /**
     * Returns the foreground color for the error column for the specified
     * domain object. Default is null. Subclasses may override.
     *
     * @param delegate
     *            the domain object
     * @return the foreground color for the error column
     */
    protected RGB doGetForegroundColorForError(E delegate) {
        return doCanModifyValue(delegate) ? ColorSettings.MODIFYABLE : ColorSettings.UNMODIFYABLE;
    }

    /**
     * Returns an image for the specified domain object. Default is null.
     * Subclasses may override.
     *
     * @param delegate
     *            the domain object
     * @return an image
     */
    protected Image doGetImage(E delegate, int columnIndex) {
        return null;
    }

    /**
     * Returns the value for the key column. This value will be used as initial
     * value when the key column gets edited.
     *
     * @param delegate
     *            the domain object
     * @return the key for the key column
     */
    protected String doGetKey(E delegate) {
        return null;
    }

    /**
     * Returns a command to apply a new key. This method will be called when the
     * key in the key column is edited by the user. Subclasses should override
     * if changing the key is a needed feature. The method returns null by
     * default.
     *
     * @param delegate
     *            the domain object
     * @param key
     *            the new key
     * @return a command to apply a new key
     */
    protected Command doSetKey(E delegate, Object key) {
        return null;
    }

    /**
     * Returns the key for the key column. This value will displayed in the key
     * column when its in normal view state. This method delegates to
     * {@link #doGetKey(Object)} by default. Subclasses may override.
     *
     * @param delegate
     *            the domain object
     * @return the key for the key column
     */
    protected String doGetKeyDescription(E delegate) {
        return doGetKey(delegate);
    }

    /**
     * Returns the value for the value column. This value will be used as
     * initial value when the value column gets edited.
     *
     * @param delegate
     *            the domain object
     * @return the value for the value column
     */
    protected String doGetValue(E delegate) {
        return null;
    }

    /**
     * Returns a command to apply a new value. This method will be called when
     * the value in the value column is edited by the user. Subclasses should
     * override if changing the value is a needed feature. The method returns
     * null by default.
     *
     * @param delegate
     *            the domain object
     * @param value
     *            the new value
     * @return a command to apply a new value
     */
    protected Command doSetValue(E delegate, Object value) {
        return null;
    }

    /**
     * Returns the value for the value column. This value will displayed in the
     * value column when its in normal view state. This method delegates to
     * {@link #doGetValue(Object)} by default. Subclasses may override.
     *
     * @param delegate
     *            the domain object
     * @return the value for the value column
     */
    protected String doGetValueForDisplay(E delegate) {
        return doGetValue(delegate);
    }

    /**
     * Returns true, if the key in the key column can be modified. Default is
     * false. Subclasses may override.
     *
     * @param delegate
     *            the domain object
     * @return true, if the key in the key column can be modified
     */
    protected boolean doCanModifyKey(E delegate) {
        return false;
    }

    /**
     * Returns true, if the value in the value column can be modified. Default
     * is false. Subclasses may override.
     *
     * @param delegate
     *            the domain object
     * @return true, if the value in the value column can be modified
     */
    protected boolean doCanModifyValue(E delegate) {
        return true;
    }

    /**
     * Compares this row to the specified row. Returns a negative integer, zero,
     * or a positive integer as this row is less than, equal to, or greater than
     * the specified row. Default is 0 (== equal to). Subclasses may override.
     *
     * @param row
     *            another table row
     *
     * @return a negative integer, zero, or a positive integer as this row is
     *         less than, equal to, or greater than the specified row
     */
    protected int doCompareTo(ITableRow row) {
        return 0;
    }

    private boolean hasError() {
        return StringUtil.hasLength(error);
    }

}
