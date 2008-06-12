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

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.List;

import org.csstudio.ams.service.preferenceservice.declaration.PreferenceService;
import org.csstudio.ams.service.preferenceservice.declaration.PreferenceServiceJMSKeys;
import org.csstudio.nams.common.activatorUtils.AbstractBundleActivator;
import org.csstudio.nams.common.activatorUtils.BundleActivatorUtils;
import org.csstudio.nams.common.activatorUtils.OSGiBundleActivationMethod;
import org.csstudio.nams.common.activatorUtils.OSGiBundleDeactivationMethod;
import org.csstudio.nams.common.activatorUtils.OSGiService;
import org.csstudio.nams.common.activatorUtils.Required;
import org.csstudio.nams.common.material.regelwerk.Regelwerk;
import org.csstudio.nams.common.service.ExecutionService;
import org.csstudio.nams.common.service.StepByStepProcessor;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.InconsistentConfiguration;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.LocalStoreConfigurationService;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.StorageException;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.UnknownConfigurationElementError;
import org.csstudio.nams.service.history.declaration.HistoryService;
import org.csstudio.nams.service.logging.declaration.Logger;
import org.csstudio.nams.service.messaging.declaration.Consumer;
import org.csstudio.nams.service.messaging.declaration.MessagingService;
import org.csstudio.nams.service.messaging.declaration.MessagingSession;
import org.csstudio.nams.service.messaging.declaration.MultiConsumersConsumer;
import org.csstudio.nams.service.messaging.declaration.NAMSMessage;
import org.csstudio.nams.service.messaging.declaration.PostfachArt;
import org.csstudio.nams.service.messaging.declaration.Producer;
import org.csstudio.nams.service.messaging.exceptions.MessagingException;
import org.csstudio.nams.service.regelwerkbuilder.declaration.RegelwerkBuilderService;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.osgi.framework.BundleActivator;

import de.c1wps.desy.ams.alarmentscheidungsbuero.AlarmEntscheidungsBuero;
import de.c1wps.desy.ams.allgemeines.Ausgangskorb;
import de.c1wps.desy.ams.allgemeines.Eingangskorb;
import de.c1wps.desy.ams.allgemeines.Vorgangsmappe;
import de.c1wps.desy.ams.allgemeines.Vorgangsmappenkennung;

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
public class DecisionDepartmentActivator extends AbstractBundleActivator
		implements IApplication, BundleActivator {

	/**
	 * The plug-in ID of this bundle.
	 */
	public static final String PLUGIN_ID = "org.csstudio.nams.application.department.decision";

	/**
	 * Gemeinsames Attribut des Activators und der Application: Der Logger.
	 */
	private static Logger logger;

	/**
	 * Gemeinsames Attribut des Activators und der Application: Fatory for
	 * creating Consumers
	 */
	private static MessagingService messagingService;

	/**
	 * Indicates if the application instance should continue working. Unused in
	 * the activator instance.
	 * 
	 * This field is set by another thread to indicate that application should
	 * shut down.
	 */
	private volatile boolean _continueWorking;

	/**
	 * Referenz auf den Thread, welcher die JMS Nachrichten anfragt. Wird
	 * genutzt um den Thread zu "interrupten". Wird nur von der Application
	 * benutzt.
	 */
	private Thread _receiverThread;

	/**
	 * Service für das Entscheidungsbüro um das starten der asynchronen
	 * Ausführung von Einzelaufgaben (Threads) zu kapseln.
	 */
	private static ExecutionService executionService;

	private static PreferenceService preferenceService;

	private static RegelwerkBuilderService regelwerkBuilderService;

	private static HistoryService historyService;

	private MessagingSession amsMessagingSessionForConsumer;

	/**
	 * Consumer zum Lesen auf Alarmnachrichten-Quelle.
	 */
	private Consumer extAlarmConsumer;

	/**
	 * Consumer zum Lesen auf externer-Komando-Quelle.
	 */
	private Consumer extCommandConsumer;

	/**
	 * Consumer zum Lesen auf ams-Komando-Quelle.
	 */
	private Consumer amsCommandConsumer;

	/**
	 * Producer zum Senden auf ams-Zielablage (normally Distributor or
	 * MessageMinder).
	 */
	private Producer amsAusgangsProducer;

	/**
	 * Service to receive configuration-data. Used by
	 * {@link RegelwerkBuilderService}.
	 */
	private static LocalStoreConfigurationService localStoreConfigurationService;

	/**
	 * MessageSession für externe Quellen und Ziele.
	 */
	private MessagingSession extMessagingSessionForConsumer;

	/**
	 * MessageSession für ams interne Quellen und Ziele.
	 */
	private MessagingSession amsMessagingSessionForProducer;

	// private AbstractMultiConsumerMessageHandler
	// messageHandlerToRecieveUntilApplicationQuits;

	/**
	 * Starts the bundle activator instance. First Step.
	 * 
	 * @see BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	@OSGiBundleActivationMethod
	public void startBundle(@OSGiService
	@Required
	Logger injectedLogger, @OSGiService
	@Required
	MessagingService injectedMessagingService, @OSGiService
	@Required
	PreferenceService injectedPreferenceService, @OSGiService
	@Required
	RegelwerkBuilderService injectedBuilderService, @OSGiService
	@Required
	HistoryService injectedHistoryService, @OSGiService
	@Required
	LocalStoreConfigurationService injectedLocalStoreConfigurationService,
			@OSGiService
			@Required
			ExecutionService injectedExecutionService) throws Exception {

		// ** Services holen...

		// Logging Service
		logger = injectedLogger;

		logger.logInfoMessage(this, "plugin " + PLUGIN_ID
				+ " initializing Services");

		// Messaging Service
		messagingService = injectedMessagingService;

		// Preference Service (wird als konfiguration verwendet!!)
		preferenceService = injectedPreferenceService;

		// RegelwerkBuilder Service
		regelwerkBuilderService = injectedBuilderService;

		// History Service
		historyService = injectedHistoryService;

		// LocalStoreConfigurationService
		localStoreConfigurationService = injectedLocalStoreConfigurationService;

		// Execution Service
		// TODO wird noch nicht vollstaendig benutzt! Ins Dec-Office einbauen
		executionService = injectedExecutionService;

		logger.logInfoMessage(this, "plugin " + PLUGIN_ID
				+ " started succesfully.");
	}

	/**
	 * Stops the bundle activator instance. Last Step.
	 * 
	 * @see BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	@OSGiBundleDeactivationMethod
	public void stopBundle(@OSGiService
	@Required
	Logger logger) throws Exception {
		logger.logInfoMessage(this, "Plugin " + PLUGIN_ID
				+ " stopped succesfully.");
	}

	/**
	 * Starts the bundle application instance. Second Step.
	 * 
	 * @see IApplication#start(IApplicationContext)
	 */
	public Object start(IApplicationContext context) {
		_receiverThread = Thread.currentThread();
		AlarmEntscheidungsBuero alarmEntscheidungsBuero = null;
		StepByStepProcessor ausgangskorbBearbeiter = null;
		_continueWorking = true;

		logger
				.logInfoMessage(this,
						"Decision department application is going to be initialized...");

		try {
			// haben wir durch den preferenceService schon
			// TODO soll diese noch auf gültige Werte geprüft werden
			// logger.logInfoMessage(this,
			// "Decision department application is loading configuration...");

			// Properties properties = initProperties();

			// TODO clientid!! gegebenenfalls aus preferencestore holen
			logger.logInfoMessage(this,
					"Decision department application is creating consumers...");

			amsMessagingSessionForConsumer = messagingService
					.createNewMessagingSession(
							"amsConsumer",
							new String[] {
									preferenceService
											.getString(PreferenceServiceJMSKeys.P_JMS_AMS_PROVIDER_URL_1),
									preferenceService
											.getString(PreferenceServiceJMSKeys.P_JMS_AMS_PROVIDER_URL_2) });
			// TODO clientid!! gegebenenfalls aus preferencestore holen
			extMessagingSessionForConsumer = messagingService
					.createNewMessagingSession(
							"extConsumer",
							new String[] {
									preferenceService
											.getString(PreferenceServiceJMSKeys.P_JMS_EXTERN_PROVIDER_URL_1),
									preferenceService
											.getString(PreferenceServiceJMSKeys.P_JMS_EXTERN_PROVIDER_URL_2) });

			extAlarmConsumer = extMessagingSessionForConsumer
					.createConsumer(
							preferenceService
									.getString(PreferenceServiceJMSKeys.P_JMS_EXT_TOPIC_ALARM),
							PostfachArt.TOPIC);
			extCommandConsumer = extMessagingSessionForConsumer
					.createConsumer(
							preferenceService
									.getString(PreferenceServiceJMSKeys.P_JMS_EXT_TOPIC_COMMAND),
							PostfachArt.TOPIC);
			amsCommandConsumer = amsMessagingSessionForConsumer
					.createConsumer(
							preferenceService
									.getString(PreferenceServiceJMSKeys.P_JMS_AMS_TOPIC_COMMAND),
							PostfachArt.TOPIC);
			logger.logInfoMessage(this,
					"Decision department application is creating producers...");

			// TODO clientid!!
			amsMessagingSessionForProducer = messagingService
					.createNewMessagingSession(
							"amsProducer",
							new String[] { preferenceService
									.getString(PreferenceServiceJMSKeys.P_JMS_AMS_SENDER_PROVIDER_URL) });

			amsAusgangsProducer = amsMessagingSessionForProducer
					.createProducer(
							preferenceService
									.getString(PreferenceServiceJMSKeys.P_JMS_AMS_TOPIC_MESSAGEMINDER),
							PostfachArt.TOPIC);

		} catch (Throwable e) {
			logger
					.logFatalMessage(
							this,
							"Exception while initializing the alarm decision department.",
							e);
			_continueWorking = false;
		}

		/*-
		 * Vor der naechsten Zeile darf niemals ein Zugriff auf die lokale
		 * Cofigurations-DB (application-DB) erfolgen, da zuvor dort noch
		 * keine validen Daten liegen. Der folgende Aufruf blockiert
		 * solange, bis der Distributor bestaetigt, dass die Synchronisation
		 * erfolgreich ausgefuehrt wurde.
		 */
		_continueWorking = versucheZuSynchronisieren(this, logger,
				amsAusgangsProducer, amsCommandConsumer,
				localStoreConfigurationService);

		if (_continueWorking) {
			// try {
			// /*-
			// * Vor der naechsten Zeile darf niemals ein Zugriff auf die lokale
			// * Cofigurations-DB (application-DB) erfolgen, da zuvor dort noch
			// * keine validen Daten liegen. Der folgende Aufruf blockiert
			// * solange, bis der Distributor bestaetigt, dass die
			// Synchronisation
			// * erfolgreich ausgefuehrt wurde.
			// */
			// logger
			// .logInfoMessage(
			// this,
			// "Decision department application orders distributor to
			// synchronize configuration...");
			// MessagingException occuredMessagingException = null;
			// try {
			// SyncronisationsAutomat
			// .syncronisationUeberDistributorAusfueren(
			// amsAusgangsProducer, amsCommandConsumer,
			// localStoreConfigurationService);
			// } catch (MessagingException me) {
			// occuredMessagingException = me;
			// }
			// if (SyncronisationsAutomat.hasBeenCanceled()) {
			// // Abbruch bei Syncrinisation
			// logger
			// .logInfoMessage(
			// this,
			// "Decision department application was interrupted and requested to
			// shut down during synchroisation of configuration.");
			// } else {
			// if (occuredMessagingException != null) {
			// logger.logFatalMessage(this,
			// "Exception while synchronizing configuration.",
			// occuredMessagingException);
			// _continueWorking = false;
			// } else {
			try {
				logger
						.logInfoMessage(this,
								"Decision department application is configuring execution service...");
				initialisiereThredGroupTypes(executionService);

				logger
						.logInfoMessage(this,
								"Decision department application is creating decision office...");

				List<Regelwerk> alleRegelwerke = regelwerkBuilderService
						.gibAlleRegelwerke();

				alarmEntscheidungsBuero = new AlarmEntscheidungsBuero(
						alleRegelwerke.toArray(new Regelwerk[alleRegelwerke
								.size()]));
				// }
				// }
			} catch (Throwable e) {
				logger
						.logFatalMessage(
								this,
								"Exception while initializing the alarm decision department.",
								e);
				_continueWorking = false;
			}
		}

		if (_continueWorking) {
			logger
					.logInfoMessage(this,
							"******* Decision department application successfully initialized, beginning work... *******");

			// TODO Thread zum auslesen des Ausgangskorbes...

			final Ausgangskorb<Vorgangsmappe> vorgangAusgangskorb = alarmEntscheidungsBuero
					.gibAlarmVorgangAusgangskorb();
			final Eingangskorb<Vorgangsmappe> vorgangEingangskorb = alarmEntscheidungsBuero
					.gibAlarmVorgangEingangskorb();

			// Ausgangskoerbe nebenläufig abfragen
			ausgangskorbBearbeiter = new StepByStepProcessor() {
				@Override
				protected void doRunOneSingleStep() throws Throwable {
					// Vorgangsmappe vorgangZumSenden = vorgangAusgangskorb.???
					// TODO Sende Vorgangsmappe.... (Ausgangskorb erweitern eine
					// entnehme-Operation zu haben).
					// TODO Besser: Decission Office erhält einen "Ausgangskorb"
					// der in Wirklichkeit ein StandardAblagekorb ist
					// der selber auch ein Eingangskorb ist und so kann der
					// DokumentVerbraucher korrekt darauf arbeiten...
					// new DokumentVerbraucherArbeiter<Vorgangsmappe>(new
					// DokumentenBearbeiter<Vorgangsmappe>() {
					//
					// public void bearbeiteVorgang(
					// Vorgangsmappe entnehmeAeltestenEingang)
					// throws InterruptedException {
					// // TODO mache was mit
					//					
					// }
					//				
					// },
					// vorgangAusgangskorb);
				}
			};
			// ausgangskorbBearbeiter.runAsynchronous();

			// start receiving Messages, runs while _continueWorking is true.
			receiveMessagesUntilApplicationQuits(vorgangEingangskorb);
		}
		logger
				.logInfoMessage(
						this,
						"Decision department has stopped message processing and continue shutting down...");

		if (alarmEntscheidungsBuero != null) {
			alarmEntscheidungsBuero
					.beendeArbeitUndSendeSofortAlleOffeneneVorgaenge();
		}

		// Warte auf Thread für Ausgangskorb-Bearbeitung
		if (ausgangskorbBearbeiter != null
				&& ausgangskorbBearbeiter.isCurrentlyRunning()) {
			ausgangskorbBearbeiter.stopWorking();
		}

		// Alle Verbindungen schließen
		logger
				.logInfoMessage(this,
						"Decision department application is closing opened connections...");
		if (amsAusgangsProducer != null) {
			amsAusgangsProducer.close();
		}
		if (amsCommandConsumer != null) {
			amsCommandConsumer.close();
		}
		if (amsMessagingSessionForConsumer != null) {
			amsMessagingSessionForConsumer.close();
		}
		if (amsMessagingSessionForProducer != null) {
			amsMessagingSessionForProducer.close();
		}
		if (extAlarmConsumer != null) {
			extAlarmConsumer.close();
		}
		if (extCommandConsumer != null) {
			extCommandConsumer.close();
		}
		if (extMessagingSessionForConsumer != null) {
			extMessagingSessionForConsumer.close();
		}

		logger.logInfoMessage(this,
				"Decision department application successfully shuted down.");
		return IApplication.EXIT_OK;
	}

	/**
	 * Versucht via dem Distributor eine Synchronisation auszufürehn. Das
	 * Ergebnis gibt an, ob weitergearbeitet werden soll.
	 * 
	 * @param instance
	 * @param logger
	 * @param amsAusgangsProducer
	 * @param amsCommandConsumer
	 * @param localStoreConfigurationService
	 * @return {@code true} bei Erfolg, {@false} sonst.
	 */
	private static boolean versucheZuSynchronisieren(
			DecisionDepartmentActivator instance, Logger logger,
			Producer amsAusgangsProducer, Consumer amsCommandConsumer,
			LocalStoreConfigurationService localStoreConfigurationService) {
		boolean result = false;
		try {

			logger
					.logInfoMessage(
							instance,
							"Decision department application orders distributor to synchronize configuration...");
			SyncronisationsAutomat.syncronisationUeberDistributorAusfueren(
					amsAusgangsProducer, amsCommandConsumer,
					localStoreConfigurationService);
			if (!SyncronisationsAutomat.hasBeenCanceled()) {
				// Abbruch bei Syncrinisation
				result = true;
			}
		} catch (MessagingException messagingException) {
			if (SyncronisationsAutomat.hasBeenCanceled()) {
				// Abbruch bei Syncrinisation
				logger
						.logInfoMessage(
								instance,
								"Decision department application was interrupted and requested to shut down during synchroisation of configuration.");

			} else {

				logger.logFatalMessage(instance,
						"Exception while synchronizing configuration.",
						messagingException);
				result = false;

			}
		} catch (StorageException storageException) {
			logger.logFatalMessage(instance,
					"Exception while synchronizing configuration.",
					storageException);
			result = false;
		} catch (UnknownConfigurationElementError unknownConfigurationElementError) {
			logger.logFatalMessage(instance,
					"Exception while synchronizing configuration.",
					unknownConfigurationElementError);
			result = false;
		} catch (InconsistentConfiguration inconsistentConfiguration) {
			logger.logFatalMessage(instance,
					"Exception while synchronizing configuration.",
					inconsistentConfiguration);
			result = false;
		}
		return result;
	}

	private void initialisiereThredGroupTypes(
			ExecutionService executionServiceToBeInitialize) {
		executionServiceToBeInitialize
				.registerGroup(
						ThreadTypesOfDecisionDepartment.ABTEILUNGSLEITER,
						new ThreadGroup(
								ThreadTypesOfDecisionDepartment.ABTEILUNGSLEITER
										.name()));
		// executionServiceToBeInitialize
		// .registerGroup(
		// AbstractMultiConsumerMessageHandler.MultiConsumerMessageThreads.CONSUMER_THREAD,
		// new ThreadGroup(
		// AbstractMultiConsumerMessageHandler.MultiConsumerMessageThreads.CONSUMER_THREAD
		// .name()));
		// executionServiceToBeInitialize
		// .registerGroup(
		// AbstractMultiConsumerMessageHandler.MultiConsumerMessageThreads.HANDLER_THREAD,
		// new ThreadGroup(
		// AbstractMultiConsumerMessageHandler.MultiConsumerMessageThreads.HANDLER_THREAD
		// .name()));
		executionServiceToBeInitialize
				.registerGroup(
						MultiConsumersConsumer.MultiConsumerConsumerThreads.CONSUMER_THREAD,
						new ThreadGroup(
								MultiConsumersConsumer.MultiConsumerConsumerThreads.CONSUMER_THREAD
										.name()));
		// TODO Register remaining types!
	}

	/**
	 * This method is receiving Messages and handle them. It will block until
	 * _continueWorking get false.
	 * 
	 * @param eingangskorb
	 *            Der {@link Eingangskorb} to read on.
	 */
	private void receiveMessagesUntilApplicationQuits(
			final Eingangskorb<Vorgangsmappe> eingangskorb) {

		Consumer[] consumerArray = new Consumer[] { amsCommandConsumer,
				extAlarmConsumer, extCommandConsumer };

		MultiConsumersConsumer consumersConsumer = new MultiConsumersConsumer(
				logger, consumerArray, executionService);

		while (_continueWorking) {
			try {
				NAMSMessage message = consumersConsumer.receiveMessage();
				try {
					logger.logInfoMessage(this,
							"Decision department recieves a message to handle: "
									+ message.toString());
					if (message.enthaeltAlarmnachricht()) {
						try {
							eingangskorb.ablegen(new Vorgangsmappe(
									Vorgangsmappenkennung.createNew(/**
																	 * TODO Host
																	 * Service
																	 * statt new
																	 * InetAddress()
																	 * .getLocalHost
																	 * benutzen
																	 */
									InetAddress.getLocalHost(), /**
																 * TODO Calender
																 * Service statt
																 * new Date()
																 * benutzen
																 */
									new Date()), message.alsAlarmnachricht()));
						} catch (UnknownHostException e) {
							logger.logFatalMessage(this, "Host unreachable", e);
						} catch (InterruptedException e) {
							logger.logInfoMessage(this,
									"Message processing interrupted", e);
						}
					} else if (message.enthaeltSystemnachricht()) {
						if (message.alsSystemachricht()
								.istSyncronisationsAufforderung()) {
							// TODO wir müssen syncronisieren
							// 1. altes office runterfahren und noch offene
							// vorgaenge schicken
							// 2. sychronizieren
							// 3. regel neu laden
							// 4. neues office anlegen
							// 5. neues office straten
						}
					}
					// // TODO andere Nachrichten Typen behandeln
					// // steuer Nachrichten wie z.B.: "Regelwerke neu laden"
					// // oder "einzelne Regelwerke kurzfristig deaktivieren"
					// oder
					// // "shutdown"
				} finally {
					try {
						message.acknowledge();
					} catch (MessagingException e) {
						logger.logWarningMessage(this,
								"unable to ackknowlwedge message: "
										+ message.toString(), e);
					}
				}
			} catch (MessagingException e) {
				// TODO was soll hier geschehen?
				logger.logErrorMessage(this,
						"Exception during recieve of message.", e);
			} catch (InterruptedException ie) {
				logger.logInfoMessage(this, "Recieve of message interrupted",
						ie);
			}
		}

		consumersConsumer.close();

		// messageHandlerToRecieveUntilApplicationQuits = new
		// AbstractMultiConsumerMessageHandler(
		// consumerArray, executionService) {
		//
		// public void handleMessage(NAMSMessage message) {
		//				
		// }
		//
		// };
		// while (_continueWorking) {
		// try {
		// messageHandlerToRecieveUntilApplicationQuits
		// .joinMasterProcessor();
		// } catch (InterruptedException e) {
		// // moeglicher interrupt ist ohne auswirkung auf das verhalten
		// // des systems
		// logger.logDebugMessage(this,
		// "wait for receiver thred interrupted", e);
		// }
		// }
		//
		//		
		// messageHandlerToRecieveUntilApplicationQuits.beendeArbeit();
	}

	/**
	 * Stops the bundle application instance.Ppenultimate Step.
	 * 
	 * @see IApplication#start(IApplicationContext)
	 */
	public void stop() {
		logger
				.logInfoMessage(this,
						"Start to shut down decision department application on user request...");
		_continueWorking = false;
		if (SyncronisationsAutomat.isRunning()) {
			logger.logInfoMessage(this, "Canceling running syncronisation...");
			SyncronisationsAutomat.cancel();
		}
		// if (messageHandlerToRecieveUntilApplicationQuits != null) {
		// logger.logInfoMessage(this, "Stopping message recieving...");
		// messageHandlerToRecieveUntilApplicationQuits.beendeArbeit();
		// }
		logger.logInfoMessage(this, "Interrupting working thread...");
		_receiverThread.interrupt();
	}
}
