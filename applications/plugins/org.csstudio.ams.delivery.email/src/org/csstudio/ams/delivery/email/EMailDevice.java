
/*
 * Copyright (c) 2011 Stiftung Deutsches Elektronen-Synchrotron,
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

package org.csstudio.ams.delivery.email;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Properties;
import javax.mail.Address;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.csstudio.ams.delivery.BaseAlarmMessage;
import org.csstudio.ams.delivery.IDeliveryDevice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO (mmoeller) : 
 * 
 * @author mmoeller
 * @version 1.0
 * @since 10.12.2011
 */
public class EMailDevice implements IDeliveryDevice {
    
    private static final Logger LOG = LoggerFactory.getLogger(EMailDevice.class);

    private Session mailSession;
    
    private EMailWorkerProperties props;
    
    public EMailDevice(EMailWorkerProperties properties) {
        props = properties;
        initEmail();
    }

    public EMailWorkerProperties getMailProperties() {
        return props;
    }

    private boolean initEmail() {
        
        try {
            props = new EMailWorkerProperties();

            final Properties mailProps = new Properties();
            loadMailProps(mailProps);

            javax.mail.Authenticator auth = null;
            if (props.getMailAuthUser() != null
                    && props.getMailAuthUser().length() > 0) {
                auth = new javax.mail.Authenticator(){
                    @Override
                    public PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(getMailProperties().getMailAuthUser()
                                , getMailProperties().getMailAuthPassword());
                    }};
            }

            for (int i = 1 ; i <= 3 ; i++) {
                try {
                    LOG.info("Init email (address=" + props.getMailSenderAdress()
                            + "' user=" + props.getMailAuthUser() + ") try=" + i);
                    mailSession = javax.mail.Session.getDefaultInstance(mailProps, auth);
                    if (mailSession != null) {
                        return true; // initEmail done
                    }

                    LOG.warn("Email initialization failed. try=" + i);
                } catch (final Exception e)  {
                    LOG.warn("Email initialization failed. try=" + i);
                }

                sleep(5000);
            }
        } catch (final Exception e) {
            LOG.error("Could not init email: {}", e);
        }
        
        return false;
    }

    @Override
    public void stopDevice() {
        if (mailSession != null) {
            mailSession = null;
        }
        if (props != null) {
            props = null;
        }
        LOG.info("Email communication closed.");
    }

    private void sleep(long ms) {
        synchronized (this) {
            try {
                this.wait(ms);
            } catch (InterruptedException ie) {
                LOG.warn("[*** InterruptedException ***]: In method sleep(): {}", ie.getMessage());
            }
        }
    }
    
    @Override
    public BaseAlarmMessage receiveMessage() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean sendMessage(BaseAlarmMessage msg) throws Exception {

        EMailAlarmMessage message = (EMailAlarmMessage) msg;
        String text = message.getMessageText();
        final String emailadr = message.getMessageReceiver();
        final String userName = message.getReceiverName();

        LOG.info("sendEmailMsg(): -1- userName = " + userName + ", emailadr = " + emailadr + ", text = \"" + text + "\"");

        boolean success = false;
        
        // Only for short net breaks
        for (int j = 1 ; j <= 5 ; j++) {
            if (sendEmail(message.getMailSubject(), text, emailadr, userName)) {
                success = true;
                break;
            } 
            
            sleep(2000);
        }

        return success;
    }

    private boolean sendEmail(final String subject, final String content,
            final String recAddr, final String recName)
                    throws Exception {
        
        LOG.info("Start sendEmail()");

        final javax.mail.Message msg = new MimeMessage(mailSession);
        final Address sender = new InternetAddress(props.getMailSenderAdress());
        msg.setFrom(sender);
        final Address receiver = new InternetAddress(recAddr/*, recName*/);

        msg.setRecipient(javax.mail.Message.RecipientType.TO, receiver);
        msg.setSubject(subject);
        msg.setContent(content, "text/plain");

        try{
            Transport.send(msg);
        } catch(final Exception e) {
            LOG.error("Could not Transport.send(): {}", e);
            return false; //only with exceptions at this line => email error
        }

        LOG.info("Email sent to " + recName + " (" + recAddr + ")");
        return true;
    }

    private void loadMailProps(final Properties mailProps) throws Exception
    {
        InputStream input = null;
        try
        {
            //If possible, one should try to avoid hard-coding a path in this
            //manner; in a web application, one should place such a file in
            //WEB-INF, and access it using ServletContext.getResourceAsStream.
            //Another alternative is Class.getResourceAsStream.
            //This file contains the javax.mail config properties mentioned above.
            //input = new FileInputStream( "C:\\Temp\\MyMailServer.txt" );
            //input = LogicBase.class.getResourceAsStream("properties/MailServerConfig.txt");
            //input = EMailConnectorWork.class.getResourceAsStream("/properties/MailServerConfig.txt");
            input = new ByteArrayInputStream(props.getMailServerConfig().getBytes());
            mailProps.load( input );
        }
        catch ( final Exception ex )
        {
            ex.printStackTrace();
            throw ex;
        }
        finally
        {
            try {
                if (input != null) {
                    input.close();
                }
            } catch (final Exception ex) {
                // Can be ignored
            }
        }
    }
}
