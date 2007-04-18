package org.csstudio.alarm.treeView.views.models;

import java.util.Enumeration;
import java.util.Hashtable;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

public class ContextTreeObjectPropertySource implements IPropertySource {

	private static final String PROPERTY_TEXT =  "text";
	private static final String PROPERTY_NAME =  "name";
	private static final String PROPERTY_DNAME =  "dn";
	private String defText;
	private ContextTreeObject obj;
	
	private IPropertyDescriptor[] propertyDescriptors;
	
	public ContextTreeObjectPropertySource(ContextTreeObject ob, String defaultText) {
		defText = defaultText;
		this.obj = ob;
	}
	
	public Object getEditableValue() {
		// TODO Auto-generated method stub
		return "LDAPProperty";
	}

	public IPropertyDescriptor[] getPropertyDescriptors() {
		Hashtable<String,String> attb; 
		if (propertyDescriptors == null) {
			//Create descriptor and set category
			PropertyDescriptor NameDescriptor = new TextPropertyDescriptor(PROPERTY_NAME,"Name");
			PropertyDescriptor DNameDescriptor = new TextPropertyDescriptor(PROPERTY_DNAME,"DN");
			PropertyDescriptor dummy;
			NameDescriptor.setCategory("Name");
			DNameDescriptor.setCategory("Name");
			attb= obj.getAttributes();
			int n = attb.size();
			propertyDescriptors = new IPropertyDescriptor[n+2+2];
			propertyDescriptors[0] = NameDescriptor;
			propertyDescriptors[1] = DNameDescriptor;
			//attributes
			Enumeration<String> keys = attb.keys();
			int i=2;
			while (keys.hasMoreElements()){
				String curkey = keys.nextElement();
				dummy = new TextPropertyDescriptor(curkey,curkey);
				dummy.setCategory("Attributes");
				propertyDescriptors[i] = dummy;
				i++;
			}
			dummy = new TextPropertyDescriptor("ActiveAlarmSeverity","ActiveAlarmSeverity");
			dummy.setCategory("Alarms");
			propertyDescriptors[i] = dummy;
			i++;
			dummy = new TextPropertyDescriptor("PassiveAlarmSeverity","PassiveAlarmSeverity");
			dummy.setCategory("Alarms");
			propertyDescriptors[i] = dummy;
			i++;				
		}
		return propertyDescriptors;
	}
	
	public Object getPropertyValue(Object id) {
		// TODO Auto-generated method stub
		if (id.toString() == "ActiveAlarmSeverity"){
			if (obj.getMyActiveAlarmState()==null){return "Not set";}
			return obj.getMyActiveAlarmState().getProperties().get("SEVERITY");
		}
		if (id.toString() == "PassiveAlarmSeverity"){
			if (obj.getMyPassiveAlarmState()==null){return "Not set";}
			return obj.getMyPassiveAlarmState().getProperties().get("SEVERITY");
		}
		if (id.toString() == "name"){
			return obj.getName();
		}
		if (id.toString() == "dn"){
			return obj.getDn();
		}
		else {
			return obj.getAttributes().get(id);
		}
	}

	public boolean isPropertySet(Object id) {
		// TODO Auto-generated method stub
		System.out.println("isPropertySet not supported");
		return false;
	}

	public void resetPropertyValue(Object id) {
		// TODO Auto-generated method stub
		System.out.println("resetPropertyValue not supported");

	}

	public void setPropertyValue(Object id, Object value) {
		//update the atribute
		obj.updateAttribute(id.toString(),value.toString());
	}

}
