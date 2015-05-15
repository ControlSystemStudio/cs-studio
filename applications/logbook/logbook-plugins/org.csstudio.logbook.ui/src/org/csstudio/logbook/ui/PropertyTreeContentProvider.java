/**
 *
 */
package org.csstudio.logbook.ui;

import java.util.List;

import org.csstudio.logbook.Property;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

/**
 * @author shroffk
 *
 */
public class PropertyTreeContentProvider implements ITreeContentProvider {

    private List<Property> properties;


    @Override
    public void dispose() {
    }


    @SuppressWarnings("unchecked")
    @Override
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
    this.properties = (List<Property>) newInput;
    }

    @Override
    public Object[] getElements(Object inputElement) {
    return properties.toArray();
    }

    @Override
    public Object[] getChildren(Object parentElement) {
    if (parentElement instanceof Property) {
        return ((Property) parentElement).getAttributes().toArray();
    }
    return null;
    }

    @Override
    public Object getParent(Object element) {
    return null;
    }

    @Override
    public boolean hasChildren(Object element) {
    if (element instanceof Property) {
        return !((Property) element).getAttributes().isEmpty();
    }
    return false;
    }

}
