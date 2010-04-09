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

import javax.naming.directory.DirContext;

import org.csstudio.utility.ldap.model.IOC;
import org.csstudio.utility.ldap.model.LdapContentModel;
import org.csstudio.utility.ldap.model.Record;
import org.csstudio.utility.ldap.reader.LdapSeachResultObserver;

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
     * @param ldapDataObserver the observer that fills the LDAPContent Model
     * @param searchRoot the search root for the LDAP lookup
     * @param filter the search filter the search filter for the LDAP lookup
     * @return the content model enriched with the results from the current lookup
     */
    LdapContentModel getEntries(LdapSeachResultObserver ldapDataObserver,
                                String searchRoot,
                                String filter);

    /**
     * Retrieves LDAP entries for the given query and search scope and fills the
     * content model of the search result observer.
     *
     * @param ldapDataObserver observer
     * @param searchRoot search root
     * @param filter the query filter
     * @param searchScope the search scope
     * @return the enriched (or new) content model
     */
    LdapContentModel getEntries(LdapSeachResultObserver ldapDataObserver,
                                String searchRoot,
                                String filter,
                                int searchScope);


    /**
     * @param ldapDataObserver
     * @param facilityName
     * @param iocName
     * @return
     * @throws InterruptedException
     */
    LdapContentModel getRecords(LdapSeachResultObserver ldapDataObserver,
                                String facilityName,
                                String iocName) throws InterruptedException;

    /**
     * @param context
     * @param ioc
     * @param recordName
     * @return
     */
    boolean createLDAPRecord(DirContext context, IOC ioc, String recordName);

    /**
     * @param context
     * @param iocName
     * @param facilityName
     * @param validRecords
     */
    void tidyUpIocEntryInLdap(DirContext context, String iocName, String facilityName, Set<Record> validRecords);

    /**
     * @param context
     * @param ioc
     */
    void removeIocEntryFromLdap(DirContext context, IOC ioc);

    /**
     * @param context
     * @param iocName
     * @param facilityName
     */
    void removeIocEntryFromLdap(DirContext context, String iocName, String facilityName);


    /**
     * @param context
     * @param ioc
     * @param record
     */
    void removeRecordEntryFromLdap(DirContext context, IOC ioc, Record record);


    /**
     * @param recordName
     * @return
     */
    IOC getIOCForRecordName(String recordName);


}
