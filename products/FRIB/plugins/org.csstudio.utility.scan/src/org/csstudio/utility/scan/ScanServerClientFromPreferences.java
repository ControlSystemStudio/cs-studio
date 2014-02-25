package org.csstudio.utility.scan;


import java.util.List;
import java.util.logging.Logger;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import edu.msu.frib.scanserver.api.Data;
import edu.msu.frib.scanserver.api.Scan;
import edu.msu.frib.scanserver.api.ScanServerClient;
import edu.msu.frib.scanserver.api.ScanServerClientImpl.SSCBuilder;
import edu.msu.frib.scanserver.api.commands.CommandComposite;
import edu.msu.frib.scanserver.api.commands.CommandSet;

/**
 * ScanServerClient that takes the configuration from the CSS preferences.
 * 
 * 
 */
public class ScanServerClientFromPreferences implements ScanServerClient {

    private static final Logger log = Logger
	    .getLogger(ScanServerClientFromPreferences.class.getName());
    private volatile ScanServerClient client;

    public ScanServerClientFromPreferences() {
	reloadConfiguration();
    }

    public void reloadConfiguration() {
	try {
	    final IPreferencesService prefs = Platform.getPreferencesService();
	    String url = prefs.getString(Activator.PLUGIN_ID,
		    PreferenceConstants.Scan_URL,
		    "http://localhost:4810", null);
	    
	    log.info("Creating Channelfinder client : " + url);
	    client = SSCBuilder.serviceURL(url)
				.create();
	} catch (Exception e) {
	    log.severe(e.getMessage());
	}

    }

	@Override
	public void close() {
		client.close();
		
	}

	@Override
	public void delete(Long arg0) {
		client.delete(arg0);
	}

	@Override
	public void deleteScan(Long arg0) {
		client.deleteScan(arg0);
	}

	@Override
	public List<Scan> getAllScans() {
		return client.getAllScans();
	}

	@Override
	public Scan getScan(Long arg0) {
		return client.getScan(arg0);
	}

	@Override
	public CommandComposite getScanCommands(Long arg0) {
		return client.getScanCommands(arg0);
	}

	@Override
	public Data getScanData(Long arg0) {
		return client.getScanData(arg0);
	}

	@Override
	public Long queueScan(String arg0, CommandSet arg1) {
		return client.queueScan(arg0, arg1);
	}

   
}
