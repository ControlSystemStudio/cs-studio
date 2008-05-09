package org.csstudio.nams.application.department.distribution;

import org.csstudio.nams.common.plugin.utils.BundleActivatorUtils;
import org.csstudio.nams.common.service.ExecutionService;
import org.csstudio.nams.service.logging.declaration.Logger;
import org.csstudio.nams.service.messaging.declaration.Consumer;
import org.csstudio.nams.service.messaging.declaration.ConsumerFactoryService;
import org.csstudio.nams.service.messaging.declaration.Producer;
import org.csstudio.nams.service.messaging.declaration.ProducerFactoryService;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * <p>
 * The distribution department or more precise the activator and application class
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
 * TODO Das Verhalten des Distributors hier realisieren, das hier vorhandene Gerüst verwenden.
 *      Ähnlichkeiten zum decission dept. abgleichen, ggf. in common herausziehen.
 * 
 * @author <a href="mailto:mz@c1-wps.de">Matthias Zeimer</a>
 * 
 * @version 0.1-2008-05-09: Created.
 */
public class DistributionDepartmentActivator implements IApplication,
		BundleActivator {

	/**
	 * The plug-in ID of this bundle.
	 */
	public static final String PLUGIN_ID = "org.csstudio.nams.application.department.distribution";

	

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
	 * Gemeinsames Attribut des Activators und der Application: Fatory for
	 * creating Producers
	 */
	private static ProducerFactoryService producerFactoryService;

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
	private Producer _producer;

	
	/**
	 * Referenz auf den Thread, welcher die JMS Nachrichten anfragt. Wird
	 * genutzt um den Thread zu "interrupten".
	 */
	private Thread _receiverThread;



	/**
	 * Service für das Entscheidungsbüro um das starten der asynchronen
	 * Ausführung von Einzelaufgaben (Threads) zu kapseln.
	 */
	private static ExecutionService executionService;

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

		producerFactoryService = BundleActivatorUtils.getAvailableService(
				context, ProducerFactoryService.class);
		if (producerFactoryService == null)
			throw new RuntimeException("no consumer factory service avail!");
		
		executionService = BundleActivatorUtils.getAvailableService(context,
				ExecutionService.class);
		if (executionService == null)
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
//		logger
//				.logInfoMessage(this,
//						"Decision department application is going to be initialized...");
//
//		try {
//			logger.logInfoMessage(this,
//					"Decision department application is loading properies...");
//			Properties properties = initProperties();
//			logger.logInfoMessage(this,
//					"Decision department application is creating consumers...");
//			_consumer = consumerFactoryService
//					.createConsumer(
//							properties
//									.getProperty(PropertiesFileKeys.MESSAGING_CONSUMER_CLIENT_ID
//											.name()),
//							properties
//									.getProperty(PropertiesFileKeys.MESSAGING_CONSUMER_SOURCE_NAME
//											.name()),
//							PostfachArt.TOPIC,
//							properties
//									.getProperty(
//											PropertiesFileKeys.MESSAGING_CONSUMER_SERVER_URLS
//													.name()).split(","));
//			
//			logger.logInfoMessage(this,
//				"Decision department application is creating producers...");
//			_producer = producerFactoryService.createProducer(
//					properties.getProperty(PropertiesFileKeys.MESSAGING_PRODUCER_CLIENT_ID.name()), 
//					properties.getProperty(PropertiesFileKeys.MESSAGING_PRODUCER_DESTINATION_NAME.name()),
//					PostfachArt.TOPIC,
//					properties.getProperty(PropertiesFileKeys.MESSAGING_PRODUCER_SERVER_URLS.name()).split(","));
//			
//			logger
//					.logInfoMessage(this,
//							"Decision department application is configuring execution service...");
//			initialisiereThredGroupTypes(executionService);
//
//			logger
//					.logInfoMessage(this,
//							"Decision department application is creating decision office...");
//
//			// FIXME Das Regelwerk aus der Konfiguration bauen bzw. aus einem
//			// passenden fachlichen Service holen.
//			Regelwerk[] regelwerke = new Regelwerk[0];
//			// ---ende
//
//			alarmEntscheidungsBuero = new AlarmEntscheidungsBuero(regelwerke);
//		} catch (InitPropertiesException e) {
//			logger.logFatalMessage(this,
//					"Exception while initializing properties.", e);
//			return IApplication.EXIT_OK;
//		} catch (MessagingException e) {
//			logger.logFatalMessage(this,
//					"Exception during creation of the jms consumer or producer.", e);
//			return IApplication.EXIT_OK;
//		} catch (Exception e) { // TODO noch eine andere Exception wählen
//			logger
//					.logFatalMessage(
//							this,
//							"Exception while initializing the alarm decision department.",
////							e);
//			return IApplication.EXIT_OK;
//		}

		_receiverThread = Thread.currentThread();
		_continueWorking = true;
		logger
				.logInfoMessage(this,
						"Decision department application successfully initialized, begining work...");

		// TODO Thread zum auslesen des Ausgangskorbes...

		// start receiving Messages, runs while _continueWorking is true.
//		receiveMessagesUntilApplicationQuits(alarmEntscheidungsBuero 
//				.gibAlarmVorgangEingangskorb());

		logger.logInfoMessage(this,
				"Decision department application is shutting down...");

//		alarmEntscheidungsBuero
//				.beendeArbeitUndSendeSofortAlleOffeneneVorgaenge();

		_producer.close();
		
		logger.logInfoMessage(this,
				"Decision department application successfully shuted down.");
		return IApplication.EXIT_OK;
	}

	private void initialisiereThredGroupTypes(
			ExecutionService executionServiceToBeInitialize) {
//		executionServiceToBeInitialize
//				.registerGroup(
//						ThreadTypesOfDecisionDepartment.ABTEILUNGSLEITER,
//						new ThreadGroup(
//								ThreadTypesOfDecisionDepartment.ABTEILUNGSLEITER
//										.name()));
		// TODO here more...
	}

//	private void receiveMessagesUntilApplicationQuits(
//			Eingangskorb<Vorgangsmappe> eingangskorb) {
//		while (_continueWorking) {
//			
//			// es kommen nicht nur Alarmniachrichten rein.
//			// deshalb brauchen wir einen eigenen Message Typ 
//			// um zu entscheiden was weiter damit gemacht werden soll.
//			NAMSMessage receivedMessage = _consumer.receiveMessage();
//			
//			if(receivedMessage != null) {
//				logger.logInfoMessage(this, "Neue Nachricht erhalten: "
//						+ receivedMessage.toString());
//				_producer.sendMessage(receivedMessage);
//
//				// TODO prüfen um was für eine neue Nachricht es sich handelt
//
//				// TODO falls es sich um eine Alarmnachricht handelt
//				// Vorgangsmappe anlegen und in den Eingangskorb des Büros legen
//				// eingangskorb.ablegen(receivedMessage);
//
//				// TODO andere Nachrichten Typen behandeln
//				// steuer Nachrichten wie z.B.: "Regelwerke neu laden" 
//				// oder "einzelne Regelwerke kurzfristig deaktivieren" oder "shutdown" 
//			} else {
//				// sollte nur beim beenden der Anwendung vorkommen
//				logger.logInfoMessage(this, "null Nachricht erhalten");
//			}
//			
//			Thread.yield();
//		}
//	}

//	private Properties initProperties() throws InitPropertiesException {
//		String configFileName = System
//				.getProperty(PropertiesFileKeys.CONFIG_FILE.name());
//		if (configFileName == null) {
//			String message = "No config file avail on Property-Id \""
//					+ PropertiesFileKeys.CONFIG_FILE.name()
//					+ "\" specified.";
//			logger.logFatalMessage(this, message);
//			throw new InitPropertiesException(message);
//		}
//
//		File file = new File(configFileName);
//		if (!file.exists() && !file.canRead()) {
//			String message = "config file named \"" + file.getAbsolutePath()
//					+ "\" does not exist or is not readable.";
//			logger.logFatalMessage(this, message);
//			throw new InitPropertiesException(message);
//		}
//
//		try {
//			FileInputStream fileInputStream = new FileInputStream(file);
//			Properties properties = new Properties();
//			properties.load(fileInputStream);
//
//			// prüpfen ob die nötigen key enthalten sind
//			Set<Object> keySet = properties.keySet();
//			if (!keySet
//					.contains(PropertiesFileKeys.MESSAGING_CONSUMER_CLIENT_ID
//							.name())
//					|| !keySet
//							.contains(PropertiesFileKeys.MESSAGING_CONSUMER_SERVER_URLS
//									.name())
//					|| !keySet
//							.contains(PropertiesFileKeys.MESSAGING_CONSUMER_SOURCE_NAME
//									.name())
//					|| !keySet
//							.contains(PropertiesFileKeys.MESSAGING_PRODUCER_CLIENT_ID
//									.name())
//					|| !keySet
//							.contains(PropertiesFileKeys.MESSAGING_PRODUCER_DESTINATION_NAME
//									.name())
//					|| !keySet
//							.contains(PropertiesFileKeys.MESSAGING_PRODUCER_SERVER_URLS
//									.name())) {
//				String message = "config file named \""
//						+ file.getAbsolutePath() + "\" not valid.";
//				logger.logFatalMessage(this, message);
//				throw new Exception(message);
//			}
//
//			logger.logInfoMessage(this,
//					"Configuration properties loaded from configuration file \""
//							+ file.getAbsolutePath() + "\"");
//			return properties;
//		} catch (Exception e) {
//			throw new InitPropertiesException(e);
//		}
//
//	}

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
