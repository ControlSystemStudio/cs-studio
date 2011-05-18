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
import javax.naming.ServiceUnavailableException;
import javax.naming.directory.Attributes;
import javax.naming.ldap.LdapName;

import org.csstudio.alarm.treeview.views.AbstractTreeModificationItem;
import org.csstudio.alarm.treeview.views.AlarmTreeModificationException;
import org.csstudio.alarm.treeview.AlarmTreePlugin;
import org.csstudio.utility.ldap.service.ILdapService;

/**
 * Creates the LDAP component with the given name.
 *
 * @author bknerr
 * @author $Author$
 * @version $Revision$
 * @since 17.06.2010
 */
final class CreateLdapEntryModificationItem extends AbstractTreeModificationItem {

    private final LdapName _newName;
    private final Attributes _attrs;

    /**
     * Constructor.
     * @param parent
     * @param newName
     * @param attrs
     * @param recordName
     */
    CreateLdapEntryModificationItem(@Nonnull final LdapName newName,
                                    @Nonnull final Attributes attrs) {
        _newName = new LdapName(newName.getRdns());
        _attrs = (Attributes) attrs.clone();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDescription() {
        return "CREATE LDAP ENTRY " + _newName.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void apply() throws AlarmTreeModificationException {
        final ILdapService service = AlarmTreePlugin.getDefault().getLdapService();
        if (service == null) {
            throw new AlarmTreeModificationException("Entry creation failed.",
                                                     new ServiceUnavailableException("LDAP service unavailable."));
        }
        if (!service.createComponent(_newName, _attrs)) {
            throw new AlarmTreeModificationException("CREATE RECORD for " + _newName.toString()
                    + " failed in LDAP.", null);
        }
    }
}
