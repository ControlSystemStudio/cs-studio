/*
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron, Member of the Helmholtz
 * Association, (DESY), HAMBURG, GERMANY. THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN
 * "../AS IS" BASIS. WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO
 * EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH
 * THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE IN
 * ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR CORRECTION. THIS
 * DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE. NO USE OF ANY SOFTWARE IS
 * AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER. DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE,
 * SUPPORT, UPDATES, ENHANCEMENTS, OR MODIFICATIONS. THE FULL LICENSE SPECIFYING FOR THE SOFTWARE
 * THE REDISTRIBUTION, MODIFICATION, USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE
 * DISTRIBUTION OF THIS PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY
 * FIND A COPY AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */

package org.csstudio.alarm.table.preferences;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

public class ColumnTableEditorMouseListener extends MouseAdapter {

	private final TableEditor _editor;
	private final PreferenceColumnTableEditor _preferenceColumnTableEditor;

	public ColumnTableEditorMouseListener(final TableEditor editor, final PreferenceColumnTableEditor preferenceColumnTableEditor) {
		_editor = editor;
		_preferenceColumnTableEditor = preferenceColumnTableEditor;
	}

	/**
	 * Dispose the editor before selecting a new row otherwise the cell and not
	 * the row will be selected and it is not possible to move the row up and
	 * down.
	 */
	@Override
	public void mouseDown(final MouseEvent event) {
		// Dispose any existing editor
		Control old = _editor.getEditor();
		if (old != null)
			old.dispose();
	}

	/**
	 * Make the selected cell editable with a double click. (Copy from an
	 * internet example)
	 */
	public void mouseDoubleClick(final MouseEvent event) {
		// Dispose any existing editor
		Control old = _editor.getEditor();
		if (old != null)
			old.dispose();

		// Determine where the mouse was clicked
		Point pt = new Point(event.x, event.y);

		// Determine which row was selected
		final TableItem item = _preferenceColumnTableEditor.getTable().getItem(pt);
		if (item != null) {
			// Determine which column was selected
			int column = -1;
			for (int i = 0, n = _preferenceColumnTableEditor.getTable().getColumnCount(); i < n; i++) {
				Rectangle rect = item.getBounds(i);
				if (rect.contains(pt)) {
					// This is the selected column
					column = i;
					break;
				}
			}

			// Create the Text object for our editor
			final Text text = new Text(_preferenceColumnTableEditor.getTable(), SWT.NONE);
			text.setForeground(item.getForeground());

			// Transfer any text from the cell to the Text control,
			// set the color to match this row, select the text,
			// and set focus to the control
			text.setText(item.getText(column));
			text.setForeground(item.getForeground());
			text.selectAll();
			text.setFocus();

			// Recalculate the minimum width for the editor
			_editor.minimumWidth = text.getBounds().width;

			// Set the control into the editor
			_editor.setEditor(text, item, column);

			// Add a handler to transfer the text back to the cell
			// any time it's modified
			final int col = column;
			text.addModifyListener(new ModifyListener() {
				public void modifyText(final ModifyEvent event) {
					_preferenceColumnTableEditor.updateColumnSettings();
					// Set the text of the editor's control back into the cell
					item.setText(col, text.getText());
				}
			});
		}
	}

	public void cleanUp() {
		Control old = _editor.getEditor();
		if (old != null)
			old.dispose();

	}
}
