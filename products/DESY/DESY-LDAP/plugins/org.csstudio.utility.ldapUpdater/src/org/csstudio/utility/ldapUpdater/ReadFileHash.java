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
	
	private DataModel _model;
	
	private IPreferencesService _prefs;
	
	public ReadFileHash (DataModel model) {
		this._model=model;
	    _prefs = Platform.getPreferencesService();
	}
	
	public void readFile() {
        String histfile="history.dat";
        HashMap<String, Long> _histMap = new HashMap<String, Long>();
        BufferedReader fr;
        
        try {
			fr = new BufferedReader(new FileReader(_prefs.getString(Activator.getDefault().getPluginId(),
    	    		LdapUpdaterPreferenceConstants.LDAP_HIST_PATH, "", null) + histfile ));

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
                    	String emsg = "Fehler in Datei " + histfile + ", Zeile ist: " + "\"" + line + "\"" ;
                    	CentralLogger.getInstance().error(this, emsg);
                    	throw new RuntimeException(emsg);
                    }
                }
                _histMap.put(m.group(1), Long.parseLong(m.group(3)));
            }
        }
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();	
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        CentralLogger.getInstance().info(this, "IOC names in history-file : "+_histMap.size());
       _model.setHistoryMap(_histMap);
        
//        for ( String line : _model.getHistoryMap().get(ioc.getName())){
//        		System.out.println(line);       
//        }
	}    
}
