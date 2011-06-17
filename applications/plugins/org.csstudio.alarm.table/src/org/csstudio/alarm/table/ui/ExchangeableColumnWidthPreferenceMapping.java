package org.csstudio.alarm.table.ui;

import org.csstudio.alarm.table.JmsLogsPlugin;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.TableColumn;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExchangeableColumnWidthPreferenceMapping {

    private static final Logger LOG = LoggerFactory
            .getLogger(ExchangeableColumnWidthPreferenceMapping.class);
    
	int[] _columnWidth;
	TableViewer _tableViewer;
	String _currentTopicSet;
	
	/**
	 * Adding {@link DisposeListener} to each column that the column width can
	 * be retrieved when the table is disposed.
	 * 
	 * @param tableViewer
	 * @param currentTopicSet 
	 * @param defaultTopicSet
	 */
	public ExchangeableColumnWidthPreferenceMapping(TableViewer tableViewer, String currentTopicSet) {
		this._tableViewer = tableViewer;
		_currentTopicSet = currentTopicSet;
		TableColumn[] columns = tableViewer.getTable().getColumns();
		_columnWidth = new int[columns.length];
		if (columns.length > 0) {
			for (final TableColumn tableColumn : columns) {
				tableColumn.addDisposeListener(new DisposeListener() {
					@Override
                    public void widgetDisposed(DisposeEvent e) {
						_columnWidth[tableColumn.getParent().indexOf(
								tableColumn)] = tableColumn.getWidth();
					}
				});
			}
		} else {
            LOG.warn("There are no column in the table to add a DisposeListener");
		}
	}

	/**
	 * Saves the width of each column in preferences.
	 */
	public void saveColumn(String columnSetPreferenceKey,
			String topicSetPreferenceKey) {
		IPreferenceStore store = JmsLogsPlugin.getDefault()
				.getPreferenceStore();
		// if the view is not disposed but the topic set is changed the column
		// width is not saved by the dispose listener. The width have to saved
		// manually.
		if (_tableViewer != null) {
			readColumnWidthFromTable();
		}
		// get the index of the topic set to identify the corresponding
		// column set.
		Integer indexOfTopicSet = getIndexOfTopicSet(topicSetPreferenceKey,
				store, _currentTopicSet);
		String newPreferenceColumnString = ""; //$NON-NLS-1$
		String[] columnSets = store.getString(columnSetPreferenceKey)
				.split("\\?"); //$NON-NLS-1$
		String[] columns;
		if ((indexOfTopicSet < 0) && (_currentTopicSet == null)) {
			// archive view has no topic set, thus indexOfTopicSet is -1
			columns = columnSets[0].split(";"); //$NON-NLS-1$
		} else {
			if (indexOfTopicSet < 0) {
				// it is not the archive view and there are no previous topic
				// set! Error in preference handling, do not save column set
				return;
			}
			columns = columnSets[indexOfTopicSet].split(";"); //$NON-NLS-1$
		}
		newPreferenceColumnString = setNewColumnWidth(
				newPreferenceColumnString, columns);
		newPreferenceColumnString = newPreferenceColumnString.substring(0,
				newPreferenceColumnString.length() - 1);
		if ((indexOfTopicSet < 0) && (_currentTopicSet == null)) {
			// archive view has no topic set, thus indexOfTopicSet is -1
			columnSets[0] = newPreferenceColumnString;
		} else {
			columnSets[indexOfTopicSet] = newPreferenceColumnString;
		}
		StringBuffer sb = new StringBuffer();
		for (String columnSet : columnSets) {
			sb.append(columnSet);
			sb.append("?");
		}
		store.setValue(columnSetPreferenceKey, sb.toString());
		if (store.needsSaving()) {
			String qualifier = JmsLogsPlugin.getDefault().PLUGIN_ID;
			IPreferencesService prefsService = Platform.getPreferencesService();
			IEclipsePreferences root = prefsService.getRootNode();
			Preferences node = root.node(InstanceScope.SCOPE).node(qualifier);
			try {
				node.flush();
			} catch (BackingStoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// JmsLogsPlugin.getDefault().savePluginPreferences();
		}
	}

	String setNewColumnWidth(String newPreferenceColumnString, String[] columns) {
		for (int i = 0; i < columns.length; i++) {
			newPreferenceColumnString = newPreferenceColumnString
					.concat(columns[i].split(",")[0] + "," + _columnWidth[i] + ";"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}
		return newPreferenceColumnString;
	}

	Integer getIndexOfTopicSet(String topicSetPreferenceKey,
			IPreferenceStore store, String currenTopicSet) {
		String topicSetsString = store.getString(topicSetPreferenceKey);
		String[] topicSets = topicSetsString.split(";");
		Integer topicSetIndex = 0;
		for (String topicSet : topicSets) {
			String[] topicProperties = topicSet.split("\\?");
			if (topicProperties[2].equals(currenTopicSet)) {
				return topicSetIndex;
			}
			topicSetIndex++;
		}
		return -1;
	}

	
	void readColumnWidthFromTable() {
		TableColumn[] columns = _tableViewer.getTable().getColumns();
		_columnWidth = new int[columns.length];
		int i = 0;
		if (columns.length > 0) {
			for (final TableColumn tableColumn : columns) {
				_columnWidth[i] = tableColumn.getWidth();
				i++;
			}
		}
	}
}
