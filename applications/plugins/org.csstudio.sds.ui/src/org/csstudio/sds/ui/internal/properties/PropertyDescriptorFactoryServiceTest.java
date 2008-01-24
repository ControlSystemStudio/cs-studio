package org.csstudio.sds.ui.internal.properties;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.csstudio.sds.model.properties.PropertyTypesEnum;
import org.csstudio.sds.ui.SdsUiPlugin;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Stefan Hofer
 * @version $Revision$
 * 
 */
public class PropertyDescriptorFactoryServiceTest {

	/**
	 * The configuration elements for extension point
	 * <code>org.csstudio.sds.ui.propertyDescriptorFactories</code>.
	 */
	private IConfigurationElement[] _confElements;

	/**
	 * Test method for
	 * {@link org.csstudio.sds.ui.internal.properties.PropertyDescriptorFactoryService#getInstance()}.
	 */
	@Test
	public final void testGetInstance() {
		assertNotNull(PropertyDescriptorFactoryService.getInstance());
	}

	/**
	 * Test method for
	 * {@link org.csstudio.sds.ui.internal.properties.PropertyDescriptorFactoryService#hasPropertyDescriptorFactory(PropertyTypesEnum)}.
	 */
	@Test
	public final void testHasPropertyDescriptorFactory() {
		for (IConfigurationElement element : _confElements) {
			String typeIdString = element.getAttribute("typeId"); //$NON-NLS-1$
			
			PropertyTypesEnum typeId;
			try {
				typeId = PropertyTypesEnum.createFromPortable(typeIdString);
			} catch (Exception e) {
				// apply String as default
				typeId = PropertyTypesEnum.STRING;
			}

			assertTrue(PropertyDescriptorFactoryService.getInstance()
					.hasPropertyDescriptorFactory(typeId));
		}
	}

	/**
	 * Test method for
	 * {@link org.csstudio.sds.ui.internal.properties.PropertyDescriptorFactoryService#getPropertyDescriptorFactory(PropertyTypesEnum)}.
	 */
	@Test
	public final void testGetPropertyDescriptorFactory() {
		for (IConfigurationElement element : _confElements) {
			String typeIdString = element.getAttribute("typeId"); //$NON-NLS-1$
			
			PropertyTypesEnum typeId;
			try {
				typeId = PropertyTypesEnum.createFromPortable(typeIdString);
			} catch (Exception e) {
				// apply String as default
				typeId = PropertyTypesEnum.STRING;
			}
			assertNotNull(PropertyDescriptorFactoryService.getInstance()
					.getPropertyDescriptorFactory(typeId));
		}
	}

	/**
	 * Read extension point registry.
	 */
	@Before
	public final void setUp() {
		IExtensionRegistry extReg = Platform.getExtensionRegistry();
		String id = SdsUiPlugin.EXTPOINT_PROPERTY_DESRIPTORS_FACTORIES;
		_confElements = extReg.getConfigurationElementsFor(id);
	}

}
