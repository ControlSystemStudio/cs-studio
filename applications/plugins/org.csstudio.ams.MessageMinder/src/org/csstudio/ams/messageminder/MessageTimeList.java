/*
 * Copyright (c) 2007 Stiftung Deutsches Elektronen-Synchrotron,
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
/*
 * $Id: MessageTimeList.java,v 1.1 2007/11/29 07:54:19 mmoeller Exp $
 */
package org.csstudio.ams.messageminder;

import java.util.LinkedList;

import org.csstudio.data.values.ITimestamp;

/**
 * @author hrickens
 * @author $Author: mmoeller $
 * @version $Revision: 1.1 $
 * @since 30.10.2007
 */
public class MessageTimeList extends LinkedList<ITimestamp> {

    /**
     * 
     */
    private static final long serialVersionUID = -8815141248484417092L;
    /**
     * max number of of message that send per defined period.
     */
    private int _maxYoungMessages;
    /**
     * store the number of unsent messages.
     */
    private int _unsentMsgCount;
    /**
     * the period inn second in there same message don't sending.
     */
    private long _period;
    
    /**
     * The default constructor.
     */
    public MessageTimeList() {
        _maxYoungMessages=4;    //TODO: set from preferncePage
        _period = 60;           //TODO: set from preferncePage
    }

    /**
     * @return the time stamp from last entry.
     */
    public final ITimestamp getLastDate(){
        if(size()<1){
            return null;
        }
        return getLast();
    }
    
    /**
     * Add the new time stamp and delete time stamp that older as the period say.
     * 
     * @param now add the Date from the new message.
     * @return true when terms and conditions ok, to send a Message, also return false. 
     */
    @Override
    public final boolean add(final ITimestamp now) {
        if(!super.add(now)){
            throw new RuntimeException("can't add a ITimestamp to the Arraylist");
        }
        for (int i=0;i<size()-1;) {
            ITimestamp time = get(i);
            if(now.seconds()-time.seconds()>_period){ // to old --> delete time stamp.
                remove(i);
            }else if(size()>_maxYoungMessages){ // to many young msg --> don't send msg.
                _unsentMsgCount++;
                    return false;
            }else{ // non or less young msg --> send msg.
                return true;
            }
        }
        return true;
    }

    /**
     * Set the unsent message count to 0.
     */
    public final void resetUnsentMsgCount() {
        _unsentMsgCount=0;
    }

    /**
     * @return get the Number of unsent messages.
     */
    public final int getUnsentsgCount() {
        return _unsentMsgCount;
    }

}
