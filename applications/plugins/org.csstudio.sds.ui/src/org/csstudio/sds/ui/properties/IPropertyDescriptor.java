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
 package org.csstudio.sds.ui.properties;

import org.csstudio.sds.model.PropertyTypesEnum;
import org.csstudio.sds.ui.internal.properties.view.IPropertySource;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.swt.widgets.Composite;

/**
 * A descriptor for a property to be presented by a standard property sheet page (<code>PropertySheetPage</code>).
 * These descriptors originate with property sources (<code>IPropertySource</code>).
 * <p>
 * A property descriptor carries the following information:
 * <ul>
 * <li>property id (required)</li>
 * <li>display name (required)</li>
 * <li>brief description of the property (optional)</li>
 * <li>category for grouping related properties (optional)</li>
 * <li>label provider used to display the property value (optional)</li>
 * <li>cell editor for changing the property value (optional)</li>
 * <li>help context id (optional)</li>
 * </ul>
 * </p>
 * <p>
 * Clients may implement this interface to provide specialized property
 * descriptors; however, there are standard implementations declared in this
 * package that take care of the most common cases:
 * <ul>
 * <li><code>PropertyDescriptor - read-only property</code></li>
 * <li><code>TextPropertyDescriptor</code> - edits with a
 * <code>TextCellEditor</code></li>
 * <li><code>CheckboxPropertyDescriptor - edits with a
 *      CheckboxCellEditor</code></li>
 * <li><code>ComboBoxPropertyDescriptor - edits with a
 *      <code>ComboBoxCellEditor</code></code></li>
 * <li><code>ColorPropertyDescriptor - edits with a
 *      <code>ColorCellEditor</code></code></li>
 * </ul>
 * </p>
 *
 * @see IPropertySource#getPropertyDescriptors
 *
 * @author Sven Wende
 */
public interface IPropertyDescriptor {

    /**
     * Creates and returns a new cell editor for editing this property. Returns
     * <code>null</code> if the property is not editable.
     *
     * @param parent
     *            the parent widget for the cell editor
     * @return the cell editor for this property, or <code>null</code> if this
     *         property cannot be edited
     */
    CellEditor createPropertyEditor(Composite parent);

    /**
     * Returns the name of the category to which this property belongs.
     * Properties belonging to the same category are grouped together visually.
     * This localized string is shown to the user
     *
     * @return the category name, or <code>null</code> if the default category
     *         is to be used
     */
    String getCategory();

    /**
     * Returns a brief description of this property. This localized string is
     * shown to the user when this property is selected.
     *
     * @return a brief description, or <code>null</code> if none
     */
    String getDescription();

    /**
     * Returns the display name for this property. This localized string is
     * shown to the user as the name of this property.
     *
     * @return a displayable name
     */
    String getDisplayName();

    /**
     * Returns a list of filter types to which this property belongs. The user
     * is able to toggle the filters to show/hide properties belonging to a
     * filter type.
     * <p>
     * Valid values for these flags are declared as constants on
     * <code>IPropertySheetEntry</code>
     * </p>
     *
     * @return a list of filter types to which this property belongs, or
     *         <code>null</code> if none
     */
    String[] getFilterFlags();

    /**
     * Returns the help context id for this property or <code>null</code> if
     * this property has no help context id.
     * <p>
     * NOTE: Help support system API's changed since 2.0 and arrays of contexts
     * are no longer supported.
     * </p>
     * <p>
     * Thus the only valid non-<code>null</code> return type for this method
     * is a <code>String</code> representing a context id. The previously
     * valid return types are deprecated. The plural name for this method is
     * unfortunate.
     * </p>
     *
     * @return the help context id for this entry
     */
    Object getHelpContextIds();

    /**
     * Returns the id for this property. This object is used internally to
     * distinguish one property descriptor from another.
     *
     * @return the property id
     */
    Object getId();

    /**
     * Returns the label provider for this property. The label provider is used
     * to obtain the text (and possible image) for displaying the <it>value</it>
     * of this property.
     *
     * @return the label provider used to display this property
     */
    ILabelProvider getLabelProvider();

    /**
     * Returns whether this property descriptor and the given one are
     * compatible.
     * <p>
     * The property sheet uses this method during multiple selection to
     * determine whether two property descriptors with the same id are in fact
     * the same property and can be displayed as a single entry in the property
     * sheet.
     * </p>
     *
     * @param anotherProperty
     *            the other property descriptor
     * @return <code>true</code> if the property descriptors are compatible,
     *         and <code>false</code> otherwise
     */
    boolean isCompatibleWith(IPropertyDescriptor anotherProperty);

    PropertyTypesEnum getPropertyType();

}
