
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

package org.csstudio.ams.application.monitor.service;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.csstudio.ams.application.monitor.internal.AmsMonitorPreference;
import org.csstudio.ams.application.monitor.util.CommonMailer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author mmoeller
 * @version 1.0
 * @since 14.05.2012
 */
public class MonitorMessageSender {
    
    private static final Logger LOG = LoggerFactory.getLogger(MonitorMessageSender.class);
    
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    
    public static boolean sendErrorSms(String text) {
        
        String[] list = null;
        String to = "";
        boolean success = false;
        
        success = true;
        
        LOG.debug("Try to send SMS: " + text);
        String mailText = text + " [" + dateFormat.format(Calendar.getInstance().getTime()) + "]";

        String server = AmsMonitorPreference.ALARM_MAIL_SERVER.getValue();
        String from = AmsMonitorPreference.ALARM_MAIL_SENDER.getValue();
        String domainPart = AmsMonitorPreference.ALARM_MAIL_DOMAIN_PART.getValue();
        String localPart = AmsMonitorPreference.ALARM_MAIL_LOCAL_PART.getValue();
        String subject = AmsMonitorPreference.ALARM_MAIL_SUBJECT.getValue();
        String amsGroup = AmsMonitorPreference.ALARM_AMS_GROUP.getValue();
        
        // The mail should be send to the SMS Gateway (localPart = sms/${NUMBER})
        if(localPart.indexOf("${NUMBER}") != -1) {
            list = DatabaseService.getPhoneNumbers(amsGroup);
            if(list != null) {
                for(int i = 0;i < list.length;i++) {
                    list[i] = localPart.replaceAll("\\$\\{NUMBER\\}", list[i]) + "@" + domainPart;
                    LOG.info("SMS to: " + list[i]);
                }
                success = (CommonMailer.sendMultiMail(server, from, list, subject, mailText) == 0);
            } else {
                // We do not have any phone number. Use the emergency number from the preferences.
                String emergency = AmsMonitorPreference.ALARM_SMS_EMERGENCY_NUMBER.getValue();
                to = localPart.replaceAll("\\$\\{NUMBER\\}", emergency) + "@" + domainPart;
                LOG.info("SMS to: " + to);
                success = CommonMailer.sendMail(server, from, to, subject, mailText);
            }
        } else {
            to = localPart + "@" + domainPart;
            LOG.info("SMS to: " + to);
            success = CommonMailer.sendMail(server, from, to, subject, mailText);
        }
        
        // Maybe we want to send the Alarm SMS a second time using the Old Alarm System (via James)
        boolean useOas = AmsMonitorPreference.ALARM_SMS_USE_OAS.getValue();
        if(useOas) {
            list = DatabaseService.getPhoneNumbers(amsGroup);
            if(list != null) {
                for(int i = 0;i < list.length;i++) {
                    list[i] = "N:" + list[i] + " " + mailText;
                    LOG.info("SMS to: " + list[i]);
                }
                success = (CommonMailer.sendMultiMail(server, from, "sms@krykmail.desy.de", list, mailText) == 0);
            }
        }
        
        return success;
    }

    public static boolean sendWarnMail(String text) {
        
        boolean success = true;
        
        LOG.debug("Try to send mail: " + text);

        String mailText = text + " [" + dateFormat.format(Calendar.getInstance().getTime()) + "]";
        
        String server = AmsMonitorPreference.ALARM_MAIL_SERVER.getValue();
        String from = AmsMonitorPreference.ALARM_MAIL_SENDER.getValue();
        String subject = AmsMonitorPreference.ALARM_MAIL_SUBJECT.getValue();
        String amsGroup = AmsMonitorPreference.ALARM_AMS_GROUP.getValue();
        
        String to = "";
        String[] list = DatabaseService.getEMailAddresses(amsGroup);
        if(list != null) {
            for(int i = 0;i < list.length;i++) {
                to = to + list[i] + ",";
                LOG.info("Mail to: " + list[i]);
            }
            to = to.trim();
            if(to.endsWith(",")) {
                to = to.substring(0, to.length() - 1);
            }
            success = CommonMailer.sendMail(server, from, to, subject, mailText);
        } else {
            // We do not have any mail address. Use the emergency number from the preferences.
            to = AmsMonitorPreference.ALARM_EMERGENCY_MAIL.getValue();
            LOG.info("Mail to: " + to);
            success = CommonMailer.sendMail(server, from, to, subject, mailText);
        }
                
        return success;
    }
}
