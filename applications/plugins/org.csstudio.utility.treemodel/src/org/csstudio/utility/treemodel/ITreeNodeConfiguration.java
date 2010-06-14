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

import java.util.Set;

import javax.annotation.Nonnull;

import com.google.common.collect.ImmutableSet;


/**
 * Node configuration interface for parametrisable content tree model.
 * (for LDAP based trees as alarms, controls, etc. and others)
 *
 * @author bknerr
 * @author $Author$
 * @version $Revision$
 * @since 03.05.2010
 * @param <T> the type of the object class for LDAP
 */
public interface ITreeNodeConfiguration<T extends Enum<T>> {

    /**
     * Returns the root value for the root type.
     * @return the root value.
     */
    @Nonnull
    String getRootTypeName();

    /**
     * Returns the name of this object class in the directory.
     * @return the name of this object class in the directory.
     */
    @Nonnull
    String getDescription();

    /**
     * Returns the name of the tree node (the e.g. RDN in Ldap).
     * @return returns the name of the tree node (the e.g. RDN in Ldap).
     *
     */
    @Nonnull
    String getNodeTypeName();

    /**
     * Returns the object class that a container entry nested within this an
     * entry of this object class should have. If this object class is not a
     * container class or if there is no recommended class for nested
     * containers, this method returns <code>null</code>.
     *
     * @return the recommended object class for a container within a container
     *         of this object class. <code>null</code> if there is no
     *         recommended class.
     */
    @Nonnull
    Set<T> getNestedContainerClasses();

    /**
     * Returns the object class of an LDAP rdn attribute (efan, eren, ...).
     *
     * @param nodeTypeName
     *            the rdn
     * @return the object class
     */
    @Nonnull
    T getNodeTypeByNodeTypeName(@Nonnull String nodeTypeName);

    /**
     * The set of permitted attribute types for a node type.
     * @return the immutable set of permitted attributes.
     */
    @Nonnull
    ImmutableSet<String> getAttributes();
}
