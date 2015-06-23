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


/**
 * Default implementation of <code>Identifier</code> interface.
 *
 * @author Igor Kriznar (igor.kriznarATcosylab.com)
 * @version $id$
 */
public class IdentifierImpl implements Identifier
{
    private String longName;
    String name;
    String uniqueName;
    Type type;

    /**
     * Creates a new IdentifierImpl object.
     *
     * @param name new name
     * @param uniqueName new unique name
     * @param longName new long qualified name
     * @param type new indentifiable type
     */
    public IdentifierImpl(String name, String uniqueName, String longName,
        Type type)
    {
        this.name = name;
        this.uniqueName = uniqueName;
        this.longName = longName;
        this.type = type;
    }

    /* (non-Javadoc)
     * @see org.csstudio.dal.context.Identifier#getLongQualifiedName()
     */
    public String getLongQualifiedName()
    {
        return longName;
    }

    /* (non-Javadoc)
     * @see org.csstudio.dal.context.Identifier#getName()
     */
    public String getName()
    {
        return name;
    }

    /* (non-Javadoc)
     * @see org.csstudio.dal.context.Identifier#getType()
     */
    public Type getType()
    {
        return type;
    }

    /* (non-Javadoc)
     * @see org.csstudio.dal.context.Identifier#getUniqueName()
     */
    public String getUniqueName()
    {
        return uniqueName;
    }
}

/* __oOo__ */
