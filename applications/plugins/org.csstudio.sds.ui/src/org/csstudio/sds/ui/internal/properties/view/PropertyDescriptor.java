package org.csstudio.sds.ui.internal.properties.view;

import org.csstudio.sds.model.WidgetProperty;
import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ICellEditorValidator;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.widgets.Composite;

/**
 * Standard implementation for property descriptors.
 * <p>
 * The required attributes of property descriptors (id and display name) are
 * passed to the constructor; the optional attributes can be configured using
 * the various set methods (all have reasonable default values):
 * <ul>
 * <li><code>setDescription</code></li>
 * <li><code>setCategory</code></li>
 * <li><code>setLabelProvider</code></li>
 * <li><code>setHelpContexts</code></li>
 * </ul>
 * Subclasses should reimplement <code>getPropertyEditor</code> to provide a
 * cell editor for changing the value; otherwise the property will be
 * effectively read only.
 * </p>
 * <p>
 * There are several concrete subclasses provided in this package that cover the
 * most common cases:
 * <ul>
 * <li><code>TextPropertyDescriptor</code> - edits with a
 * <code>TextCellEditor</code></li>
 * <li><code>ComboBoxPropertyDescriptor - edits with a
 *      <code>ComboBoxCellEditor</code></code></li>
 * <li><code>ColorPropertyDescriptor - edits with a 
 *      <code>ColorCellEditor</code></code></li>
 * </ul>
 * </p>
 */
public abstract class PropertyDescriptor implements IPropertyDescriptor {

	/**
	 * The property id.
	 */
	private Object _id;

	/**
	 * The name to display for the property.
	 */
	private String _display;

	/**
	 * Category name, or <code>null</code> if none (the default).
	 */
	private String _category = null;

	/**
	 * All Java types, the properties value is compatible to.
	 */
	private Class[] _compatibleJavaTypes;

	/**
	 * Description of the property, or <code>null</code> if none (the
	 * default).
	 */
	private String _description = null;

	/**
	 * The help context ids, or <code>null</code> if none (the default).
	 */
	private Object _helpIds;

	/**
	 * The flags used to filter the property.
	 */
	private String[] _filterFlags;

	/**
	 * The object that provides the property value's text and image, or
	 * <code>null</code> if the default label provider is used (the default).
	 */
	private ILabelProvider _labelProvider = null;

	/**
	 * The object to validate the values in the cell editor, or
	 * <code>null</code> if none (the default).
	 */
	private ICellEditorValidator _validator;

	/**
	 * Indicates if the descriptor is compatible with other descriptors of this
	 * type. <code>false</code> by default.
	 */
	private boolean _incompatible = false;

	/**
	 * Creates a new property descriptor with the given id and display name.
	 * 
	 * @param id
	 *            the id
	 * @param displayName
	 *            the display name
	 */
	public PropertyDescriptor(final Object id, final String displayName) {
		Assert.isNotNull(id);
		Assert.isNotNull(displayName);
		_id = id;
		_display = displayName;
		_compatibleJavaTypes = new Class[] { Object.class };
	}

	/**
	 * Sets all Java types to which the value of a {@link WidgetProperty} is
	 * compatible to. This will be used for rule filtering in the
	 * {@link DynamicAspectsWizard}.
	 * 
	 * @param compatibleJavaTypes
	 *            an array which contains all Java types, the property value is
	 *            compatible too
	 */
	public void setCompatibleJavaTypes(Class[] compatibleJavaTypes) {
		_compatibleJavaTypes = compatibleJavaTypes;
	}

	/**
	 * Returns true if the values of a property that is described by this
	 * descriptor are compatible to the specified Java type.
	 * 
	 * @param type
	 *            the Java type
	 * @return true, if the values are compatible to the specified type, false
	 *         otherwise
	 */
	public boolean propertyValueIsCompatibleTo(Class type) {
		boolean result = false;
		
		for(Class c : _compatibleJavaTypes) {
			if(c.isAssignableFrom(type)) {
				result = true;
			}
		}
		
		return result;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * The <code>PropertyDescriptor</code> implementation of this
	 * <code>IPropertyDescriptor</code> method returns <code>null</code>.
	 * <p>
	 * Since no cell editor is returned, the property is read only.
	 */
	public CellEditor createPropertyEditor(final Composite parent) {
		return null;
	}

	/**
	 * Returns <code>true</code> if this property descriptor is to be always
	 * considered incompatible with any other property descriptor. This prevents
	 * a property from displaying during multiple selection.
	 * 
	 * @return <code>true</code> to indicate always incompatible
	 */
	protected final boolean getAlwaysIncompatible() {
		return _incompatible;
	}

	/**
	 * {@inheritDoc}
	 */
	public final String getCategory() {
		return _category;
	}

	/**
	 * {@inheritDoc}
	 */
	public final String getDescription() {
		return _description;
	}

	/**
	 * {@inheritDoc}
	 */
	public final String getDisplayName() {
		return _display;
	}

	/**
	 * {@inheritDoc}
	 */
	public final String[] getFilterFlags() {
		return _filterFlags;
	}

	/**
	 * {@inheritDoc}
	 */
	public final Object getHelpContextIds() {
		return _helpIds;
	}

	/**
	 * {@inheritDoc}
	 */
	public final Object getId() {
		return _id;
	}

	/**
	 * {@inheritDoc}
	 */
	public ILabelProvider getLabelProvider() {
		if (_labelProvider != null) {
			return _labelProvider;
		}
		return new LabelProvider();
	}

	/**
	 * Gets the ICellEditorValidator of this PropertyDescriptor.
	 * 
	 * @return ICellEditorValidator The ICellEditorValidator of this
	 *         PropertyDescriptor
	 */
	protected final ICellEditorValidator getValidator() {
		return _validator;
	}

	/**
	 * Checks if this PropertyDescriptor has a LabelProvider.
	 * 
	 * @return boolean True if this PropertyDescriptor has a LabelProvider,
	 *         false otherwise
	 */
	public final boolean isLabelProviderSet() {
		return _labelProvider != null;
	}

	/**
	 * {@inheritDoc}
	 */
	public final boolean isCompatibleWith(
			final IPropertyDescriptor anotherProperty) {
		if (getAlwaysIncompatible()) {
			return false;
		}

		// Compare id
		Object id1 = getId();
		Object id2 = anotherProperty.getId();
		if (!id1.equals(id2)) {
			return false;
		}

		// Compare Category (may be null)
		if (getCategory() == null) {
			if (anotherProperty.getCategory() != null) {
				return false;
			}
		} else {
			if (!getCategory().equals(anotherProperty.getCategory())) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Sets a flag indicating whether this property descriptor is to be always
	 * considered incompatible with any other property descriptor. Setting this
	 * flag prevents a property from displaying during multiple selection.
	 * 
	 * @param flag
	 *            <code>true</code> to indicate always incompatible
	 */
	public final void setAlwaysIncompatible(final boolean flag) {
		_incompatible = flag;
	}

	/**
	 * Sets the category for this property descriptor.
	 * 
	 * @param category
	 *            the category for the descriptor, or <code>null</code> if
	 *            none
	 * @see #getCategory
	 */
	public final void setCategory(final String category) {
		_category = category;
	}

	/**
	 * Sets the description for this property descriptor. The description should
	 * be limited to a single line so that it can be displayed in the status
	 * line.
	 * 
	 * @param description
	 *            the description, or <code>null</code> if none
	 * @see #getDescription
	 */
	public final void setDescription(final String description) {
		_description = description;
	}

	/**
	 * Sets the the filter flags for this property descriptor. The description
	 * should be limited to a single line so that it can be displayed in the
	 * status line.
	 * <p>
	 * Valid values for these flags are declared as constants on
	 * <code>IPropertySheetEntry</code>
	 * </p>
	 * 
	 * @param value
	 *            the filter flags
	 * @see #getFilterFlags
	 */
	public final void setFilterFlags(final String[] value) {
		_filterFlags = value;
	}

	/**
	 * Sets the help context id for this property descriptor.
	 * 
	 * NOTE: Help support system API's changed since 2.0 and arrays of contexts
	 * are no longer supported.
	 * <p>
	 * Thus the only valid parameter type for this method is a
	 * <code>String</code> representing a context id. The previously valid
	 * parameter types are deprecated. The plural name for this method is
	 * unfortunate.
	 * </p>
	 * 
	 * @param contextIds
	 *            the help context ids, or <code>null</code> if none
	 * @see #getHelpContextIds
	 */
	public final void setHelpContextIds(final Object contextIds) {
		_helpIds = contextIds;
	}

	/**
	 * Sets the label provider for this property descriptor.
	 * <p>
	 * If no label provider is set an instance of <code>LabelProvider</code>
	 * will be created as the default when needed.
	 * </p>
	 * 
	 * @param provider
	 *            the label provider for the descriptor, or <code>null</code>
	 *            if the default label provider should be used
	 * @see #getLabelProvider
	 */
	public final void setLabelProvider(final ILabelProvider provider) {
		_labelProvider = provider;
	}

	/**
	 * Sets the input validator for the cell editor for this property
	 * descriptor.
	 * <p>
	 * [Issue: This method should be unnecessary is the cell editor's own
	 * validator is used. ]
	 * </p>
	 * 
	 * @param validator
	 *            the cell input validator, or <code>null</code> if none
	 */
	public final void setValidator(final ICellEditorValidator validator) {
		_validator = validator;
	}

	public boolean isCompatibleWith(Class type) {
		boolean result = false;
		
		for(Class clazz : _compatibleJavaTypes) {
			if(clazz.isAssignableFrom(type)) {
				result = true;
			}
		}
		return result;
	}

}
