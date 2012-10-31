package org.csstudio.logbook.olog;

import org.csstudio.logbook.LogbookClient;
import org.csstudio.logbook.LogbookClientFactory;

import edu.msu.nscl.olog.api.Olog;

public class OlogLogbookClientFactory implements LogbookClientFactory {

	@Override
	public LogbookClient getClient() throws Exception {
		return new OlogLogbookClient(Olog.getClient());
	}

	@Override
	public LogbookClient getClient(String username, String password)
			throws Exception {
		return new OlogLogbookClient(Olog.getClient());
	}

}
