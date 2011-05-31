
/* 
 * Copyright (c) 2010 Stiftung Deutsches Elektronen-Synchrotron, 
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

package org.csstudio.archive.sdds.server.conversion;

/**
 * @author Markus Moeller
 *
 */
public class SampleDouble
{
    /** */
    private long timeSec;

    /** */
    private long timeUSec;
    
    /** */
    private long status;
    
    /** */
    private long dim;
    
    /** */
    private DoubleValue value;
    
    /**
     * @param timeSec
     * @param timeUSec
     * @param status
     * @param dim
     * @param value
     */
    public SampleDouble(long timeSec, long timeUSec, long status, long dim, DoubleValue value)
    {
        this.timeSec = timeSec;
        this.timeUSec = timeUSec;
        this.status = status;
        this.dim = dim;
        this.value = value;
    }

    /**
     * 
     * @return
     */
    public long getTimeSec()
    {
        return timeSec;
    }
    
    public void setTimeSec(long timeSec)
    {
        this.timeSec = timeSec;
    }
    
    public long getTimeUSec()
    {
        return timeUSec;
    }
    
    public void setTimeUSec(long timeUSec)
    {
        this.timeUSec = timeUSec;
    }
    
    public long getStatus()
    {
        return status;
    }
    
    public void setStatus(long status)
    {
        this.status = status;
    }
    
    public long getDim()
    {
        return dim;
    }
    
    public void setDim(long dim)
    {
        this.dim = dim;
    }
    
    public DoubleValue getValue()
    {
        return value;
    }
    
    public void setValue(DoubleValue value)
    {
        this.value = value;
    }
}
