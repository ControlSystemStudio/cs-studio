
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
        time = 0L;
        nanoSeconds = 0L;
        status = 0L;
        severity = 0L;
        value = null;
        sddsType = SDDSType.NOT_SET;
    }

    /**
     * @param t
     * @param nanos
     * @param stat
     * @param val
     * @param type
     */
    public EpicsRecordData(long t, long nanos, long stat, Object val, SDDSType type) {
        
        this.time = t;
        this.nanoSeconds = nanos;
        
        if(stat != 0L) {
            this.status = (stat & 0x00000000ffff0000L) >> 16;
            this.severity = (stat & 0x000000000000ffffL);
        } else {
            this.status = 0L;
            this.severity = 0L;
        }
        
        this.value = val;
        this.sddsType = type;
    }

    /**
     * 
     * @param t
     * @param nano
     * @param stat
     * @param val
     */
    public EpicsRecordData(long t, long nano, long stat, Object val) {
        
        this.time = t;
        this.nanoSeconds = nano;
        
        if(stat != 0L) {
            this.status = (stat & 0x00000000ffff0000L) >> 16;
            this.severity = (stat & 0x000000000000ffffL);
        } else {
            this.status = 0L;
            this.severity = 0L;
        }
        
        this.value = val;
        if(val != null) {
            this.sddsType = SDDSType.getByTypeName(val.getClass().getSimpleName());
        } else {
            this.value = Double.NaN;
            this.sddsType = SDDSType.SDDS_DOUBLE;
        }
    }

    /**
     * Returns true, if the value is valid. It is valid if the severity is OK, MINOR, MAJOR, REPEAT or
     * Est. Repeat.
     * 
     * @return True if the value of this record is valid
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
     * @return The time stamp
     */
    public long getTime() {
        return time;
    }

    /**
     * 
     * @param t
     */
    public void setTime(long t) {
        this.time = t;
    }

    /**
     * 
     * @return The nano second part of the time stamp
     */
    public long getNanoSeconds() {
        return nanoSeconds;
    }

    /**
     * 
     * @param nanos
     */
    public void setNanoSeconds(long nanos) {
        this.nanoSeconds = nanos;
    }

    /**
     * 
     * @return The status and severity as one long value
     */
    public long getStatusAndSeverity()  {
        return ((status << 16) | severity);
    }
    
    /**
     * 
     * @return The status of the record
     */
    public long getStatus() {
        return status;
    }

    /**
     * 
     * @param stat
     */
    public void setStatus(int stat) {
        this.status = stat;
    }

    /**
     * 
     * @return The severity
     */
    public long getSeverity() {
        return severity;
    }

    /**
     * 
     * @param sevr
     */
    public void setSeverity(int sevr) {
        this.severity = sevr;
    }

    /**
     * 
     * @return The value of this record
     */
    public Object getValue() {
        return value;
    }

    /**
     * 
     * @param v
     */
    public void setValue(Object v) {
        this.value = v;
    }

    /**
     * 
     * @return The SDDS type
     */
    public SDDSType getSddsType() {
        return sddsType;
    }

    /**
     * 
     * @param type
     */
    public void setSddsType(SDDSType type) {
        this.sddsType = type;
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
