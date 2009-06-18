package org.csstudio.alarm.table.preferences;

import org.csstudio.alarm.table.JmsLogsPlugin;
import org.eclipse.jface.preference.IPreferenceStore;

public class SeverityMapping {

    /**
     * returns the severity value for the severity key of this message.
     * 
     * @return
     */
    public static String findSeverityValue(String severityKey) {

        if (severityKey.equals("")) {
            return "";
        }
        IPreferenceStore preferenceStore = JmsLogsPlugin.getDefault()
                .getPreferenceStore();

        if (severityKey.equals(preferenceStore.getString("key 0"))) { //$NON-NLS-1$
            return preferenceStore.getString("value 0"); //$NON-NLS-1$
        }
        if (severityKey.equals(preferenceStore.getString("key 1"))) { //$NON-NLS-1$
            return preferenceStore.getString("value 1"); //$NON-NLS-1$
        }
        if (severityKey.equals(preferenceStore.getString("key 2"))) { //$NON-NLS-1$
            return preferenceStore.getString("value 2"); //$NON-NLS-1$
        }
        if (severityKey.equals(preferenceStore.getString("key 3"))) { //$NON-NLS-1$
            return preferenceStore.getString("value 3"); //$NON-NLS-1$
        }
        if (severityKey.equals(preferenceStore.getString("key 4"))) { //$NON-NLS-1$
            return preferenceStore.getString("value 4"); //$NON-NLS-1$
        }
        if (severityKey.equals(preferenceStore.getString("key 5"))) { //$NON-NLS-1$
            return preferenceStore.getString("value 5"); //$NON-NLS-1$
        }
        if (severityKey.equals(preferenceStore.getString("key 6"))) { //$NON-NLS-1$
            return preferenceStore.getString("value 6"); //$NON-NLS-1$
        }
        if (severityKey.equals(preferenceStore.getString("key 7"))) { //$NON-NLS-1$
            return preferenceStore.getString("value 7"); //$NON-NLS-1$
        }
        if (severityKey.equals(preferenceStore.getString("key 8"))) { //$NON-NLS-1$
            return preferenceStore.getString("value 8"); //$NON-NLS-1$
        }
        if (severityKey.equals(preferenceStore.getString("key 9"))) { //$NON-NLS-1$
            return preferenceStore.getString("value 9"); //$NON-NLS-1$
        }

        return "invalid severity";
    }

    /**
     * Returns the number of the severity. The number represents the level of
     * the severity.
     * 
     * @return
     */
    public static int getSeverityNumber(String severityKey) {
        IPreferenceStore preferenceStore = JmsLogsPlugin.getDefault()
                .getPreferenceStore();
        

        if (severityKey.equals(preferenceStore.getString("key 0"))) { //$NON-NLS-1$
            return 0;
        }
        if (severityKey.equals(preferenceStore.getString("key 1"))) { //$NON-NLS-1$
            return 1;
        }
        if (severityKey.equals(preferenceStore.getString("key 2"))) { //$NON-NLS-1$
            return 2;
        }
        if (severityKey.equals(preferenceStore.getString("key 3"))) { //$NON-NLS-1$
            return 3;
        }
        if (severityKey.equals(preferenceStore.getString("key 4"))) { //$NON-NLS-1$
            return 4;
        }
        if (severityKey.equals(preferenceStore.getString("key 5"))) { //$NON-NLS-1$
            return 5;
        }
        if (severityKey.equals(preferenceStore.getString("key 6"))) { //$NON-NLS-1$
            return 6;
        }
        if (severityKey.equals(preferenceStore.getString("key 7"))) { //$NON-NLS-1$
            return 7;
        }
        if (severityKey.equals(preferenceStore.getString("key 8"))) { //$NON-NLS-1$
            return 8;
        }
        if (severityKey.equals(preferenceStore.getString("key 9"))) { //$NON-NLS-1$
            return 9;
        }

        return -1;

    }
    
}
