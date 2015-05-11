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


import com.google.common.collect.ImmutableSet;


/**
 * Node configuration interface for parameterizable content tree model.
 * (for LDAP based trees as alarms, controls, etc. and others)
 *
 * @author $Author$
 * @version $Revision$
 * @since 03.05.2010
 * @param <T> the type of the object class for LDAP
 */
public interface ITreeNodeConfiguration<T extends Enum<T>> {

    /**
     * Returns the real root element of the tree node configuration.
     * @return
     */
    T getRoot();

    /**
     * Returns the explaining description of the tree node.
     * @return returns the description
     *
     */
    String getDescription();

    /**
     * Returns the name of the tree node (the e.g. last RDN in LdapName).
     * @return returns the name of the tree node (the e.g. last RDN in LdapName).
     *
     */
    String getNodeTypeName();

    /**
     * The tree items a tree item can contain.
     */
    ImmutableSet<T> getNestedContainerTypes();

    /**
     * Returns the node type definition for the given name.
     *
     * @param nodeTypeName
     *            the rdn
     * @return the object class
     */
    T getNodeTypeByNodeTypeName(String nodeTypeName);

    /**
     * The set of permitted attribute types for a node type.
     * @return the immutable set of permitted attributes.
     */
    ImmutableSet<String> getAttributes();
}
