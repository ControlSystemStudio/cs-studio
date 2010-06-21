/*
 * Copyright (c) 2007 Stiftung Deutsches Elektronen-Synchrotron,
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
package org.csstudio.utility.ldap.service;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.ModificationItem;
import javax.naming.ldap.LdapName;

import org.csstudio.utility.ldap.reader.LDAPReader;
import org.csstudio.utility.ldap.reader.LdapSearchResult;
import org.csstudio.utility.ldap.reader.LDAPReader.IJobCompletedCallBack;
import org.csstudio.utility.ldap.reader.LDAPReader.LdapSearchParams;
import org.csstudio.utility.treemodel.CreateContentModelException;
import org.csstudio.utility.treemodel.ITreeNodeConfiguration;

/**
 * LDAP Service.
 *
 * @author bknerr
 * @author $Author$
 * @version $Revision$
 * @since 08.04.2010
 */
public interface ILdapService {

    /**
     * Creates a new Record in LDAP.
     * @param newComponentName the record
     * @param attributes the attributes of the new ldap component
     * @return true if the new record could be created, false otherwise
     */
    boolean createComponent(@Nonnull LdapName newComponentName,
                            @Nullable Attributes attributes);


    /**
     * Removes the leaf component from the LDAP context.
     * Attention, the component may not have children components!
     * @param component .
     */
    boolean removeLeafComponent(@Nonnull LdapName component);


    /**
     * Retrieves LDAP entries for the given query and search scope synchronously.
     * Blocks until the LDAP read has been performed!
     *
     * @param searchRoot search root
     * @param filter the query filter
     * @param searchScope the search scope
     * @return the search result
     */
    @CheckForNull
    LdapSearchResult retrieveSearchResultSynchronously(@Nonnull LdapName searchRoot,
                                                       @Nonnull String filter,
                                                       int searchScope);

    /**
     * Returns an LDAPReader job that can be scheduled by the user arbitrarily.
     *
     * @param params the LDAP search params
     * @param result the
     * @param callBack called on job completion
     * @return the LDAP reader job
     */
    @Nonnull
    LDAPReader createLdapReaderJob(@Nonnull LdapSearchParams params,
                                   @Nullable LdapSearchResult result,
                                   @Nullable IJobCompletedCallBack callBack);


    /**
     * Modifies given attributes for given LDAP component
     * @param name .
     * @param mods .
     * @throws NamingException
     */
    void modifyAttributes(@Nonnull LdapName name, @Nonnull ModificationItem[] mods) throws NamingException;


    /**
     * Renames LDAP component.
     * @param oldLdapName .
     * @param newLdapName .
     * @throws NamingException
     */
    void rename(@Nonnull LdapName oldLdapName, @Nonnull LdapName newLdapName) throws NamingException;


    /**
     * Retrieves the attributes for a given LDAP component
     * @param ldapName .
     * @return the attributes
     * @throws NamingException
     */
    @CheckForNull
    Attributes getAttributes(@Nonnull LdapName ldapName) throws NamingException;


    /**
     * Retrieves the named object from LDAP context.
     *
     * @param name the object name
     * @throws NamingException when a naming exception occurs.
     */
    @CheckForNull
    Object lookup(@Nonnull LdapName name) throws NamingException;


    /**
     * Moves the object with the given name to the new location.
     * <b>Attention</b>: the jndi api does not provide this function, hence it is necessary
     * to copy the subtree (depth first search) to the new location and delete it at the old
     * location.
     * @param root
     *
     * @param oldLdapName
     * @param newLdapName
     * @return whether the complete move could be performed
     * @throws NamingException
     * @throws CreateContentModelException
     */
    <T extends Enum<T> & ITreeNodeConfiguration<T>> boolean move(T configurationRoot, LdapName oldLdapName, LdapName newLdapName)
        throws NamingException, CreateContentModelException;



}
