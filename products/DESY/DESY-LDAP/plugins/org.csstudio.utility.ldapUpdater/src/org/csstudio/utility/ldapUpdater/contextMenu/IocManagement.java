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
package org.csstudio.utility.ldapUpdater.contextMenu;

import java.util.Map;

import javax.annotation.Nonnull;
import javax.naming.InvalidNameException;
import javax.naming.ServiceUnavailableException;

import org.apache.log4j.Logger;
import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.platform.management.CommandParameters;
import org.csstudio.platform.management.CommandResult;
import org.csstudio.platform.management.IManagementCommand;
import org.csstudio.utility.ldap.treeconfiguration.LdapEpicsControlsConfiguration;
import org.csstudio.utility.ldapUpdater.LdapAccess;
import org.csstudio.utility.ldapUpdater.contextMenu.CommandEnumeration.IocModificationCommand;
import org.csstudio.utility.ldapUpdater.service.ILdapUpdaterService;
import org.csstudio.utility.ldapUpdater.service.impl.LdapUpdaterServiceImpl;


/**
 * IOC Management Commands for the context menu of the LDAP Updater product.
 *
 * @author bknerr 17.03.2010
 */
public class IocManagement implements IManagementCommand {

    private static final Logger LOG = CentralLogger.getInstance().getLogger(IocManagement.class);

    private static final ILdapUpdaterService LDAP_UPDATER_SERVICE = LdapUpdaterServiceImpl.INSTANCE;

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    @SuppressWarnings("unchecked")
    public final CommandResult execute(@Nonnull final CommandParameters parameters) {

        final Map<String, String> map = (Map<String, String>) parameters.get("ioc");
        final String command = (String) parameters.get("command");

        commandDispatchAndExecute(command, map);

        return CommandResult.createSuccessResult();
    }


    private void commandDispatchAndExecute(@Nonnull final String command, @Nonnull final Map<String, String> map) {

        final String iocName = map.get(LdapEpicsControlsConfiguration.IOC.getNodeTypeName());
        final String facilityName = map.get(LdapEpicsControlsConfiguration.FACILITY.getNodeTypeName());


        try {
            switch (IocModificationCommand.valueOf(command)) {
                case DELETE :
                    LDAP_UPDATER_SERVICE.removeIocEntryFromLdap(iocName,
                                                                facilityName);
                    break;
                case TIDY_UP : LDAP_UPDATER_SERVICE.tidyUpIocEntryInLdap(iocName,
                                                                         facilityName,
                                                                         LdapAccess.getValidRecordsForIOC(iocName));
                break;
                default : throw new AssertionError("Unknown Ioc Modification Command: " + command);
            }
        } catch (final InvalidNameException e) {
            LOG.error("Invalid name composition while accessing LDAP.", e);
        } catch (final InterruptedException e) {
            LOG.error("Interrupted.", e);
            Thread.currentThread().interrupt();
        } catch (final ServiceUnavailableException e) {
            LOG.error("LDAP service not available.", e);
        }
    }



}

