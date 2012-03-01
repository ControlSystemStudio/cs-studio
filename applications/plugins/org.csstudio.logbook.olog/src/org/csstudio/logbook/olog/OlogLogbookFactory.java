/**
 * 
 */
package org.csstudio.logbook.olog;

import java.util.ArrayList;
import java.util.Collection;

import org.csstudio.logbook.ILogbook;
import org.csstudio.logbook.ILogbookFactory;

import edu.msu.nscl.olog.api.Logbook;
import edu.msu.nscl.olog.api.Olog;
import edu.msu.nscl.olog.api.OlogClient;

/**
 * @author Delphy Nypaver Armstrong
 * @author Kay Kasemir
 * @author Eric Berryman
 * @author shroffk
 * 
 */
public class OlogLogbookFactory implements ILogbookFactory {
	final private OlogClient client = Olog.getClient();

	/**
	 * @throws Exception
	 * 
	 */
	public OlogLogbookFactory() throws Exception {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.csstudio.logbook.ILogbookFactory#getLogbooks()
	 */
	@Override
	public String[] getLogbooks() throws Exception {
		Collection<String> logbookNames = new ArrayList<String>();
		for (Logbook logbook : client.listLogbooks()) {
			logbookNames.add(logbook.getName());
		}
		return logbookNames.toArray(new String[logbookNames.size()]);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.csstudio.logbook.ILogbookFactory#getDefaultLogbook()
	 */
	@Override
	public String getDefaultLogbook() {
		return "Operations";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.csstudio.logbook.ILogbookFactory#connect(java.lang.String,
	 * java.lang.String, java.lang.String)
	 */
	@Override
	public ILogbook connect(String logbook, String user, String password)
			throws Exception {

		return new OlogLogbook(logbook, user, password);
	}

}
