package org.csstudio.utility.recordproperty;

import java.io.Serializable;

import org.csstudio.platform.model.IProcessVariable;
import org.eclipse.core.runtime.PlatformObject;

public class RecordPropertyEntry extends PlatformObject implements IProcessVariable, Serializable{

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

	/**
	 * When some other plugin is opened via Record Property, it sends
	 * record name and field name ("recordname.fieldname").
	 */
	@Override
	public String getName() {
		
		return RecordPropertyView.getRecordName()+"."+getPvName();
	}

	@Override
	public String getTypeId() {

		return IProcessVariable.TYPE_ID;
	}

}
