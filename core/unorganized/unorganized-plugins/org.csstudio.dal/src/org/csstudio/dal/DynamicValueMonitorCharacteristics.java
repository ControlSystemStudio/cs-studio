/*
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchroton,
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

package org.csstudio.dal;


/**
 * Characteristic names definition for <code>DynamicValueMonitor</code>.
 *
 * @see org.csstudio.dal.CharacteristicContext
 */
public interface DynamicValueMonitorCharacteristics
	extends CharacteristicContext
{
	/**
	 * A name constant for the dynamic monitor characteristic
	 * defaultTimerTrigger. This characteristic expresses, in long
	 * milliseconds, the default time  interval between monitor notifications.
	 * If such characteristic exists, it must have this name.
	 */
	public static final String C_DEFAULT_TIMER_TRIGGER = "defaultTimerTrigger";

	/**
	 * A name constant for the dynamic monitor characteristic
	 * heartbeat. Heartbeat is a boolean characteristic, denoting if the
	 * monitor sends value updates even if the value has not changed, just to
	 * confirm that the link to the underlying data source layer is still
	 * open.
	 */
	public static final String C_HEARTBEAT = "heartbeat";

	/**
	 * A name constant for the dynamic monitor characteristic timer
	 * trigger. Timer trigger is a settable characteristic, specifying the
	 * time interval in milliseconds, that must elapse before a new monitor
	 * notification is dispatched.
	 */
	public static final String C_TIMER_TRIGGER = "timerTrigger";

	/**
	 * A name constant for the dynamic monitor characteristic
	 * expressing, in long milliseconds, the minimal supported timer trigger.
	 *
	 * <p> Monitor may support or array of suppoted discreet timer triggers
	 * (C_SUPPORTED_TIMER_TRIGGERS) or min and max timer trigger on continuous value interval
	 * (C_MINIMUM_TIMER_TRIGGER and C_MINIMUM_TIMER_TRIGGER) or none of above, if there are no
	 * special limitations.</p>
	 */
	public static final String C_MINIMUM_TIMER_TRIGGER = "minimumTimerTrigger";

	/**
	 * A name constant for the dynamic monitor characteristic
	 * expressing, in long milliseconds, the maximal supported timer trigger.
	 *
	 * <p> Monitor may support or array of suppoted discreet timer triggers
	 * (C_SUPPORTED_TIMER_TRIGGERS) or min and max timer trigger on continuous value interval
	 * (C_MINIMUM_TIMER_TRIGGER and C_MINIMUM_TIMER_TRIGGER) or none of above, if there are no
	 * special limitations.</p>
	 */
	public static final String C_MAXIMUM_TIMER_TRIGGER = "maximumTimerTrigger";

	/**
	 * A name constant for the dynamic monitor characteristic
	 * expressing set of supported timer triggers. Characcteristic vallue is array of
	 * long values in milliseconds.
	 *
	 * <p> Monitor may support or array of suppoted discreet timer triggers
	 * (C_SUPPORTED_TIMER_TRIGGERS) or min and max timer trigger on continuous value interval
	 * (C_MINIMUM_TIMER_TRIGGER and C_MINIMUM_TIMER_TRIGGER) or none of above, if there are no
	 * special limitations.</p>
	 */
	public static final String C_SUPPORTED_TIMER_TRIGGERS = "supportedTimerTriggers";
}

/* __oOo__ */
