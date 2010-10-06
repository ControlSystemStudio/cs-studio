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

import java.util.HashMap;

import javax.annotation.Nonnull;

import org.csstudio.alarm.table.JmsLogsPlugin;
import org.eclipse.jface.preference.IPreferenceStore;

/**
 * SeverityMapping defines two mappings: From key to value for the severity and from key to severity number. 
 * 
 * @author jpenning
 * @author $Author: jpenning $
 * @since 05.10.2010
 */
public class SeverityMapping implements ISeverityMapping {

	private final HashMap<String, String> _severityKeyValueMapping = new HashMap<String, String>();
	private final HashMap<String, Integer> _severityKeyNumberMapping = new HashMap<String, Integer>();

	public SeverityMapping() {
		IPreferenceStore preferenceStore = JmsLogsPlugin.getDefault()
				.getPreferenceStore();
		enterValueAndNumberForKey(preferenceStore, "key 0", "value 0", 0);
		enterValueAndNumberForKey(preferenceStore, "key 1", "value 1", 1);
		enterValueAndNumberForKey(preferenceStore, "key 2", "value 2", 2);
		enterValueAndNumberForKey(preferenceStore, "key 3", "value 3", 3);
		enterValueAndNumberForKey(preferenceStore, "key 4", "value 4", 4);
		enterValueAndNumberForKey(preferenceStore, "key 5", "value 5", 5);
		enterValueAndNumberForKey(preferenceStore, "key 6", "value 6", 6);
		enterValueAndNumberForKey(preferenceStore, "key 7", "value 7", 7);
		enterValueAndNumberForKey(preferenceStore, "key 8", "value 8", 8);
		enterValueAndNumberForKey(preferenceStore, "key 9", "value 9", 9);
	}
	
	private void enterValueAndNumberForKey(
			@Nonnull IPreferenceStore preferenceStore, @Nonnull String key,
			@Nonnull String value, int number) {
		_severityKeyValueMapping.put(preferenceStore.getString(key), preferenceStore.getString(value));
		_severityKeyNumberMapping.put(preferenceStore.getString(key), number);
	}


    /**
     * returns the severity value for the severity key of this message.
     *
     * @return
     */
	@Nonnull
	@Override
    public String findSeverityValue(@Nonnull final String severityKey) {
    	String severityValue = _severityKeyValueMapping.get(severityKey);
        if (severityValue == null) {
            return "invalid severity";
        } else {
        	return severityValue;
        }
    }

    /**
     * Returns the number of the severity. The number represents the level of
     * the severity.
     *
     * @return
     */
	@Override
    public int getSeverityNumber(@Nonnull final String severityKey) {
    	Integer severityNumber = _severityKeyNumberMapping.get(severityKey);
    	//if there is no mapping return 10, that means the lowest severity
    	if (severityNumber == null) {
    		return 10;
    	} else {
    		return severityNumber;
    	}
    }
}
