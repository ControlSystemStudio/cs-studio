package org.csstudio.utility.nameSpaceBrowser.ui;

import org.csstudio.platform.model.IControlSystemItem;

public class ControlSystemItem implements IControlSystemItem {

	private String name;
	private String TYPE_ID = "css:controlSystemItem"; //$NON-NLS-1$
	private String path;


	public ControlSystemItem(String name, String path) {
		this.name = name;
		this.path = path;
	}

	public String getName() {
		// TODO Auto-generated method stub
		return name;
	}

	public String getPath() {
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
