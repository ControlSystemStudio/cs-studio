
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

package org.csstudio.ams.application.monitor.message;

import java.io.Serializable;
import java.util.Iterator;
import java.util.TreeSet;

/**
 * Holds the sent check initiation messages ordered by the event time entry.
 * 
 * @author mmoeller
 * @version 1.0
 * @since 13.04.2012
 */
public class MessageMemory<T extends AbstractCheckMessage> implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private TreeSet<T> sendMessages;
    
    public MessageMemory() {
        sendMessages = new TreeSet<T>(new CheckMessageComparator());
    }
    
    public void forgetAll() {
        sendMessages.clear();
    }
    
    public int size() {
        return sendMessages.size();
    }
    
    public void add(T message) {
        sendMessages.add(message);
    }
    
    public T first() {
        return sendMessages.first();
    }
    
    public T pollFirst() {
        return sendMessages.pollFirst();
    }
    
    public boolean isEmpty() {
        return sendMessages.isEmpty();
    }
    
    public boolean hasContent() {
        return !sendMessages.isEmpty();
    }
    
    @Override
    public String toString() {
        
        StringBuffer result = new StringBuffer();
        
        result.append("MessageMemory {\n");
        
        for (T o : sendMessages) {
            result.append(" " + o.toString());
        }
        
        result.append("}");
        
        return result.toString();
    }
    
    public boolean containsInitiatorMessageForAnswer(IAnswerMessage o) {
        
        boolean contains = false;
        
        if (hasContent()) {
            Iterator<T> iter = sendMessages.iterator();
            while (iter.hasNext()) {
                T message = iter.next();
                if (message instanceof InitiatorMessage) {
                    if (o.isAnswerForMessage((InitiatorMessage) message)) {
                        contains = true;
                        break;
                    }
                }
            }
        }
        
        return contains;
    }
    
    public boolean containsMessageWithClassValue(String classValue) {
        
        boolean doesContain = false;
        
        if (hasContent()) {
            Iterator<T> iter = sendMessages.iterator();
            while (iter.hasNext()) {
                String value = iter.next().getClassValue();
                if (value != null) {
                    if (value.equals(classValue)) {
                        doesContain = true;
                        break;
                    }
                }
            }
        }
        
        return doesContain;
    }
}
