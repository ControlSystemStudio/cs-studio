/*
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchrotron,
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN '../AS IS' BASIS.
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
package org.csstudio.domain.common.statistic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author claus
 *
 */
public class AlarmHandler {
    private static final Logger LOG = LoggerFactory.getLogger(AlarmHandler.class);


    /**
     *
     */
    private String _logLevel = "info";
    private Double deadband = 5.0;
    private Double highAbsoluteLimit = 1000.0;
    private boolean highAbsoluteLimitIsActive = false;
    private StoredData highAbsoluteLimitLastAlarm = null;
    private Double highRelativeLimit = 1000.0;
    private boolean highRelativeLimitIsActive = false;
    private StoredData highRelativeLimitLastAlarm = null;
    private String descriptor    = null;
    private String application = null;

    public final String getApplication() {
        return application;
    }

    public final void setApplication(final String application) {
        this.application = application;
    }

    public final String getDescriptor() {
        return descriptor;
    }

    public final void setDescriptor(final String descriptor) {
        this.descriptor = descriptor;
    }

    public AlarmHandler() {

    }

    public AlarmHandler ( final Double highAbsoluteLimit, final Double highRelativeLimit) {
        /*
         * set limits
         */
        setHighAbsoluteLimit(highAbsoluteLimit);
        setHighRelativeLimit(highRelativeLimit);
    }

    public final void process ( final Double value, final Collector collector) {
        /*
         * alarm chaecking
         */
        if ( (value > highAbsoluteLimit) ) {
            if ( !isHighAbsoluteLimitIsActive()) {
                /*
                 * set absolute limit on
                 */

                Object[] logArgs = new Object[] {getApplication(), getDescriptor(), value, collector.getInfo()};

                LOG.warn("{0} : {1} above absolute High limit! Value: {2} Info: {3}", logArgs);
                setHighAbsoluteLimitIsActive(true);
            }
        } else {
            if ( value < ( highAbsoluteLimit * (100.0 - deadband))) {
                setHighAbsoluteLimitIsActive(false);
            }
        }

        if ( (value > highAbsoluteLimit) && (value > ( collector.getMeanValuerelative() * getHighRelativeLimit()/ 100.0 ))) {
            if ( !isHighRelativeLimitIsActive()) {
                /*
                 * set absolute limit on
                 */
                LOG.warn(getApplication() + " : " + getDescriptor() + " : >"
                                + getHighRelativeLimit() + "% "
                                + "above floating mean value ("
                                + collector.getMeanValuerelative()
                                + ")! Value: " + value + "Info: "
                                + collector.getInfo());
                setHighRelativeLimitIsActive(true);
            }
        } else {
            if ( value < ( (collector.getMeanValuerelative() * getHighRelativeLimit()/ 100.0 ) * (100.0 - deadband))) {
                setHighRelativeLimitIsActive(false);
            }
        }


    }


    public final Double getDeadband() {
        return deadband;
    }
    public final void setDeadband(final Double deadband) {
        this.deadband = deadband;
    }
    public final void setDeadband(final int deadband) {
        this.deadband = new Double(deadband);
    }
    public final Double getHighAbsoluteLimit() {
        return this.highAbsoluteLimit;
    }
    public final void setHighAbsoluteLimit(final Double highAbsoluteLimit) {
        this.highAbsoluteLimit = highAbsoluteLimit;
    }
    public final void setHighAbsoluteLimit(final int highAbsoluteLimit) {
        this.highAbsoluteLimit = new Double(highAbsoluteLimit);
    }
    public final boolean isHighAbsoluteLimitIsActive() {
        return this.highAbsoluteLimitIsActive;
    }
    public final void setHighAbsoluteLimitIsActive(final boolean highAbsoluteLimitIsActive) {
        this.highAbsoluteLimitIsActive = highAbsoluteLimitIsActive;
    }
    public final StoredData getHighAbsoluteLimitLastAlarm() {
        return this.highAbsoluteLimitLastAlarm;
    }
    public final void setHighAbsoluteLimitLastAlarm(final StoredData highAbsoluteLimitLastAlarm) {
        this.highAbsoluteLimitLastAlarm = highAbsoluteLimitLastAlarm;
    }
    public final Double getHighRelativeLimit() {
        return this.highRelativeLimit;
    }
    public final void setHighRelativeLimit(final Double highRelativeLimit) {
        this.highRelativeLimit = highRelativeLimit;
    }
    public final void setHighRelativeLimit(final int highRelativeLimit) {
        this.highRelativeLimit = new Double(highRelativeLimit);
    }
    public final boolean isHighRelativeLimitIsActive() {
        return this.highRelativeLimitIsActive;
    }
    public final void setHighRelativeLimitIsActive(final boolean highRelativeLimitIsActive) {
        this.highRelativeLimitIsActive = highRelativeLimitIsActive;
    }
    public final StoredData getHighRelativeLimitLastAlarm() {
        return this.highRelativeLimitLastAlarm;
    }
    public final void setHighRelativeLimitLastAlarm(final StoredData highRelativeLimitLastAlarm) {
        this.highRelativeLimitLastAlarm = highRelativeLimitLastAlarm;
    }
    public final String getLogLevel() {
        return this._logLevel;
    }
    public final void setLogLevel(final String logLevel) {
        this._logLevel = logLevel;
    }



}
