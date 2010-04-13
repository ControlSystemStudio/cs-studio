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
package service;

import java.util.Set;

import javax.annotation.Nonnull;
import javax.naming.directory.DirContext;

import org.csstudio.utility.ldap.model.IOC;
import org.csstudio.utility.ldap.model.Record;
import org.csstudio.utility.ldap.reader.LdapSearchResult;

/**
 * LDAP Service.
 *
 * @author bknerr
 * @author $Author$
 * @version $Revision$
 * @since 08.04.2010
 */
public interface LdapService {


    /**
     * Retrieves the LDAP entries for the records belonging to the given facility and IOC.
     * @param facilityName facility
     * @param iocName ioc
     * @return the seach result
     * @throws InterruptedException
     */
    LdapSearchResult retrieveRecords(@Nonnull String facilityName,
                                     @Nonnull String iocName) throws InterruptedException;

    /**
     * Creates a new Record in LDAP.
     * @param context the directory context
     * @param ioc the ioc
     * @param recordName the record
     * @return true if the new record could be created, false otherwise
     */
    boolean createLDAPRecord(@Nonnull DirContext context, @Nonnull IOC ioc, @Nonnull String recordName);

    /**
     * Removes all records for the given IOC from LDAP that are not contained in the valid records set.
     * @param context .
     * @param iocName .
     * @param facilityName .
     * @param validRecords .
     */
    void tidyUpIocEntryInLdap(@Nonnull DirContext context, String iocName, String facilityName, Set<Record> validRecords);

    /**
     * Removes the IOC entry from the LDAP context.
     *
     * @param context .
     * @param ioc .
     */
    void removeIocEntryFromLdap(DirContext context, IOC ioc);

    /**
     * Removes the IOC entry from the LDAP context.
     * @param context .
     * @param iocName .
     * @param facilityName .
     */
    void removeIocEntryFromLdap(@Nonnull DirContext context,  String iocName, String facilityName);


    /**
     * Removes the record entry from the LDAP context.
     * @param context .
     * @param ioc .
     * @param record .
     */
    void removeRecordEntryFromLdap(@Nonnull DirContext context,  IOC ioc,  Record record);


    /**
     * Retrieves the IOC object from LDAP to which the given record belongs to.
     * @param recordName .
     * @return the IOC containing this record
     */
    IOC getIOCForRecordName(@Nonnull String recordName);


    /**
     * Retrieves LDAP entries for the given query and search scope.
     *
     * @param searchRoot search root
     * @param filter the query filter
     * @param searchScope the search scope
     * @return the search result
     */
    LdapSearchResult retrieveSearchResult(String searchRoot, String filter, int searchScope);


}
