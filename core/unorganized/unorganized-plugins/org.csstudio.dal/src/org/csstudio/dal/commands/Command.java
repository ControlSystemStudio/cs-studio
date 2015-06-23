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

package org.csstudio.dal.commands;

import org.csstudio.dal.RemoteException;


/**
 * Interface for atomic remote operation.
 *
 * @author Igor Kriznar (igor.kriznarATcosylab.com)
 */
public interface Command
{
    /**
     * The name of the command within its context.
     *
     * @return the name
     */
    public String getName();

    /**
     * Executes command syncrhonously. Thread calling this method is
     * blocked until  execution is finished.
     *
     * @return returned valune, <code>null</code> if non exists
     *
     * @throws RemoteException if executions fails
     */
    public Object execute(Object... parameters) throws RemoteException;

    /**
     * Returns input parameter types.
     *
     * @return array of types
     */
    public Class[] getParameterTypes();

    /**
     * The owner command context.
     *
     * @return the owner command context
     */
    public CommandContext getOwner();

    /**
     * Returns <code>true</code> if this command can be executed asynchronously
     *
     * @return <code>true</code> if asynchronous
     */
    public boolean isAsynchronous();

    /**
     * Returned type, may be <code>null</code> if no value is returned.
     *
     * @return return type or <code>null</code>
     */
    public Class getReturnedType();
}

/* __oOo__ */
