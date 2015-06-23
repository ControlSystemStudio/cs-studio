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

import java.util.Map;

import org.csstudio.dal.context.Linkable;
import org.csstudio.dal.context.PropertyContext;


/**
 * <code>DynamicValueProperty</code> is context for dynamic value channel with a reference to
 * parent context.
 *
 * @author Igor Kriznar (igor.kriznarATcosylab.com)
 */
public interface DynamicValueProperty<T> extends SimpleProperty<T>,
    AsynchronousAccess<T>, AsynchronousCharacteristicContext, Updateable<T>,
    Linkable, EventSystemContext<DynamicValueEvent<T, SimpleProperty<T>>>
{
    /**
     * Returns parent property context, may be
     * <code>PropertyFamily</code> or <code>AbstractDevice</code>.
     *
     * @return parent context
     */
    public PropertyContext getParentContext();

    /**
     * Returns map with supported parameters for obtaining expert monitor, may be <code>null</code>.
     * Key value pairs may be in following combinations:
     *
     * <ul>
     * <li>If key is defined and value is <code>null</code> it means that it is enough to provide
     * key with <code>null</code> valeu in parameter in order to acctivate specific event parameter.</li>
     * <li>If value is of tyle <code>Class</code>, this means that only values of specified type
     * are supported for this key.</li>
     * <li>If value is array, than only elements in array are supported as parameters for this key.</li>
     * <li>If value is Java 1.5 enum class, than only enum elements are supported as parameters
     * for this key.</li>
     * </ul>
     *
     * <p>If returned map is <code>null</code> or empty, than no special parameters are supported
     * and they have no efect, thus expert monitor is not supported.</p>
     *
     * @return map with supported parameters.
     */
    public Map<String, Object> getSupportedExpertMonitorParameters();

    /**
     * Creates new expert monitor, which supports communication layer specific parameters.
     * @param listener listener which will receive expert value updates.
     * @param parameters communication layer specific parameters
     * @return expert monitor
     * @throws RemoteException if requested expert monitor in not supported or available.
     *
     * @see ValueUpdateable#getSupportedParameters()
     */
    public <E extends SimpleProperty<T>, M extends ExpertMonitor,DynamicValueMonitor> M createNewExpertMonitor(DynamicValueListener<T, E> listener,
        Map<String, Object> parameters) throws RemoteException;
}

/* __oOo__ */
