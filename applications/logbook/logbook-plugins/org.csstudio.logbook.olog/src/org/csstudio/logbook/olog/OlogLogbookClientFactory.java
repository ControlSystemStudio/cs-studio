package org.csstudio.logbook.olog;

import static edu.msu.nscl.olog.api.OlogClientImpl.OlogClientBuilder.serviceURL;

import org.csstudio.logbook.LogbookClient;
import org.csstudio.logbook.LogbookClientFactory;
import org.csstudio.utility.olog.PreferenceConstants;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;

import edu.msu.nscl.olog.api.Olog;

public class OlogLogbookClientFactory implements LogbookClientFactory {

    @Override
    public LogbookClient getClient() throws Exception {
    return new OlogLogbookClient(Olog.getClient());
    }

    @Override
    public LogbookClient getClient(String username, String password)
        throws Exception {
    final IPreferencesService prefs = Platform.getPreferencesService();
    String url = prefs.getString(org.csstudio.utility.olog.Activator.PLUGIN_ID,
            PreferenceConstants.Olog_URL,
            "https://localhost:8181/Olog/resources", null);
    return new OlogLogbookClient(serviceURL(url).withHTTPAuthentication(true).username(username)
        .password(password).create());
    }

}
