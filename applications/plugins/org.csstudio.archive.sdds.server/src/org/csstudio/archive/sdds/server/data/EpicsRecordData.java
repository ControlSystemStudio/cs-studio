
/* 
 * Copyright (c) 2009 Stiftung Deutsches Elektronen-Synchrotron, 
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

package org.csstudio.archive.sdds.server.data;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.csstudio.archive.sdds.server.file.SDDSType;
import org.csstudio.archive.sdds.server.util.ArchiveSeverity;

/**
 * @author Markus Moeller
 *
 */
public class EpicsRecordData
{
    /** */
    private long time;
    
    /** */
    private long nanoSeconds;
    
    /** */
    private long status;
    
    /** */
    private long severity;

    /** */
    private Object value;
    
    /** */
    private SDDSType sddsType;

    /**
     * 
     */
    public EpicsRecordData() {
        time = 0;
        nanoSeconds = 0;
        status = 0;
        value = null;
        sddsType = SDDSType.NOT_SET;
    }

    /**
     * @param time
     * @param nanoSeconds
     * @param status
     * @param value
     * @param sddsType
     */
    public EpicsRecordData(long time, long nanoSeconds, long status, Object value, SDDSType sddsType) {
        
        this.time = time;
        this.nanoSeconds = nanoSeconds;
        
        if(status != 0) {
            this.status = (status & 0x00000000ffff0000L) >> 16;
            this.severity = (status & 0x000000000000ffffL);
        } else {
            this.status = 0;
            this.severity = 0;
        }
        
        this.value = value;
        this.sddsType = sddsType;
    }

    /**
     * 
     * @param time
     * @param nanoSeconds
     * @param status
     * @param value
     */
    public EpicsRecordData(long time, long nanoSeconds, long status, Object value) {
        
        this.time = time;
        this.nanoSeconds = nanoSeconds;
        
        if(status != 0) {
            this.status = (status & 0x00000000ffff0000L) >> 16;
            this.severity = (status & 0x000000000000ffffL);
        } else {
            this.status = 0;
            this.severity = 0;
        }
        
        this.value = value;
        if(value != null) {
            this.sddsType = SDDSType.getByTypeName(value.getClass().getSimpleName());
        } else {
            this.value = Double.NaN;
            this.sddsType = SDDSType.SDDS_DOUBLE;
        }
    }

    /**
     * Returns true, if the value is valid. It is valid if the severity is OK, MINOR, MAJOR, REPEAT or
     * Est. Repeat.
     * 
     * @return
     */
    public boolean isValueValid() {
        
        boolean result = false;
        
        if((getSeverity() < ArchiveSeverity.INVALID.getSeverityValue())
                || (getSeverity() == ArchiveSeverity.REPEAT.getSeverityValue())
                || (getSeverity() == ArchiveSeverity.EST_REPEAT.getSeverityValue())) {
            
            result = true;
        }
        
        return result;
    }
    
    /**
     * 
     * @return
     */
    public long getTime() {
        return time;
    }

    /**
     * 
     * @param time
     */
    public void setTime(long time) {
        this.time = time;
    }

    /**
     * 
     * @return
     */
    public long getNanoSeconds() {
        return nanoSeconds;
    }

    /**
     * 
     * @param nanoSeconds
     */
    public void setNanoSeconds(long nanoSeconds) {
        this.nanoSeconds = nanoSeconds;
    }

    /**
     * 
     * @return
     */
    public long getStatusAndSeverity()  {
        return ((status << 16) | severity);
    }
    
    /**
     * 
     * @return
     */
    public long getStatus() {
        return status;
    }

    /**
     * 
     * @param status
     */
    public void setStatus(int status) {
        this.status = status;
    }

    /**
     * 
     * @return
     */
    public long getSeverity() {
        return severity;
    }

    /**
     * 
     * @param status
     */
    public void setSeverity(int severity) {
        this.severity = severity;
    }

    /**
     * 
     * @return
     */
    public Object getValue() {
        return value;
    }

    /**
     * 
     * @param value
     */
    public void setValue(Object value) {
        this.value = value;
    }

    /**
     * 
     * @return
     */
    public SDDSType getSddsType() {
        return sddsType;
    }

    /**
     * 
     * @param sddsType
     */
    public void setSddsType(SDDSType sddsType) {
        this.sddsType = sddsType;
    }
    
    /**
     * 
     */
    @Override
	public String toString() {
        
        StringBuffer t = new StringBuffer();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS ");
        Date date = new Date(time * 1000);
        
        t.append("EpicsRecordData{");
        t.append(dateFormat.format(date) + "(" + time + "),");
        t.append("nanoSeconds=" + nanoSeconds + ",");
        t.append("status=" + status + ",");
        t.append("severity=" + severity + ",");
        t.append("value=" + value + ",");
        t.append("SDDSType=" + sddsType + "}");
        
        return t.toString();
    }
}
