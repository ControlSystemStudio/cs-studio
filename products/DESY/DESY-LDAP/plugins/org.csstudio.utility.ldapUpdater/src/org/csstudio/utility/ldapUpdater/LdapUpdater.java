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
package org.csstudio.utility.ldapUpdater;

// import Entry;


/*

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Formatter;
*/

import java.util.ArrayList;

import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.utility.ldap.reader.ErgebnisListe;
import org.csstudio.utility.ldapUpdater.model.DataModel;
import org.csstudio.utility.ldapUpdater.preferences.LdapUpdaterPreferenceConstants;
import org.csstudio.utility.namespace.utility.ControlSystemItem;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;

// import Test;

/**
 * Updates the IOC information in the LDAP directory.
 * 
 * @author valett
 * @author $Author$
 * @version $Revision$
 * @since 17.04.2008
 */
public class LdapUpdater {

	private static LdapUpdater _instance; 
	
	private DataModel _model;
    private ErgebnisListe _ergebnis;
    private boolean _ready = false;
    private boolean _ldapReadDone = false;
	private ArrayList<ControlSystemItem> _al;
	public boolean busy=false;
	
	public static LdapUpdater getInstance() {
		
		if (_instance == null) {
			synchronized (LdapUpdater.class) {
						
				if ( _instance == null) {
					_instance = new LdapUpdater();
				}
			}
		}
		return _instance;
	}
	
	/** Gets the System.currentTimeMillis 
	 * splits to hh, mm,ss
	 * includes leading zeroes if required
	 * generates string with format hh:mm:ss
	 * @return this string
	* used also in ldapUpdater.java, copied !!!
	 */
		public final String millis2TimeString ( ) {
			long one_minute=60; 			// s
			long one_hour=60*one_minute; 	// s
			long one_day=one_hour*24; 		// s 

			long nowMillis = System.currentTimeMillis();
			long now=nowMillis/1000L; // s

			long ss=now % one_day; 			// seconds since midnight, Greenwich Winter Time 
			long hh=ss / one_hour;			// h ; 
			ss=ss % one_hour;				// s ; 
			long mm=ss / one_minute;		// m
			ss=ss % one_minute;				// 

		    String hhs=String.valueOf(hh); if ( hhs.length()==1 ) { hhs="0"+hhs; }
		    String mms=String.valueOf(mm); if ( mms.length()==1 ) { mms="0"+mms; }
		    String sss=String.valueOf(ss); if ( sss.length()==1 ) { sss="0"+sss; }
		    String hmsString=hhs+":"+mms+":"+sss ;		
			return hmsString;
		}

	
	public final void start() throws Exception {
		if ( busy ) {
			return;
		}
		busy=true;
        CentralLogger.getInstance().info(this, "start" );
   	
    	_model=new DataModel();

    	IocListReader iocReader=new IocListReader(_model);
    	
    	ReadFileHash hashReader=new ReadFileHash(_model);
    	
    	ReadLdapDatabase ldapReader=new ReadLdapDatabase(_model);
    	   	
    	long startTime = System.currentTimeMillis();
        long endTime=0L;
        long deltaTime;
        
        String now=millis2TimeString ();
        CentralLogger.getInstance().info(this, "start" + " at " + now + "(UTC)" + "  ( " + startTime +" )" );
//		System.out.println();
//		CentralLogger.getInstance().debug(this, "hallo debug");
//		CentralLogger.getInstance().info(this, "hallo info");
//		CentralLogger.getInstance().error(this, "hallo error");
		ldapReader.readLdap();        
        hashReader.readFile();
        iocReader.readIocList();
        iocReader.readIocRecordNames();
        
        for (IOC ioc : _model.getIocList()) {
    		CentralLogger.getInstance().debug(this, "" + ioc.getName());
    	}
        CentralLogger.getInstance().info(this, "Read from IOC list : " + _model.getIocList().size());
        		
        while(!_model.isReady()){ Thread.sleep(100);}
        CentralLogger.getInstance().info(this, "ldap Read Done");

        UpdateComparator updComp=new UpdateComparator(_model); 

        updComp.compareLDAPWithIOC();
         
        endTime = System.currentTimeMillis();
        deltaTime = endTime - startTime;
        now=millis2TimeString ();
        CentralLogger.getInstance().info(this, "end" + " at " + now + "(UTC)" + "  ( " + endTime +" )" );
        CentralLogger.getInstance().info(this, "time used : " + deltaTime/1000.  + " s" );
        CentralLogger.getInstance().info(this, "Ende." );
        busy=false;
    }

}


