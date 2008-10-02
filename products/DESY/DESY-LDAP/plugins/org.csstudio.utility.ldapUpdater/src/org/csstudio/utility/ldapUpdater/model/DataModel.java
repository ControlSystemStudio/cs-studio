package org.csstudio.utility.ldapUpdater.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.csstudio.utility.ldapUpdater.IOC;

public class DataModel {
	private boolean _ready=false;

	/**
	 * historyMap is a hash map won from the long file history.dat
	 */
    private HashMap<String, Long> _historyMap;
    
    private ArrayList<String> ldapList;
    private ArrayList<String> _ldapRecordNames;
    
    private List<IOC> _iocList;

    
    private List<String> _BootedIocNames;
    private List<String> _NewIocNames = new ArrayList<String>();
    private List<String> _ObsoleteIocNames;
    
	public List<String> getBootedIocNames() {
		return _BootedIocNames;
	}

	public void setBootedIocNames(List<String> bootedIocNames) {
		_BootedIocNames = bootedIocNames;
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

	public ArrayList<String> getLdapList() {
		return ldapList;
	}

	public void setLdapList(ArrayList<String> ldapList) {
		this.ldapList = ldapList;
	}

	public boolean isReady() {
		return _ready;
	}

	public void setReady(boolean ready) {
		_ready = ready;
	}

	public List<String> getNewIocNames() {
		return _NewIocNames;
	}

	public void setNewIocNames(List<String> newIocNames) {
		_NewIocNames = newIocNames;
	}

	public List<String> getObsoleteIocNames() {
		return _ObsoleteIocNames;
	}

	public void setObsoleteIocNames(List<String> obsoleteIocNames) {
		_ObsoleteIocNames = obsoleteIocNames;
	}

	public void setLdapRecordNames(ArrayList<String> ldapRecordNames) {
		_ldapRecordNames=ldapRecordNames;
	}

	public ArrayList<String> getLdapRecordNames() {
		return _ldapRecordNames;
	}
	
}
