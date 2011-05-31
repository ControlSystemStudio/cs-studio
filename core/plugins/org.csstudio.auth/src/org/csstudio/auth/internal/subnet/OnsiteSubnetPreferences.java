/* 
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron, 
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
 package org.csstudio.auth.internal.subnet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.csstudio.auth.internal.AuthActivator;
import org.csstudio.auth.security.SecurityFacade;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.core.runtime.preferences.InstanceScope;

/**
 * Preferences for the onsite/offsite settings.
 * 
 * @author Joerg Rathlev
 */
public final class OnsiteSubnetPreferences {
	
	/**
	 * Preference key under which the list of onsite networks is stored.
	 */
	public static final String PREFERENCE_KEY = "onsite_subnets";

	private static final Logger log = Logger.getLogger(OnsiteSubnetPreferences.class.getName());
	
	/**
	 * Returns the onsite subnets configured in the preferences.
	 * @return the array of onsite subnets.
	 */
	public static Collection<Subnet> getOnsiteSubnets() {
		IPreferencesService prefs = Platform.getPreferencesService();
//TODO (jhatje): duplicated default value.
		String list = prefs.getString(AuthActivator.ID, PREFERENCE_KEY, "131.169.0.0/255.255.0.0,", null);
		String[] entries = list.split(",");
		Collection<Subnet> subnets = new ArrayList<Subnet>();
		for (int i = 0; i < entries.length; i++) {
			if (entries[i].length() > 0) {
				try {
					subnets.add(Subnet.parseSubnet(entries[i]));
				} catch (IllegalArgumentException e) {
					log.log(Level.WARNING, "Invalid entry in onsite subnet preferences: " + entries[i]);
				}
			}
		}
		return subnets;
	}
}
