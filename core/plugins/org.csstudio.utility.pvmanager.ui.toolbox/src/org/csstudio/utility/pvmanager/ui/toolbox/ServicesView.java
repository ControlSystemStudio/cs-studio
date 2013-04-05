/**
 * 
 */
package org.csstudio.utility.pvmanager.ui.toolbox;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
    private TreeViewer treeViewer;

    /**
     * 
     */
    public ServicesView() {
	// TODO Auto-generated constructor stub
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

	Composite composite = new Composite(parent, SWT.NONE);
	TreeColumnLayout tcl_composite = new TreeColumnLayout();
	composite.setLayout(tcl_composite);

	treeViewer = new TreeViewer(composite, SWT.BORDER);
	Tree tree = treeViewer.getTree();
	tree.setHeaderVisible(true);
	tree.setLinesVisible(true);

	TreeViewerColumn treeViewerColumn = new TreeViewerColumn(treeViewer,
		SWT.NONE);
	treeViewerColumn.setLabelProvider(new ColumnLabelProvider() {
	    public Image getImage(Object element) {
		return null;
	    }

	    public String getText(Object element) {
		if (element instanceof Service) {
		    return ((Service) element).getName();
		} else if (element instanceof ServiceMethod) {
		    return serviceMethod2String((ServiceMethod) element);
		} else if (element instanceof Entry) {
		    return ((Entry<String, String>) element).getKey();
		}
		return "";
	    }
	});
	TreeColumn trclmnNewColumn = treeViewerColumn.getColumn();
	tcl_composite.setColumnData(trclmnNewColumn, new ColumnWeightData(10,
		ColumnWeightData.MINIMUM_WIDTH, true));
	trclmnNewColumn.setText("Name");

	TreeViewerColumn treeViewerColumn_1 = new TreeViewerColumn(treeViewer,
		SWT.NONE);
	treeViewerColumn_1.setLabelProvider(new ColumnLabelProvider() {
	    public Image getImage(Object element) {
		return null;
	    }

	    public String getText(Object element) {
		if (element instanceof Service) {
		    return ((Service) element).getDescription();
		} else if (element instanceof Entry) {
		    return ((Entry<String, String>) element).getValue();
		}
		return "";
	    }
	});
	TreeColumn trclmnNewColumn_1 = treeViewerColumn_1.getColumn();
	tcl_composite.setColumnData(trclmnNewColumn_1, new ColumnWeightData(7,
		ColumnWeightData.MINIMUM_WIDTH, true));
	trclmnNewColumn_1.setText("Description");
	treeViewer.setContentProvider(new ServiceTreeContentProvider());

	List<String> serviceNames = new ArrayList<String>(ServiceRegistry
		.getDefault().listServices());
	Collections.sort(serviceNames);
	List<Service> services = new ArrayList<Service>();
	for (String serviceName : serviceNames) {
	    services.add(ServiceRegistry.getDefault().findService(serviceName));
	}
	treeViewer.setInput(services);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
     */
    @Override
    public void setFocus() {
	treeViewer.getControl().setFocus();
    }

    private String serviceMethod2String(ServiceMethod serviceMethod) {
	StringBuffer stringBuffer = new StringBuffer();
	stringBuffer.append(serviceMethod.getName()).append("(");
	List<String> arguments = new ArrayList<String>();
	for (Entry<String, Class<?>> argument : serviceMethod
		.getArgumentTypes().entrySet()) {
	    arguments.add(argument.getValue().getSimpleName() + " "
		    + argument.getKey());
	}
	stringBuffer.append(Joiner.on(", ").join(arguments));
	stringBuffer.append(")");
	stringBuffer.append(": ");
	List<String> results = new ArrayList<String>();
	for (Entry<String, Class<?>> result : serviceMethod.getResultTypes()
		.entrySet()) {
	    results.add(result.getValue().getSimpleName() + " "
		    + result.getKey());
	}
	stringBuffer.append(Joiner.on(", ").join(results));
	return stringBuffer.toString();
    }
}
