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

package org.csstudio.utility.ldapUpdater;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.utility.ldapUpdater.model.DataModel;
import org.csstudio.utility.ldapUpdater.preferences.LdapUpdaterPreferenceConstants;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;

public class ReadFileHash {
	
	private static final String HISTORY_DAT_FILE = "history.dat";

	private static final CentralLogger LOGGER = CentralLogger.getInstance();

	private IPreferencesService _prefs;
	
	public ReadFileHash () {
	    _prefs = Platform.getPreferencesService();
	}
	
	public HashMap<String, Long> readFile() {
        HashMap<String, Long> histMap = new HashMap<String, Long>();
        BufferedReader fr;
        
        try {
			String string = _prefs.getString(Activator.getDefault().getPluginId(),
    	    		LdapUpdaterPreferenceConstants.LDAP_HIST_PATH, "", null);
			fr = new BufferedReader(new FileReader(string + HISTORY_DAT_FILE ));

//			String pathFile=LdapUpdaterPreferenceConstants.LDAP_HIST_PATH + HISTORY_DAT_FILE ;
			String line;
        while ((line = fr.readLine()) != null) {
            if (line.length() > 0) {
//              CentralLogger.getInstance().info(this,line);
            	Pattern comment = Pattern.compile("\\s*#.*");
                Matcher commentMatcher = comment.matcher(line);
                if (commentMatcher.matches()) {
                	continue;
                }
                Pattern p = Pattern.compile("(\\S+)\\s+(\\S+)\\s+(\\S+)"); // old format : 3 parameters on histfile line
                Matcher m = p.matcher(line);
                if(!m.matches()) {
                    p = Pattern.compile("(\\S+)\\s+(\\S+)\\s+(\\S+)\\s+(\\S+)\\s+(\\S+)"); // new format : 5 parameters on histfile line
                    m = p.matcher(line);                	
                    if(!m.matches()) {
                    	String emsg = "Fehler in Datei " + HISTORY_DAT_FILE + ", Zeile ist: " + "\"" + line + "\"" ;
                    	LOGGER.error(this, emsg);
                    	throw new RuntimeException(emsg);
                    }
                }
                // FIXME (kvalett) : depends on the order in the histfile - 
                // Better to convert the time stamp to DateTime and compare for most recent time
                histMap.put(m.group(1), Long.parseLong(m.group(3)));
            }
        }
		} catch (FileNotFoundException e) {
			LOGGER.error (this, "Error : File not Found(r) : " + LdapUpdaterPreferenceConstants.LDAP_HIST_PATH + HISTORY_DAT_FILE );
			// TODO (bknerr) : wtf ???
			//_model.setSerror(_model.getSerror()+1);			
		} catch (IOException e) {
			// TODO (bknerr) : wtf ???
			//_model.setSerror(_model.getSerror()+2);			
			LOGGER.error (this, "I/O-Exception while handling " + LdapUpdaterPreferenceConstants.LDAP_HIST_PATH + HISTORY_DAT_FILE );
		}

        LOGGER.info(this, "IOC names in history-file : "+histMap.size());

        return histMap;
        
	}    
}
