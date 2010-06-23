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

import org.csstudio.alarm.table.JmsLogsPlugin;
import org.eclipse.jface.preference.IPreferenceStore;

public class SeverityMapping implements ISeverityMapping {

	private final HashMap<String, String> _severityKeyValueMapping = new HashMap<String, String>();

	private final HashMap<String, Integer> _severityKeyNumberMapping = new HashMap<String, Integer>();

	public SeverityMapping() {
		IPreferenceStore preferenceStore = JmsLogsPlugin.getDefault()
				.getPreferenceStore();

		_severityKeyValueMapping.put(preferenceStore.getString("key 0"), preferenceStore.getString("value 0"));
		_severityKeyValueMapping.put(preferenceStore.getString("key 1"), preferenceStore.getString("value 1"));
		_severityKeyValueMapping.put(preferenceStore.getString("key 2"), preferenceStore.getString("value 2"));
		_severityKeyValueMapping.put(preferenceStore.getString("key 3"), preferenceStore.getString("value 3"));
		_severityKeyValueMapping.put(preferenceStore.getString("key 4"), preferenceStore.getString("value 4"));
		_severityKeyValueMapping.put(preferenceStore.getString("key 5"), preferenceStore.getString("value 5"));
		_severityKeyValueMapping.put(preferenceStore.getString("key 6"), preferenceStore.getString("value 6"));
		_severityKeyValueMapping.put(preferenceStore.getString("key 7"), preferenceStore.getString("value 7"));
		_severityKeyValueMapping.put(preferenceStore.getString("key 8"), preferenceStore.getString("value 8"));
		_severityKeyValueMapping.put(preferenceStore.getString("key 9"), preferenceStore.getString("value 9"));

		_severityKeyNumberMapping.put(preferenceStore.getString("key 0"), 0);
		_severityKeyNumberMapping.put(preferenceStore.getString("key 1"), 1);
		_severityKeyNumberMapping.put(preferenceStore.getString("key 2"), 2);
		_severityKeyNumberMapping.put(preferenceStore.getString("key 3"), 3);
		_severityKeyNumberMapping.put(preferenceStore.getString("key 4"), 4);
		_severityKeyNumberMapping.put(preferenceStore.getString("key 5"), 5);
		_severityKeyNumberMapping.put(preferenceStore.getString("key 6"), 6);
		_severityKeyNumberMapping.put(preferenceStore.getString("key 7"), 7);
		_severityKeyNumberMapping.put(preferenceStore.getString("key 8"), 8);
		_severityKeyNumberMapping.put(preferenceStore.getString("key 9"), 9);
	}


    /**
     * returns the severity value for the severity key of this message.
     *
     * @return
     */
    public String findSeverityValue(final String severityKey) {
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
    public int getSeverityNumber(final String severityKey) {
    	Integer severityNumber = _severityKeyNumberMapping.get(severityKey);
    	//if there is no mapping return 10, that means the lowest severity
    	if (severityNumber == null) {
    		return 10;
    	} else {
    		return severityNumber;
    	}
    }
}
