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

import org.csstudio.sds.model.ActionData;
import org.csstudio.sds.model.PropertyTypesEnum;
import org.csstudio.sds.ui.internal.properties.ActionDataCellEditor;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.widgets.Composite;

/**
 * Descriptor for a property that has a value which should be edited with a
 * {@link ActionData} cell editor.
 *
 * @author Kai Meyer
 * @version $Revision: 1.1 $
 *
 */
public final class ActionDataPropertyDescriptor extends TextPropertyDescriptor {

    /**
     * The name of the property.
     */
    private final String _name;

    /**
     * Standard constructor.
     *
     * @param id
     *            the id of the property
     * @param displayName
     *            the name to display for the property
     * @param category
     *            the category
     */
    public ActionDataPropertyDescriptor(final Object id, final String displayName, PropertyTypesEnum type, final
            String category) {
        super(id, displayName, type, category);
        _name = displayName;
        this.setLabelProvider(new ActionDataLabelProvider());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CellEditor createPropertyEditor(final Composite parent) {
        CellEditor editor = new ActionDataCellEditor(parent, _name);
        if (getValidator() != null) {
            editor.setValidator(getValidator());
        }
        return editor;
    }

    /**
     * A label provider for a {@link ActionData} value.
     *
     * @author Kai Meyer
     *
     */
    private final class ActionDataLabelProvider extends LabelProvider {
        /**
         * {@inheritDoc}
         */
        @Override
        public String getText(final Object element) {
            if (element instanceof ActionData) {
                return ((ActionData)element).toString();
            } else {
                return element.toString();
            }
        }
    }
}
