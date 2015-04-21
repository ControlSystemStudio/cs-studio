package org.csstudio.opibuilder.properties;

import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.visualparts.AbstractDialogCellEditor;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

public class ServiceMethodPropertyDescriptor extends TextPropertyDescriptor {
    
    private final AbstractWidgetModel widgetModel;

    public ServiceMethodPropertyDescriptor(final Object id, final String displayName, final AbstractWidgetModel widgetModel) {
	super(id, displayName);
	this.widgetModel = widgetModel;
	setLabelProvider(new LabelProvider(){
	    @Override
	    public String getText(Object element) {
		if(element instanceof ServiceMethodDescription){
		    return ((ServiceMethodDescription) element).getService()+"/"+((ServiceMethodDescription) element).getMethod();
		}
	        return super.getText(element);
	    }
	});
    }

    @Override
    public CellEditor createPropertyEditor(Composite parent) {
	return new AbstractDialogCellEditor(parent,
		"Service Description") {

	    private ServiceMethodDescription serviceMethodDescription;

	    @Override
	    protected void openDialog(Shell parentShell,
		    String dialogTitle) {
		ServiceMethodDialog serviceMethodDialog = new ServiceMethodDialog(
			parentShell, widgetModel);
		serviceMethodDialog.setBlockOnOpen(true);
		if (serviceMethodDialog.open() == Window.OK) {		    
		    serviceMethodDescription = serviceMethodDialog
			    .getServiceMethodDescription();
		}

	    }

	    @Override
	    protected boolean shouldFireChanges() {
		return serviceMethodDescription != null;
	    }

	    @Override
	    protected Object doGetValue() {
		return serviceMethodDescription;
	    }

	    @Override
	    protected void doSetValue(Object value) {
		if (value == null
			|| !(value instanceof ServiceMethodDescription))
		    serviceMethodDescription = null;
		else
		    serviceMethodDescription = (ServiceMethodDescription) value;
	    }

	};
    }
}
