
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

package org.csstudio.alarm.jms2ora.preferences;

/**
 * @author Markus Moeller
 *
 */
public class PreferenceConstants {
    
    public static final String XMPP_USER_NAME = "xmppUserName";
    public static final String XMPP_PASSWORD = "xmppPassword";
    public static final String XMPP_SERVER = "xmppServer";
    public static final String XMPP_REMOTE_USER_NAME = "xmppRemoteUserName";
    public static final String XMPP_REMOTE_PASSWORD = "xmppRemotePassword";
    public static final String XMPP_SHUTDOWN_PASSWORD = "xmppShutdownPassword";
    public static final String JMS_PROVIDER_URLS = "jmsProviderUrls";
    public static final String JMS_PRODUCER_URL = "jmsProducerUrl";
    public static final String JMS_TOPIC_NAMES = "jmsTopicNames";
    public static final String JMS_CONTEXT_FACTORY_CLASS = "contextFactoryClass";
    public static final String DISCARD_TYPES = "discardTypes";
    public static final String DISCARD_NAMES = "discardNames";
    public static final String DEFAULT_VALUE_PRECISION = "defaultValuePrecision";
    public static final String WATCHDOG_WAIT = "watchdogWait";
    public static final String WATCHDOG_PERIOD = "watchdogPeriod";
    public static final String FILTER_SEND_BOUND = "filterSendBound";
    public static final String FILTER_MAX_SENT_MESSAGES = "filterMaxSentMessages";
    public static final String LOG_STATISTIC = "logStatistic";
    
    /** Waiting time (in ms) of the MessageProcessor thread */
    public static final String MESSAGE_PROCESSOR_SLEEPING_TIME = "msgProcessorSleepingTime";
    
    /** Min. waiting time (in seconds) before next storage will be started */ 
    public static final String TIME_BETWEEN_STORAGE = "timeBetweenStorage";

    /** Flag that indicates if empty message properties have to be stored */
    public static final String STORE_EMPTY_VALUES = "storeEmptyValues";
    
    /** The description of the running instance */
    public static final String DESCRIPTION = "description";
    
    /**
     * Max. time difference between now and the last received message (ms).
     * The preference value is given as minutes and has to be converted. 
     */
    public static final String MAX_RECEIVE_DIFF_TIME = "maxReceiveDiffTime";

    /**
     *  Max. time difference between now and the last stored message (ms).
     * The preference value is given as minutes and has to be converted. 
     */
    public static final String MAX_STORE_DIFF_TIME = "maxStoreDiffTime";
}
