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
package org.csstudio.utility.ldapUpdater.service;

import java.util.GregorianCalendar;
import java.util.Set;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.naming.InvalidNameException;
import javax.naming.ServiceUnavailableException;
import javax.naming.ldap.LdapName;

import org.csstudio.utility.ldap.model.Record;
import org.csstudio.utility.ldap.service.ILdapContentModelBuilder;
import org.csstudio.utility.ldap.service.ILdapSearchResult;
import org.csstudio.utility.treemodel.ContentModel;
import org.csstudio.utility.treemodel.ITreeNodeConfiguration;

/**
 * Provides access to LdapService for specific LdapUpdater methods.
 *
 * @author bknerr
 * @author $Author$
 * @version $Revision$
 * @since 05.05.2010
 */
public interface ILdapUpdaterService {

    /**
     * Creates a new Record in LDAP.
     * @param newLdapName the new complete ldap name of the record
     * @return true if the new record could be created, false otherwise
     * @throws ServiceUnavailableException if the LDAP service is not available
     */
    boolean createLdapRecord(@Nonnull LdapName newLdapName) throws ServiceUnavailableException;

    /**
     * Creates a new IOC in LDAP
     * @param newLdapName the new complete name of the IOC
     * @param gregorianCalendar
     * @return true if the new ioc could be created, false otherwise
     * @throws ServiceUnavailableException
     */
    boolean createLdapIoc(@Nonnull LdapName newLdapName, @Nullable GregorianCalendar gregorianCalendar) throws ServiceUnavailableException;

    /**
     * Retrieves the LDAP entries for the records belonging to the given facility and IOC.
     * @param facilityName facility
     * @param iocName ioc
     * @return the seach result
     *
     * @throws InterruptedException
     * @throws ServiceUnavailableException
     */
    @CheckForNull
    ILdapSearchResult retrieveRecordsForIOC(@Nonnull String facilityName,
                                            @Nonnull String iocName)
            throws InterruptedException, ServiceUnavailableException;

    /**
     * Retrieves the LDAP entries for the records belonging to the given facility and IOC.
     * @param fullName the complete LDAP name for this ioc
     *
     * @throws InterruptedException
     * @throws InvalidNameException
     * @throws ServiceUnavailableException
     */
    @CheckForNull
    ILdapSearchResult retrieveRecordsForIOC(@Nonnull LdapName fullName)
            throws InterruptedException, InvalidNameException, ServiceUnavailableException;


    /**
     * Removes all records for the given IOC from LDAP that are not contained in the valid records set.
     * @param iocName .
     * @param facilityName .
     * @param validRecords .
     * @throws ServiceUnavailableException
     */
    void tidyUpIocEntryInLdap(@Nonnull String iocName, @Nonnull String facilityName, @Nonnull Set<Record> validRecords)
            throws ServiceUnavailableException;

    /**
     * Removes the IOC entry from the LDAP context.
     * @param iocName .
     * @param facilityName .
     * @throws InvalidNameException
     * @throws InterruptedException
     * @throws ServiceUnavailableException
     */
    void removeIocEntryFromLdap(@Nonnull String iocName,
                                @Nonnull String facilityName) throws InvalidNameException, InterruptedException, ServiceUnavailableException;

    /**
     * Returns the ldap content model builder for the specified parameters
     * @param model an already existing model which shall be enriched with
     * @param <T> the tree configuration type of the content model
     * @return the content model builder
     * @throws ServiceUnavailableException
     */
    @Nonnull
    <T extends Enum<T> & ITreeNodeConfiguration<T>> ILdapContentModelBuilder<T>
        getLdapContentModelBuilder(@Nonnull final ContentModel<T> model) throws ServiceUnavailableException;

}
