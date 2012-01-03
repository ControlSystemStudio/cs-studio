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

import javax.annotation.Nonnull;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FontDialog;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Executes action after mouse double click according to columns and MouseActionDescription.
 *
 * @author jpenning
 * @author $Author$
 * @version $Revision$
 * @since 24.06.2010
 */
public class TopicTableEditorMouseListener extends MouseAdapter {

    private static final Logger LOG = LoggerFactory.getLogger(TopicTableEditorMouseListener.class);
    
	private final TableEditor _editor;
	private final Table _table;
	private final PreferenceTopicTableEditor _preferenceTopicTableEditor;

	public TopicTableEditorMouseListener(@Nonnull final TableEditor editor,
	                                     @Nonnull final PreferenceTopicTableEditor preferenceTopicTableEditor) {
		_editor = editor;
		_preferenceTopicTableEditor = preferenceTopicTableEditor;
		_table = preferenceTopicTableEditor.getTableViewer().getTable();
	}

	/**
	 * Dispose the editor before selecting a new row otherwise the cell and not
	 * the row will be selected and it is not possible to move the row up and
	 * down.
	 */
	@Override
	public void mouseDown(@Nonnull final MouseEvent event) {
		// Dispose any existing editor
		final Control old = _editor.getEditor();
		if (old != null) {
            old.dispose();
        }

		// Determine where the mouse was clicked
		final Point pt = new Point(event.x, event.y);

		// Determine which row was selected
		final TableItem item = _table.getItem(pt);
		for (int i = 0; i < _table.getItemCount(); i++) {
			if (item == _table.getItem(i)) {
				final String topicTitle = item.getText(2);
				if (_preferenceTopicTableEditor != null) {
					_preferenceTopicTableEditor.setRowOfTopicSelection(i,
							topicTitle);
					break;
				}
                LOG.error("Cannot update column tabel (null)!");
                break;
			}
		}
	}

	/**
	 * Make the selected cell editable with a double click. (Copy from an
	 * internet example)
	 */
	@Override
    public void mouseDoubleClick(@Nonnull final MouseEvent event) {
		// Dispose any existing editor
		final Control old = _editor.getEditor();
		if (old != null) {
            old.dispose();
        }

		// Determine where the mouse was clicked
		final Point pt = new Point(event.x, event.y);

		// Determine which row was selected
		final TableItem item = _table.getItem(pt);
		if (item != null) {
			final int column = getColumnIndex(pt, item);
			final ColumnDescription columnDescription = _preferenceTopicTableEditor.getColumnDescriptions().get(column);
            switch (columnDescription.getMouseActionDescription()) {
                case NO_ACTION:
                    break;

                case EDIT_STRING:
                    editString(item, column);
                    break;

                case OPEN_FONT_DIALOGUE:
                    openFontDialogue(item, column);
                    break;

                case TOGGLE_BOOL:
                    toggleBool(item, column);
                    break;

                default:
                    LOG.error("Mouse action {} not handled after double clicking column {}", columnDescription.getMouseActionDescription(), column);

            }
		}
	}

    private void toggleBool(@Nonnull final TableItem item, final int column) {
        final String text = item.getText(column);
        if (text.equals("false")) {
            item.setText(column, "true");
        } else {
            item.setText(column, "false");
        }
        LOG.debug("text of column {}: {}",column, text);
    }

    private void openFontDialogue(@Nonnull final TableItem item, final int column) {
        // read current font settings to initialize FontDialog
        final String[] fontDataString = item.getText(column).split(",");
        final FontData[] font = createFontFromPreferenceString(fontDataString);
        try {
            FontDialog fontDialog = new FontDialog(_table.getShell());
            fontDialog.setFontList(font);
            font[0] = fontDialog.open();
        } catch (final Exception e) {
            LOG.error("Error creating font ",e);
        }
        item.setText(column, font[0].getName() + "," + font[0].getStyle()
                + "," + font[0].getHeight());
        
        Object[] args = new Object[] {column, font[0].getName(), font[0].getStyle(), font[0].getHeight(), font[0].toString()};
        LOG.debug("text of column {}: Name {} style {} height {} string {}", args);
    }

    private void editString(@Nonnull final TableItem item, final int column) {
        // Create the Text object for our editor
        final Text text = new Text(_table, SWT.NONE);
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
            @Override
            public void modifyText(@Nonnull final ModifyEvent event) {
                // Set the text of the editor's control back into the cell
                item.setText(col, text.getText());
                _preferenceTopicTableEditor
                        .updateTopicTitle(text.getText());
            }
        });
    }

	/**
	 * Create new {@link FontData} Object from Preference String. If there is no
	 * preference string or the values are not valid, create {@link FontData}
	 * from JfaceDefault.
	 *
	 * @param fontDataString
	 * @return
	 */
    @Nonnull
	private FontData[] createFontFromPreferenceString(@Nonnull final String[] fontDataString) {
		FontData[] font = new FontData[1];
		try {
			if ((fontDataString[0] != null) && (fontDataString[1] != null)
					&& (fontDataString[2] != null)) {
				font[0] = new FontData(fontDataString[0], Integer
						.parseInt(fontDataString[2]), Integer
						.parseInt(fontDataString[1]));
			}
		} catch (final Exception e) {
		    LOG.error("Cannot create font, ", e);
		}
		if (font[0] == null) {
			font = JFaceResources.getDefaultFont().getFontData();
		}
		return font;
	}

	private int getColumnIndex(@Nonnull final Point pt, @Nonnull final TableItem item) {
		// Determine which column was selected
		int column = -1;
		for (int i = 0, n = _table.getColumnCount(); i < n; i++) {
			final Rectangle rect = item.getBounds(i);
			if (rect.contains(pt)) {
				// This is the selected column
				column = i;
				break;
			}
		}
		return column;
	}
}
