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
package org.csstudio.nams.application.department.decision;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;
import java.util.Set;

import org.csstudio.nams.application.department.decision.exceptions.InitPropertiesException;
import org.csstudio.nams.common.material.AlarmNachricht;
import org.csstudio.nams.common.plugin.utils.BundleActivatorUtils;
import org.csstudio.nams.common.service.ExecutionService;
import org.csstudio.nams.service.logging.declaration.Logger;
import org.csstudio.nams.service.messaging.declaration.Consumer;
import org.csstudio.nams.service.messaging.declaration.ConsumerFactoryService;
import org.csstudio.nams.service.messaging.declaration.PostfachArt;
import org.csstudio.nams.service.messaging.exceptions.MessagingException;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * <p>
 * The decision department or more precise the activator and application class
 * to controls their life cycle.
 * </p>
 * 
 * <p>
 * <strong>Pay attention:</strong> There are always exactly two instances of
 * this class present: The <emph>bundle activator instance</emph> and the
 * <emph>bundle application instance</emph>. The communication of both is
 * hidden in this class to hide the dirty static singleton communication. This
 * is required during the instantation of extensions (like {@link IApplication})
 * is done in the framework and not by the plug in itself like it should be.
 * Cause of this all service field filled by the <emph>bundles activator</emph>
 * start operation are static to be accessible from the <emph>bundles
 * application</emph> start.
 * </p>
 * 
 * @author <a href="mailto:mz@c1-wps.de">Matthias Zeimer</a>
 * @author <a href="mailto:gs@c1-wps.de">Goesta Steen</a>
 * 
 * @version 0.1-2008-04-25: Created.
 * @version 0.1.1-2008-04-28 (MZ): Change to use {@link BundleActivatorUtils}.
 */
public class DecisionDepartmentActivator implements IApplication,
		BundleActivator {

	/**
	 * The plug-in ID of this bundle.
	 */
	public static final String PLUGIN_ID = "org.csstudio.nams.application.department.decision";

	/**
	 * Schluesel für die Property-Datei dieser Anwendung. Die Namen der Elemente ({@link Enum#name()})
	 * sind die Schlüssel der Einträge.
	 */
	private static enum PropertiesFileKeys {
		/**
		 * Id des Properties-Dateinamen in den System-Properties der VM.
		 * 
		 * Ehemalig: "configFile"
		 */
		PROPERTY_KEY_CONFIG_FILE,

		/**
		 * Der Schlüssel des Properties-Datei-Eintrages für die client id des
		 * jms message consumer(s).
		 * 
		 * Ehemalig: "CONSUMER_CLIENT_ID"
		 */
		PROPERTY_KEY_MESSAGING_CONSUMER_CLIENT_ID,

		/**
		 * Der Schlüssel des Properties-Datei-Eintrages für den source name des
		 * jms message consumer(s).
		 * 
		 * Ehemalig: "CONSUMER_SOURCE_NAME"
		 */
		PROPERTY_KEY_MESSAGING_CONSUMER_SOURCE_NAME,

		/**
		 * Der Schlüssel des Properties-Datei-Eintrages für die server urls für
		 * den jms message consumer.
		 * 
		 * Ehemalig: "CONSUMER_SERVER_URLS"
		 */
		PROPERTY_KEY_MESSAGING_CONSUMER_SERVER_URLS
	}

	/**
	 * Gemeinsames Attribut des Activators und der Application: Der Logger.
	 */
	private static Logger logger;

	/**
	 * Gemeinsames Attribut des Activators und der Application: Fatory for
	 * creating Consumers
	 */
	private static ConsumerFactoryService consumerFactoryService;

	/**
	 * Indicates if the application instance should continue working. Unused in
	 * the activator instance.
	 * 
	 * This field is set by another thread to indicate that application should
	 * shut down.
	 */
	private volatile boolean _continueWorking;

	/**
	 * wir nur von der Application benutzt
	 */
	private Consumer _consumer;

	private Thread _receiverThread;

	private static ExecutionService executorService;

	/**
	 * Starts the bundle activator instance. First Step.
	 * 
	 * @see BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		logger = BundleActivatorUtils
				.getAvailableService(context, Logger.class);

		consumerFactoryService = BundleActivatorUtils.getAvailableService(
				context, ConsumerFactoryService.class);
		if (consumerFactoryService == null)
			throw new RuntimeException("no consumer factory service avail!");

		executorService = BundleActivatorUtils.getAvailableService(context,
				ExecutionService.class);
		if (executorService == null)
			throw new RuntimeException("No executor service avail!");

		logger.logInfoMessage(this, "plugin " + PLUGIN_ID
				+ " started succesfully.");
	}

	/**
	 * Stops the bundle activator instance. Last Step.
	 * 
	 * @see BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		logger.logInfoMessage(this, "plugin " + PLUGIN_ID
				+ " stopped succesfully.");
	}

	/**
	 * Starts the bundle application instance. Second Step.
	 * 
	 * @see IApplication#start(IApplicationContext)
	 */
	public Object start(IApplicationContext context) {
		logger
				.logInfoMessage(this,
						"Decision department application is going to be initialized...");

		try {
			Properties properties = initProperties();

			// erzeugen des consumers
			_consumer = consumerFactoryService
					.createConsumer(
							properties
									.getProperty(PropertiesFileKeys.PROPERTY_KEY_MESSAGING_CONSUMER_CLIENT_ID
											.name()),
							properties
									.getProperty(PropertiesFileKeys.PROPERTY_KEY_MESSAGING_CONSUMER_SOURCE_NAME
											.name()),
							PostfachArt.TOPIC,
							properties
									.getProperty(
											PropertiesFileKeys.PROPERTY_KEY_MESSAGING_CONSUMER_SERVER_URLS
													.name()).split(","));
			// erzeuge und starte Buero
			// TODO Lade configuration, konvertiere diese und ewrzeuge die
			// bueros
			// und
			// TODO starte deren Arbeit
			// TODO use the executorService to create Threads in office
			// subsystem!

		} catch (InitPropertiesException e) {
			logger.logFatalMessage(this,
					"Exception while initializing properties.", e);
			return IApplication.EXIT_OK;
		} catch (MessagingException e) {
			logger.logFatalMessage(this,
					"Exception during creation of the jms consumer.", e);
			return IApplication.EXIT_OK;
		} catch (Exception e) { // TODO noch eine andere Exception w√§hlen
			logger
					.logFatalMessage(
							this,
							"Exception while initializing the alarm decision department.",
							e);
			return IApplication.EXIT_OK;
		}

		_receiverThread = Thread.currentThread();
		_continueWorking = true;
		logger
				.logInfoMessage(this,
						"Decision department application successfully initialized, begining work...");

		// start receiving Messages, runs while _continueWorking is true.
		receiveMessagesUntilApplicationQuits();

		// TODO stoppe bueros

		logger.logInfoMessage(this,
				"Decision department application successfully shuted down.");
		return IApplication.EXIT_OK;
	}

	private void receiveMessagesUntilApplicationQuits() {
		while (_continueWorking) {
			/*-
			 * jms.receive (tue was)
			 */

			AlarmNachricht recievedMessage = _consumer.recieveMessage();
			logger.logInfoMessage(this, "Neue Alarmnachricht erhalten: "
					+ recievedMessage.toString());

			// TODO an das B√ºro √ºbergeben!

			Thread.yield();
		}
	}

	private Properties initProperties() throws InitPropertiesException {
		String configFileName = System
				.getProperty(PropertiesFileKeys.PROPERTY_KEY_CONFIG_FILE.name());
		if (configFileName == null) {
			String message = "No config file avail on Property-Id \""
					+ PropertiesFileKeys.PROPERTY_KEY_CONFIG_FILE.name()
					+ "\" specified.";
			logger.logFatalMessage(this, message);
			throw new InitPropertiesException(message);
		}

		File file = new File(configFileName);
		if (!file.exists() && !file.canRead()) {
			String message = "config file named \"" + file.getAbsolutePath()
					+ "\" is not readable.";
			logger.logFatalMessage(this, message);
			throw new InitPropertiesException(message);
		}

		try {
			FileInputStream fileInputStream = new FileInputStream(file);
			Properties properties = new Properties();
			properties.load(fileInputStream);

			// pr√ºpfen ob die n√∂tigen key enthalten sind
			Set<Object> keySet = properties.keySet();
			if (!keySet
					.contains(PropertiesFileKeys.PROPERTY_KEY_MESSAGING_CONSUMER_CLIENT_ID
							.name())
					|| !keySet
							.contains(PropertiesFileKeys.PROPERTY_KEY_MESSAGING_CONSUMER_SERVER_URLS
									.name())
					|| !keySet
							.contains(PropertiesFileKeys.PROPERTY_KEY_MESSAGING_CONSUMER_SOURCE_NAME
									.name())) {
				throw new Exception("config file named \""
						+ file.getAbsolutePath() + "\" not valid.");
			}

			logger.logInfoMessage(this,
					"Configuration properties loaded from configuration file \""
							+ file.getAbsolutePath() + "\"");
			return properties;
		} catch (Exception e) {
			throw new InitPropertiesException(e);
		}

	}

	/**
	 * Stops the bundle application instance.Ppenultimate Step.
	 * 
	 * @see IApplication#start(IApplicationContext)
	 */
	public void stop() {
		logger.logInfoMessage(this,
				"Shuting down decision department application...");
		_continueWorking = false;
		_consumer.close();
		_receiverThread.interrupt();
	}
}
