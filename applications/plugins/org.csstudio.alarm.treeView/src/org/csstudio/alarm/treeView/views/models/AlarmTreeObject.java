package org.csstudio.alarm.treeView.views.models;

import org.eclipse.ui.views.properties.IPropertySource;

public class AlarmTreeObject extends Alarm implements ISimpleTreeObject{

	private AlarmTreeObject parent;
	private AlarmTreeObjectPropertySource aTreeObjPS; 
	
	public AlarmTreeObject() {
		super();
		// TODO Auto-generated constructor stub
	}

	public AlarmTreeObject(AlarmTreeParent parent){
		super();
		this.parent = parent;
	}
	
	public String getName() {
		// TODO Auto-generated method stub
		return name;
	}

	public void setName(String name) {
		// TODO Auto-generated method stub
		this.name = name;
	}

	//yes I have to explain this - in order to erase all alarms from the structure, we must get to the root - AlarmConnection
	//so we are calling disableAlarm on the parent so long that we get to the AlarmConnection, where this method is overridden
	//method disableAlarm without parameters means that we want to disable this alarm
	
	public void disableAlarm(Alarm alm){
		parent.disableAlarm(alm);
	}
	
	public void disableAlarm(){
		parent.disableAlarm(this);
	}
	
	public ISimpleTreeObject getParent() {
		// TODO Auto-generated method stub
		return parent;
	}

	public Object getAdapter(Class adapter) {
		// TODO Auto-generated method stub
		if (adapter == IPropertySource.class){
			if (aTreeObjPS == null){
				aTreeObjPS = new AlarmTreeObjectPropertySource(this);
			}
			return aTreeObjPS;
		}
		return null;
	}
	
	public String toString(){
		return getName();
	}

}
