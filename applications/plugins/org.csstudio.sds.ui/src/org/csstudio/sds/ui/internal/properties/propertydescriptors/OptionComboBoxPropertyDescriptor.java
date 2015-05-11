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
import java.util.Map;

import org.csstudio.sds.model.IOption;
import org.csstudio.sds.model.PropertyTypesEnum;
import org.csstudio.sds.ui.internal.properties.view.OptionComboBoxCellEditor;
import org.csstudio.sds.ui.properties.PropertyDescriptor;
import org.csstudio.ui.util.ImageUtil;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;

/**
 * Descriptor for a property that has a value which should be edited
 * with a combo box cell editor.  This class provides a default
 * <code>ILabelProvider</code> that will render the label of the given
 * descriptor as the <code>String</code> found in the labels array at the
 * currently selected index.
 * <p>
 * The value of the property is a String-based id of one of the provided options.
 * </p>
 * <p>
 * This class may be instantiated; it is not intended to be subclassed.
 * </p>
 *
 * @author Sven Wende
 */
public final class OptionComboBoxPropertyDescriptor extends PropertyDescriptor {
    /**
     * Maps option identifiers to options.
     */
    private Map<String, IOption> _identifierToOptionMapping;

    /**
     * The labels to display in the combo box.
     */
    private IOption[] _options;

    /**
     * Creates an property descriptor with the given id, display name, and list
     * of value labels to display in the combo box cell editor.
     *
     * @param id the id of the property
     * @param displayName the name to display for the property
     * @param category the category
     * @param labelsArray the labels to display in the combo box
     */
    public OptionComboBoxPropertyDescriptor(final Object id, final String displayName, PropertyTypesEnum type, final String category,
            final IOption[] options) {
        super(id, displayName, type);
        assert options != null;
        setCategory(category);
        _options = options;

        // init set
        _identifierToOptionMapping = new HashMap<String, IOption>();
        for(IOption o : _options) {
            _identifierToOptionMapping.put(o.getIdentifier(), o);
        }
    }

    /**
    * {@inheritDoc}
     */
    @Override
    public CellEditor createPropertyEditor(final Composite parent) {
        CellEditor editor = new OptionComboBoxCellEditor(parent, _options, SWT.READ_ONLY);

        if (getValidator() != null) {
            editor.setValidator(getValidator());
        }
        return editor;
    }

    /**
     * @return The <code>ComboBoxPropertyDescriptor</code> implementation of this
     * <code>IPropertyDescriptor</code> method returns the value set by
     * the <code>setProvider</code> method or, if no value has been set
     * it returns a <code>ComboBoxLabelProvider</code> created from the
     * valuesArray of this <code>ComboBoxPropertyDescriptor</code>.
     *
     * @see #setLabelProvider(ILabelProvider)
     */
    @Override
    public ILabelProvider getLabelProvider() {
        if (isLabelProviderSet()) {
            return super.getLabelProvider();
        }
        return new OptionLabelProvider();
    }

    private class OptionLabelProvider extends LabelProvider {

        @Override
        public String getText(Object element) {
            IOption option = _identifierToOptionMapping.get(element.toString());
            return option!=null ? option.toString() : "invalid";
        }

        @Override
        public Image getImage(Object element) {
            IOption option = _identifierToOptionMapping.get(element.toString());
            return option!=null ? ImageUtil.getInstance().getImage("org.csstudio.sds", "icons/Action_Enabled_Cursor.gif") : null;
        }
    }
}

