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
package org.csstudio.csdata;

import java.io.Serializable;

/** Control System Process Variable Name
 *
 *  Allows Drag-and-Drop to transfer PV names,
 *  can be used for context menu object contributions.
 *
 *  All control system model items must serialize for Drag-and-Drop.
 *  They should be immutable. They should implement proper <code>equals()</code>
 *  and <code>hashCode()</code> to support collections.
 *
 *  @author Sven Wende - Contributed to original ProcessVariable
 *  @author Gabriele Carcassi
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class ProcessVariable implements Serializable
{
    /** @see Serializable */
    final private static long serialVersionUID = 1L;

    /** Process Variable name */
    private final String name;

    /** Initialize
     *  @param name Process Variable name
     */
    public ProcessVariable(final String name)
    {
        if (name == null)
            throw new IllegalArgumentException("Empty name");
        this.name = name;
    }

    /** @return Process Variable Name */
    public String getName()
    {
        return name;
    }

    /** Determine hash code from name
     *  {@inheritDoc}
     */
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    /** Check equality by name
     *  {@inheritDoc}
     */
    @Override
    public boolean equals(final Object obj)
    {
        if (this == obj)
            return true;
        if (! (obj instanceof ProcessVariable))
            return false;
        final ProcessVariable other = (ProcessVariable) obj;
        return name.equals(other.getName());
    }

    @Override
    public String toString()
    {
        return "ProcessVariable '" + name + "'";
    }
}
