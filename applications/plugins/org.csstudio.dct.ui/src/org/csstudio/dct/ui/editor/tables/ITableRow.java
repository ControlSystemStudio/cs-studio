package org.csstudio.dct.ui.editor.tables;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Composite;

/**
 * Represents a table row in a standardized {@link BaseTable}. The standardized
 * table consists of 3 columns. The first column contains a key (or
 * description), the second a value and the third (optionally) an error message.
 * Rows for the standardized table have to implement this interface which allows
 * for the manipulation of many layout aspects (e.g. fore and background
 * colors), too.
 * 
 * @author Sven Wende
 * 
 */
public interface ITableRow extends Comparable<ITableRow> {

	/**
	 * Returns the key which is displayed in the first column of the table.
	 * 
	 * @return the key
	 */
	String getKey();

	/**
	 * This method is called, when the user changes the key in the first column
	 * of the table.
	 * 
	 * @param key
	 *            the new key
	 */
	void setKey(String key);

	/**
	 * Returns a description for the key in the first column.
	 * 
	 * @return a description for the key in the first column
	 */
	String getKeyDescription();

	/**
	 * Returns the value which is initially displayed in the second column when
	 * the user switches into edit mode.
	 * 
	 * @return the editing value for the second column
	 */
	Object getValue();
	
	/**
	 * This method is called, when the user changes the value in the second column
	 * of the table.
	 * 
	 * @param value
	 *            the new value
	 */
	void setValue(Object value);

	/**
	 * Returns the value which is displayed in the second column.
	 * 
	 * @return the display value for the second column
	 */
	Object getValueForDisplay();

	/**
	 * Returns true, if there is an error for this row.
	 * 
	 * @return true, if there is an error for this row
	 */
	boolean hasError();

	/**
	 * Returns the error which is displayed in the third column of the table.
	 * 
	 * @return the error
	 */
	String getError();
	
	/**
	 * Returns the foreground color for the first (key) column.
	 * 
	 * @return the foreground color for the first (key) column
	 */
	RGB getForegroundColorForKey();

	/**
	 * Returns the background color for the first (key) column.
	 * 
	 * @return the background color for the first (key) column
	 */
	RGB getBackgroundColorForKey();

	/**
	 * Returns the foreground color for the second (value) column.
	 * 
	 * @return the foreground color for the second (value) column
	 */
	RGB getForegroundColorForValue();

	/**
	 * Returns the background color for the second (value) column.
	 * 
	 * @return the background color for the second (value) column
	 */
	RGB getBackgroundColorForValue();

	/**
	 * Returns the foreground color for the third (error) column.
	 * 
	 * @return the foreground color for the third (error) column
	 */
	RGB getForegroundColorForErrors();

	/**
	 * Returns the background color for the third (error) column.
	 * 
	 * @return the background color for the third (error) column
	 */
	RGB getBackgroundColorForErrors();

	/**
	 * Returns the font for the first (key) column.
	 * 
	 * @return the font for the first (key) column
	 */
	FontData getFontForKey();

	/**
	 * Returns the font for the second (value) column.
	 * 
	 * @return the font for the second (value) column
	 */
	FontData getFontForValue();

	/**
	 * Returns the font for the third (error) column.
	 * 
	 * @return the font for the third (error) column
	 */
	FontData getFontForError();
	
	/**
	 * Returns an image for the row.
	 * 
	 * @return an image for the row
	 */
	Image getImage();

	/**
	 * Returns true, if the key in the first column can be changed by the user.
	 * @return true, if the key in the first column can be changed by the user
	 */
	boolean canModifyKey();

	/**
	 * Returns true, if the value in the second column can be changed by the user.
	 * @return true, if the value in the second column can be changed by the user
	 */	
	boolean canModifyValue();

	/**
	 * Returns the cell editor for the second (value) column.
	 * 
	 * @param parent the parent composite
	 * 
	 * @return the cell editor for the second (value) column
	 */
	CellEditor getValueCellEditor(Composite parent);

	

}
