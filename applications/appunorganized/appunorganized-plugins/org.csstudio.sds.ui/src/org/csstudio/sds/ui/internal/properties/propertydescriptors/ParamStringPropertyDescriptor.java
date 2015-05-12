/*
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron,
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS.
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE.
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS,
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION,
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
 package org.csstudio.sds.ui.internal.properties.propertydescriptors;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.csstudio.sds.model.PropertyTypesEnum;
import org.csstudio.sds.model.WidgetProperty;
import org.csstudio.sds.ui.editparts.AbstractBaseEditPart;
import org.csstudio.sds.ui.internal.editor.DisplayEditor;
import org.csstudio.sds.ui.internal.properties.ParamStringCellEditor;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;

/**
 * Descriptor for a property that has a value which should be edited with a parameterized text
 * cell editor.
 * <p>
 * This class may be instantiated; it is not intended to be subclassed.
 * </p>
 * <p>
 *
 * @author Kai Meyer
 */
public final class ParamStringPropertyDescriptor extends TextPropertyDescriptor {

    /**
     * Creates an property descriptor with the given id and display name.
     *
     * @param id
     *            the id of the property
     * @param displayName
     *            the name to display for the property
     * @param category
     *            the category
     */
    public ParamStringPropertyDescriptor(final Object id, final String displayName, PropertyTypesEnum type,
            final String category) {
        super(id, displayName, type, category);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CellEditor createPropertyEditor(final Composite parent) {

        // swende: Der Zugriff auf die Model-Properties ist in TooltipSection besser gelöst. Mit der Entsorgung der alten Property-View wird dieser Schmerz hier sich von alleine erledigen!
        Map<String, WidgetProperty> properties = new HashMap<String, WidgetProperty>();
        IEditorPart activeEditor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
        if (activeEditor instanceof DisplayEditor) {
            List<AbstractBaseEditPart> editParts = ((DisplayEditor)activeEditor).getSelectedEditParts();
            if (editParts.size()!=0) {
                List<String> propertyNames = new LinkedList<String>();
                 propertyNames.addAll(editParts.get(0).getWidgetModel().getVisiblePropertyIds());
                 for (int i=1;i<editParts.size();i++) {
                     propertyNames.retainAll(editParts.get(i).getWidgetModel().getVisiblePropertyIds());
                 }
                 for (String name : propertyNames) {
                     if (!name.equals(this.getId())) {
                         properties.put(name, editParts.get(0).getWidgetModel().getPropertyInternal(name));
                     }
                 }
            }
        }
        // At least move all above the factory


        CellEditor editor = new ParamStringCellEditor(parent, properties);
        if (getValidator() != null) {
            editor.setValidator(getValidator());
        }
        return editor;
    }

}
