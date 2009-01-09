package org.csstudio.dct.ui.editor.tables;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Composite;

public interface ITableRow extends Comparable<ITableRow>{

	boolean hasError();
	
	String getKey();

	void setKey(String string);
	
	String getKeyDescription();

	Object getValue();
	
	Object getValueForDisplay();

	RGB getForegroundColorForKey();

	RGB getBackgroundColorForKey();

	RGB getForegroundColorForValue();

	RGB getBackgroundColorForValue();

	RGB getForegroundColorForErrors();
	
	RGB getBackgroundColorForErrors();

	FontData getFontForKey();

	FontData getFontForValue();

	Image getImage();

	void setValue(Object value);

	boolean canModifyKey();

	boolean canModifyValue();

	CellEditor getValueCellEditor(Composite parent);

	String getError();

}
