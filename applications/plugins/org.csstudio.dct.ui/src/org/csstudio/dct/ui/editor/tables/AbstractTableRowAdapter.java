package org.csstudio.dct.ui.editor.tables;

import org.csstudio.dct.ui.editor.ColorSettings;
import org.csstudio.platform.util.StringUtil;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Composite;

public class AbstractTableRowAdapter<E> implements ITableRow {
	public static final String DEFAULT_FONT = "Arial";
	public static final int DEFAULT_FONT_SIZE = 11;

	private CommandStack commandStack;
	private E delegate;
	private String error;

	public AbstractTableRowAdapter(E delegate, CommandStack commandStack) {
		assert delegate != null;
		assert commandStack != null;
		this.delegate = delegate;
		this.commandStack = commandStack;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public boolean hasError() {
		return StringUtil.hasLength(error);
	}
	
	public E getDelegate() {
		return delegate;
	}

	public final RGB getBackgroundColorForKey() {
		return doGetBackgroundColorForKey(delegate);
	}

	public final RGB getBackgroundColorForValue() {
		return doGetBackgroundColorForValue(delegate);
	}

	public final FontData getFontForKey() {
		return doGetFontForKey(delegate);
	}

	public final FontData getFontForValue() {
		return doGetFontForValue(delegate);
	}

	public final RGB getForegroundColorForKey() {
		return StringUtil.hasLength(error) ? getForegroundColorForErrors() : doGetForegroundColorForKey(delegate);
	}

	public final RGB getForegroundColorForValue() {
		return StringUtil.hasLength(error) ? getForegroundColorForErrors() : doGetForegroundColorForValue(delegate);
	}

	public final RGB getForegroundColorForErrors() {
		return new RGB(255, 0, 0);
	}

	public final RGB getBackgroundColorForErrors() {
		return null;
	}

	public final Image getImage() {
		return doGetImage(delegate);
	}

	public final String getKey() {
		return doGetKey(delegate);
	}

	public final void setKey(String key) {
		Command cmd = doSetKey(delegate, key);

		if (cmd != null) {
			commandStack.execute(cmd);
		}

	}

	public final String getKeyDescription() {
		return doGetKeyDescription(delegate);
	}

	public final Object getValue() {
		Object v = doGetValue(delegate);

		return v != null ? v : "";
	}

	public final Object getValueForDisplay() {
		return doGetValueForDisplay(delegate);
	}

	public final void setValue(Object value) {
		Command cmd = doSetValue(delegate, value);

		if (cmd != null) {
			commandStack.execute(cmd);
		}
	}

	public final boolean canModifyKey() {
		return doCanModifyKey(delegate);
	}

	public final boolean canModifyValue() {
		return doCanModifyValue(delegate);
	}

	public final CellEditor getValueCellEditor(Composite parent) {
		return doGetValueCellEditor(delegate, parent);
	}

	public int compareTo(ITableRow row) {
		return 0;
	}

	protected CellEditor doGetValueCellEditor(E delegate, Composite parent) {
		return new TextCellEditor(parent);
	}

	protected RGB doGetBackgroundColorForKey(E delegate) {
		return null;
	}

	protected RGB doGetBackgroundColorForValue(E delegate) {
		return null;
	}

	protected FontData doGetFontForKey(E delegate) {
		return new FontData(DEFAULT_FONT, DEFAULT_FONT_SIZE, SWT.NORMAL);
	}

	protected FontData doGetFontForValue(E delegate) {
		return new FontData(DEFAULT_FONT, DEFAULT_FONT_SIZE, SWT.NORMAL);
	}

	protected RGB doGetForegroundColorForKey(E delegate) {
		return null;
	}

	protected RGB doGetForegroundColorForValue(E delegate) {
		return canModifyValue() ? ColorSettings.MODIFYABLE : ColorSettings.UNMODIFYABLE;
	}

	protected Image doGetImage(E delegate) {
		return null;
	}

	protected String doGetKey(E delegate) {
		return null;
	}

	protected Command doSetKey(E delegate, String key) {
		return null;
	}

	protected String doGetKeyDescription(E delegate) {
		return doGetKey(delegate);
	}

	protected Object doGetValue(E delegate) {
		return null;
	}

	protected Object doGetValueForDisplay(E delegate) {
		return doGetValue(delegate);
	}

	protected Command doSetValue(E delegate, Object value) {
		return null;
	}

	protected boolean doCanModifyKey(E delegate) {
		return false;
	}

	protected boolean doCanModifyValue(E delegate) {
		return true;
	}


}
