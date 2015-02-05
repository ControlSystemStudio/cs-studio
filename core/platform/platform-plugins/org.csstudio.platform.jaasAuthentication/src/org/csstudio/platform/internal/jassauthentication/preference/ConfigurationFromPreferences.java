package org.csstudio.platform.internal.jassauthentication.preference;

import java.util.ArrayList;
import java.util.List;

import javax.security.auth.login.AppConfigurationEntry;
import javax.security.auth.login.Configuration;

public class ConfigurationFromPreferences extends Configuration {	
	
	@Override
	public AppConfigurationEntry[] getAppConfigurationEntry(String name) {
		List<AppConfigurationEntry> configEntryList = new ArrayList<AppConfigurationEntry>();
		
		JAASConfigurationEntry[] jaasConfigEntries = 
			PreferencesHelper.getJAASConfigurationEntries(false);
		
		for(JAASConfigurationEntry configEntry : jaasConfigEntries) {
			AppConfigurationEntry ace = new AppConfigurationEntry(
					configEntry.getLoginModuleName(),
					configEntry.getLoginModuleControlFlag(),
					configEntry.getModuleOptionsMap());
			configEntryList.add(ace);
		}
		
		return configEntryList.toArray(new AppConfigurationEntry[configEntryList.size()]);
	}

	@Override
	public void refresh()
	{
	    // NOP
	}
}
