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

package org.csstudio.dal.group;

import java.util.EventObject;

import org.csstudio.dal.DynamicValueProperty;


/**
 * Event object fired in <code>GroupListener</code> event methods.
 *
 * @author Igor Kriznar (igor.kriznarATcosylab.com)
 *
 * @param <T> exact group type
 */
public class PropertyGroupEvent<T extends DynamicValueProperty<?>> extends EventObject
{
    private static final long serialVersionUID = 1L;
    private T[] members;

    /**
     * Creates a new GroupEvent object.
     *
     * @param source source of the event
     * @param members members which were added or removed
     */
    public PropertyGroupEvent(PropertyCollection<T> source, T[] members)
    {
        super(source);
        this.members = members;

        if (members == null) {
            throw new NullPointerException("members");
        }

        if (members.length == 0) {
            throw new IllegalArgumentException("Members array is empty");
        }
    }

    /**
     * Returns collection source, if this event is assotiated with
     * property group.
     *
     * @return collection source
     */
    @SuppressWarnings("unchecked")
    public PropertyCollection<T> getPropertyCollectionSource()
    {
        return (PropertyCollection<T>)getSource();
    }

    /**
     * Returns members, which were added or romoved from collection.
     *
     * @return added or removed collection members
     */
    public T[] getMembers()
    {
        return members;
    }
} /* __oOo__ */


/* __oOo__ */
