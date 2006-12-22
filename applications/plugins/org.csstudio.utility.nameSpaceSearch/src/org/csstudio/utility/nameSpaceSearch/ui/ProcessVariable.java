package org.csstudio.utility.nameSpaceSearch.ui;

import org.csstudio.platform.model.IProcessVariable;

public class ProcessVariable implements IProcessVariable {

	private String name;
	private String[] path;


//	public ProcessVariable(String name) {
//		this.name = name;
//		this.path = "";
//	}

	public ProcessVariable(String name, String[] path) {
		this.name = name;
		this.path = path;
	}

	public String getName() {
		// TODO Auto-generated method stub
		return name;
	}

	public String[] getPath() {
		return path;
	}

	public String getTypeId() {
		return TYPE_ID;
	}

	public Object getAdapter(Class adapter) {
		// TODO Auto-generated method stub
		return null;
	}

	public String toString(){
		return name;
	}
}
