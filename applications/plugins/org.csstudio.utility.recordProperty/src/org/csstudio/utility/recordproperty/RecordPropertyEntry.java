package org.csstudio.utility.recordproperty;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.csstudio.platform.model.IProcessVariable;
import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

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
	public String getName() {
		/*
		IWorkbench workbench = PlatformUI.getWorkbench();
        IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
        IWorkbenchPage page = window.getActivePage();
        
        RecordPropertyView rpv = (RecordPropertyView) page.showView(ID, createNewInstance(),
                                          IWorkbenchPage.VIEW_ACTIVATE);
        */
		String recordName = RecordPropertyView.getRecordName();
		
		
		return validateRecord(recordName)+"."+getPvName();
	}

	public String getTypeId() {

		return IProcessVariable.TYPE_ID;
	}
	
	private String validateRecord(String _record) {
		
		String REGEX = "(\\.[a-zA-Z1-9]{0,6})$";
		
		Pattern p = Pattern.compile(REGEX);
		Matcher m = p.matcher(_record);
		
		_record = m.replaceAll("");
		
		return _record;
	}

}
