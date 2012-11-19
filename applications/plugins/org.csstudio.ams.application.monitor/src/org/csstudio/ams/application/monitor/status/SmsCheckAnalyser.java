
/*
 * Copyright (c) 2012 Stiftung Deutsches Elektronen-Synchrotron,
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
 *
 * $Id: DesyKrykCodeTemplates.xml,v 1.7 2010/04/20 11:43:22 bknerr Exp $
 */

package org.csstudio.ams.application.monitor.status;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import org.csstudio.ams.application.monitor.IRemoteService;
import org.csstudio.ams.application.monitor.check.NewSmsCheckProcessor;
import org.csstudio.ams.application.monitor.internal.AmsMonitorPreference;
import org.csstudio.ams.application.monitor.service.MonitorMessageSender;
import org.csstudio.ams.application.monitor.service.XmppRemoteService;
import org.csstudio.ams.application.monitor.util.CommonMailer;
import org.remotercp.service.connection.session.ISessionService;
import org.slf4j.Logger;

/**
 * @author mmoeller
 * @version 1.0
 * @since 16.05.2012
 */
public class SmsCheckAnalyser implements ICheckAnalyser {

    private Logger logger;
    private NewSmsCheckProcessor checkProcessor;
    private ISessionService xmppService;

    public SmsCheckAnalyser(NewSmsCheckProcessor o, Logger l, ISessionService service) {
        checkProcessor = o;
        logger = l;
        xmppService = service;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void analyseCheck() {
        
        logger.info("Result of SMS Delivery worker check: {}", checkProcessor.getCurrentCheckStatusInfo().toString());
        
        if (checkProcessor.isOk()) {
            
            logger.info("SmsDeliveryWorker is working.");
            if (checkProcessor.wasNotificationSent()) {
                CheckStatusInfo csi = checkProcessor.getPreviousCheckStatusInfo();
                if (csi != null) {
                    if (checkProcessor.wasErrorSent()) {
                        MonitorMessageSender.sendErrorSms("SmsDeliveryWorker switched from "
                                                          + csi.getCheckStatus() + " to OK.");
                    } else if (checkProcessor.wasWarnSent()) {
                        MonitorMessageSender.sendWarnMail("SmsDeliveryWorker switched from "
                                                          + csi.getCheckStatus() + " to OK.");
                    }
                }
                checkProcessor.clearNotificationState();
            }

            if (checkProcessor.previousCheckWasRestarted()) {
                logger.info("Notification will be send to the e-Logbook");
                String value = AmsMonitorPreference.RESTART_MAIL_LIST.getValue();
                String[] recipients = value.split(",");
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm.ss");
                CommonMailer.sendMultiMail("smtp.desy.de",
                                      "ams-monitor@desy.de",
                                      recipients,
                                      "AmsDeliverySystem has been restarted",
                                      dateFormat.format(Calendar.getInstance().getTime()) + ": AmsDeliverySystem has been restarted.");
            }

            return;
        }

        logger.info("Has reached error count: {}", checkProcessor.reachedErrorCount());

        if (checkProcessor.currentCheckIsWarn()) {
            if (checkProcessor.wasErrorSent()) {
                // Aufhebungsalarm
                CheckStatusInfo csi = checkProcessor.getPreviousCheckStatusInfo();
                if (csi != null) {
                    if (csi.getCheckStatus() == CheckStatus.ERROR) {
                        MonitorMessageSender.sendErrorSms("SmsDeliveryWorker switched from " + csi.getCheckStatus() + " to WARN.");
                        checkProcessor.setErrorSent(false);
                    }
                }
            }
        }
        
        if (checkProcessor.getCurrentCheckStatusInfo().getErrorReason() == ErrorReason.SMS_DELIVERY_WORKER) {
            handleWorkerError();
        } else if (checkProcessor.getCurrentCheckStatusInfo().getErrorReason() == ErrorReason.DELIVERY_DEVICE) {
            handleDeviceError();
        }
   }
   
   private void handleWorkerError() {
       
       logger.error("SmsDeliveryWorker does not work.");
       
       if (checkProcessor.previousCheckWasRestarted() && !checkProcessor.wasErrorSent()) {
           CheckStatusInfo csi = checkProcessor.getCurrentCheckStatusInfo();
           MonitorMessageSender.sendErrorSms(csi.getErrorReason().getAlarmMessage());
           checkProcessor.setErrorSent(true);
       }

       if (checkProcessor.reachedErrorCount()) {
           
           logger.error("Number of allowed errors reached. AmsDeliverySystem will be restarted.");
           if (restartDeliverySystem()) {
               logger.info("AmsDeliverySystem has been restarted.");
               checkProcessor.getCurrentCheckStatusInfo().setCheckStatus(CheckStatus.RESTARTED);
               checkProcessor.getCurrentCheckStatusInfo().setErrorReason(ErrorReason.SMS_DELIVERY_WORKER);
           } else {
               logger.error("AmsDeliverySystem could NOT be restarted.");
           }
           
           if (!checkProcessor.wasErrorSent()) {
               if (!checkProcessor.currentCheckIsRestarted()) {
                   CheckStatusInfo csi = checkProcessor.getCurrentCheckStatusInfo();
                   MonitorMessageSender.sendErrorSms(csi.getErrorReason().getAlarmMessage());
                   checkProcessor.setErrorSent(true);
               } else {
                   logger.info("SMS will not be sent yet.");
               }
           } else {
               logger.info("SMS was sent before.");
           }
       } else {
           logger.warn("AmsMonitor does not send a alarm SMS now.");
       }
    }
    
   private void handleDeviceError() {
       
       logger.error("GSM modems have got some problems.");
       
       if (checkProcessor.previousCheckWasRestarted() && !checkProcessor.wasErrorSent()) {
           CheckStatusInfo csi = checkProcessor.getCurrentCheckStatusInfo();
           MonitorMessageSender.sendErrorSms(csi.getErrorReason().getAlarmMessage() + csi.getErrorText());
           checkProcessor.setErrorSent(true);
       }

       if (checkProcessor.currentCheckIsWarn()) {
           if (checkProcessor.reachedWarnCount() && !checkProcessor.wasWarnSent()) {
               MonitorMessageSender.sendWarnMail("SmsDeliveryWorker: WARN: "
                                                 + checkProcessor.getCurrentCheckStatusInfo().getErrorText());
               checkProcessor.setWarnSent(true);
           } else {
               logger.info("AmsMonitor does not send warn notification yet.");
           }
       }
       
       if (checkProcessor.currentCheckIsError()) {
           
           if (checkProcessor.reachedErrorCount()) {
                  
               logger.error("Number of allowed errors reached. AmsDeliverySystem will be restarted.");
               if (restartDeliverySystem()) {
                   logger.info("AmsDeliverySystem has been restarted.");
                   checkProcessor.getCurrentCheckStatusInfo().setCheckStatus(CheckStatus.RESTARTED);
                   checkProcessor.getCurrentCheckStatusInfo().setErrorReason(ErrorReason.DELIVERY_DEVICE);
               } else {
                   logger.error("AmsDeliverySystem could NOT be restarted.");
               }

               if (!checkProcessor.wasErrorSent()) {
                   if (!checkProcessor.currentCheckIsRestarted()) {
                       CheckStatusInfo csi = checkProcessor.getCurrentCheckStatusInfo();
                       MonitorMessageSender.sendErrorSms(csi.getErrorReason().getAlarmMessage()
                                                         + csi.getErrorText());
                       checkProcessor.setErrorSent(true);
                   } else {
                       logger.info("Alarm notification will not be sent yet.");
                   }
               } else {
                   logger.info("Alarm notification was sent before.");
               }
           }
       } else {
           logger.info("AmsMonitor does not send notification yet.");
       }
   }
   
   private boolean restartDeliverySystem() {
       boolean success = false;
       String amsHost = AmsMonitorPreference.AMS_HOST.getValue().toLowerCase();
       String amsUser = AmsMonitorPreference.AMS_USER.getValue().toLowerCase();
       String groupName = AmsMonitorPreference.XMPP_GROUP_NAME.getDefaultValue();
       String[] procArray = new String[] {"ams-delivery-system"};
       long restartTime = AmsMonitorPreference.RESTART_WAIT_TIME.getValue();
       IRemoteService remoteService = new XmppRemoteService(xmppService,
                                                            groupName,
                                                            checkProcessor.getWorkspaceLocation(),
                                                            restartTime);
       success = remoteService.restart(procArray, amsHost, amsUser);
       return success;
   }
}
