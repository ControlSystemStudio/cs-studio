
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
 *
 */

package org.csstudio.ams.systemmonitor.util;

import java.util.Date;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * @author Markus Möller
 *
 */
public class CommonMailer {
    
    public static int sendMultiMail(String mailhost, String from,
                                    String to, String subject[],
                                    String text) {
        int result = 0;
        
        if(to == null) {
            return -1;
        }
        
        for(String s : subject) {
            if(sendMail(mailhost, from, to, s, text) == false) {
                // Increment the counter if it failed to send a mail
                result++;
            }
        }
        
        return result;
    }

    public static int sendMultiMail(String mailhost, String from,
                                    String[] to, String subject,
                                    String text) {
        int result = 0;
        
        if(to == null) {
            return -1;
        }
        
        for(String s : to) {
            if(sendMail(mailhost, from, s, subject, text) == false) {
                // Increment the counter if it failed to send a mail
                result++;
            }
        }
        
        return result;
    }
    
    public static boolean sendMail(String mailhost, String from, String to, String subject) {
        return sendMail(mailhost, from, to, subject, "");
    }
    
    public static boolean sendMail(String mailhost, String from,
                                   String to, String subject, String text) {
        
        Message msg = null;
        Session session = null;
        boolean success = false;
        
        if(to == null) {
            return success; // False at this time
        }
        
        if(subject == null) {
            return success; // False at this time
        }
        
        try {
            Properties props = System.getProperties();
            props.put("mail.smtp.host", mailhost);

            // Get a Session object
            session = Session.getInstance(props, null);

            // construct the message
            msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(from));

            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to, false));                        
            msg.setSubject(subject);
            msg.setText(text);

            msg.setHeader("X-Mailer", "AmsSystemMonitor Mailer");
            msg.setSentDate(new Date());

            Transport.send(msg);
            
            success = true;
        } catch(AddressException ae) {
            success = false;
        } catch(MessagingException me) {
            success = false;
        }
        
        return success;
    }   
}
