package org.csstudio.platform;

import java.util.ArrayList;
import java.util.Collection;

import org.csstudio.platform.logging.CentralLogger;
import org.eclipse.core.runtime.Preferences;

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

	/**
	 * Returns the onsite subnets configured in the preferences.
	 * @return the array of onsite subnets.
	 */
	public static Collection<Subnet> getOnsiteSubnets() {
		Preferences prefs = CSSPlatformPlugin.getDefault().getPluginPreferences();
		String list = prefs.getString(PREFERENCE_KEY);
		String[] entries = list.split(",");
		Collection<Subnet> subnets = new ArrayList<Subnet>();
		for (int i = 0; i < entries.length; i++) {
			if (entries[i].length() > 0) {
				try {
					subnets.add(Subnet.parseSubnet(entries[i]));
				} catch (IllegalArgumentException e) {
					CentralLogger.getInstance().warn(
							OnsiteSubnetPreferences.class,
							"Invalid entry in onsite subnet preferences: "
									+ entries[i]);
				}
			}
		}
		return subnets;
	}
}
