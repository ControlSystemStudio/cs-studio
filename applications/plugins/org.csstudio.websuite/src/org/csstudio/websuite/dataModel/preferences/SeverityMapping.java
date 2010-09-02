
/* 
 * Copyright (c) 2009 Stiftung Deutsches Elektronen-Synchrotron, 
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS. 
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED 
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND 
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE 
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, 
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR 
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE 
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR 
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE. 
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS, 
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION, 
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS 
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY 
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */

package org.csstudio.websuite.dataModel.preferences;

import org.csstudio.websuite.WebSuiteActivator;
import org.csstudio.websuite.internal.PreferenceConstants;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;

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
        
        IPreferencesService preferenceStore = Platform.getPreferencesService();

        if (severityKey.equals(preferenceStore.getString(WebSuiteActivator.PLUGIN_ID, PreferenceConstants.KEY_0, "", null))) { //$NON-NLS-1$
            return preferenceStore.getString(WebSuiteActivator.PLUGIN_ID, PreferenceConstants.VALUE_0, "", null); //$NON-NLS-1$
        }
        if (severityKey.equals(preferenceStore.getString(WebSuiteActivator.PLUGIN_ID, PreferenceConstants.KEY_1, "", null))) { //$NON-NLS-1$
            return preferenceStore.getString(WebSuiteActivator.PLUGIN_ID, PreferenceConstants.VALUE_1, "", null); //$NON-NLS-1$
        }
        if (severityKey.equals(preferenceStore.getString(WebSuiteActivator.PLUGIN_ID, PreferenceConstants.KEY_2, "", null))) { //$NON-NLS-1$
            return preferenceStore.getString(WebSuiteActivator.PLUGIN_ID, PreferenceConstants.VALUE_2, "", null); //$NON-NLS-1$
        }
        if (severityKey.equals(preferenceStore.getString(WebSuiteActivator.PLUGIN_ID, PreferenceConstants.KEY_3, "", null))) { //$NON-NLS-1$
            return preferenceStore.getString(WebSuiteActivator.PLUGIN_ID, PreferenceConstants.VALUE_3, "", null); //$NON-NLS-1$
        }
        if (severityKey.equals(preferenceStore.getString(WebSuiteActivator.PLUGIN_ID, PreferenceConstants.KEY_4, "", null))) { //$NON-NLS-1$
            return preferenceStore.getString(WebSuiteActivator.PLUGIN_ID, PreferenceConstants.VALUE_4, "", null); //$NON-NLS-1$
        }
        if (severityKey.equals(preferenceStore.getString(WebSuiteActivator.PLUGIN_ID, PreferenceConstants.KEY_5, "", null))) { //$NON-NLS-1$
            return preferenceStore.getString(WebSuiteActivator.PLUGIN_ID, PreferenceConstants.VALUE_5, "", null); //$NON-NLS-1$
        }
        if (severityKey.equals(preferenceStore.getString(WebSuiteActivator.PLUGIN_ID, PreferenceConstants.KEY_6, "", null))) { //$NON-NLS-1$
            return preferenceStore.getString(WebSuiteActivator.PLUGIN_ID, PreferenceConstants.VALUE_6, "", null); //$NON-NLS-1$
        }
        if (severityKey.equals(preferenceStore.getString(WebSuiteActivator.PLUGIN_ID, PreferenceConstants.KEY_7, "", null))) { //$NON-NLS-1$
            return preferenceStore.getString(WebSuiteActivator.PLUGIN_ID, PreferenceConstants.VALUE_7, "", null); //$NON-NLS-1$
        }
        if (severityKey.equals(preferenceStore.getString(WebSuiteActivator.PLUGIN_ID, PreferenceConstants.KEY_8, "", null))) { //$NON-NLS-1$
            return preferenceStore.getString(WebSuiteActivator.PLUGIN_ID, PreferenceConstants.VALUE_8, "", null); //$NON-NLS-1$
        }
        if (severityKey.equals(preferenceStore.getString(WebSuiteActivator.PLUGIN_ID, PreferenceConstants.KEY_9, "", null))) { //$NON-NLS-1$
            return preferenceStore.getString(WebSuiteActivator.PLUGIN_ID, PreferenceConstants.VALUE_9, "", null); //$NON-NLS-1$
        }

        return "invalid severity";
    }

    /**
     * Returns the number of the severity. The number represents the level of
     * the severity.
     * 
     * @return
     */
    public static int getSeverityNumber(String severityKey)
    {
        IPreferencesService preferenceStore = Platform.getPreferencesService();

        if (severityKey.equals(preferenceStore.getString(WebSuiteActivator.PLUGIN_ID, PreferenceConstants.KEY_0, "", null))) { //$NON-NLS-1$
            return 0;
        }
        if (severityKey.equals(preferenceStore.getString(WebSuiteActivator.PLUGIN_ID, PreferenceConstants.KEY_1, "", null))) { //$NON-NLS-1$
            return 1;
        }
        if (severityKey.equals(preferenceStore.getString(WebSuiteActivator.PLUGIN_ID, PreferenceConstants.KEY_2, "", null))) { //$NON-NLS-1$
            return 2;
        }
        if (severityKey.equals(preferenceStore.getString(WebSuiteActivator.PLUGIN_ID, PreferenceConstants.KEY_3, "", null))) { //$NON-NLS-1$
            return 3;
        }
        if (severityKey.equals(preferenceStore.getString(WebSuiteActivator.PLUGIN_ID, PreferenceConstants.KEY_4, "", null))) { //$NON-NLS-1$
            return 4;
        }
        if (severityKey.equals(preferenceStore.getString(WebSuiteActivator.PLUGIN_ID, PreferenceConstants.KEY_5, "", null))) { //$NON-NLS-1$
            return 5;
        }
        if (severityKey.equals(preferenceStore.getString(WebSuiteActivator.PLUGIN_ID, PreferenceConstants.KEY_6, "", null))) { //$NON-NLS-1$
            return 6;
        }
        if (severityKey.equals(preferenceStore.getString(WebSuiteActivator.PLUGIN_ID, PreferenceConstants.KEY_7, "", null))) { //$NON-NLS-1$
            return 7;
        }
        if (severityKey.equals(preferenceStore.getString(WebSuiteActivator.PLUGIN_ID, PreferenceConstants.KEY_8, "", null))) { //$NON-NLS-1$
            return 8;
        }
        if (severityKey.equals(preferenceStore.getString(WebSuiteActivator.PLUGIN_ID, PreferenceConstants.KEY_9, "", null))) { //$NON-NLS-1$
            return 9;
        }

        return -1;

    }
    
}
