/**
 * 
 */
package org.csstudio.opibuilder.properties;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.csstudio.utility.pvmanager.ui.toolbox.ServiceTreeWidget;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.epics.pvmanager.service.Service;
import org.epics.pvmanager.service.ServiceMethod;
import org.epics.pvmanager.service.ServiceRegistry;

import static org.csstudio.opibuilder.properties.ServiceMethodDescription.createServiceMethodDescription;
/**
 * @author shroffk
 *
 */
public class ServiceTreeDialog extends Dialog{

    private ServiceTreeWidget serviceTreeWidget;
    
    private ServiceMethodDescription serviceMethodDescription;
    
    protected ServiceTreeDialog(Shell parentShell) {
	super(parentShell);
	setShellStyle(getShellStyle() | SWT.RESIZE | SWT.MAX);
    }

    @Override
    protected Control createDialogArea(Composite parent) {
	final Composite container = (Composite) super.createDialogArea(parent);      
	      container.setLayout(new FormLayout());  
	      
	serviceTreeWidget = new ServiceTreeWidget(container, SWT.NONE);
	FormData fd_serviceTreeWidget = new FormData();
	fd_serviceTreeWidget.bottom = new FormAttachment(100);
	fd_serviceTreeWidget.top = new FormAttachment(0);
	fd_serviceTreeWidget.left = new FormAttachment(0);
	fd_serviceTreeWidget.right = new FormAttachment(100);
	serviceTreeWidget.setLayoutData(fd_serviceTreeWidget);
	List<String> serviceNames = new ArrayList<String>(ServiceRegistry
		.getDefault().listServices());
	Collections.sort(serviceNames);
	List<Service> services = new ArrayList<Service>();
	for (String serviceName : serviceNames) {
	    services.add(ServiceRegistry.getDefault().findService(serviceName));
	}	
	serviceTreeWidget.setServiceNames(services);	
	serviceTreeWidget.addSelectionChangedListener(new ISelectionChangedListener() {
	    
	    @Override
	    public void selectionChanged(SelectionChangedEvent event) {
		if(event.getSelection() instanceof TreeSelection){		    
		    TreePath[] treeSelection = ((TreeSelection) event.getSelection()).getPaths();
		    if(treeSelection[0].getSegmentCount() == 2){
			serviceMethodDescription = createServiceMethodDescription(
				((Service)treeSelection[0].getFirstSegment()).getName(), 
				(ServiceMethod)treeSelection[0].getLastSegment());
		    }
		}
		getShell().pack();		
	    }
	});
	return container;
    }
   
    /**
     * 
     * @return
     */
    public ServiceMethodDescription getSelectedServiceMethodDescription() {
	return serviceMethodDescription;
    }
    
    
    
}
