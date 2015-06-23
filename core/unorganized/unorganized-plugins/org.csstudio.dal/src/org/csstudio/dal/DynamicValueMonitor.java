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
 * Interface <code>DynamicValueMonitor</code> defines the way in which an observer
 * (realized as JavaBeans listener) can influence the flow of events from the
 * data access event source. Subscription can be changed by each observer separately
 * and it affects that observer only. Event delivery is changed by changing characteristics
 * in the characteristic context of the subscription.
 * <p>
 * Timer trigger is the only statically declared trigger in this interface; implementations
 * may choose to provide additional triggers through the characteristic context.
 * Timer trigger may operate at the prescribed default frequency, with all other (optional)
 * triggers set to their respective default values. In this case the method
 * <code>isDefault</code> must return <code>true</code>. The default subscription may be
 * handled separately (in a more efficient way) by the underlying implementation.
 * </p>
 * <p>
 * Datatypes library does not link this interface explicitely from any other interface.
 * In this way the implementation is free to choose how it will offer subscription control
 * and whether it will offer it at all. If there is no subscription control, the same
 * events must be dispatched to all listeners, as in JavaBeans specification.
 * </p>
 * <p>
 * Methods of this interface which generate transient connection objects such as requests and
 * responses, store those objects directly into the latest values of the property to
 * which this monitor belongs. For example, if changing trigger thorugh this method generates
 * a request the request will be accessible through the <code>AbstractProperty</code>
 * <code>Updateable</code> interface.
 * </p>
 *
 * @see org.csstudio.dal.CharacteristicContext
 * @see org.csstudio.dal.DynamicValueMonitorCharacteristics
 * @see org.csstudio.dal.SimpleMonitor
 */
public interface DynamicValueMonitor extends CharacteristicContext,
    DynamicValueMonitorCharacteristics, SimpleMonitor, Suspendable
{
}

/* __oOo__ */
