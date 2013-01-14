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
 */
package org.csstudio.domain.desy.alarm;


import java.io.Serializable;

import javax.annotation.Nonnull;

import org.csstudio.domain.desy.system.ControlSystemType;


/**
 * Interface of an alarm.
 *
 * An alarm is defined as describing characteristic of a identifiable system variable (in form of an
 * distinct value or or state @see also states and values in org.epics.pvmanager) that may yield
 * information about the feature's relevance according to a given rule set and/or value range set.
 * An alarm serves as the ultimate cause to propagate (alarm) notifications throughout the system.
 *
 * Sidenote: Whether a control system considers an OK or UNKNOWN state as alarm or not, is up to the
 * implementation.
 *
 * TODO (bknerr, jhatje, jpenning, hrickens) : The 'identifiable system feature' may be
 * {@link org.csstudio.platform.model.IProcessVariable} or
 * {@link org.csstudio.platform.model.IControlSystemItem} or any of the many others. I introduced
 * {@link org.csstudio.domain.desy.system.ISystemVariable} for now, was not sure which one if any to take.
 * Gabriele has a very strong library (pvmanager), but it is not conceptually based around
 * identifiable system variables.
 *
 * @author bknerr
 * @since 04.11.2010
 */
public interface IAlarm extends /* Alarm, */ Serializable {

    @Nonnull
    ControlSystemType getControlSystemType();
}
