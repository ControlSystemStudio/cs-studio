/*
		* Copyright (c) 2010 Stiftung Deutsches Elektronen-Synchrotron,
		* Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
		*
		* THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS.
		* WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT
		NOT LIMITED
		* TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE
		AND
		* NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS
		BE LIABLE
		* FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF
		CONTRACT,
		* TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
		SOFTWARE OR
		* THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE
		DEFECTIVE
		* IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING,
		REPAIR OR
		* CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART
		OF THIS LICENSE.
		* NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS
		DISCLAIMER.
		* DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES,
		ENHANCEMENTS,
		* OR MODIFICATIONS.
		* THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION,
		MODIFICATION,
		* USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE
		DISTRIBUTION OF THIS
		* PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU
		MAY FIND A COPY
		* AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
		*/
package org.csstudio.utility.quickstart.preferences;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO (jhatje) :
 *
 * @author jhatje
 * @author $Author: jhatje $
 * @version $Revision: 1.1.2.2 $
 * @since 05.08.2010
 */
public class PreferenceValidator {
 
    private static final Logger LOG = LoggerFactory.getLogger(PreferenceValidator.class);
    
    /**
     * Check if there are all preference entries present and valid.
     * 
     * @param tableRowFromPreferences
     * @return
     */
    public String[] checkPreferenceValidity(String[] tableRowFromPreferences) {
        String[] newPreferencePart = new String[3];
        newPreferencePart[0] = "invalid";
        newPreferencePart[1] = "";
        newPreferencePart[2] = "false";
        try {
        if (tableRowFromPreferences == null || tableRowFromPreferences.length == 0) {
            return newPreferencePart;
        }
        if (tableRowFromPreferences.length == 1 && tableRowFromPreferences[0] != null) {
            newPreferencePart[0] = tableRowFromPreferences[0];
            return newPreferencePart;
        }
        if (tableRowFromPreferences.length == 2 && tableRowFromPreferences[0] != null) {
            newPreferencePart[0] = tableRowFromPreferences[0];
            if (tableRowFromPreferences[1] != null) {
                if (tableRowFromPreferences[1].equals("true") || tableRowFromPreferences[1].equals("false")) {
                    newPreferencePart[2] = tableRowFromPreferences[1];
                } else {
                    newPreferencePart[1] = tableRowFromPreferences[1];
                }
            }
            return newPreferencePart;
        }
        if (tableRowFromPreferences.length == 3 && tableRowFromPreferences[0] != null) {
            newPreferencePart[0] = tableRowFromPreferences[0];
            if (tableRowFromPreferences[1] != null) {
                newPreferencePart[1] = tableRowFromPreferences[1];
            }
            if (tableRowFromPreferences[2] != null) {
                if (tableRowFromPreferences[2].equals("true") || tableRowFromPreferences[2].equals("false")) {
                    newPreferencePart[2] = tableRowFromPreferences[2];
                }
            }
            return newPreferencePart;
        }
        } catch (Exception e) {
            LOG.error("Error in quickstart preferences format.");
        }
        return newPreferencePart;
    }
    
}
