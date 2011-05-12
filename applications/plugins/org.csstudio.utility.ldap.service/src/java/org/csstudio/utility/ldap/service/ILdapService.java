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
 */
package org.csstudio.utility.ldap.service;

import java.util.Map;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.naming.InvalidNameException;
import javax.naming.NameParser;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.ModificationItem;
import javax.naming.ldap.LdapName;

import org.csstudio.utility.treemodel.ContentModel;
import org.csstudio.utility.treemodel.CreateContentModelException;
import org.csstudio.utility.treemodel.ITreeNodeConfiguration;
import org.eclipse.core.runtime.jobs.Job;

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
     * Reconnects to an LDAP according to the new preferences map
     * @param ldapPrefs the map of preferences, if <code>null</code> the default values from preferences are used
     * @return true, if reconnection has been successful, false otherwise
     */
    boolean reInitializeLdapConnection(@CheckForNull final Map<String, String> ldapPrefs);


    /**
     * Returns the ldap content model builder for the specified parameters
     * @param objectClassRoot the tree configuration of the content model
     * @param searchResult the current search result to build the model from
     * @param <T> the tree configuration type of the content model
     * @return the content model builder
     */
    @Nonnull
    <T extends Enum<T> & ITreeNodeConfiguration<T>> ILdapContentModelBuilder<T>
        getLdapContentModelBuilder(@Nonnull final T objectClassRoot,
                                   @Nonnull final ILdapSearchResult searchResult);

    /**
     * Returns the ldap content model builder for the specified parameters
     * @param model an already existing model which shall be enriched with
     * @param <T> the tree configuration type of the content model
     * @return the content model builder
     */
    @Nonnull
    <T extends Enum<T> & ITreeNodeConfiguration<T>> ILdapContentModelBuilder<T>
        getLdapContentModelBuilder(@Nonnull final ContentModel<T> model);

    @Nonnull
    <T extends Enum<T> & ITreeNodeConfiguration<T>>
    ContentModel<T> getLdapContentModelForSearchResult(@Nonnull final T configurationRoot,
                                                       @Nonnull final ILdapSearchResult result) throws CreateContentModelException;

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
     * Removes the component from the LDAP context incl. its subtree!
     * @param component .
     * @throws InvalidNameException
     * @throws CreateContentModelException
     */
    <T extends Enum<T> & ITreeNodeConfiguration<T>>
        boolean removeComponent(@Nonnull final T configurationRoot,
                                @Nonnull LdapName component) throws InvalidNameException, CreateContentModelException;

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
    ILdapSearchResult retrieveSearchResultSynchronously(@Nonnull LdapName searchRoot,
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
    Job createLdapReaderJob(@Nonnull ILdapSearchParams params,
                            @Nullable ILdapSearchResult result,
                            @Nullable ILdapReadCompletedCallback callBack);


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
     * Returns a name parser for this LDAP service.
     * @return the parser
     * @throws NamingException
     */
    @CheckForNull
    NameParser getLdapNameParser() throws NamingException;

}
