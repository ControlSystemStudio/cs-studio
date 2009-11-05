package org.csstudio.alarm.table.ui;

import org.eclipse.jface.viewers.TableViewer;

public class AlarmExchangeableColumnWidthPreferenceMapping extends
        ExchangeableColumnWidthPreferenceMapping {

    public AlarmExchangeableColumnWidthPreferenceMapping(TableViewer tableViewer, String currentTopicSet) {
        super(tableViewer, currentTopicSet);
    }

//    /**
//     * Saves the width of each column in preferences.
//     */
//    public void saveColumn(String columnSetPreferenceKey, String topicSetPreferenceKey) {
//    	IPreferenceStore store = JmsLogsPlugin.getDefault()
//    	.getPreferenceStore();
//		// if the view is not deposed but the topic set is changed the column
//		// width is not saved by the dispose listener. They width have to saved
//		// manually.
//    	if(_tableViewer != null) {
//    		readColumnWidthFromTable();
//    	}
//    	//get the index of the topic set to identify the corresponding 
//		//column set.
//		Integer indexOfTopicSet = getIndexOfTopicSet(topicSetPreferenceKey, store);
//        String newPreferenceColumnString = ""; //$NON-NLS-1$
//		String[] columnSets = store.getString(columnSetPreferenceKey).split("?"); //$NON-NLS-1$
//		String[] columns = columnSets[indexOfTopicSet].split(";"); //$NON-NLS-1$
//        newPreferenceColumnString = setNewColumnWidth(
//				newPreferenceColumnString, columns);
//		newPreferenceColumnString = newPreferenceColumnString.substring(0,
//				newPreferenceColumnString.length() - 1);
//		columnSets[indexOfTopicSet] = newPreferenceColumnString;
//		StringBuffer sb = new StringBuffer();
//		for (String columnSet : columnSets) {
//			sb.append(columnSet);
//		}
//       if (store.needsSaving()) {
//        	String qualifier = JmsLogsPlugin.getDefault().PLUGIN_ID;
//    		IPreferencesService prefsService = Platform.getPreferencesService();
//    		IEclipsePreferences root = prefsService.getRootNode();
//    		Preferences node = root.node(InstanceScope.SCOPE).node(qualifier);
//    		try {
//				node.flush();
//			} catch (BackingStoreException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
////            JmsLogsPlugin.getDefault().savePluginPreferences();
//        }
//    }

	String setNewColumnWidth(String newPreferenceColumnString,
			String[] columns) {
		for (int i = 0; i < columns.length; i++) {
            newPreferenceColumnString = newPreferenceColumnString
                    .concat(columns[i].split(",")[0] + "," + _columnWidth[i + 1] + ";"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        }
		return newPreferenceColumnString;
	}
}
