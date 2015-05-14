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

import org.csstudio.dal.SimpleProperty;
import org.csstudio.dal.context.Identifier.Type;
import org.csstudio.dal.device.AbstractDevice;
import org.csstudio.dal.proxy.Proxy;


/**
 * Convenience tool, which creates instance of <code>Identifier</code> for
 * <code>Identifiable</code>.
 *
 * @author Igor Kriznar (igor.kriznarATcosylab.com)
 */
public final class IdentifierUtilities
{
    private IdentifierUtilities()
    {
        super();
    }

    /**
     * Creates new instance of <code>Identifier</code>.
     *
     * @param p implementation of <code>Identifeable</code>
     *
     * @return new instance of <code>Identifier</code>
     */
    public static Identifier createIdentifier(SimpleProperty<?> p)
    {
        return new IdentifierImpl(p.getName(), p.getUniqueName(),
            p.getUniqueName(), Type.PROPERTY);
    }

    /**
     * Creates new instance of <code>Identifier</code>.
     *
     * @param p implementation of <code>Identifeable</code>
     *
     * @return new instance of <code>Identifier</code>
     */
    public static Identifier createIdentifier(Proxy p)
    {
        return new IdentifierImpl(p.getUniqueName(), p.getUniqueName(),
            p.getUniqueName(), Type.PROXY);
    }

    /**
     * Creates new instance of <code>Identifier</code>.
     *
     * @param p implementation of <code>Identifeable</code>
     *
     * @return new instance of <code>Identifier</code>
     */
    public static Identifier createIdentifier(PlugContext p)
    {
        String n = "DAL-Plug/" + p.getPlugType();

        return new IdentifierImpl(n, n, n, Type.PLUG);
    }

    /**
     * Creates new instance of <code>Identifier</code>.
     *
     * @param p implementation of <code>Identifeable</code>
     *
     * @return new instance of <code>Identifier</code>
     */
    public static Identifier createIdentifier(AbstractDevice d)
    {
        return new IdentifierImpl(d.getUniqueName(), d.getUniqueName(),
            d.getUniqueName(), Type.DEVICE);
    }
}

/* __oOo__ */
