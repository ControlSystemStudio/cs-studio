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
package org.csstudio.alarm.treeview.ldap;

import javax.annotation.Nonnull;
import javax.naming.InvalidNameException;
import javax.naming.ldap.LdapName;

import org.csstudio.alarm.treeview.views.AbstractTreeModificationItem;
import org.csstudio.alarm.treeview.views.AlarmTreeModificationException;
import org.csstudio.alarm.treeview.AlarmTreePlugin;
import org.csstudio.utility.ldap.service.ILdapService;
import org.csstudio.utility.ldap.service.LdapServiceException;
import org.csstudio.utility.ldap.treeconfiguration.LdapEpicsAlarmcfgConfiguration;
import org.csstudio.utility.treemodel.CreateContentModelException;

/**
 * Modification item that deletes a given LDAP component including its subtree.
 *
 * @author bknerr
 * @author $Author$
 * @version $Revision$
 * @since 21.06.2010
 */
public final class DeleteRecursivelyModificationItem extends AbstractTreeModificationItem {

    private final LdapName _nodeName;

    /**
     * Constructor.
     * @param node
     * @param nodeName
     */
    DeleteRecursivelyModificationItem(@Nonnull final LdapName nodeName) {
        _nodeName = nodeName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public String getDescription() {
        return "DELETE NODE " + _nodeName.toString();
    }

    @Override
    public void apply() throws AlarmTreeModificationException {
        try {
            final ILdapService service = AlarmTreePlugin.getDefault().getLdapService();
            if (service == null) {
                throw new AlarmTreeModificationException("Removal failed due to unavailable LDAP service", null);
            }

            boolean isOk = service.removeComponent(LdapEpicsAlarmcfgConfiguration.VIRTUAL_ROOT, _nodeName);
            if (!isOk) {
                throw new AlarmTreeModificationException("Removal failed. LDAP query failed or model did not contain entry for "
                                                                 + _nodeName.toString(), null);
            }
        } catch (final InvalidNameException e) {
            throw new AlarmTreeModificationException("New name could not be constructed as LDAP name.", e);
        } catch (final CreateContentModelException e) {
            throw new AlarmTreeModificationException("Content model could not be constructed for subtree of " + _nodeName.toString(), e);
        } catch (LdapServiceException e) {
            throw new AlarmTreeModificationException("LDAP service exception when removing " + _nodeName.toString(), e);
        }
    }
}
