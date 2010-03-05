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

import java.io.File;
import java.util.List;

import org.csstudio.platform.logging.CentralLogger;


import org.csstudio.utility.ldap.reader.ErgebnisListe;
import org.csstudio.utility.ldap.reader.ErgebnisListeObserver;
import org.csstudio.utility.ldap.reader.IocFinder;
import org.csstudio.utility.ldap.reader.LDAPReader;
import org.csstudio.utility.ldapUpdater.model.DataModel;

import org.csstudio.utility.ldapUpdater.myDateTimeString;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;

import quicktime.qd.SetGWorld;

/**
 * Updates the IOC information in the LDAP directory.
 * 
 * @author valett
 * @author $Author$
 * @version $Revision$
 * @since 17.04.2008
 */

public class LdapUpdater {

	public boolean _busy = false;
	
	private static final CentralLogger LOGGER = CentralLogger.getInstance();

	private static LdapUpdater INSTANCE; 

	private DataModel _model;

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
		myDateTimeString dateTimeString = new myDateTimeString();
		String now= dateTimeString.getDateTimeString( "yyyy-MM-dd", "HH:mm:ss", startTime);
		StringBuilder strBuilder = new StringBuilder();
		strBuilder.append("-------------------------------------------------------------------" )
		          .append("start at ").append(now).append("  ( ")
		          .append(startTime).append(" )");
		LOGGER.info(this, strBuilder.toString() );

		_model = new DataModel();

		try {

			ReadLdapDatabase ldapDataBase = new ReadLdapDatabase(_model);
			ldapDataBase.readLdapEcons();     /* liest eine liste, die alle ldap econ namen enthält */
			
			while(!_model.isReady()){ Thread.sleep(100);}
			LOGGER.info(this, "ldap Read Done");

			ReadFileHash hashReader = new ReadFileHash();
			_model.setHistoryMap(hashReader.readFile()); /* liest das history file */

			// TODO (kvalett) : once the procedure is clarified - ask mclausen 
			//validateHistoryFileEntriesVsLDAPEntries();
			
			List<IOC> iocList= IOCFilesDirTree.findIOCFiles(1); /* neu, statt der von epxLDAPgen.sh erzeugten Liste */
			
			
			IocGroupAndRecordFactory iocFactory = new IocGroupAndRecordFactory();
			iocList = iocFactory.createGroupAffiliation(iocList, _model.getEconToEfanMap());

			iocList = iocFactory.readRecordsForIOCs(iocList);	/* liest u.a. alle  record namen aus /applic/dirServer-files */
			
			_model.setIocList(iocList);

			// TODO (bknerr) : remove model from constructor
			UpdateComparator updComp=new UpdateComparator(_model); 
			updComp.compareLDAPWithIOC();

			endTime = System.currentTimeMillis();
			deltaTime = endTime - startTime;
			now = dateTimeString.getDateTimeString( "yyyy-MM-dd", "HH:mm:ss", endTime);
			LOGGER.info(this, "end" + " at " + now + "  ( " + endTime + " )" );
			LOGGER.info(this, "time used : " + deltaTime/1000.  + " s" );
			LOGGER.info(this, "Ende." );
			LOGGER.info(this, "-------------------------------------------------------------------" );
			
			_busy=false;

		} catch (Exception e) {
			// TODO (kvalett): handle exception
			e.printStackTrace();
		}

	}
	


}


