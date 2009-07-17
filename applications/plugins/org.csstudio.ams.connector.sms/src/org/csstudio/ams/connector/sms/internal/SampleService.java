
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
import org.eclipse.jface.preference.IPreferenceStore;

/**
 * This example service demonstrates that the preference IDs that are used on
 * preference pages should be defined by the services that use them.<br>
 * </p>
 * Beside the definition of the preference IDs, this services provides the
 * method <code>showPreferences</code> that reads out the preference values
 * from the plugin's preference store and displays them. This demonstrates how
 * the preferences are accessed.
 * 
 * @author Alexander Will
 * 
 */
public class SampleService
{
    public static final String P_PREFERENCE_STRING = "org.csstudio.ams.connector.sms.preferences.modem";

    public static final String P_MODEM_COUNT = "org.csstudio.ams.connector.sms.preferences.modemCount";

	public static final String P_MODEM1_COMPORT = "org.csstudio.ams.connector.sms.preferences.modem1ComPort";
	public static final String P_MODEM1_COMBAUDRATE = "org.csstudio.ams.connector.sms.preferences.modem1ComBaudrate";
	public static final String P_MODEM1_MANUFACTURE = "org.csstudio.ams.connector.sms.preferences.modem1Manufacture";
	public static final String P_MODEM1_MODEL = "org.csstudio.ams.connector.sms.preferences.modem1Model";
	public static final String P_MODEM1_SIMPIM = "org.csstudio.ams.connector.sms.preferences.modem1SimPin";
    public static final String P_MODEM1_NUMBER = "org.csstudio.ams.connector.sms.preferences.modem1Number";

    public static final String P_MODEM2_COMPORT = "org.csstudio.ams.connector.sms.preferences.modem2ComPort";
    public static final String P_MODEM2_COMBAUDRATE = "org.csstudio.ams.connector.sms.preferences.modem2ComBaudrate";
    public static final String P_MODEM2_MANUFACTURE = "org.csstudio.ams.connector.sms.preferences.modem2Manufacture";
    public static final String P_MODEM2_MODEL = "org.csstudio.ams.connector.sms.preferences.modem2Model";
    public static final String P_MODEM2_SIMPIM = "org.csstudio.ams.connector.sms.preferences.modem2SimPin";
    public static final String P_MODEM2_NUMBER = "org.csstudio.ams.connector.sms.preferences.modem2Number";

    public static final String P_MODEM3_COMPORT = "org.csstudio.ams.connector.sms.preferences.modem3ComPort";
    public static final String P_MODEM3_COMBAUDRATE = "org.csstudio.ams.connector.sms.preferences.modem3ComBaudrate";
    public static final String P_MODEM3_MANUFACTURE = "org.csstudio.ams.connector.sms.preferences.modem3Manufacture";
    public static final String P_MODEM3_MODEL = "org.csstudio.ams.connector.sms.preferences.modem3Model";
    public static final String P_MODEM3_SIMPIM = "org.csstudio.ams.connector.sms.preferences.modem3SimPin";
    public static final String P_MODEM3_NUMBER = "org.csstudio.ams.connector.sms.preferences.modem3Number";

    public static final String P_MODEM_READ_WAITING_PERIOD = "org.csstudio.ams.connector.sms.preferences.modemReadWaitingPeriod";

    /**
	 * The only one instance of this service.
	 */
	private static SampleService _instance;

	/**
	 * Private constructor due to the singleton pattern.
	 */
	private SampleService() {
		// do nothing particular.
	}

	/**
	 * Return the only one instance of this service.
	 * 
	 * @return The only one instance of this service.
	 */
	public static SampleService getInstance() {
		if (_instance == null) {
			_instance = new SampleService();
		}

		return _instance;
	}

	/**
	 * Read out the preference from the plugin's preference store and display
	 * them on the console.
	 * 
	 */
	public final void showPreferences()
	{
		IPreferenceStore store = SmsConnectorPlugin.getDefault().getPreferenceStore();

		System.out.println(P_MODEM1_COMPORT + ": " + store.getString(P_MODEM1_COMPORT));
		System.out.println(P_MODEM1_COMBAUDRATE + ": " + store.getString(P_MODEM1_COMBAUDRATE));
		System.out.println(P_MODEM1_MANUFACTURE + ": " + store.getString(P_MODEM1_MANUFACTURE));
		System.out.println(P_MODEM1_MODEL + ": " + store.getString(P_MODEM1_MODEL));
		System.out.println(P_MODEM1_SIMPIM + ": " + store.getString(P_MODEM1_SIMPIM));
		
	    System.out.println(P_MODEM2_COMPORT + ": " + store.getString(P_MODEM2_COMPORT));
	    System.out.println(P_MODEM2_COMBAUDRATE + ": " + store.getString(P_MODEM2_COMBAUDRATE));
        System.out.println(P_MODEM2_MANUFACTURE + ": " + store.getString(P_MODEM2_MANUFACTURE));
        System.out.println(P_MODEM2_MODEL + ": " + store.getString(P_MODEM2_MODEL));
        System.out.println(P_MODEM2_SIMPIM + ": " + store.getString(P_MODEM2_SIMPIM));
        
        System.out.println(P_MODEM3_COMPORT + ": " + store.getString(P_MODEM3_COMPORT));
        System.out.println(P_MODEM3_COMBAUDRATE + ": " + store.getString(P_MODEM3_COMBAUDRATE));
        System.out.println(P_MODEM3_MANUFACTURE + ": " + store.getString(P_MODEM3_MANUFACTURE));
        System.out.println(P_MODEM3_MODEL + ": " + store.getString(P_MODEM3_MODEL));
        System.out.println(P_MODEM3_SIMPIM + ": " + store.getString(P_MODEM3_SIMPIM));
	}
}
