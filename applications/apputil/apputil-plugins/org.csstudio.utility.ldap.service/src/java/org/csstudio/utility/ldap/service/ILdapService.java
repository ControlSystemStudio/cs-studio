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

import javax.naming.InvalidNameException;
import javax.naming.NameParser;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.LdapName;

import org.csstudio.utility.treemodel.ContentModel;
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
     * Reconnects to an LDAP according to the new preferences map
     * @param ldapPrefs the map of preferences, if <code>null</code> the default values from preferences are used
     * @return true, if reconnection has been successful, false otherwise
     */
    boolean reInitializeLdapConnection(final Map<String, String> ldapPrefs);


    /**
     * Returns the ldap content model builder for the specified parameters
     * @param objectClassRoot the tree configuration of the content model
     * @param searchResult the current search result to build the model from
     * @param <T> the tree configuration type of the content model
     * @return the content model builder
     * @throws LdapServiceException
     */
    <T extends Enum<T> & ITreeNodeConfiguration<T>> ILdapContentModelBuilder<T>
        getLdapContentModelBuilder(final T objectClassRoot,
                                   final ILdapSearchResult searchResult) throws LdapServiceException;

    /**
     * Returns the ldap content model builder for the specified parameters
     * @param model an already existing model which shall be enriched with
     * @param <T> the tree configuration type of the content model
     * @return the content model builder
     * @throws LdapServiceException
     */
    <T extends Enum<T> & ITreeNodeConfiguration<T>> ILdapContentModelBuilder<T>
        getLdapContentModelBuilder(final ContentModel<T> model) throws LdapServiceException;

    <T extends Enum<T> & ITreeNodeConfiguration<T>>
    ContentModel<T> getLdapContentModelForSearchResult(final T configurationRoot,
                                                       final ILdapSearchResult result) throws CreateContentModelException, LdapServiceException;

    /**
     * Creates a new Record in LDAP.
     * @param newComponentName the record
     * @param attributes the attributes of the new ldap component
     * @return true if the new record could be created, false otherwise
     */
    boolean createComponent(LdapName newComponentName,
                            Attributes attributes);


    /**
     * Removes the leaf component from the LDAP context.
     * Attention, the component may not have children components!
     * @param component .
     */
    boolean removeLeafComponent(LdapName component);

    /**
     * Removes the component from the LDAP context incl. its subtree!
     * @param component .
     * @throws InvalidNameException
     * @throws CreateContentModelException
     * @throws LdapServiceException
     */
    <T extends Enum<T> & ITreeNodeConfiguration<T>>
        boolean removeComponent(final T configurationRoot,
                                LdapName component) throws InvalidNameException, CreateContentModelException, LdapServiceException;

    /**
     * Retrieves LDAP entries for the given query and search scope synchronously.
     * Blocks until the LDAP read has been performed!
     *
     * @param searchRoot search root
     * @param filter the query filter
     * @param searchScope the search scope
     * @return the search result
     */
    ILdapSearchResult retrieveSearchResultSynchronously(LdapName searchRoot,
                                                        String filter,
                                                        int searchScope);

    /**
     * Returns an LDAPReader job that can be scheduled by the user arbitrarily.
     *
     * @param params the LDAP search params
     * @param callBack called on job completion
     * @return the LDAP reader job
     */
    ILdapReaderJob createLdapReaderJob(ILdapSearchParams params,
                                       ILdapReadCompletedCallback callBack);


    /**
     * Modifies given attributes for given LDAP component
     * @param name .
     * @param mods .
     * @throws NamingException
     */
    void modifyAttributes(LdapName name, ModificationItem[] mods) throws NamingException;


    /**
     * Renames LDAP component.
     * @param oldLdapName .
     * @param newLdapName .
     * @throws NamingException
     */
    void rename(LdapName oldLdapName, LdapName newLdapName) throws NamingException;


    /**
     * Retrieves the attributes for a given LDAP component
     * @param ldapName .
     * @return the attributes
     * @throws NamingException
     */
    Attributes getAttributes(LdapName ldapName) throws NamingException;


    /**
     * Retrieves the named object from LDAP context.
     *
     * @param name the object name
     * @throws NamingException when a naming exception occurs.
     */
    Object lookup(LdapName name) throws NamingException;


    /**
     * Returns a name parser for this LDAP service.
     * @return the parser
     * @throws LdapServiceException
     */
    NameParser getLdapNameParser() throws LdapServiceException;

    /**
     * Parses a given SearchResult entry from LDAP into an LdapName object.
     * @param the service
     * @param row a search result row
     * @return the ldap composite name
     * @throws LdapServiceException
     */
    LdapName parseSearchResult(final SearchResult row) throws LdapServiceException;
}
