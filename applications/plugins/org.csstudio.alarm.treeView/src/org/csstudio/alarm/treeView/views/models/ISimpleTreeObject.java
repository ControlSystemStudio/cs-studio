package org.csstudio.alarm.treeView.views.models;

import org.eclipse.core.runtime.IAdaptable;

public abstract interface ISimpleTreeObject extends IAdaptable{
	
	public abstract String getName();
	public abstract void setName(String name);
	public abstract ISimpleTreeObject getParent();
	
}
