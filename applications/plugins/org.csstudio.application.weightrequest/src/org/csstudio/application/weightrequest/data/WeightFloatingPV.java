
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

package org.csstudio.application.weightrequest.data;

import org.csstudio.application.weightrequest.WeightRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.aps.jca.CAException;
import gov.aps.jca.CAStatus;
import gov.aps.jca.cas.ProcessVariableEventCallback;
import gov.aps.jca.cas.ProcessVariableReadCallback;
import gov.aps.jca.cas.ProcessVariableWriteCallback;
import gov.aps.jca.dbr.DBR;
import gov.aps.jca.dbr.DBRType;
import gov.aps.jca.dbr.DBR_Double;
import gov.aps.jca.dbr.DBR_TIME_Double;
import gov.aps.jca.dbr.Severity;
import gov.aps.jca.dbr.Status;
import gov.aps.jca.dbr.TIME;
import gov.aps.jca.dbr.TimeStamp;

import com.cosylab.epics.caj.cas.handlers.AbstractCASResponseHandler;
import com.cosylab.epics.caj.cas.util.FloatingDecimalProcessVariable;

/**
 * TODO (mmoeller) : 
 * 
 * @author mmoeller
 * @version 1.0
 * @since 01.12.2011
 */
public class WeightFloatingPV extends FloatingDecimalProcessVariable
                              implements ValueListener {
    
    private static Logger LOG = LoggerFactory.getLogger(WeightFloatingPV.class);
    private WeightRequest request;
    private TimeStamp timestamp;
    private Status status;
    private Severity severity;
    private double currentValue;
    private MetaData metaData;

    public WeightFloatingPV(String pvName,
                            ProcessVariableEventCallback pvEventCallback,
                            MetaData mData,
                            long refreshRate) {
      
        super(pvName, pvEventCallback);
        request = new WeightRequest("http://krykwaageh54.desy.de/cgi-bin/display.cgi", refreshRate);
        // request = new WeightRequest("http://localhost:8080/Web-Playground/random.jsp", refreshRate);
        metaData = mData;
        initialize();
    }

    protected void initialize() {
        request.addListener(this);
        request.start();
    }

    public void close() {
        LOG.info("WeightFloatingPV is going to be closed.");
        this.request.setShutdown();
        try {
            request.join();
        } catch (InterruptedException ie) {
            LOG.warn("[*** InterruptedException ***]: {}", ie.getMessage());
        }
    }

    @Override
    public void onValue(ValueEvent value) {
        
        LOG.debug("Wert erhalten: {}", value);
        
        if (value.isValid()) {
            status = Status.NO_ALARM;
            severity = Severity.NO_ALARM;
        } else {
            status = Status.READ_ACCESS_ALARM;
            severity = Severity.INVALID_ALARM;
        }

        if (value.isValid()) {
            setDoubleValue(value.getValue().doubleValue());
        } else {
            setDoubleValue(this.currentValue);
        }
    }

    @Override
    protected CAStatus readValue(DBR value,
                                 ProcessVariableReadCallback asyncReadCallback)
                                         throws CAException {
        
        DBR_TIME_Double timeDBR = (DBR_TIME_Double)value;
        fillInStatusAndTime(timeDBR);
        timeDBR.getDoubleValue()[0] = currentValue;
        return CAStatus.NORMAL;
    }

    @Override
    protected CAStatus writeValue(DBR value,
                                  ProcessVariableWriteCallback asyncWriteCallback)
                                          throws CAException {
      
        DBR_Double doubleDBR = (DBR_Double)value;
        currentValue = doubleDBR.getDoubleValue()[0];
        timestamp = new TimeStamp();

        if (interest) {
                
            int mask = EventMask.DBE_VALUE.getEventMask() 
                       | EventMask.DBE_ARCHIVE.getEventMask();
            
            if (status != Status.NO_ALARM) {
                mask |= EventMask.DBE_ALARM.getEventMask();
            }
            
            DBR monitorDBR = AbstractCASResponseHandler.createDBRforReading(this);
            ((DBR_Double) monitorDBR).getDoubleValue()[0] = currentValue;
            fillInDBR(monitorDBR);
            fillInStatusAndTime((TIME) monitorDBR);

            eventCallback.postEvent(mask, monitorDBR);
        }
        
        return CAStatus.NORMAL;
    }

    protected void fillInStatusAndTime(TIME timeDBR) {
        timeDBR.setStatus(this.status);
        timeDBR.setSeverity(this.severity);
        timeDBR.setTimeStamp(this.timestamp);
    }

    public void setDoubleValue(double newValue) {
        
        DBR_Double valueHolder = new DBR_Double(1);
        double[] valueArray = valueHolder.getDoubleValue();

        synchronized (this) {
            valueArray[0] = newValue;
            try {
                write(valueHolder, null);
            } catch (CAException e) {
                LOG.error("[*** CAException ***]: " + e.getMessage());
            }
        }
    }

    @Override
    public DBRType getType() {
        return DBRType.DOUBLE;
    }

    @Override
    public short getPrecision() {
        return this.metaData.getPrecision();
    }

    @Override
    public String getUnits() {
        return this.metaData.getEgu();
    }

    @Override
    public Number getLowerDispLimit() {
        return this.metaData.getLowerDisplayValue();
    }

    @Override
    public Number getUpperDispLimit() {
        return this.metaData.getUpperDisplayValue();
    }

    @Override
    public Number getLowerAlarmLimit() {
        return this.metaData.getLowerAlarmValue();
    }

    @Override
    public Number getUpperAlarmLimit() {
        return this.metaData.getUpperAlarmValue();
    }

    @Override
    public Number getLowerWarningLimit() {
        return this.metaData.getLowerWarningValue();
    }

    @Override
    public Number getUpperWarningLimit() {
        return this.metaData.getUpperWarningValue();
    }

    @Override
    public Number getLowerCtrlLimit() {
        return this.metaData.getLowerControlValue();
    }

    @Override
    public Number getUpperCtrlLimit() {
        return this.metaData.getUpperControlValue();
    }
}
