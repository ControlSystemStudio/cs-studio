package org.csstudio.alarm.treeView.views.models;

import java.util.ArrayList;

public class ContextTreeParent extends ContextTreeObject implements ISimpleTreeParent {

	private ArrayList<ContextTreeObject> children;
	public ContextTreeParent(ContextTreeParent parent) {
		super(parent);
		children = new ArrayList<ContextTreeObject>();
	}
	
	public ContextTreeParent(){
		super();
		children = new ArrayList<ContextTreeObject>();
	}
			
	public boolean hasChildren() {
		return (childCount()>0);
	}
	
	public void addChild(ContextTreeObject child){
		children.add(child);
	}
	
	public ContextTreeObject[] getChildren() {
		return (ContextTreeObject [])children.toArray(new ContextTreeObject[children.size()]);		
	}
	
	public int childCount() {
		return children.size();
	}

	public void addChild(ISimpleTreeObject child) {
		// TODO Auto-generated method stub
	}
}
