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

package org.csstudio.dal.impl;

import org.csstudio.dal.RemoteException;
import org.csstudio.dal.commands.Command;
import org.csstudio.dal.commands.CommandContext;
import org.csstudio.dal.proxy.CommandProxy;


/**
 * Glue implementation of Command. It wraps syncrhonous command proxy.
 *
 * @author Igor Kriznar (igor.kriznarATcosylab.com)
 */
public class CommandImpl implements Command
{
    protected CommandProxy proxy;
    protected AbstractDeviceImpl owner;

    /**
     * Creates a new CommandImpl object.
     *
     * @param p Command proxy
     * @param ctx Device
     */
    public CommandImpl(CommandProxy p, AbstractDeviceImpl ctx)
    {
        super();
        proxy = p;
        owner = ctx;
    }

    /* (non-Javadoc)
     * @see org.csstudio.dal.commands.Command#getName()
     */
    public String getName()
    {
        return proxy.getName();
    }

    /* (non-Javadoc)
     * @see org.csstudio.dal.commands.Command#execute(java.lang.Object...)
     */
    public Object execute(Object... parameters) throws RemoteException
    {
        return proxy.execute(parameters);
    }

    /* (non-Javadoc)
     * @see org.csstudio.dal.commands.Command#getParameterTypes()
     */
    public Class[] getParameterTypes()
    {
        return proxy.getParameterTypes();
    }

    /* (non-Javadoc)
     * @see org.csstudio.dal.commands.Command#getOwner()
     */
    public CommandContext getOwner()
    {
        return owner;
    }

    /* (non-Javadoc)
     * @see org.csstudio.dal.commands.Command#getReturnedType()
     */
    public Class getReturnedType()
    {
        return proxy.getReturnedType();
    }

    public boolean isAsynchronous()
    {
        return false;
    }
}

/* __oOo__ */
