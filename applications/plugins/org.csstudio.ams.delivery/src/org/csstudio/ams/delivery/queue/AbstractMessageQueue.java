
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

package org.csstudio.ams.delivery.queue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author mmoeller
 * @version 1.0
 * @since 18.12.2011
 */
public abstract class AbstractMessageQueue<E> implements MessageListener {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractMessageQueue.class);

    protected ConcurrentLinkedQueue<E> content;
    
    protected Object lockObject;
    
    protected AbstractMessageQueue(Object lock) {
        content = new ConcurrentLinkedQueue<E>();
        lockObject = lock;
    }

    public synchronized List<E> getCurrentContent() {
        final List<E> result = Collections.synchronizedList(new ArrayList<E>(content));
        content.removeAll(result);
        return result;
    }

    public synchronized E nextMessage() {
        return content.poll();
    }

    public synchronized boolean addMessage(final E e) {
        return content.add(e);
    }

    public synchronized boolean isEmpty() {
        return content.isEmpty();
    }

    public synchronized boolean hasContent() {
        return !content.isEmpty();
    }

    public synchronized int size() {
        return content.size();
    }

    @Override
    public void onMessage(final Message msg) {
        if (msg instanceof MapMessage) {
            LOG.info("Message received: {}", msg);
            final E o = convertMessage((MapMessage) msg);
            if (o != null) {
                synchronized (lockObject) {
                    content.add(o);
                    lockObject.notify();
                }
            }
            acknowledge(msg);
        } else {
            LOG.warn("Message is not a MapMessage object. Ignoring it...");
        }
    }

    protected abstract E convertMessage(MapMessage message);

    protected void acknowledge(final Message message) {
        try {
            message.acknowledge();
        } catch (final JMSException jmse) {
            LOG.warn("Cannot acknowledge message: {}", message);
        }
    }
}
