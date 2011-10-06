
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

package org.csstudio.ams.connector.email;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Properties;

import javax.jms.MapMessage;
import javax.jms.Message;
import javax.mail.Address;
import javax.mail.PasswordAuthentication;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.csstudio.ams.AmsActivator;
import org.csstudio.ams.AmsConstants;
import org.csstudio.ams.Log;
import org.csstudio.platform.utility.jms.JmsRedundantReceiver;
import org.eclipse.jface.preference.IPreferenceStore;

public class EMailConnectorWork extends Thread implements AmsConstants
{
    private EMailConnectorStart         ecs                 = null;

    private javax.mail.Session          mailSession         = null;
    private EMailConnectorProperties    props               = null;

    private JmsRedundantReceiver        amsReceiver  = null;
    // private Context                     amsContext          = null;
    // private ConnectionFactory           amsFactory          = null;
    // private Connection                  amsConnection       = null;
    // private javax.jms.Session           amsSession          = null;

    // CHANGED BY: Markus Moeller, 28.06.2007
    // private TopicSubscriber             amsSubscriberEmail  = null;
    // private MessageConsumer             amsSubscriberEmail  = null;

    private boolean bStop = false;
    private boolean bStoppedClean = false;

    public EMailConnectorWork(final EMailConnectorStart starterClass)
    {
        this.ecs = starterClass;
    }

    @Override
    public void run()
    {
        boolean bInitedEmail = false;
        boolean bInitedJms = false;
        int iErr = EMailConnectorStart.STAT_OK;
        Log.log(this, Log.INFO, "start email connector work");
        bStop = false;

        while(bStop == false)
        {
            try
            {
                if (!bInitedEmail)
                {
                    bInitedEmail = initEmail();
                    if (!bInitedEmail)
                    {
                        iErr = EMailConnectorStart.STAT_ERR_EMAIL;
                        ecs.setStatus(iErr);                                    // set it for not overwriting with next error
                    }
                }

                if (!bInitedJms)
                {
                    bInitedJms = initJms();
                    if (!bInitedJms)
                    {
                        iErr = EMailConnectorStart.STAT_ERR_JMSCON;
                        ecs.setStatus(iErr);                                    // set it for not overwriting with next error
                    }
                }

                sleep(100);

                if (bInitedEmail && bInitedJms)
                {
                    iErr = EMailConnectorStart.STAT_OK;
                    if (ecs.getStatus() == EMailConnectorStart.STAT_INIT) {
                        ecs.setStatus(EMailConnectorStart.STAT_OK);
                    }

                    Log.log(this, Log.DEBUG, "runs");

                    Message message = null;
                    try
                    {
                        message = amsReceiver.receive("amsSubscriberEmail");
                    }
                    catch(final Exception e)
                    {
                        Log.log(this, Log.FATAL, "could not receive from internal jms", e);
                        iErr = EMailConnectorStart.STAT_ERR_JMSCON;
                    }
                    if (message != null)
                     {
                        iErr = sendEmailMsg(message);                           // send 1 Email, other in the next run
                    }

//                  if (iErr == EMailConnectorStart.STAT_OK)
//                      iErr = readEmailMsg(10);                                // later if reply and changeStat are needed

                    if (iErr == EMailConnectorStart.STAT_ERR_EMAIL_SEND)
                    {
                        closeEmail();
                        bInitedEmail = false;
                        closeJms();                                             // recover msg
                        bInitedJms = false;
                    }
                    if (iErr == EMailConnectorStart.STAT_ERR_EMAIL)
                    {
                        closeEmail();
                        bInitedEmail = false;
                    }
                    if (iErr == EMailConnectorStart.STAT_ERR_JMSCON)
                    {
                        closeJms();
                        bInitedJms = false;
                    }
                }

                // set status in every loop
                ecs.setStatus(iErr);                                            // set error status, can be OK if no error
            }
            catch (final Exception e)
            {
                ecs.setStatus(EMailConnectorStart.STAT_ERR_UNKNOWN);
                Log.log(this, Log.FATAL, e);

                closeEmail();
                bInitedEmail = false;
                closeJms();
                bInitedJms = false;
            }
        }

        closeJms();
        closeEmail();
        bStoppedClean = true;

        Log.log(this, Log.INFO, "email connector exited");
    }

    /**
     * Sets the boolean variable that controlls the main loop to true
     */
    public synchronized void stopWorking()
    {
        bStop = true;
    }

    /**
     * Returns the shutdown state.
     *
     * @return True, if the shutdown have occured clean otherwise false
     */
    public boolean stoppedClean()
    {
        return bStoppedClean;
    }

    private boolean initEmail()
    {
        try
        {
            props = new EMailConnectorProperties();

            final Properties mailProps = new Properties();
            loadMailProps(mailProps);

            javax.mail.Authenticator auth = null;
            if (props.getMailAuthUser() != null && props.getMailAuthUser().length() > 0)
            {
                auth = new javax.mail.Authenticator(){
                    @Override
                    public PasswordAuthentication getPasswordAuthentication()
                    {
                        return new PasswordAuthentication(props.getMailAuthUser()
                                , props.getMailAuthPassword());
                    }};
            }

            for (int i = 1 ; i <= 3 ; i++)
            {
                try
                {
                    Log.log(this, Log.INFO, "init email (address=" + props.getMailSenderAdress()
                            + "' user=" + props.getMailAuthUser() + ") try=" + i);
                    mailSession = javax.mail.Session.getDefaultInstance(mailProps, auth);
                    if (mailSession != null)
                     {
                        return true;                                            // initEmail done
                    }

                    Log.log(this, Log.WARN, "Email initialization failed. try=" + i);
                }
                catch (final Exception e)
                {
                    Log.log(this, Log.WARN, "Email initialization failed. try=" + i);
                }

                sleep(5000);
            }
        }
        catch (final Exception e)
        {
            Log.log(this, Log.FATAL, "could not init email", e);
        }
        return false;
    }

    public void closeEmail()
    {
        if (mailSession != null)
        {
            mailSession = null;
        }
        if (props != null)
        {
            props = null;
        }
        Log.log(this, Log.INFO, "Email communication closed.");
    }

    private boolean initJms()
    {
        boolean result = false;

        try
        {
            final IPreferenceStore storeAct = AmsActivator.getDefault().getPreferenceStore();

            final boolean durable = Boolean.parseBoolean(storeAct.getString(org.csstudio.ams.internal.AmsPreferenceKey.P_JMS_AMS_CREATE_DURABLE));

            /*
            Hashtable<String, String> properties = new Hashtable<String, String>();

            properties.put(Context.INITIAL_CONTEXT_FACTORY,
                    storeAct.getString(org.csstudio.ams.internal.SampleService.P_JMS_AMS_CONNECTION_FACTORY_CLASS));
            properties.put(Context.PROVIDER_URL,
                    storeAct.getString(org.csstudio.ams.internal.SampleService.P_JMS_AMS_PROVIDER_URL_1));
            amsContext = new InitialContext(properties);

            amsFactory = (ConnectionFactory) amsContext.lookup(
                    storeAct.getString(org.csstudio.ams.internal.SampleService.P_JMS_AMS_CONNECTION_FACTORY));
            amsConnection = amsFactory.createConnection();

            // ADDED BY: Markus M�ller, 25.05.2007
            amsConnection.setClientID("EMailConnectorWorkInternal");

            amsSession = amsConnection.createSession(false, javax.jms.Session.CLIENT_ACKNOWLEDGE);

            amsConnection.start();
            */

            // CHANGED BY: Markus Moeller, 25.05.2007
            /*
            amsSubscriberEmail = amsSession.createDurableSubscriber((Topic)amsContext.lookup(
                    storeAct.getString(org.csstudio.ams.internal.SampleService.P_JMS_AMS_TOPIC_EMAIL_CONNECTOR)),
                    storeAct.getString(org.csstudio.ams.internal.SampleService.P_JMS_AMS_TSUB_EMAIL_CONNECTOR));
            */

            // CHANGED BY: Markus Moeller, 28.06.2007
            /*
            amsSubscriberEmail = amsSession.createDurableSubscriber(amsSession.createTopic(
                    storeAct.getString(org.csstudio.ams.internal.SampleService.P_JMS_AMS_TOPIC_EMAIL_CONNECTOR)),
                    storeAct.getString(org.csstudio.ams.internal.SampleService.P_JMS_AMS_TSUB_EMAIL_CONNECTOR));
            */

            /*
            amsSubscriberEmail = amsSession.createConsumer(amsSession.createTopic(
                    storeAct.getString(org.csstudio.ams.internal.SampleService.P_JMS_AMS_TOPIC_EMAIL_CONNECTOR)));
            */

            final String url1 = storeAct.getString(org.csstudio.ams.internal.AmsPreferenceKey.P_JMS_AMS_PROVIDER_URL_1);
			final String url2 = storeAct.getString(org.csstudio.ams.internal.AmsPreferenceKey.P_JMS_AMS_PROVIDER_URL_2);
			Log.log(this, Log.INFO, "Connecting for urls: "+url1+" and "+url2);
			amsReceiver = new JmsRedundantReceiver("EMailConnectorWorkReceiverInternal", url1,
                    url2);

            if(!amsReceiver.isConnected())
            {
                Log.log(this, Log.FATAL, "could not create amsReceiver");
                return false;
            }

            result = amsReceiver.createRedundantSubscriber(
                    "amsSubscriberEmail",
                    storeAct.getString(org.csstudio.ams.internal.AmsPreferenceKey.P_JMS_AMS_TOPIC_EMAIL_CONNECTOR),
                    storeAct.getString(org.csstudio.ams.internal.AmsPreferenceKey.P_JMS_AMS_TSUB_EMAIL_CONNECTOR),
                    durable);
            if(result == false)
            {
                Log.log(this, Log.FATAL, "could not create amsSubscriberEmail");
                return false;
            }

            return true;
        }
        catch(final Exception e)
        {
            Log.log(this, Log.FATAL, "could not init internal Jms", e);
        }
        return false;
    }

    public void closeJms()
    {
        Log.log(this, Log.INFO, "exiting internal jms communication");

        /*
        if (amsSubscriberEmail != null){try{amsSubscriberEmail.close();amsSubscriberEmail=null;}
        catch (JMSException e){Log.log(this, Log.WARN, e);}}
        if (amsSession != null){try{amsSession.close();amsSession=null;}
        catch (JMSException e){Log.log(this, Log.WARN, e);}}
        if (amsConnection != null){try{amsConnection.stop();}
        catch (JMSException e){Log.log(this, Log.WARN, e);}}
        if (amsConnection != null){try{amsConnection.close();amsConnection=null;}
        catch (JMSException e){Log.log(this, Log.WARN, e);}}
        if (amsContext != null){try{amsContext.close();amsContext=null;}
        catch (NamingException e){Log.log(this, Log.WARN, e);}}
        */

        if(amsReceiver != null)
        {
            amsReceiver.closeAll();
        }

        Log.log(this, Log.INFO, "jms internal communication closed");
    }

    private boolean acknowledge(final Message msg)
    {
        try
        {
            msg.acknowledge();
            return true;
        }
        catch(final Exception e)
        {
            Log.log(this, Log.FATAL, "could not acknowledge", e);
        }
        return false;
    }

    private int sendEmailMsg(final Message message) throws Exception {

        if (!(message instanceof MapMessage)) {
            Log.log(this, Log.WARN, "got unknown message " + message);
            if (!acknowledge(message)) {
                return EMailConnectorStart.STAT_ERR_JMSCON;
            }
            return EMailConnectorStart.STAT_OK;
        }

        final MapMessage msg = (MapMessage) message;
        String text = msg.getString(MSGPROP_RECEIVERTEXT);
        final String emailadr = msg.getString(MSGPROP_RECEIVERADDR);
        final String userName = msg.getString(MSGPROP_SUBJECT_USERNAME);

        Log.log(Log.INFO, "EMailConnectorWork.sendEmailMsg(): -1- userName="+userName+", emailadr="+emailadr+", text="+text+"\"");

        final String mySubject = props.getMailSubject();
        String myContent = props.getMailContent();
        myContent = myContent.replaceAll("%N", userName);

        // Sometimes it happens that the placeholder (e.g. $VALUE$, $HOST$, ...)
        // for the alarm message properties are still present.
        // The dollar sign of this placeholders have to be deleted because they cause an
        // IllegalArgumentException when calling method replaceAll()
        text = cleanTextString(text);

        myContent = myContent.replaceAll("%AMSG", text);

        Log.log(Log.INFO, "EMailConnectorWork.sendEmailMsg(): -2- userName="+userName+", emailadr="+emailadr+", text="+text+"\"");
        Log.log(Log.INFO, "EMailConnectorWork.sendEmailMsg(): myContent="+myContent);

        int iErr = EMailConnectorStart.STAT_ERR_UNKNOWN;
        for (int j = 1 ; j <= 5 ; j++)                                      //only for short net breaks
        {
            if (sendEmail(mySubject, myContent, emailadr, userName))
            {
                if (acknowledge(message)) {
                    return EMailConnectorStart.STAT_OK;
                }

                iErr = EMailConnectorStart.STAT_ERR_JMSCON;
            }
            else
            {
                iErr = EMailConnectorStart.STAT_ERR_EMAIL_SEND;
            }

            sleep(2000);
        }

        return iErr;
    }

    private boolean sendEmail(final String subject,
            final String content,
            final String recAddr,
            final String recName) throws Exception
    {
        Log.log(this, Log.INFO, "start sendEmail");

        final javax.mail.Message msg = new MimeMessage(mailSession);
        final Address sender = new InternetAddress(props.getMailSenderAdress());
        msg.setFrom(sender);
        final Address receiver = new InternetAddress(recAddr/*, recName*/);

        msg.setRecipient(javax.mail.Message.RecipientType.TO, receiver);
        msg.setSubject(subject);
        msg.setContent(content, "text/plain");

        try{
            Transport.send(msg);
        }
        catch(final Exception e)
        {
            Log.log(this, Log.INFO, "could not Transport.send()", e);
            return false;                                                       //only with exceptions at this line => email error
        }

        Log.log(this, Log.INFO, "Email sent to " + recName + " (" + recAddr + ")");
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

    private String cleanTextString(final String text) {

        if (text == null) {
            return "";
        } else if (text.length() == 0) {
            return "";
        }

        return text.replace("$", "");
    }

/*
    private void dummyConnect(String pop3Host, String user, String password) throws Exception
    //for gmx.de or web.de accounts
    {
        // Get a Session object
        Properties sysProperties = System.getProperties();
        Session session = Session.getDefaultInstance(sysProperties, null);
        session.setDebug(true);

        // Connect to host
        Store store = session.getStore("pop3");
        store.connect(pop3Host, -1, user, password);

        // Open the default folder
        Folder folder = store.getDefaultFolder();
        if (folder == null)
            throw new NullPointerException("No default mail folder");
        folder = folder.getFolder("INBOX");
        if (folder == null)
            throw new NullPointerException("Unable to get folder: " + folder);

        // Get message count
        folder.open(Folder.READ_WRITE);
        int totalMessages = folder.getMessageCount();

        if (totalMessages == 0)
            System.out.println(folder + " is empty");
        else
            System.out.println(folder + " totalMessages = " + totalMessages);

        folder.close(true);
        store.close();
    }
*/
}
