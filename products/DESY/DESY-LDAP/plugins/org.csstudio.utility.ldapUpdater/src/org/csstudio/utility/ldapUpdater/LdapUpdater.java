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
import java.util.Set;

import org.apache.log4j.Logger;
import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.utility.ldap.reader.LdapService;
import org.csstudio.utility.ldap.reader.LdapServiceImpl;
import org.csstudio.utility.ldapUpdater.model.HistoryFileContentModel;
import org.csstudio.utility.ldapUpdater.model.IOC;
import org.csstudio.utility.ldapUpdater.model.LDAPContentModel;

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


	volatile boolean _busy = false;

	/**
	 * Don't instantiate with constructor.
	 */
	private LdapUpdater()
	{
		// empty
	}
	
	/**
	 * Factory method for creating a singleton instance.
	 * @return the singleton instance of this class
	 */
	public static LdapUpdater getInstance() {
		synchronized (LdapUpdater.class) {
			if ( INSTANCE == null) {
				INSTANCE = new LdapUpdater();
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
		strBuilder.append("\n-------------------------------------------------------------------\n" )
		          .append("start at ").append(now).append("  ( ")
		          .append(startTime).append(" )");
		LOGGER.info(strBuilder.toString() );

		LDAPContentModel ldapContentModel = new LDAPContentModel();
		LdapService service = new LdapServiceImpl();

		HistoryFileAccess histFileReader = new HistoryFileAccess();
		HistoryFileContentModel historyFileModel = histFileReader.readFile(); /* liest das history file */

		try {
		
			ReadLdapObserver ldapDataObserver = new ReadLdapObserver(ldapContentModel);
			ldapDataObserver.setResult(service.readLdapEntries("ou=EpicsControls", "econ=*", ldapDataObserver));
			while(!ldapDataObserver.isReady()){ Thread.sleep(100);} // observer finished update of the model
			LOGGER.info("LDAP Read Done");

			validateHistoryFileEntriesVsLDAPEntries(ldapContentModel, historyFileModel);
			
			List<IOC> iocList= IOCFilesDirTree.findIOCFiles(1); /* neu, statt der von epxLDAPgen.sh erzeugten Liste */

			UpdateComparator updComp = new UpdateComparator(); 
			updComp.updateLDAPFromIOCList(service, ldapDataObserver, ldapContentModel, iocList, historyFileModel);

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
		
		
		boolean inconsistency = false;
		
		Set<String> iocsFromLDAP = ldapContentModel.getIOCNames();
		Set<String> iocsFromHistFile = historyFileModel.getIOCNames();
		
		iocsFromLDAP.removeAll(iocsFromHistFile);
		for (String ioc : iocsFromLDAP) {
			LOGGER.error("IOC " + ioc + " from LDAP is not present in history file!");
			inconsistency = true;
		}
		
		iocsFromLDAP = ldapContentModel.getIOCNames();
		iocsFromHistFile.removeAll(iocsFromLDAP);
		for (String ioc : iocsFromHistFile) {
			LOGGER.error("IOC " + ioc + " found in history file is not present in LDAP!");
			inconsistency = true;
		}
		
		if (inconsistency) {
			//throw new IllegalStateException("Inconsistency of LDAP and history file " + HistoryFileAccess.HISTORY_DAT_FILE);
		}
	}

	public static String convertMillisToDateTimeString(long millis, String datetimeFormat) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(millis);
		DateFormat formatter = new SimpleDateFormat(datetimeFormat);
		String now = formatter.format(calendar.getTime());
		return now;
	}
}


