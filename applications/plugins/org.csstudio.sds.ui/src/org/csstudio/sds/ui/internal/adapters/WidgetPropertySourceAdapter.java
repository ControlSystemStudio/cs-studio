package org.csstudio.sds.ui.internal.adapters;

import java.util.Map;

import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.DynamicsDescriptor;
import org.csstudio.sds.model.WidgetProperty;
import org.csstudio.sds.ui.internal.properties.PropertyDescriptorFactoryService;
import org.csstudio.sds.ui.internal.properties.view.IPropertyDescriptor;
import org.csstudio.sds.ui.internal.properties.view.IPropertySource;
import org.csstudio.sds.ui.internal.properties.view.PropertyDescriptor;
import org.csstudio.sds.ui.properties.IPropertyDescriptorFactory;

/**
 * Adapter that enriches {@link AbstractWidgetModel} so that
 * {@link IPropertySource} behaviour is supported. <br>
 * 
 * @author Sven Wende
 * @version $Revision$
 * 
 */
public final class WidgetPropertySourceAdapter implements IPropertySource {
	/**
	 * The encapsulated display widget model.
	 */
	private AbstractWidgetModel _widgetModel;

	/**
	 * Constructor.
	 * 
	 * @param widgetModel
	 *            a display widget model
	 */
	public WidgetPropertySourceAdapter(final AbstractWidgetModel widgetModel) {
		_widgetModel = widgetModel;
	}

	/**
	 * {@inheritDoc}
	 */
	public Object getEditableValue() {
		return "Properties of display widget model"; //$NON-NLS-1$
	}

	/**
	 * {@inheritDoc}
	 */
	public IPropertyDescriptor[] getPropertyDescriptors() {
		IPropertyDescriptor[] result = new IPropertyDescriptor[_widgetModel
				.getVisiblePropertyCount()];

		PropertyDescriptorFactoryService service = PropertyDescriptorFactoryService
				.getInstance();

		int i = 0;

		for (String propertyId : _widgetModel.getVisiblePropertyNames()) {
			WidgetProperty widgetProperty = _widgetModel
					.getProperty(propertyId);

			IPropertyDescriptor descriptor = null;

			// get a property descriptor for the current property´s type
			if (service.hasPropertyDescriptorFactory(widgetProperty
					.getPropertyType())) {
				final IPropertyDescriptorFactory factory = service
						.getPropertyDescriptorFactory(widgetProperty
								.getPropertyType());

				descriptor = factory.createPropertyDescriptor(propertyId,
						widgetProperty);

				if (descriptor instanceof PropertyDescriptor) {
					PropertyDescriptor pDescriptor = (PropertyDescriptor) descriptor;

					pDescriptor.setCategory(widgetProperty.getCategory()
							.toString());

					pDescriptor.setCompatibleJavaTypes(widgetProperty.getCompatibleJavaTypes());
				}

			}

			if (descriptor == null) {
				throw new IllegalArgumentException(
						"Could not create property descriptor for property "
								+ widgetProperty.getDescription() + " of type "
								+ widgetProperty.getPropertyType());
			}
			result[i] = descriptor;

			i++;
		}

		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	public Object getPropertyValue(final Object id) {
		assert id != null;
		Object result = null;

		String propertyId = id.toString();

		if (_widgetModel.hasProperty(propertyId)) {
			result = _widgetModel.getProperty(propertyId).getPropertyValue();
		}

		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isPropertySet(final Object id) {
		String propertyId = id.toString();

		if (!_widgetModel.hasProperty(propertyId)) {
			return false;
		}

		final Object defaultValue = _widgetModel.getProperty(propertyId)
				.getDefaultValue();
		final Object currentValue = _widgetModel.getProperty(propertyId)
				.getPropertyValue();

		final boolean hasDefaultValue = (defaultValue != null);
		final boolean hasCurrentValue = (currentValue != null);

		return (hasDefaultValue && !defaultValue.equals(currentValue))
				|| (!hasDefaultValue && hasCurrentValue);
	}

	/**
	 * {@inheritDoc}
	 */
	public void resetPropertyValue(final Object id) {
		String propertyId = id.toString();

		if (isPropertySet(propertyId)) {
			final Object defaultValue = _widgetModel.getProperty((String) id)
					.getDefaultValue();
			_widgetModel.setPropertyValue(propertyId, defaultValue);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void setPropertyValue(final Object id, final Object value) {
		String propertyId = id.toString();

		if (_widgetModel.hasProperty(propertyId)) {
			_widgetModel.setPropertyValue(propertyId, value);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public DynamicsDescriptor getDynamicsDescriptor(final Object id) {
		String propertyId = id.toString();

		if (_widgetModel.hasProperty(propertyId)) {
			// TODO: Laut API müssten die Properties hier gecloned werden!
			// (swende)
			return _widgetModel.getDynamicsDescriptor(propertyId);
		}

		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setDynamicsDescriptor(final Object id,
			final DynamicsDescriptor dynamicsDescriptor) {
		String propertyId = id.toString();

		if (_widgetModel.hasProperty(propertyId)) {
			DynamicsDescriptor newDescriptor = dynamicsDescriptor != null ? dynamicsDescriptor
					.clone()
					: null;
			_widgetModel.setDynamicsDescriptor(propertyId, newDescriptor);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Map<String, String> getAliases() {
		return _widgetModel.getAllInheritedAliases();
	}
}
