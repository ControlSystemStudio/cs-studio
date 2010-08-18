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
 package org.csstudio.sds.model.initializers;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;

import org.csstudio.sds.SdsPlugin;
import org.csstudio.sds.internal.model.test.TestWidgetModel;
import org.csstudio.sds.model.initializers.WidgetInitializationService.ControlSystemSchemaDescriptor;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 * This test requires the org.csstudio.sds.test plugin to be loaded.
 * 
 * @author Stefan Hofer
 * @version $Revision: 1.5 $
 * 
 */
@Ignore("The test schema used by this test does not exist any more")
public final class WidgetInitializationServiceTest {

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
	private WidgetInitializationService _initService;

	/**
	 * Ensures that necessary extension points are served.
	 */
	@Before
	public void setUp() {

		assertTrue(isTestSchemaLoaded());
		assertTrue(isTestInitializerLoaded());

		_initService = WidgetInitializationService.getInstance();
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
	public void testGetInitializationSchemaDescriptors() {
		HashMap<String, ControlSystemSchemaDescriptor> schemaDescriptors = _initService
				.getInitializationSchemaDescriptors();
		assertTrue(
				"at least test schema and manual schema loaded", schemaDescriptors.size() >= 2); //$NON-NLS-1$
		assertNotNull(schemaDescriptors.get(TEST_SCHEMA_ID).getDescription());
		assertNotNull(schemaDescriptors.get(TEST_SCHEMA_ID).createSchema());
	}

}
