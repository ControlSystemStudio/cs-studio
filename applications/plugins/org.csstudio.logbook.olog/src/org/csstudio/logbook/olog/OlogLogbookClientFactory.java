package org.csstudio.logbook.olog;

import org.csstudio.logbook.LogbookClient;
import org.csstudio.logbook.LogbookClientFactory;

import edu.msu.nscl.olog.api.Olog;

public class OlogLogbookClientFactory implements LogbookClientFactory {

	@Override
	public LogbookClient getClient() {
		try {
			return new OlogLogbookClient(Olog.getClient());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public LogbookClient getClient(String username, String password) {
		return null;
	}

}
