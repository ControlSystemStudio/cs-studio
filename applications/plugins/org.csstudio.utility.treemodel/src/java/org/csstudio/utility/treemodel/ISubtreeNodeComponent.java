/*
 * Copyright (c) 2010 Stiftung Deutsches Elektronen-Synchrotron,
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
 *
 * $Id$
 */
package org.csstudio.utility.treemodel;

import java.util.Collection;
import java.util.Map;
import java.util.Set;


/**
 * Interface for the structural component for the content model tree.
 *
 * @author bknerr
 * @author $Author$
 * @version $Revision$
 * @since 30.04.2010
 * @param <T> Enum type of possible LDAP structural components
 */
public interface ISubtreeNodeComponent<T extends Enum<T> & ITreeNodeConfiguration<T>> extends INodeComponent<T> {

    /**
     * Retrieves the list of children of the current component (but without children subtrees).
     * @return a collection of direct children components
     */
    Collection<INodeComponent<T>> getDirectChildren();

    /**
     * Retrieves a child component with the given nameKey
     * @param name .
     * @return the child with the specified name
     */
    INodeComponent<T> getChild(String nameKey);

    /**
     * Adds the given component as child.
     * @param child the new child
     */
    void addChild(INodeComponent<T> child);

    /**
     * Removes the child with the given name (and hence its complete subtree).
     * @param name .
     */
    void removeChild(String name);

    Set<T> getSubComponentTypes();

    Map<String, INodeComponent<T>> getChildrenByType(T type);

    /**
     * Clears a subtree node of all its children.
     */
    void removeAllChildren();
}
