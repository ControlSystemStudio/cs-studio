/* 
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchroton, 
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
package org.csstudio.ams.connector.sms.internal;

import org.csstudio.ams.connector.sms.SmsConnectorPlugin;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;

/**
 * Preference initializer implemenation. This class initializes the preferences
 * with default values. New preference settings should be initialized in this
 * class, too.
 * 
 * @author Alexander Will
 */
public final class PreferencesInitializer extends AbstractPreferenceInitializer {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void initializeDefaultPreferences() {
		IEclipsePreferences node = new DefaultScope()
				.getNode(SmsConnectorPlugin.PLUGIN_ID);

		node.put(SampleService.P_MODEM_COUNT, "1");

		node.put(SampleService.P_MODEM1_COMPORT, "COM1");
		node.put(SampleService.P_MODEM1_COMBAUDRATE, "9600");
		node.put(SampleService.P_MODEM1_MANUFACTURE, "Siemens");
		node.put(SampleService.P_MODEM1_MODEL, "MC35i");
		node.put(SampleService.P_MODEM1_SIMPIM, "0010");
		
	    node.put(SampleService.P_MODEM2_COMPORT, "");
	    node.put(SampleService.P_MODEM2_COMBAUDRATE, "");
	    node.put(SampleService.P_MODEM2_MANUFACTURE, "");
	    node.put(SampleService.P_MODEM2_MODEL, "");
	    node.put(SampleService.P_MODEM2_SIMPIM, "");
	    
	    node.put(SampleService.P_MODEM3_COMPORT, "");
	    node.put(SampleService.P_MODEM3_COMBAUDRATE, "");
	    node.put(SampleService.P_MODEM3_MANUFACTURE, "");
	    node.put(SampleService.P_MODEM3_MODEL, "");
	    node.put(SampleService.P_MODEM3_SIMPIM, "");
	    
	    node.put(SampleService.P_MODEM_READ_WAITING_PERIOD, "5000");
	}
}
