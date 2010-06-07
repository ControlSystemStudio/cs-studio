/*
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron,
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
package org.csstudio.utility.ldapUpdater.action;

import javax.annotation.Nonnull;

import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.platform.management.CommandParameters;
import org.csstudio.platform.management.CommandResult;
import org.csstudio.platform.management.IManagementCommand;
import org.csstudio.utility.ldapUpdater.LdapUpdater;

/**
 * Command to start the tidy up mechanism for LDAP.
 * This action scans the IOC file directory for existing IOC record files and compares to the IOC entries in the LDAP system.
 * IOC names found in the LDAP that are not present in the directory are removed from LDAP including all records.
 *
 * @author bknerr
 */
public class TidyUpLdapAction implements IManagementCommand {

	/* (non-Javadoc)
	 * @see org.csstudio.platform.management.IManagementCommand#execute(org.csstudio.platform.management.CommandParameters)
	 */
	@Override
	@Nonnull
    public final CommandResult execute(@Nonnull final CommandParameters parameters) {

	      final LdapUpdater ldapUpdater = LdapUpdater.INSTANCE;
	        try {
	            if (!ldapUpdater.isBusy()){
	                ldapUpdater.tidyUpLdapFromIOCFiles();
	            }else{
	                return CommandResult.createMessageResult("ldapUpdater is busy for max. 150 s (was probably started by timer). Try later!");
	            }

	        } catch (final Exception e) {
	            e.printStackTrace();
	            CentralLogger.getInstance().error (this, "\"" + e.getCause() + "\"" + "-" + "Exception while running ldapUpdater" );
	        }
	        return CommandResult.createSuccessResult();
	}

}
