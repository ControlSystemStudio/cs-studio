package org.csstudio.sds.model.initializers;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;

import org.csstudio.sds.SdsPlugin;
import org.csstudio.sds.internal.model.initializers.ManualSchema;
import org.csstudio.sds.internal.model.test.TestWidgetModel;
import org.csstudio.sds.model.WidgetProperty;
import org.csstudio.sds.model.initializers.ModelInitializationService.ControlSystemSchemaDescriptor;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.junit.Before;
import org.junit.Test;

/**
 * This test requires the org.csstudio.sds.test plugin to be loaded.
 * 
 * @author Stefan Hofer
 * @version $Revision$
 * 
 */
public final class ModelInitializationServiceTest {

	/**
	 * The class that implements this schema is defined in the
	 * org.csstudio.sds.test plugin.
	 */
	private static final String TEST_SCHEMA_ID = "schema.test"; //$NON-NLS-1$

	/**
	 * This is just a shortcut to the type IDs that are defined in the
	 * respective types.
	 */
	private static final Object TEST_INITIALIZER_ID = "element.test"; //$NON-NLS-1$

	/**
	 * The service under test.
	 */
	private ModelInitializationService _initService;

	/**
	 * The model to initialize.
	 */
	private TestWidgetModel _model;

	/**
	 * Ensures that necessary extension points are served.
	 */
	@Before
	public void setUp() {

		assertTrue(isTestSchemaLoaded());
		assertTrue(isTestInitializerLoaded());

		_initService = ModelInitializationService.getInstance();
		_model = new TestWidgetModel();

	}

	/**
	 * @return Whether the test preconditions are fulfilled.
	 */
	private boolean isTestSchemaLoaded() {
		IExtensionRegistry extReg = Platform.getExtensionRegistry();
		String id = SdsPlugin.EXTPOINT_WIDGET_MODEL_INITIALIZERS;
		IConfigurationElement[] confElements = extReg
				.getConfigurationElementsFor(id);

		boolean testSchemaLoaded = false;

		for (IConfigurationElement element : confElements) {
			if (element.getName().equals("controlSystemSchema")) {
				String typeId = element.getAttribute("schemaId"); //$NON-NLS-1$

				if (typeId != null) {
					if (typeId.equals(TEST_SCHEMA_ID)) {
						testSchemaLoaded = true;
					}
				}
			}
		}
		return testSchemaLoaded;
	}

	/**
	 * @return Whether the test preconditions are fulfilled.
	 */
	private boolean isTestInitializerLoaded() {
		IExtensionRegistry extReg = Platform.getExtensionRegistry();
		String id = SdsPlugin.EXTPOINT_WIDGET_MODEL_INITIALIZERS;
		IConfigurationElement[] confElements = extReg
				.getConfigurationElementsFor(id);

		boolean testInitializerLoaded = false;

		for (IConfigurationElement element : confElements) {
			if (element.getName().equals("widgetModelInitializer")) {
				String schema = element.getAttribute("schemaId"); //$NON-NLS-1$
				String typeId = element.getAttribute("widgetTypeId"); //$NON-NLS-1$

				if (typeId != null) {
					if (typeId!=null && typeId.equals(TEST_INITIALIZER_ID) && schema!=null && schema.equals(TEST_SCHEMA_ID)) {
						testInitializerLoaded = true;
					}
				}
			}
		}
		return testInitializerLoaded;
	}

	/**
	 * Test method.
	 */
	@Test
	public void testInitialize() {
		double oldValue = 2.2;
		_model.setPropertyValue(_model.getDoubleTestProperty(), oldValue);

		_initService.initialize(_model, TEST_SCHEMA_ID);
		WidgetProperty doubleTestProperty = _model
				.getProperty(TestWidgetModel.PROP_TEST);
		double newValue = (Double) doubleTestProperty.getPropertyValue();
		assertFalse(oldValue == newValue);

		_model.setPropertyValue(TestWidgetModel.PROP_TEST, oldValue);
		_initService.initialize(_model, ManualSchema.ID);
		newValue = (Double) doubleTestProperty.getPropertyValue();
		assertTrue(oldValue == newValue);
	}

	/**
	 * Test method.
	 */
	@Test
	public void testGetInitializationSchemaDescriptors() {
		HashMap<String, ControlSystemSchemaDescriptor> schemaDescriptors = _initService
				.getInitializationSchemaDescriptors();
		assertTrue(
				"at least test schema and manual schema loaded", schemaDescriptors.size() >= 2); //$NON-NLS-1$
		assertNotNull(schemaDescriptors.get(TEST_SCHEMA_ID).getDescription());
		assertNotNull(schemaDescriptors.get(TEST_SCHEMA_ID).createSchema());
	}

}
