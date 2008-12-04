package org.csstudio.dct.ui.editor;

import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;

public interface ITableRow {

	String getKey();

	void setKey(String string);
	
	String getKeyDescription();

	Object getValue();
	
	Object getValueForDisplay();

	RGB getForegroundColorForKey();

	RGB getBackgroundColorForKey();

	RGB getForegroundColorForValue();

	RGB getBackgroundColorForValue();

	FontData getFontForKey();

	FontData getFontForValue();

	Image getImage();

	void setValue(Object value);

	boolean canModifyKey();

	boolean canModifyValue();

}
