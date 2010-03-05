package org.csstudio.utility.ldapUpdater.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.csstudio.utility.ldapUpdater.IOC;

public class DataModel {
	private boolean _ready=false;

	/**
	 * historyMap is a hash map won from the long file history.dat
	 */
    private List<IOC> _iocList;
    private List<String> _bootedIocNames;
    private List<String> _newIocNames = new ArrayList<String>();
    private List<String> _obsoleteIocNames;

    // FIXME : 
    private HashMap<String, Long> _historyMap;   
    private Map<String, String> _econToEfanMap = Collections.emptyMap();
    private Map<String, String> _erenToEconMap = Collections.emptyMap();
    
 
//	Getters and Setters :
	    
	public List<String> getBootedIocNames() {
		return _bootedIocNames;
	}

	public void setBootedIocNames(List<String> bootedIocNames) {
		_bootedIocNames = bootedIocNames;
	}

	public List<IOC> getIocList() {
		return _iocList;
	}

	public void setIocList(List<IOC> iocList) {
		_iocList = iocList;
	}

	public HashMap<String, Long> getHistoryMap() {
		return _historyMap;
	}

	public void setHistoryMap(HashMap<String, Long> historyMap) {
		_historyMap = historyMap;
	}

	public boolean isReady() {
		return _ready;
	}

	public void setReady(boolean ready) {
		_ready = ready;
	}

	public List<String> getNewIocNames() {
		return _newIocNames;
	}

	public void setNewIocNames(List<String> newIocNames) {
		_newIocNames = newIocNames;
	}

	public List<String> getObsoleteIocNames() {
		return _obsoleteIocNames;
	}

	public void setObsoleteIocNames(List<String> obsoleteIocNames) {
		_obsoleteIocNames = obsoleteIocNames;
	}

	public void setEconToEfanMap(Map<String, String> map) {
		_econToEfanMap = new HashMap<String, String>(map);
	}

	public Map<String, String> getEconToEfanMap() {
		return _econToEfanMap;
	}
	
	public void setErenToEconMap(Map<String, String> map) {
		_erenToEconMap = new HashMap<String, String>(map);
	}

	public Map<String, String> getErenToEconMap() {
		return _erenToEconMap;
	}
}
