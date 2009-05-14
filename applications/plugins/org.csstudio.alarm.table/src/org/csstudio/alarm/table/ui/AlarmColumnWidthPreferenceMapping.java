package org.csstudio.alarm.table.ui;

import org.csstudio.alarm.table.JmsLogsPlugin;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.TableViewer;

public class AlarmColumnWidthPreferenceMapping extends
        ColumnWidthPreferenceMapping {

    public AlarmColumnWidthPreferenceMapping(TableViewer tableViewer) {
        super(tableViewer);
    }

    /**
     * Saves the width of each column in preferences.
     */
    public void saveColumn(String preferenceColumnConstant) {
        String newPreferenceColumnString = ""; //$NON-NLS-1$
        String[] columns = JmsLogsPlugin.getDefault().getPluginPreferences()
                .getString(preferenceColumnConstant).split(";"); //$NON-NLS-1$
        for (int i = 1; i < columns.length; i++) {
            newPreferenceColumnString = newPreferenceColumnString
                    .concat(columns[i].split(",")[0] + "," + _columnWidth[i] + ";"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        }
        newPreferenceColumnString = newPreferenceColumnString.substring(0,
                newPreferenceColumnString.length() - 1);
        IPreferenceStore store = JmsLogsPlugin.getDefault()
                .getPreferenceStore();
        store.setValue(preferenceColumnConstant,
                newPreferenceColumnString);
        if (store.needsSaving()) {
            JmsLogsPlugin.getDefault().savePluginPreferences();
        }
    }
}
