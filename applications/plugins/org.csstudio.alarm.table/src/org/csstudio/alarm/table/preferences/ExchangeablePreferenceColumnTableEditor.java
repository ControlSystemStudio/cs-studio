package org.csstudio.alarm.table.preferences;

import org.csstudio.platform.logging.CentralLogger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

/**
 * Preference Table to set the names and widths of columns in 
 * message tables.
 * 
 * @author jhatje
 * 
 */
public class ExchangeablePreferenceColumnTableEditor extends PreferenceColumnTableEditor {


	
	/**
	 * Creates a new list field editor
	 */
	protected ExchangeablePreferenceColumnTableEditor() {
	}

	/**
	 * Creates a list field editor.
	 * 
	 * @param name
	 *            the name of the preference this field editor works on
	 * @param labelText
	 *            the label text of the field editor
	 * @param parent
	 *            the parent of the field editor's control
	 */
	public ExchangeablePreferenceColumnTableEditor(String name, String labelText,
			Composite parent) {
		super(name, labelText, parent);
		_row = -1;
	}


	/**
	 * Set the file path and menu name set by the user from preferences in the
	 * table rows.
	 */
	/*
	 * (non-Javadoc) Method declared on FieldEditor.
	 */
	protected void doLoad() {
		// if (tableViewer != null) {
		String s = getPreferenceStore().getString(getPreferenceName());
		_columnTableSettings = parseString(s);
	}


	/**
	 * Update the view of this table when a new topic set in TopicsSetTable is
	 * selected.
	 * 
	 * @param row
	 * @param topicTitle
	 */
	public void setSelectionToColumnEditor(int row, String topicTitle) {
		CentralLogger.getInstance().debug(this,
				"Selected row in topic table: " + row + " " + topicTitle);

		_mouseListener.cleanUp();

		if (_columnTableSettings == null) {
			CentralLogger.getInstance().error(this,
					"no related column settings!");
			return;
		}
		updateTopicTitle(topicTitle);

		Table table = tableViewer.getTable();
		// write current table content in the array of the previous selected
		// topic row.
		if (0 <= _row) {
			setTableSettingsToPreferenceString(table);
			table.removeAll();
		}
		_row = row;
		// put new content of the new selection of topic table to
		// this table.
		if ((0 > _row) || (_row >= _columnTableSettings.size())) {
			_currentColumnTableSet = _columnTableSettings.get(0);
			if (_currentColumnTableSet == null) {
				CentralLogger.getInstance().warn(this,
						"no column settings in default");
				return;
			}
		} else {
			_currentColumnTableSet = null;
			_currentColumnTableSet = _columnTableSettings.get(row);
		}
		TableItem item;
		for (int i = 0; i < _currentColumnTableSet.size(); i++) {
			item = new TableItem(tableViewer.getTable(), SWT.NONE);
			String[] tableRowFromPreferences = _currentColumnTableSet.get(i);
			item.setText(tableRowFromPreferences);
		}
		// _topicSetName.redraw();
		tableViewer.getTable().redraw();
	}


	public void updateTopicTitle(String topicTitle) {
		_topicSetName.setText(topicTitle);
	}


}
