/*******************************************************************************
 * Copyright (c) 2010-2016 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.validation.core.ui;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.csstudio.opibuilder.validation.Activator;
import org.csstudio.opibuilder.validation.core.SubValidationFailure;
import org.csstudio.opibuilder.validation.core.ValidationFailure;
import org.csstudio.opibuilder.validation.core.Validator;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

/**
 *
 * <code>ContentProvider</code> is meant to replace the content provider of the problems view. It provides elements in a
 * 3-level tree like structure to be able to group the sub validation failures below the parent failures.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public class ContentProvider implements ITreeContentProvider {

    private boolean filterOut = true;
    private ITreeContentProvider original;
    private Field markerField;

    private Map<ValidationFailure, Set<Object>> markersMap = new HashMap<>();

    /**
     * @param extendedMarkersView
     */
    public ContentProvider(ITreeContentProvider org) {
        this.original = org;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse .jface.viewers.Viewer, java.lang.Object,
     * java.lang.Object)
     */
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        markersMap.clear();
        original.inputChanged(viewer, oldInput, newInput);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.jface.viewers.IContentProvider#dispose()
     */
    public void dispose() {
        original.dispose();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java .lang.Object)
     */
    public Object[] getChildren(Object parentElement) {
        ValidationFailure f = getValidationFailure(parentElement);
        if (f != null && f.hasSubFailures()) {
            Set<Object> list = markersMap.get(f);
            if (list != null) {
                return list.toArray(new Object[list.size()]);
            }
        }
        return filterChildren(original.getChildren(parentElement));
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements (java.lang.Object)
     */
    public Object[] getElements(Object inputElement) {
        return filterElements(original.getElements(inputElement));
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.jface.viewers.ILazyTreeContentProvider#getParent( java.lang.Object)
     */
    public Object getParent(Object element) {
        return original.getParent(element);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java .lang.Object)
     */
    public boolean hasChildren(Object element) {
        if (Activator.getInstance().isNestMarkers()) {
            ValidationFailure f = getValidationFailure(element);
            if (f != null) {
                return f.hasSubFailures();
            }
            if (original.hasChildren(element)) {
                Object[] obj = filterChildren(original.getChildren(element));
                return obj.length > 0;
            }
            return false;
        }
        return original.hasChildren(element);
    }

    private ValidationFailure getValidationFailure(Object element) {
        if ("MarkerEntry".equals(element.getClass().getSimpleName())) {
            try {
                if (markerField == null) {
                    markerField = element.getClass().getDeclaredField("marker");
                    markerField.setAccessible(true);
                }
                IMarker m = (IMarker) markerField.get(element);
                return (ValidationFailure) m.getAttribute(Validator.ATTR_VALIDATION_FAILURE);
            } catch (RuntimeException | NoSuchFieldException | IllegalAccessException | CoreException e) {
                // ignore
            }
        }
        return null;
    }

    private Object[] filterElements(Object[] categories) {
        if (Activator.getInstance().isNestMarkers()) {
            try {
                List<Object> list = new ArrayList<>();
                for (Object o : categories) {
                    if ("MarkerCategory".equals(o.getClass().getSimpleName()) && hasChildren(o)) {
                        list.add(o);
                    }
                }
                return filterOut ? list.toArray(new Object[list.size()]) : categories;
            } catch (RuntimeException e) {
                // ignore
            }
        }
        return categories;
    }

    private Object[] filterChildren(Object[] markers) {
        if (Activator.getInstance().isNestMarkers()) {
            try {
                List<Object> list = new ArrayList<>(markers.length);
                for (Object o : markers) {
                    if ("MarkerEntry".equals(o.getClass().getSimpleName())) {
                        if (markerField == null) {
                            markerField = o.getClass().getDeclaredField("marker");
                            markerField.setAccessible(true);
                        }
                        IMarker m = (IMarker) markerField.get(o);
                        ValidationFailure v = (ValidationFailure) m.getAttribute(Validator.ATTR_VALIDATION_FAILURE);
                        if (v instanceof SubValidationFailure) {
                            Set<Object> mlist = markersMap.get(((SubValidationFailure) v).getParent());
                            if (mlist == null) {
                                mlist = new HashSet<>();
                                markersMap.put(((SubValidationFailure) v).getParent(), mlist);
                            }
                            mlist.add(o);
                            continue;
                        }
                    }
                    list.add(o);
                }
                return filterOut ? list.toArray(new Object[list.size()]) : markers;
            } catch (IllegalAccessException | NoSuchFieldException | CoreException | RuntimeException e) {
                // ignore
            }
        }
        return markers;
    }
}
