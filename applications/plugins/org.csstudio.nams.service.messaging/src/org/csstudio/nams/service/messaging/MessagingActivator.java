/* 
 * Copyright (c) C1 WPS mbH, HAMBURG, GERMANY. All Rights Reserved.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS. 
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT
 * LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR
 * PURPOSE AND  NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR 
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, 
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 * IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE 
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, 
 * REPAIR OR CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL
 * PART OF THIS LICENSE. NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER 
 * EXCEPT UNDER THIS DISCLAIMER.
 * C1 WPS HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, 
 * ENHANCEMENTS, OR MODIFICATIONS. THE FULL LICENSE SPECIFYING FOR THE 
 * SOFTWARE THE REDISTRIBUTION, MODIFICATION, USAGE AND OTHER RIGHTS AND 
 * OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS 
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU 
 * MAY FIND A COPY AT
 * {@link http://www.eclipse.org/org/documents/epl-v10.html}.
 */
package org.csstudio.nams.service.messaging;

import org.csstudio.nams.service.messaging.declaration.ConsumerFactoryService;
import org.csstudio.nams.service.messaging.declaration.ProducerFactoryService;
import org.csstudio.nams.service.messaging.extensionPoint.ConsumerFactoryServiceFactory;
import org.csstudio.nams.service.messaging.extensionPoint.ProducerFactoryServiceFactory;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class MessagingActivator implements BundleActivator {

	private static final String NAME_OF_IMPLEMENTATION_ELEMENT_OF_EXTENSION_POINT = "implementation";
	/** The plug-in ID */
	public static final String PLUGIN_ID = "org.csstudio.nams.service.messaging";

	/**
	 * The constructor
	 */
	public MessagingActivator() {
	}

	/**
	 * @see org.eclipse.core.runtime.Plugins#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		// ConsumerFactoryServiceFactory
		String extensionPointIdConsumerServiceFactory = ConsumerFactoryServiceFactory.class
				.getName();
		IConfigurationElement[] elementsConsumerFactoryServiceFactory = Platform
				.getExtensionRegistry().getConfigurationElementsFor(
						extensionPointIdConsumerServiceFactory);

		if (elementsConsumerFactoryServiceFactory.length != 1) {
			throw new RuntimeException(
					"One and only one extension for extension point \""
							+ extensionPointIdConsumerServiceFactory
							+ "\" should be present in current runtime configuration!");
		} else {
			Object executableExtension = elementsConsumerFactoryServiceFactory[0]
					.createExecutableExtension(NAME_OF_IMPLEMENTATION_ELEMENT_OF_EXTENSION_POINT);
			if (!(executableExtension instanceof ConsumerFactoryServiceFactory)) {
				throw new RuntimeException("Only a extension of type "
						+ ConsumerFactoryServiceFactory.class.getName()
						+ " for extension point \""
						+ extensionPointIdConsumerServiceFactory
						+ "\" is valid!");
			}
			ConsumerFactoryServiceFactory factory = (ConsumerFactoryServiceFactory) executableExtension;
			context.registerService(ConsumerFactoryService.class.getName(),
					factory.createConsumerFactoryService(), null);
		}

		// ProducerFactoryServiceFactory
		String extensionPointIdProducerServiceFactory = ProducerFactoryServiceFactory.class
				.getName();
		IConfigurationElement[] elementsProducerFactoryServiceFactory = Platform
				.getExtensionRegistry().getConfigurationElementsFor(
						extensionPointIdProducerServiceFactory);

		if (elementsProducerFactoryServiceFactory.length != 1) {
			throw new RuntimeException(
					"One and only one extension for extension point \""
							+ extensionPointIdProducerServiceFactory
							+ "\" should be present in current runtime configuration!");
		} else {
			Object executableExtension = elementsProducerFactoryServiceFactory[0]
					.createExecutableExtension(NAME_OF_IMPLEMENTATION_ELEMENT_OF_EXTENSION_POINT);
			if (!(executableExtension instanceof ProducerFactoryServiceFactory)) {
				throw new RuntimeException("Only a extension of type "
						+ ProducerFactoryServiceFactory.class.getName()
						+ " for extension point \""
						+ extensionPointIdProducerServiceFactory
						+ "\" is valid!");
			}
			ProducerFactoryServiceFactory factory = (ProducerFactoryServiceFactory) executableExtension;
			context.registerService(ProducerFactoryService.class.getName(),
					factory.createProducerFactoryService(), null);
		}
	}

	/**
	 * @see org.eclipse.core.runtime.Plugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
	}
}
