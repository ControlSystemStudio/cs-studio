/**
 * 
 */
package org.csstudio.utility.pvmanager.ui.toolbox;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.layout.TreeColumnLayout;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnPixelData;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.part.ViewPart;
import org.epics.pvmanager.service.Service;
import org.epics.pvmanager.service.ServiceMethod;
import org.epics.pvmanager.service.ServiceRegistry;

import com.google.common.base.Joiner;

/**
 * @author shroffk
 * 
 */
public class ServicesView extends ViewPart {

    public static final String ID = "org.csstudio.utility.pvmanager.ui.toolbox.ServicesView"; //$NON-NLS-1$
    private ServiceTreeWidget serviceTreeWidget;
    
    /**
     * 
     */
    public ServicesView() {
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets
     * .Composite)
     */
    @Override
    public void createPartControl(Composite parent) {
    	
    	serviceTreeWidget = new ServiceTreeWidget(parent, SWT.NONE);

	List<String> serviceNames = new ArrayList<String>(ServiceRegistry
		.getDefault().listServices());
	Collections.sort(serviceNames);
	List<Service> services = new ArrayList<Service>();
	for (String serviceName : serviceNames) {
	    services.add(ServiceRegistry.getDefault().findService(serviceName));
	}	
	serviceTreeWidget.setServiceNames(services);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
     */
    @Override
    public void setFocus() {
	serviceTreeWidget.setFocus();
    }

}
