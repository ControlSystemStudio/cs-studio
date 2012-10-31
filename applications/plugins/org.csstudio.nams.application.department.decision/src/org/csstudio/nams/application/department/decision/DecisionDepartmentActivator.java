
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

import org.csstudio.domain.common.statistic.Collector;
import org.csstudio.nams.application.department.decision.management.Restart;
import org.csstudio.nams.application.department.decision.management.Stop;
import org.csstudio.nams.application.department.decision.office.decision.AlarmEntscheidungsBuero;
import org.csstudio.nams.application.department.decision.remote.RemotelyStoppable;
import org.csstudio.nams.application.department.decision.simplefilter.SimpleFilterWorker;
import org.csstudio.nams.common.activatorUtils.AbstractBundleActivator;
import org.csstudio.nams.common.activatorUtils.OSGiBundleActivationMethod;
import org.csstudio.nams.common.activatorUtils.OSGiBundleDeactivationMethod;
import org.csstudio.nams.common.activatorUtils.OSGiService;
import org.csstudio.nams.common.activatorUtils.Required;
import org.csstudio.nams.common.decision.Eingangskorb;
import org.csstudio.nams.common.decision.StandardAblagekorb;
import org.csstudio.nams.common.decision.Vorgangsmappe;
import org.csstudio.nams.common.decision.Vorgangsmappenkennung;
import org.csstudio.nams.common.material.SyncronisationsBestaetigungSystemNachricht;
import org.csstudio.nams.common.material.regelwerk.Regelwerk;
import org.csstudio.nams.common.material.regelwerk.WeiteresVersandVorgehen;
import org.csstudio.nams.common.service.ExecutionService;
import org.csstudio.nams.common.service.StepByStepProcessor;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.ConfigurationServiceFactory;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.DatabaseType;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.LocalStoreConfigurationService;
import org.csstudio.nams.service.history.declaration.HistoryService;
import org.csstudio.nams.service.logging.declaration.ILogger;
import org.csstudio.nams.service.messaging.declaration.Consumer;
import org.csstudio.nams.service.messaging.declaration.MessagingService;
import org.csstudio.nams.service.messaging.declaration.MessagingSession;
import org.csstudio.nams.service.messaging.declaration.MultiConsumersConsumer;
import org.csstudio.nams.service.messaging.declaration.NAMSMessage;
import org.csstudio.nams.service.messaging.declaration.PostfachArt;
import org.csstudio.nams.service.messaging.declaration.Producer;
import org.csstudio.nams.service.messaging.exceptions.MessagingException;
import org.csstudio.nams.service.preferenceservice.declaration.PreferenceService;
import org.csstudio.nams.service.preferenceservice.declaration.PreferenceServiceConfigurationKeys;
import org.csstudio.nams.service.preferenceservice.declaration.PreferenceServiceDatabaseKeys;
import org.csstudio.nams.service.preferenceservice.declaration.PreferenceServiceJMSKeys;
import org.csstudio.nams.service.preferenceservice.declaration.PreferenceServiceManagementKeys;
import org.csstudio.nams.service.regelwerkbuilder.declaration.RegelwerkBuilderService;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.osgi.framework.BundleActivator;
import org.remotercp.common.tracker.IGenericServiceListener;
import org.remotercp.service.connection.session.ISessionService;

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
 * @version 0.1.1-2008-04-28 (MZ): Change to use
 *          org.csstudio.nams.common.activatorUtils.BundleActivatorUtils.
 * @version 0.2.0-2008-06-10 (MZ): Change to use {@link AbstractBundleActivator}.
 */
public class DecisionDepartmentActivator extends AbstractBundleActivator
        implements IApplication, RemotelyStoppable, IGenericServiceListener<ISessionService> {

    private static final int DEFAULT_THREAD_COUNT = 10;

	class AusgangsKorbBearbeiter extends StepByStepProcessor {

        private final Eingangskorb<Vorgangsmappe> vorgangskorb;

        public AusgangsKorbBearbeiter(
                final Eingangskorb<Vorgangsmappe> vorgangskorb) {
            this.vorgangskorb = vorgangskorb;
        }

        @Override
        protected void doRunOneSingleStep() throws Throwable {

            try {
                final Vorgangsmappe vorgangsmappe = this.vorgangskorb
                        .entnehmeAeltestenEingang();
                if (vorgangsmappe.istAbgeschlossenDurchTimeOut()) {
                    DecisionDepartmentActivator.historyService
                            .logTimeOutForTimeBased(vorgangsmappe);
                }
                DecisionDepartmentActivator.logger.logDebugMessage(this,
                        "gesamtErgebnis: "
                                + vorgangsmappe.gibPruefliste()
                                        .gesamtErgebnis());

                if (vorgangsmappe.gibPruefliste().gesamtErgebnis() == WeiteresVersandVorgehen.VERSENDEN) {
                    // Nachricht nicht anreichern. Wird im JMSProducer
                    // gemacht
                    // Versenden
                    DecisionDepartmentActivator.logger
                            .logInfoMessage(
                                    this,
                                    "decission office ordered message to be send: \""
                                            + vorgangsmappe
                                                    .gibAusloesendeAlarmNachrichtDiesesVorganges()
                                                    .toString()
                                            + "\" [internal process id: "
                                            + vorgangsmappe.gibMappenkennung()
                                                    .toString() + "]");
                    DecisionDepartmentActivator.this.amsAusgangsProducer
                            .sendeVorgangsmappe(vorgangsmappe);
                }

            } catch (final InterruptedException e) {
                // wird zum stoppen benötigt.
                // hier muss nichts unternommen werden
            }
        }
    }

    /**
     * The plug-in ID of this bundle.
     */
    public static final String PLUGIN_ID = "org.csstudio.nams.application.department.decision";

    /**
     * Gemeinsames Attribut des Activators und der Application: Der Logger.
     */
    private static ILogger logger;

    /**
     * Gemeinsames Attribut des Activators und der Application: Fatory for
     * creating Consumers
     */
    private static MessagingService messagingService;

    /**
     * Service für das Entscheidungsbüro um das Starten der asynchronen
     * Ausführung von Einzelaufgaben (Threads) zu kapseln.
     */
    private static ExecutionService executionService;

    private static PreferenceService preferenceService;

    private static RegelwerkBuilderService regelwerkBuilderService;

    private static HistoryService historyService;

    /**
     * Service to receive configuration-data. Used by
     * {@link RegelwerkBuilderService}.
     */
    private static LocalStoreConfigurationService localStoreConfigurationService;

    private static String managementPassword;
    
    /**
     * Versucht via dem Distributor eine Synchronisation auszufürehn. Das
     * Ergebnis gibt an, ob weitergearbeitet werden soll.
     *
     * @param instance
     * @param logger
     * @param amsAusgangsProducer
     * @param amsCommandConsumer
     * @param extComendProducer
     * @param localStoreConfigurationService
     * @return {@code true} bei Erfolg, {@false} sonst.
     */
    private static boolean versucheZuSynchronisieren(
            final DecisionDepartmentActivator instance, final ILogger logger,
            final Producer amsAusgangsProducer,
            final Consumer amsCommandConsumer,
            final LocalStoreConfigurationService localStoreConfigurationService) {
        boolean result = false;
        try {

            logger
                    .logInfoMessage(
                            instance,
                            "Decision department application orders distributor to synchronize configuration...");
            SyncronisationsAutomat.syncronisationUeberDistributorAusfueren(
                    amsAusgangsProducer, amsCommandConsumer,
                    localStoreConfigurationService,
                    DecisionDepartmentActivator.historyService);
            if (!SyncronisationsAutomat.hasBeenCanceled()) {
                // Abbruch bei Syncrinisation
                result = true;
            }
        } catch (final Throwable messagingException) {
            if (SyncronisationsAutomat.hasBeenCanceled()) {
                // Abbruch bei Syncrinisation
                logger
                        .logInfoMessage(
                                instance,
                                "Decision department application was interrupted and requested to shut down during synchroisation of configuration.");
                result = false;
            } else {

                logger.logFatalMessage(instance,
                        "Exception while synchronizing configuration.",
                        messagingException);
                result = false;

            }
        }
//        } catch (final StorageException storageException) {
//            logger.logFatalMessage(instance,
//                    "Exception while synchronizing configuration.",
//                    storageException);
//            result = false;
//        } catch (final UnknownConfigurationElementError unknownConfigurationElementError) {
//            logger.logFatalMessage(instance,
//                    "Exception while synchronizing configuration.",
//                    unknownConfigurationElementError);
//            result = false;
//        } catch (final InconsistentConfigurationException inconsistentConfiguration) {
//            logger.logFatalMessage(instance,
//                    "Exception while synchronizing configuration.",
//                    inconsistentConfiguration);
//            result = false;
//        }
        return result;
    }

    /**
     * Indicates if the application instance should continue working. Unused in
     * the activator instance.
     *
     * This field may be set by another thread to indicate that application
     * should shut down.
     */
    private volatile boolean _continueWorking;

    private boolean restart;
    
    /**
     * Referenz auf den Thread, welcher die JMS Nachrichten anfragt. Wird
     * genutzt um den Thread zu "interrupten". Wird nur von der Application
     * benutzt.
     */
    private Thread _receiverThread;

    private MessagingSession amsMessagingSessionForConsumer;

    /**
     * Consumer zum Lesen auf Alarmnachrichten-Quelle.
     */
    // private Consumer extAlarmConsumer;
    private Consumer[] extAlarmConsumer;
    
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
     * MessageSession für externe Quellen und Ziele.
     */
    private MessagingSession extMessagingSessionForConsumer;

    /**
     * MessageSession für ams interne Quellen und Ziele.
     */
    private MessagingSession amsMessagingSessionForProducer;

    private AlarmEntscheidungsBuero _alarmEntscheidungsBuero;

    // private AbstractMultiConsumerMessageHandler
    // messageHandlerToRecieveUntilApplicationQuits;

    private StepByStepProcessor _ausgangskorbBearbeiter;

    private Eingangskorb<Vorgangsmappe> eingangskorbDesDecisionOffice;

    private StandardAblagekorb<Vorgangsmappe> ausgangskorbDesDecisionOfficeUndEingangskorbDesPostOffice;

    private Producer extCommandProducer;

    /** Class that collects statistic informations. Query it via XMPP. */
    private Collector ackMessages = null;
    
    private ISessionService xmppService;

    /**
     * Indicating that application is in restart process caused bz syunchr.
     * request.
     */
    private static boolean _hasReceivedSynchronizationRequest;

	private SimpleFilterWorker simpleFilterWorker;

    /**
     * Starts the bundle application instance. Second Step.
     *
     * @see IApplication#start(IApplicationContext)
     */
    @Override
    public Object start(final IApplicationContext context)
    {
        restart = false;
        
        Stop.staticInject(this);
        Stop.staticInject(logger);
        
        Restart.staticInject(this);
        Restart.staticInject(logger);

        ackMessages = new Collector();
        ackMessages.setApplication("AmsDecisionDepartment");
        ackMessages.setDescriptor("NOT acknowleged messages");
        ackMessages.setContinuousPrint(false);
        ackMessages.setContinuousPrintCount(1000.0);

        // Initialize state for normal run

        // just to make it possible to stop while start up (will be reset
        // later):
        this._receiverThread = Thread.currentThread();
        this._alarmEntscheidungsBuero = null;
        this._ausgangskorbBearbeiter = null;
        this._continueWorking = true;
        DecisionDepartmentActivator._hasReceivedSynchronizationRequest = false;

        DecisionDepartmentActivator.logger
                .logInfoMessage(this,
                        "Decision department application is going to be initialized...");

        // XMPP login
        AbstractBundleActivator.getDefault().addSessionServiceListener(this);
        
        configureExecutionService();

        createMessagingConsumer();

        if (this._continueWorking) {
            createMessagingProducer();
        }

        do {
            if (DecisionDepartmentActivator._hasReceivedSynchronizationRequest) {
                /*-
                 * If a syncronization-message has been received the flag this._continueWorking has been set to false.
                 * This is required to stop the receving of other messages. To reinitialiye and start working with a
                 * new configuration the flag has to be reset to true.
                 */
                this._continueWorking = true;
                DecisionDepartmentActivator.logger
                        .logInfoMessage(this,
                                "Decision department application is going to be re-initialized...");
            }
            if (this._continueWorking) {
                /*-
                 * Vor der naechsten Zeile darf niemals ein Zugriff auf die lokale
                 * Cofigurations-DB (application-DB) erfolgen, da zuvor dort noch
                 * keine validen Daten liegen. Der folgende Aufruf blockiert
                 * solange, bis der Distributor bestaetigt, dass die Synchronisation
                 * erfolgreich ausgefuehrt wurde.
                 */
                this._continueWorking = DecisionDepartmentActivator
                        .versucheZuSynchronisieren(
                                this,
                                DecisionDepartmentActivator.logger,
                                this.amsAusgangsProducer,
                                this.amsCommandConsumer,
                                DecisionDepartmentActivator.localStoreConfigurationService);
            }

            if (this._continueWorking
                    && DecisionDepartmentActivator._hasReceivedSynchronizationRequest) {
                DecisionDepartmentActivator.logger.logInfoMessage(this,
                        "Attempt to commit synchronization after restart...");
                try {
                    this.extCommandProducer
                            .sendeSystemnachricht(new SyncronisationsBestaetigungSystemNachricht());
                    DecisionDepartmentActivator.logger.logInfoMessage(this,
                            "Commiting synchronization after restart done.");
                } catch (final MessagingException e) {
                    DecisionDepartmentActivator.logger
                            .logFatalMessage(
                                    this,
                                    "Exception while sending synchronize confirm message.",
                                    e);
                    this._continueWorking = false;
                }
            }

            /*-
             * if a synchronize request has been received here it is handled.
             */
            DecisionDepartmentActivator._hasReceivedSynchronizationRequest = false;

            if (this._continueWorking) {
                createDecisionOffice();
            }
            if (this._continueWorking) {
            	createSimpleFilterWorker();
            }

            if (this._continueWorking) {
                performNormalWork();
            }

        } while (DecisionDepartmentActivator._hasReceivedSynchronizationRequest);

        DecisionDepartmentActivator.logger
                .logInfoMessage(
                        this,
                        "Decision department has stopped message processing and continue shutting down...");

        closeDecissionOffice();

        closeMessagingConnections();

        if (xmppService != null) {
            xmppService.disconnect();
            DecisionDepartmentActivator.logger.logInfoMessage(this,
                       "XMPP connection disconnected.");
        }

        DecisionDepartmentActivator.logger.logInfoMessage(this,
                "Decision department application successfully shuted down.");
        
        Integer exitCode = IApplication.EXIT_OK;
        if (this.restart) {
            exitCode = IApplication.EXIT_RESTART;
        }
        return exitCode;
    }
    
    @Override
    public void bindService(ISessionService sessionService) {
        final IPreferencesService pref = Platform.getPreferencesService();
        final String xmppServer = pref.getString(DecisionDepartmentActivator.PLUGIN_ID, "xmppServer", "krynfs.desy.de", null);
        final String xmppUser = pref.getString(DecisionDepartmentActivator.PLUGIN_ID, "xmppUser", "anonymous", null);
        final String xmppPassword = pref.getString(DecisionDepartmentActivator.PLUGIN_ID, "xmppPassword", "anonymous", null);
        
        try {
            sessionService.connect(xmppUser, xmppPassword, xmppServer);
            xmppService = sessionService;
        } catch (Exception e) {
            xmppService = null;
            DecisionDepartmentActivator.logger
            .logWarningMessage(this,
                    "XMPP connection is not possible: " + e.getMessage());
        }
    }
    
    @Override
    public void unbindService(ISessionService service) {
        // Nothing to do here
    }

    private void closeMessagingConnections() {
        // Alle Verbindungen schließen
        DecisionDepartmentActivator.logger
                .logInfoMessage(this,
                        "Decision department application is closing opened connections...");
        if ((this.amsAusgangsProducer != null)
                && !this.amsAusgangsProducer.isClosed()) {
            this.amsAusgangsProducer.tryToClose();
        }
        if ((this.amsCommandConsumer != null)
                && !this.amsCommandConsumer.isClosed()) {
            this.amsCommandConsumer.close();
        }
        if ((this.amsMessagingSessionForConsumer != null)
                && !this.amsMessagingSessionForConsumer.isClosed()) {
            this.amsMessagingSessionForConsumer.close();
        }
        if ((this.amsMessagingSessionForProducer != null)
                && !this.amsMessagingSessionForProducer.isClosed()) {
            this.amsMessagingSessionForProducer.close();
        }
        
        for(Consumer c : extAlarmConsumer) {
            if (c != null) {
                if(c.isClosed() == false) {
                    c.close();
                }
            }
        }
        
        if ((this.extCommandConsumer != null)
                && !this.extCommandConsumer.isClosed()) {
            this.extCommandConsumer.close();
        }
        
        if ((this.extMessagingSessionForConsumer != null)
                && !this.extMessagingSessionForConsumer.isClosed()) {
            this.extMessagingSessionForConsumer.close();
        }
    }

    private void closeDecissionOffice() {
        if (this._alarmEntscheidungsBuero != null) {
            this._alarmEntscheidungsBuero
                    .beendeArbeitUndSendeSofortAlleOffeneneVorgaenge();
        }

        // Warte auf Thread für Ausgangskorb-Bearbeitung
        if ((this._ausgangskorbBearbeiter != null)
                && this._ausgangskorbBearbeiter.isCurrentlyRunning()) {
            // FIXME Warte bis korb leer ist.
            this._ausgangskorbBearbeiter.stopWorking();
        }
    }

    private void performNormalWork() {
        DecisionDepartmentActivator.logger
                .logInfoMessage(
                        this,
                        "******* Decision department application successfully initialized, beginning work... *******");

        // Ausgangskoerbe nebenläufig abfragen
        this._ausgangskorbBearbeiter = new AusgangsKorbBearbeiter(
                this.ausgangskorbDesDecisionOfficeUndEingangskorbDesPostOffice);

        DecisionDepartmentActivator.executionService.executeAsynchronsly(
                ThreadTypesOfDecisionDepartment.AUSGANGSKORBBEARBEITER,
                this._ausgangskorbBearbeiter);

        // start receiving Messages, runs while _continueWorking is
        // true.
        this
                .receiveMessagesUntilApplicationQuits(this.eingangskorbDesDecisionOffice);
    }

    private void createDecisionOffice() {
        try {
            DecisionDepartmentActivator.logger
                    .logInfoMessage(this,
                            "Decision department application is creating decision office...");

            final List<Regelwerk> alleRegelwerke = DecisionDepartmentActivator.regelwerkBuilderService
                    .gibKomplexeRegelwerke();

            DecisionDepartmentActivator.logger.logDebugMessage(this,
                    "alleRegelwerke size: " + alleRegelwerke.size());
            for (final Regelwerk regelwerk : alleRegelwerke) {
                DecisionDepartmentActivator.logger.logDebugMessage(this,
                        regelwerk.toString());
            }

            this.eingangskorbDesDecisionOffice = new StandardAblagekorb<Vorgangsmappe>();
            this.ausgangskorbDesDecisionOfficeUndEingangskorbDesPostOffice = new StandardAblagekorb<Vorgangsmappe>();

            
            final IPreferencesService pref = Platform.getPreferencesService();
            int threadCount = pref.getInt(DecisionDepartmentActivator.PLUGIN_ID, PreferenceServiceConfigurationKeys.FILTER_THREAD_COUNT.getKey(), DEFAULT_THREAD_COUNT, null);
            this._alarmEntscheidungsBuero = new AlarmEntscheidungsBuero(
                    DecisionDepartmentActivator.executionService,
                    alleRegelwerke
                            .toArray(new Regelwerk[alleRegelwerke.size()]),
                    this.eingangskorbDesDecisionOffice,
                    this.ausgangskorbDesDecisionOfficeUndEingangskorbDesPostOffice,
                    threadCount);
        } catch (final Throwable e) {
            DecisionDepartmentActivator.logger
                    .logFatalMessage(
                            this,
                            "Exception while initializing the alarm decision department.",
                            e);
            this._continueWorking = false;
        }
    }

	private void createSimpleFilterWorker() {
		try {
			simpleFilterWorker = new SimpleFilterWorker(localStoreConfigurationService
					.getEntireFilterConfiguration().gibAlleFilter(),
					ausgangskorbDesDecisionOfficeUndEingangskorbDesPostOffice, DecisionDepartmentActivator.logger);
		} catch (final Throwable e) {
			DecisionDepartmentActivator.logger
					.logFatalMessage(
							this,
							"Exception while initializing the alarm decision department.",
							e);
			this._continueWorking = false;
		}
	}

	private void createMessagingProducer() {
        try {

            DecisionDepartmentActivator.logger.logInfoMessage(this,
                    "Decision department application is creating producers...");

            // FIXM E(done) clientid!!
            final String amsSenderProviderUrl = DecisionDepartmentActivator.preferenceService
                    .getString(PreferenceServiceJMSKeys.P_JMS_AMS_SENDER_PROVIDER_URL);
            DecisionDepartmentActivator.logger.logDebugMessage(this,
                    "PreferenceServiceJMSKeys.P_JMS_AMS_SENDER_PROVIDER_URL = "
                            + amsSenderProviderUrl);
            this.amsMessagingSessionForProducer = DecisionDepartmentActivator.messagingService
                    .createNewMessagingSession(
                            preferenceService
                                    .getString(PreferenceServiceJMSKeys.P_JMS_AMS_TSUB_DD_OUTBOX),
                            new String[] { amsSenderProviderUrl });

            final String amsAusgangsTopic = DecisionDepartmentActivator.preferenceService
                    .getString(PreferenceServiceJMSKeys.P_JMS_AMS_TOPIC_DD_OUTBOX);
            DecisionDepartmentActivator.logger.logDebugMessage(this,
                    "PreferenceServiceJMSKeys.P_JMS_AMS_TOPIC_DD_OUTBOX(AusgangsTopic) = "
                            + amsAusgangsTopic);
            this.amsAusgangsProducer = this.amsMessagingSessionForProducer
                    .createProducer(amsAusgangsTopic, PostfachArt.TOPIC);

        } catch (final Throwable e) {
            DecisionDepartmentActivator.logger
                    .logFatalMessage(
                            this,
                            "Exception while initializing the alarm decision department.",
                            e);
            this._continueWorking = false;
        }
    }

    private void createMessagingConsumer() {
        try {
            DecisionDepartmentActivator.logger.logInfoMessage(this,
                    "Decision department application is creating consumers...");

            final String amsProvider1 = DecisionDepartmentActivator.preferenceService
                    .getString(PreferenceServiceJMSKeys.P_JMS_AMS_PROVIDER_URL_1);
            final String amsProvider2 = DecisionDepartmentActivator.preferenceService
                    .getString(PreferenceServiceJMSKeys.P_JMS_AMS_PROVIDER_URL_2);

            DecisionDepartmentActivator.logger.logDebugMessage(this,
                    "PreferenceServiceJMSKeys.P_JMS_AMS_PROVIDER_URL_1 = "
                            + amsProvider1);
            DecisionDepartmentActivator.logger.logDebugMessage(this,
                    "PreferenceServiceJMSKeys.P_JMS_AMS_PROVIDER_URL_2 = "
                            + amsProvider2);

            // FIXM E(done) clientid!! gegebenenfalls aus preferencestore
            // holen
            this.amsMessagingSessionForConsumer = DecisionDepartmentActivator.messagingService
                    .createNewMessagingSession(
                            preferenceService
                                    .getString(PreferenceServiceJMSKeys.P_JMS_AMS_TSUB_COMMAND_DECISSION_DEPARTMENT),
                            new String[] { amsProvider1, amsProvider2 });
            final String extProvider1 = DecisionDepartmentActivator.preferenceService
                    .getString(PreferenceServiceJMSKeys.P_JMS_EXTERN_PROVIDER_URL_1);
            final String extProvider2 = DecisionDepartmentActivator.preferenceService
                    .getString(PreferenceServiceJMSKeys.P_JMS_EXTERN_PROVIDER_URL_2);
            DecisionDepartmentActivator.logger.logDebugMessage(this,
                    "PreferenceServiceJMSKeys.P_JMS_EXTERN_PROVIDER_URL_1 = "
                            + extProvider1);
            DecisionDepartmentActivator.logger.logDebugMessage(this,
                    "PreferenceServiceJMSKeys.P_JMS_EXTERN_PROVIDER_URL_2 = "
                            + extProvider2);
            this.extMessagingSessionForConsumer = DecisionDepartmentActivator.messagingService
                    .createNewMessagingSession(
                            preferenceService
                                    .getString(PreferenceServiceJMSKeys.P_JMS_EXT_TSUB_ALARM),
                            new String[] { extProvider1, extProvider2 });

            final String extAlarmTopic = DecisionDepartmentActivator.preferenceService
                    .getString(PreferenceServiceJMSKeys.P_JMS_EXT_TOPIC_ALARM);
            DecisionDepartmentActivator.logger.logDebugMessage(this,
                    "PreferenceServiceJMSKeys.P_JMS_EXT_TOPIC_ALARM = "
                            + extAlarmTopic);

            // extAlarmTopic may contain a comma-seperated list of topics
            String[] topicList = extAlarmTopic.split(",");

            // FIXME gs,mz 2008-09-11 make durable when global alarm server
            // suports durable
            extAlarmConsumer = new Consumer[topicList.length];
            for(int i = 0;i < topicList.length;i++) {
                this.extAlarmConsumer[i] = this.extMessagingSessionForConsumer
                        .createConsumer(topicList[i], PostfachArt.TOPIC);
            }

            // FIXME gs,mz 2008-09-11: Wieder einkommentieren - Für
            // Testbetrieb
            // beim Desy heruasgenommen, damit Comands nur lokal gelesen
            // werden,
            // BEGIN
            // this.extCommandConsumer = this.extMessagingSessionForConsumer
            // .createConsumer(
            // DecisionDepartmentActivator.preferenceService
            // .getString(PreferenceServiceJMSKeys.P_JMS_EXT_TOPIC_COMMAND),
            // PostfachArt.TOPIC);

            // ext wird durch ams Server ersetzt
            this.extCommandConsumer = this.amsMessagingSessionForConsumer
                    .createConsumer(
                            DecisionDepartmentActivator.preferenceService
                                    .getString(PreferenceServiceJMSKeys.P_JMS_EXT_TOPIC_COMMAND),
                            PostfachArt.TOPIC_DURABLE);

            this.extCommandProducer = this.amsMessagingSessionForConsumer
                    .createProducer(
                            DecisionDepartmentActivator.preferenceService
                                    .getString(PreferenceServiceJMSKeys.P_JMS_EXT_TOPIC_COMMAND),
                            PostfachArt.TOPIC);

            // END

            final String amsCommandTopic = DecisionDepartmentActivator.preferenceService
                    .getString(PreferenceServiceJMSKeys.P_JMS_AMS_TOPIC_COMMAND);
            DecisionDepartmentActivator.logger.logDebugMessage(this,
                    "PreferenceServiceJMSKeys.P_JMS_AMS_TOPIC_COMMAND = "
                            + amsCommandTopic);
            this.amsCommandConsumer = this.amsMessagingSessionForConsumer
                    .createConsumer(amsCommandTopic, PostfachArt.TOPIC_DURABLE);
        } catch (final Throwable e) {
            DecisionDepartmentActivator.logger
                    .logFatalMessage(
                            this,
                            "Exception while initializing the alarm decision department.",
                            e);
            this._continueWorking = false;
        }
    }

    private void configureExecutionService() {
        DecisionDepartmentActivator.logger
                .logInfoMessage(this,
                        "Decision department application is configuring execution service...");
        this
                .initialisiereThredGroupTypes(DecisionDepartmentActivator.executionService);
    }

    /**
     * Starts the bundle activator instance. First Step.
     *
     * @see BundleActivator#start(org.osgi.framework.BundleContext)
     */
    @OSGiBundleActivationMethod
    public void startBundle(@OSGiService @Required final ILogger injectedLogger,
                            @OSGiService @Required final MessagingService injectedMessagingService,
                            @OSGiService @Required final PreferenceService injectedPreferenceService,
                            @OSGiService @Required final RegelwerkBuilderService injectedBuilderService,
                            @OSGiService @Required final HistoryService injectedHistoryService,
                            @OSGiService @Required final ConfigurationServiceFactory injectedConfigurationServiceFactory,
                            @OSGiService @Required final ExecutionService injectedExecutionService) throws Exception {

        // ** Services holen...

        // Logging Service
        DecisionDepartmentActivator.logger = injectedLogger;

        DecisionDepartmentActivator.logger.logInfoMessage(this, "plugin "
                + DecisionDepartmentActivator.PLUGIN_ID
                + " initializing Services");

        // Messaging Service
        DecisionDepartmentActivator.messagingService = injectedMessagingService;

        // Preference Service (wird als konfiguration verwendet!!)
        DecisionDepartmentActivator.preferenceService = injectedPreferenceService;

        // RegelwerkBuilder Service
        DecisionDepartmentActivator.regelwerkBuilderService = injectedBuilderService;

        // History Service
        DecisionDepartmentActivator.historyService = injectedHistoryService;

        // LocalStoreConfigurationService
        final DatabaseType dbType = DatabaseType.valueOf(preferenceService.getString(PreferenceServiceDatabaseKeys.P_APP_DATABASE_TYPE));

        DecisionDepartmentActivator.localStoreConfigurationService = injectedConfigurationServiceFactory
                .getConfigurationService(
                        DecisionDepartmentActivator.preferenceService
                                .getString(PreferenceServiceDatabaseKeys.P_APP_DATABASE_CONNECTION),
                        dbType,
                        DecisionDepartmentActivator.preferenceService
                                .getString(PreferenceServiceDatabaseKeys.P_APP_DATABASE_USER),
                        DecisionDepartmentActivator.preferenceService
                                .getString(PreferenceServiceDatabaseKeys.P_APP_DATABASE_PASSWORD));

        DecisionDepartmentActivator.executionService = injectedExecutionService;

        DecisionDepartmentActivator.managementPassword = DecisionDepartmentActivator.preferenceService
        .getString(PreferenceServiceManagementKeys.P_AMS_MANAGEMENT_PASSWORD);
        if(managementPassword == null) {
            managementPassword = "";
        }

        Stop.staticInject(DecisionDepartmentActivator.logger);

        DecisionDepartmentActivator.logger.logInfoMessage(this, "plugin "
                + DecisionDepartmentActivator.PLUGIN_ID
                + " started succesfully.");
    }

    /**
     * Stops the bundle application instance.Ppenultimate Step.
     * 
     * FIXME: Diese Methode darf NICHT von der Anwendung selber aufgerufen werden.
     *        Sie wird vom Framework aufgerufen, wenn z.B. die Anwendung von Außen
     *        beendet werden soll oder das Framework herunterfährt.
     *         
     * @see IApplication#start(IApplicationContext)
     */
    @Override
    public void stop() {
        // Nothing to do here
    }

    /**
     * Stops the bundle activator instance. Last Step.
     *
     * @see BundleActivator#stop(org.osgi.framework.BundleContext)
     */
    @OSGiBundleDeactivationMethod
    public void stopBundle(@OSGiService
    @Required
    final ILogger logger) throws Exception {
        logger.logInfoMessage(this, "Plugin "
                + DecisionDepartmentActivator.PLUGIN_ID
                + " stopped succesfully.");
    }

    private void initialisiereThredGroupTypes(
            final ExecutionService executionServiceToBeInitialize) {
        executionServiceToBeInitialize
                .registerGroup(
                        ThreadTypesOfDecisionDepartment.ABTEILUNGSLEITER,
                        new ThreadGroup(
                                ThreadTypesOfDecisionDepartment.ABTEILUNGSLEITER
                                        .name()));
        executionServiceToBeInitialize.registerGroup(
                ThreadTypesOfDecisionDepartment.AUSGANGSKORBBEARBEITER,
                new ThreadGroup(
                        ThreadTypesOfDecisionDepartment.AUSGANGSKORBBEARBEITER
                                .name()));
        executionServiceToBeInitialize.registerGroup(
                ThreadTypesOfDecisionDepartment.SACHBEARBEITER,
                new ThreadGroup(ThreadTypesOfDecisionDepartment.SACHBEARBEITER
                        .name()));
        executionServiceToBeInitialize.registerGroup(
                ThreadTypesOfDecisionDepartment.TERMINASSISTENZ,
                new ThreadGroup(ThreadTypesOfDecisionDepartment.TERMINASSISTENZ
                        .name()));

        executionServiceToBeInitialize
                .registerGroup(
                        MultiConsumersConsumer.MultiConsumerConsumerThreads.CONSUMER_THREAD,
                        new ThreadGroup(
                                MultiConsumersConsumer.MultiConsumerConsumerThreads.CONSUMER_THREAD
                                        .name()));
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

        //      final Consumer[] consumerArray = new Consumer[] {
        //      this.amsCommandConsumer, this.extAlarmConsumer,
        //
        //      this.extCommandConsumer
        //
        //};

        final Consumer[] consumerArray = 
            new Consumer[this.extAlarmConsumer.length + 2];
        
        consumerArray[0] = this.amsCommandConsumer;
        for(int i = 0;i < extAlarmConsumer.length;i++) {
            consumerArray[i + 1] = this.extAlarmConsumer[i];
        }
        
        consumerArray[consumerArray.length - 1] = this.extCommandConsumer;
        
        final MultiConsumersConsumer consumersConsumer = new MultiConsumersConsumer(
                DecisionDepartmentActivator.logger, consumerArray,
                DecisionDepartmentActivator.executionService);

        this._receiverThread = Thread.currentThread();
        while (this._continueWorking) {
            try {
                final NAMSMessage message = consumersConsumer.receiveMessage();
                ackMessages.incrementValue();
                try {
                    DecisionDepartmentActivator.logger.logInfoMessage(this,
                            "Decision department recieves a message to handle: "
                                    + message.toString());
                    if (message.enthaeltAlarmnachricht()) {
                        try {
                            eingangskorb.ablegen(new Vorgangsmappe(
                                    Vorgangsmappenkennung.createNew(
                                    InetAddress.getLocalHost(),
                                    new Date()), message.alsAlarmnachricht()));
                            simpleFilterWorker.bearbeiteAlarmnachricht(message.alsAlarmnachricht());
                        } catch (final UnknownHostException e) {
                            DecisionDepartmentActivator.logger.logFatalMessage(
                                    this, "Host unreachable", e);
                        } catch (final InterruptedException e) {
                            DecisionDepartmentActivator.logger.logInfoMessage(
                                    this, "Message processing interrupted", e);
                        }
                    } else if (message.enthaeltSystemnachricht()) {
                        if (message.alsSystemachricht()
                                .istSyncronisationsAufforderung()) {
                            if (!DecisionDepartmentActivator._hasReceivedSynchronizationRequest) {
                                consumersConsumer.close();
                                DecisionDepartmentActivator.historyService
                                        .logReceivedStartReplicationMessage();
                                DecisionDepartmentActivator._hasReceivedSynchronizationRequest = true;
                                DecisionDepartmentActivator.logger
                                        .logInfoMessage(
                                                this,
                                                "Decission department received re-synchronization request, going to be re-initialized...");

                                message.acknowledge();

                                // Office stoppen...
                                closeDecissionOffice();

                                // cancel receive wihout stopping application!
                                return ;
                            }
                        }
                    }
                } finally {
                    try {
                        message.acknowledge();
                        ackMessages.decrementValue();
                    } catch (final MessagingException e) {
                        ackMessages.incrementValue();
                        DecisionDepartmentActivator.logger.logWarningMessage(
                                this, "unable to ackknowlwedge message: "
                                        + message.toString(), e);
                    }
                }
            } catch (final MessagingException e) {
                // TODO was soll hier geschehen?
                DecisionDepartmentActivator.logger.logErrorMessage(this,
                        "Exception during recieve of message.", e);
            } catch (final InterruptedException ie) {
                DecisionDepartmentActivator.logger.logInfoMessage(this,
                        "Recieving of message has been interrupted", ie);
            }
        }

        consumersConsumer.close();
    }

    /**
     *
     */
    @Override
    public synchronized void stopRemotely(final ILogger logger) {
        
        DecisionDepartmentActivator.logger .logInfoMessage(this,
                "Start to shut down decision department application on user request...");
        this._continueWorking = false;
        this.restart = false;
        if (SyncronisationsAutomat.isRunning()) {
            DecisionDepartmentActivator.logger.logInfoMessage(this, "Canceling running syncronisation...");
            SyncronisationsAutomat.cancel();
        }

        DecisionDepartmentActivator.logger.logInfoMessage(this, "Interrupting working thread...");

        this._receiverThread.interrupt();

        logger.logDebugMessage(this, "DecisionDepartmentActivator.stopRemotely(): After this.stop()");
    }

    @Override
    public synchronized void restartRemotly(final ILogger logger) {
        
        DecisionDepartmentActivator.logger .logInfoMessage(this,
                "Begin to restart decision department application on user request...");
        this._continueWorking = false;
        this.restart = true;
        if (SyncronisationsAutomat.isRunning()) {
            DecisionDepartmentActivator.logger.logInfoMessage(this, "Canceling running syncronisation...");
            SyncronisationsAutomat.cancel();
        }

        DecisionDepartmentActivator.logger.logInfoMessage(this, "Interrupting working thread...");

        this._receiverThread.interrupt();

        logger.logDebugMessage(this, "DecisionDepartmentActivator.stopRemotely(): After this.stop()");
    }

    /**
     *
     * @return The management password
     */
    @Override
    public synchronized String getPassword() {
        return DecisionDepartmentActivator.managementPassword;
    }
}
