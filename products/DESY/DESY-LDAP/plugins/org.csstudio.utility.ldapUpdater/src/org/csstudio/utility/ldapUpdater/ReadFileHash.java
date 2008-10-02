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
        HashMap<String, Long> _histMap = new HashMap<String, Long>();
        BufferedReader fr;
		try {
			fr = new BufferedReader(new FileReader(_prefs.getString(Activator.getDefault().getPluginId(),
    	    		LdapUpdaterPreferenceConstants.LDAP_HIST_PATH, "", null)+"history.dat"));

        String line;
        while ((line = fr.readLine()) != null) {
            if (line.length() > 0) {
//            	System.out.println(line);
            	
//				Too many lines of output, so next line is disabled: 
//                CentralLogger.getInstance().info(this,line);
            	Pattern comment = Pattern.compile("\\s*#.*");
                Matcher commentMatcher = comment.matcher(line);
                if (commentMatcher.matches()) {
                	continue;
                }
                Pattern p = Pattern.compile("(\\S+)\\s+(\\S+)\\s+(\\S+)");
                Matcher m = p.matcher(line);
                if(!m.matches()) {
//					System.out.println("Fehler in Datei, Zeile ist: "+line);
                    CentralLogger.getInstance().error(this, "Fehler in Datei, Zeile ist: "+line);
                    throw new RuntimeException("Fehler in Datei, Zeile ist: "+line);
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
