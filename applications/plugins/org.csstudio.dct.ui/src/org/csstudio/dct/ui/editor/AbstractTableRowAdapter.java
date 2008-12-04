package org.csstudio.dct.ui.editor;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;

public class AbstractTableRowAdapter<E> implements ITableRow {
	public static final String DEFAULT_FONT = "Arial";
	public static final int DEFAULT_FONT_SIZE = 11;

	private CommandStack commandStack;
	private E delegate;

	public AbstractTableRowAdapter(E delegate, CommandStack commandStack) {
		assert delegate != null;
		assert commandStack != null;
		this.delegate = delegate;
		this.commandStack = commandStack;
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
		return doGetForegroundColorForKey(delegate);
	}

	public final RGB getForegroundColorForValue() {
		return doGetForegroundColorForValue(delegate);
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
		return doGetValue(delegate);
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
		return null;
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

	protected boolean doCanModifyKey(E delegate2) {
		return false;
	}

	protected boolean doCanModifyValue(E delegate2) {
		return true;
	}
}
