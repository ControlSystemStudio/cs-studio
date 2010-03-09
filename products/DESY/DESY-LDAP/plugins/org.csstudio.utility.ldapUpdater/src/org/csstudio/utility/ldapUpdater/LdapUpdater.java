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
/*
 * $Id$
 */

/*
 * todo : functionallity to delete one IOC's ldap data, but NOT the header info
 * should be startable via xmpp, prompt asking for IOCname.
 */

package org.csstudio.utility.ldapUpdater;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.utility.ldapUpdater.model.HistoryFileContentModel;
import org.csstudio.utility.ldapUpdater.model.LDAPContentModel;
import org.csstudio.utility.ldapUpdater.model.IOC;

/**
 * Updates the IOC information in the LDAP directory.
 * 
 * @author valett
 * @author $Author$
 * @version $Revision$
 * @since 17.04.2008
 */

public class LdapUpdater {

	public static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

	private static LdapUpdater INSTANCE; 
	
	private final Logger LOGGER = CentralLogger.getInstance().getLogger(this);


	boolean _busy = false;

	/**
	 * Don't instantiate with constructor.
	 */
	private LdapUpdater()
	{
	}
	
	/**
	 * Factory method for creating a singleton instance.
	 * @return the singleton instance of this class
	 */
	public static LdapUpdater getInstance() {
		if (INSTANCE == null) {
			synchronized (LdapUpdater.class) {
				if ( INSTANCE == null) {
					INSTANCE = new LdapUpdater();
				}
			}
		}
		return INSTANCE;
	}

	
	public final void start() throws Exception {

		if ( _busy ) {
			return;
		}

		_busy = true;

		long endTime = 0L;
		long deltaTime = 0L; 
		long startTime = System.currentTimeMillis();
		
		String now = convertMillisToDateTimeString(startTime, DATETIME_FORMAT);
		
		StringBuilder strBuilder = new StringBuilder();
		strBuilder.append("-------------------------------------------------------------------" )
		          .append("start at ").append(now).append("  ( ")
		          .append(startTime).append(" )");
		LOGGER.info(strBuilder.toString() );

		LDAPContentModel ldapContentModel = new LDAPContentModel();

		try {

			ReadLdapDatabase ldapDataBase = new ReadLdapDatabase(ldapContentModel);
			ldapDataBase.readLdapEntries();     /* liest eine liste, die alle ldap econ namen enthält */
			
			while(!ldapContentModel.isReady()){ Thread.sleep(100);}
			LOGGER.info("LDAP Read Done");

			HistoryFileAccess histFileReader = new HistoryFileAccess();
			HistoryFileContentModel historyFileModel = histFileReader.readFile(); /* liest das history file */

			validateHistoryFileEntriesVsLDAPEntries(ldapContentModel, historyFileModel);
			
			List<IOC> iocList= IOCFilesDirTree.findIOCFiles(1); /* neu, statt der von epxLDAPgen.sh erzeugten Liste */

			UpdateComparator updComp = new UpdateComparator(); 
			updComp.updateLDAPFromIOCList(ldapContentModel, iocList, historyFileModel);

			endTime = System.currentTimeMillis();
			deltaTime = endTime - startTime;
			
			now = convertMillisToDateTimeString(endTime, DATETIME_FORMAT);
			
			StringBuilder builder = new StringBuilder();
			builder.append("end at").append(now).append("  (").append(endTime).append(")\n")
			       .append("time used : ").append(deltaTime/1000.).append(" s\n")
			       .append("Ende.\n")
			       .append("-------------------------------------------------------------------\n");
			LOGGER.info( builder.toString() );
			
			_busy = false;

		} catch (Exception e) {
			// TODO (kvalett): handle exception
			e.printStackTrace();
		}

	}


	private void validateHistoryFileEntriesVsLDAPEntries(
			final LDAPContentModel ldapContentModel,
			final HistoryFileContentModel historyFileModel) {
		
		ldapContentModel.getIOCNames()
		
		for (Entry<String, Long> histEntry : historyFileModel.getEntrySet()) {
			String iocName = histEntry.getKey();
			if (ldapContentModel.getIOC(iocName) == null) {
				throw new IllegalStateException("IOC " + iocName);
			}
		}
		LOGGER.info("History file for IOCs matches IOC entries in LDAP.");
	}

	public static String convertMillisToDateTimeString(long millis, String datetimeFormat) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(millis);
		DateFormat formatter = new SimpleDateFormat(datetimeFormat);
		String now = formatter.format(calendar.getTime());
		return now;
	}
}


