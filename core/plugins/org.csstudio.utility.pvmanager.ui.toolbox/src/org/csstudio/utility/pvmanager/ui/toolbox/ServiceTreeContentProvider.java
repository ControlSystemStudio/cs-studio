/**
 * 
 */
package org.csstudio.utility.pvmanager.ui.toolbox;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.epics.pvmanager.service.Service;
import org.epics.pvmanager.service.ServiceMethod;

/**
 * @author shroffk
 * 
 */
public class ServiceTreeContentProvider implements ITreeContentProvider {

    private List<Service> services;

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.viewers.IContentProvider#dispose()
     */
    @Override
    public void dispose() {
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface
     * .viewers.Viewer, java.lang.Object, java.lang.Object)
     */
    @SuppressWarnings("unchecked")
    @Override
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	this.services = (List<Service>) newInput;
    }

    @Override
    public Object[] getElements(Object inputElement) {
	return services.toArray();
    }

    @Override
    public Object[] getChildren(Object parentElement) {
	if (parentElement instanceof Service) {
	    List<ServiceMethod> serviceMethods = new ArrayList<ServiceMethod>(
		    ((Service) parentElement).getServiceMethods().values());
	    Collections.sort(serviceMethods, new Comparator<ServiceMethod>() {

		@Override
		public int compare(ServiceMethod o1, ServiceMethod o2) {
		    return o1.getName().compareTo(o2.getName());
		}
	    });
	    return serviceMethods.toArray();
	} else if (parentElement instanceof ServiceMethod) {
	    SortedMap<String, String> argumentDescriptionMap = new TreeMap<String, String>();
	    argumentDescriptionMap.putAll(((ServiceMethod) parentElement)
		    .getArgumentDescriptions());
	    SortedMap<String, String> resultDescriptionMap = new TreeMap<String, String>();
	    resultDescriptionMap.putAll(((ServiceMethod) parentElement)
		    .getResultDescriptions());
	    List<Entry<String, String>> descriptionList = new ArrayList<Entry<String, String>>();
	    descriptionList.addAll(argumentDescriptionMap.entrySet());
	    descriptionList.addAll(resultDescriptionMap.entrySet());
	    return descriptionList.toArray();
	}
	return null;
    }

    @Override
    public Object getParent(Object element) {
	return null;
    }

    @Override
    public boolean hasChildren(Object element) {
	if (element instanceof Service) {
	    return !((Service) element).getServiceMethods().values().isEmpty();
	} else if (element instanceof ServiceMethod) {
	    return !((ServiceMethod) element).getArgumentDescriptions()
		    .isEmpty()
		    || !((ServiceMethod) element).getResultDescriptions()
			    .isEmpty();
	}
	return false;
    }
}
