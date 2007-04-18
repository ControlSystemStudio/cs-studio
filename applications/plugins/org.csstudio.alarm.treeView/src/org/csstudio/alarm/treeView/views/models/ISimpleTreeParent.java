package org.csstudio.alarm.treeView.views.models;

public abstract interface ISimpleTreeParent extends ISimpleTreeObject {

	public abstract boolean hasChildren();
	public abstract void addChild(ISimpleTreeObject child);
	public abstract ISimpleTreeObject[] getChildren();
	
	
}
