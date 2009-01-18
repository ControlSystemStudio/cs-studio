package org.csstudio.dct.ui.editor;

import org.csstudio.dct.ui.editor.tables.AbstractTableRowAdapter;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.RGB;


public class AbstractReadOnlyTableRowAdapter<E> extends AbstractTableRowAdapter<E>{

	public AbstractReadOnlyTableRowAdapter(E delegate, CommandStack commandStack) {
		super(delegate, commandStack);
	}

	@Override
	protected boolean doCanModifyKey(E delegate2) {
		return false;
	}

	@Override
	protected boolean doCanModifyValue(E delegate2) {
		return false;
	}

	@Override
	protected RGB doGetBackgroundColorForKey(E delegate) {
		return new RGB(255,255,255);
	}

	@Override
	protected RGB doGetBackgroundColorForValue(E delegate) {
		return new RGB(255,255,255);
	}

	@Override
	protected FontData doGetFontForKey(E delegate) {
		return new FontData("Arial", 10, SWT.ITALIC);
	}

	@Override
	protected FontData doGetFontForValue(E delegate) {
		return new FontData(DEFAULT_FONT, DEFAULT_FONT_SIZE, SWT.ITALIC);
	}
	
}
