package org.csstudio.alarm.treeView.views.models;

import java.util.Enumeration;
import java.util.Hashtable;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

public class AlarmTreeObjectPropertySource implements IPropertySource {
	private AlarmTreeObject obj;
	
	private IPropertyDescriptor[] propertyDescriptors;
	
	public AlarmTreeObjectPropertySource(AlarmTreeObject ob) {
		this.obj = ob;
	}

	public Object getEditableValue() {
		// TODO Auto-generated method stub
		System.out.println("getEditableValue not fully supported!!!");
		return "AlarmProperty";
	}

	public IPropertyDescriptor[] getPropertyDescriptors() {
		// TODO Auto-generated method stub
		Hashtable<String,String> attb;
		PropertyDescriptor dummy;
		if (propertyDescriptors == null)
		{
			//Lazy (or dynamic) caching I think it is called
			attb = obj.getProperties();
			int n = attb.size();
			propertyDescriptors = new IPropertyDescriptor[n];
			Enumeration<String> keys = attb.keys();
			int i=0;
			while (keys.hasMoreElements()){
				String curkey = keys.nextElement();
				dummy = new TextPropertyDescriptor(curkey,curkey);
				propertyDescriptors[i]=dummy;
				i++;
			}
		}
		return propertyDescriptors;
	}

	public Object getPropertyValue(Object id) {
		// TODO Auto-generated method stub
		if (id instanceof String)
			return obj.getProperties().get(id);
		return "notString";
	}

	public boolean isPropertySet(Object id) {
		// TODO Auto-generated method stub
		return false;
	}

	public void resetPropertyValue(Object id) {
		// TODO Auto-generated method stub

	}

	public void setPropertyValue(Object id, Object value) {
		// TODO Auto-generated method stub

	}

}
