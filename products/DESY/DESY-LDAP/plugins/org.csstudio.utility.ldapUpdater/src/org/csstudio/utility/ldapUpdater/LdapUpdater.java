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

// import java.awt.FileDialog;
// import java.awt.peer.FileDialogPeer;
// import java.text.SimpleDateFormat;
import java.io.File;
import java.util.ArrayList;

import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.utility.ldap.reader.ErgebnisListe;
import org.csstudio.utility.ldapUpdater.model.DataModel;
// import org.csstudio.utility.ldapUpdater.preferences.LdapUpdaterPreferenceConstants;
import org.csstudio.utility.namespace.utility.ControlSystemItem;
//import org.eclipse.core.runtime.Platform;
// import org.eclipse.core.runtime.preferences.IPreferencesService;
// import org.eclipse.equinox.app.IApplication;
// import org.eclipse.equinox.app.IApplicationContext;
import org.csstudio.utility.ldapUpdater.myDateTimeString;

import com.sun.jndi.toolkit.dir.DirSearch;

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
        myDateTimeString dateTimeString = new myDateTimeString();
        String now= dateTimeString.getDateTimeString( "yyyy-MM-dd", "HH:mm:ss", startTime);

        CentralLogger.getInstance().info(this, "-------------------------------------------------------------------" );
        CentralLogger.getInstance().info(this, "start" + " at " + now + "  ( " + startTime +" )" );

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
        now = dateTimeString.getDateTimeString( "yyyy-MM-dd", "HH:mm:ss", endTime);
        CentralLogger.getInstance().info(this, "end" + " at " + now + "  ( " + endTime + " )" );
        CentralLogger.getInstance().info(this, "time used : " + deltaTime/1000.  + " s" );
        CentralLogger.getInstance().info(this, "Ende." );
        CentralLogger.getInstance().info(this, "-------------------------------------------------------------------" );
        busy=false;
    }

}


