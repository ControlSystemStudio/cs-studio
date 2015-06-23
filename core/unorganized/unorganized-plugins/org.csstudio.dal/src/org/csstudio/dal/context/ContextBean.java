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

package org.csstudio.dal.context;

import com.cosylab.util.CommonException;


/**
 * Ihis interface is used by those DAL interfaces that are used as JavaBean.
 * By desing contract implementation of this interface must be JavaBean, which
 * has zero parameter public constructor.
 *
 * <p>
 * Intention of this interface is to provide means of initialization of Java
 * bean trough  <code>initialize(AbstractApplicationContext)</code>. By this
 * way bean receives parent  context and perform required initialization to be
 * functional.
 * </p>
 *
 * @author Igor Kriznar (igor.kriznarATcosylab.com)
 */
public interface ContextBean
{
    /**
     * Returns plug type string, which is distinguishing for plug which
     * creates  proxies for particular communication layer.<p>For
     * example plug that connects to EPICS device my return string "EPICS".</p>
     *
     * @return plug destingushing type name
     */
    public String getPlugType();

    /**
     * Initializes bean with application context. This method can be called only once.
     * After bean has been initialized all subsequent calls will throw exception.
     *
     * @param ctx parent application context
     *
     * @throws CommonException if initialization fails or bean has already been initialized.
     */
    public void initialize(AbstractApplicationContext ctx)
        throws CommonException;

    /**
     * Returns parent application context if bean has been initialized.
     *
     * @return application context or <code>null</code> if bean is not initialized
     */
    public AbstractApplicationContext getApplicationContext();
}

/* __oOo__ */
