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

/**
 *
 */
package org.csstudio.dal.device;

import org.csstudio.dal.CharacteristicContext;
import org.csstudio.dal.commands.AsynchronousCommandContext;
import org.csstudio.dal.context.DeviceFamily;
import org.csstudio.dal.context.Identifiable;
import org.csstudio.dal.context.LifecycleReporter;
import org.csstudio.dal.context.Linkable;
import org.csstudio.dal.context.PropertyContext;
import org.csstudio.dal.group.GroupDataAccessProvider;


/**
 * <code>AbstractDevice</code> is abstraction of an device. This interface  is
 * context for asynchronous commands and a collection for properties.
 *
 * @author Igor Kriznar (igor.kriznarATcosylab.com)
 */
public interface AbstractDevice extends AsynchronousCommandContext,
    PropertyContext, Linkable, Identifiable, LifecycleReporter,
    GroupDataAccessProvider, CharacteristicContext
{
    /**
     * Returns the Dynamic Value Unique Name. The name syntax is not specified
     * by Datatypes; the value is used as unique identifier. This name is used to initiate
     * connection to remote object and can be regardes as remote name.
     *
     * @return String DVUN for this property
     */
    public String getUniqueName();

    /**
     * Returns parent device context, only
     * <code>DeviceFamily</code> exists at the moment.
     *
     * @return parent context
     */
    public DeviceFamily<?> getParentContext();

}

/* __oOo__ */
