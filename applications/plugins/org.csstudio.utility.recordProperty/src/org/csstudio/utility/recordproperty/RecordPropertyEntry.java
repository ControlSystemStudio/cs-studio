package org.csstudio.utility.recordproperty;

import java.io.Serializable;

public class RecordPropertyEntry implements Serializable{

	/**
	 * The serial version UID.
	 */
	private static final long serialVersionUID = 8206841500198548898L;

	private String _pvName;
	
	private String _rdb;
	
	private String _val;
	
	private String _rmi;
	
	public RecordPropertyEntry(final String pv, final String rdb,
			final String val, final String rmi) {
		_pvName = pv;
		_rdb = rdb;
		_val = val;
		_rmi = rmi;
	}
	
	/**
	 * @return the PV name
	 */
	public String getPvName() {
		return _pvName;
	}
	
	/**
	 * @return the RDB
	 */
	public String getRdb() {
		return _rdb;
	}
	
	/**
	 * @return the value
	 */
	public String getVal() {
		return _val;
	}
	
	/**
	 * @return the RMI
	 */
	public String getRmi() {
		return _rmi;
	}
}
