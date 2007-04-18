package org.csstudio.alarm.treeView.views.models;

import java.util.ArrayList;

public class AlarmTreeParent extends AlarmTreeObject implements ISimpleTreeParent {

	private ArrayList<AlarmTreeObject> children;
	
	public AlarmTreeParent(AlarmTreeParent parent) {
		super(parent);
		// TODO Auto-generated constructor stub
		children = new ArrayList<AlarmTreeObject>();
	}

	public AlarmTreeParent() {
		super();
		children = new ArrayList<AlarmTreeObject>();		
	}
	
	public boolean hasChildren(){
		return (childCount()>0);
	}
	
	public int childCount(){
		return children.size();
	}
	
	
	public AlarmTreeObject[] getChildren(){
		return (AlarmTreeObject[])children.toArray(new AlarmTreeObject[children.size()]);
	}

	public void addChild(AlarmTreeObject child) {
		// TODO Auto-generated method stub
		children.add(child);		
	}

	public void addChild(ISimpleTreeObject child) {
		// TODO Auto-generated method stub
		
	}

}
