package org.csstudio.alarm.table.ui;

import org.csstudio.alarm.table.JmsLogsPlugin;
import org.csstudio.platform.logging.CentralLogger;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.TableColumn;

public class ColumnWidthPreferenceMapping {

    int[] _columnWidth;

    /**
     * Adding {@link DisposeListener} to each column that the column width
     * can be retrieved when the table is disposed.
     * 
     * @param tableViewer
     */
    public ColumnWidthPreferenceMapping(TableViewer tableViewer) {
        TableColumn[] columns = tableViewer.getTable().getColumns();
        _columnWidth = new int[columns.length];
        if (columns.length > 0) {
            for (final TableColumn tableColumn : columns) {
                tableColumn.addDisposeListener(new DisposeListener() {
                    public void widgetDisposed(DisposeEvent e) {
                        _columnWidth[tableColumn.getParent().indexOf(
                                tableColumn)] = tableColumn.getWidth();
                    }
                });
            }
        } else {
            CentralLogger.getInstance().warn(
                    this,
                    "There are no column"
                            + " in the table to add a DisposeListener");
        }
    }

    /**
     * Saves the width of each column in preferences.
     */
    public void saveColumn(String preferenceColumnConstant) {
        String newPreferenceColumnString = ""; //$NON-NLS-1$
        String[] columns = JmsLogsPlugin.getDefault().getPluginPreferences()
                .getString(preferenceColumnConstant).split(";"); //$NON-NLS-1$
        for (int i = 0; i < columns.length; i++) {
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
