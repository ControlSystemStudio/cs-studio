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
package org.csstudio.alarm.treeView.ldap;

import javax.annotation.Nonnull;
import javax.naming.InvalidNameException;
import javax.naming.NamingException;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;

import org.csstudio.alarm.treeView.model.IAlarmTreeNode;
import org.csstudio.alarm.treeView.views.AbstractTreeModificationItem;
import org.csstudio.alarm.treeView.views.AlarmTreeModificationException;
import org.csstudio.alarm.treeview.AlarmTreePlugin;
import org.csstudio.utility.ldap.service.ILdapService;

/**
 * Rename modification item. E.g. triggered when 'save in LDAP' action is performed.
 *
 * @author bknerr
 * @author $Author$
 * @version $Revision$
 * @since 21.06.2010
 */
public final class RenameModificationItem extends AbstractTreeModificationItem {

    private final IAlarmTreeNode _node;
    private final String _newName;
    private final LdapName _newLdapName;
    private final LdapName _oldLdapName;

    /**
     * Constructor.
     * @param node
     * @param newName
     * @param newLdapName
     * @param oldLdapName
     */
    RenameModificationItem(@Nonnull final IAlarmTreeNode node,
                           @Nonnull final String newName,
                           @Nonnull final LdapName newLdapName,
                           @Nonnull final LdapName oldLdapName) {
        _node = node;
        _newName = newName;
        _newLdapName = newLdapName;
        _oldLdapName = oldLdapName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void apply() throws AlarmTreeModificationException {
        try {
            _newLdapName.remove(_newLdapName.size() - 1);
            final Rdn rdn = new Rdn(_node.getTreeNodeConfiguration().getNodeTypeName(), _newName);
            _newLdapName.add(rdn);

            final ILdapService service = AlarmTreePlugin.getDefault().getLdapService();
            if (service == null) {
                throw new AlarmTreeModificationException("Rename failed.", null);
            }
            service.rename(_oldLdapName, _newLdapName);
        } catch (final InvalidNameException e) {
            throw new AlarmTreeModificationException("New name could not be constructed as LDAP name.", e);
        } catch (final NamingException e) {
            throw new AlarmTreeModificationException("LDAP rename action failed.", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDescription() {
        return "RENAME " + _oldLdapName.toString() + " to " + _newName;
    }
}
