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

import java.util.Iterator;
import java.util.Map;

import org.csstudio.sds.model.PropertyTypesEnum;
import org.csstudio.sds.ui.internal.properties.StringMapCellEditor;
import org.csstudio.sds.ui.properties.PropertyDescriptor;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.widgets.Composite;

/**
 * Descriptor for a property that has a value which should be edited with a map
 * cell editor.
 * <p>
 * This class may be instantiated; it is not intended to be subclassed.
 * </p>
 * <p>
 * Example:
 *
 * <pre>
 * IPropertyDescriptor pd = new MapPropertyDescriptor(&quot;surname&quot;, &quot;Last Name&quot;);
 * </pre>
 *
 * </p>
 *
 * @author Kai Meyer
 */
public class StringMapPropertyDescriptor extends PropertyDescriptor {

    /**
     * The name of the property.
     */
    private final String _name;
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
    public StringMapPropertyDescriptor(final Object id, final String displayName, PropertyTypesEnum type,
            final String category) {
        super(id, displayName, type);
        _name = displayName;
        assert category != null;
        setCategory(category);

        this.setLabelProvider(new MapLabelProvider());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CellEditor createPropertyEditor(final Composite parent) {
        CellEditor editor = new StringMapCellEditor(parent, _name);
        if (getValidator() != null) {
            editor.setValidator(getValidator());
        }
        return editor;
    }

    /**
     * A label provider for a Map of Strings.
     *
     * @author Kai Meyer
     */
    private final class MapLabelProvider extends LabelProvider {
        /**
         * {@inheritDoc}
         */
        @SuppressWarnings("unchecked")
        @Override
        public String getText(final Object element) {
            if (element instanceof Map) {
                Map<String, String> map = (Map<String, String>) element;
                StringBuffer buffer = new StringBuffer("[");
                if (!map.isEmpty()) {
                    Iterator<String> it = map.keySet().iterator();

                    while(it.hasNext()) {
                        String key = it.next();
                        String val = map.get(key);

                        buffer.append(key);
                        buffer.append(":");
                        buffer.append(val);
                        buffer.append(it.hasNext()?", ":"");
                    }
                }
                buffer.append("]");
                return buffer.toString();
            } else {
                return element.toString();
            }
        }

    }
}
