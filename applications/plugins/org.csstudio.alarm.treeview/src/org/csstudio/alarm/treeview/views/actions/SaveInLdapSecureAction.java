/*
 * Copyright (c) 2010 Stiftung Deutsches Elektronen-Synchrotron, Member of the Helmholtz
 * Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS. WITHOUT WARRANTY OF ANY
 * KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE IN ANY RESPECT, THE USER ASSUMES
 * THE COST OF ANY NECESSARY SERVICING, REPAIR OR CORRECTION. THIS DISCLAIMER OF WARRANTY
 * CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE. NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER
 * EXCEPT UNDER THIS DISCLAIMER. DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES,
 * ENHANCEMENTS, OR MODIFICATIONS. THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION,
 * MODIFICATION, USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY AT
 * HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
package org.csstudio.alarm.treeview.views.actions;


import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Queue;

import javax.annotation.Nonnull;

import org.csstudio.alarm.treeview.localization.Messages;
import org.csstudio.alarm.treeview.views.AlarmTreeModificationException;
import org.csstudio.alarm.treeview.views.ITreeModificationItem;
import org.csstudio.alarm.treeview.views.dialog.DetailDialog;
import org.csstudio.auth.ui.security.AbstractUserDependentAction;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.IWorkbenchPartSite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Saves the recent tree modifications in LDAP.
 *
 *
 * @author bknerr
 * @author $Author$
 * @version $Revision$
 * @since 17.06.2010
 */
public final class SaveInLdapSecureAction extends AbstractUserDependentAction {
    
    private static final Logger LOG = LoggerFactory.getLogger(SaveInLdapSecureAction.class);
    
    private static final String RIGHT_ID = "alarmConfiguration"; //$NON-NLS-1$
    
    private final IWorkbenchPartSite _site;
    private final Queue<ITreeModificationItem> _ldapModifications;
    
    /**
     * Constructor.
     * @param site
     * @param ldapModifications
     */
    SaveInLdapSecureAction(@Nonnull final IWorkbenchPartSite site,
                           @Nonnull final Queue<ITreeModificationItem> ldapModifications) {
        super(RIGHT_ID);
        _site = site;
        _ldapModifications = ldapModifications;
    }
    
    @Override
    protected void doWork() {
        final List<String> notAppliedMods= new ArrayList<String>();
        String failedMod = ""; //$NON-NLS-1$
        final List<String> appliedMods = new ArrayList<String>();
        synchronized (_ldapModifications) {
            try {
                /*
                  Note: although a concurrent queue is utilized, it has to be explicitly inhibited
                  that items are added, removed, or modified by any other thread during the following
                  queue traversal for the 'save in LDAP' action.
                  Hence, a synchronized block is necessary on the queue - that nonetheless leaves a tiny time window in between the
                  the user's 'save in LDAP' activation and the start of this block for the queue to be
                  modified!
                 */
                while (!_ldapModifications.isEmpty()) {
                    final ITreeModificationItem item = _ldapModifications.peek();
                    failedMod = item.getDescription();
                    item.apply();
                    appliedMods.add(item.getDescription() + "\n"); //$NON-NLS-1$
                    _ldapModifications.remove();
                }

                final String summary = appliedMods.isEmpty() ? Messages.SaveInLdapSecureAction_NoModifications :
                                                               Messages.SaveInLdapSecureAction_AppliedModifications + appliedMods;
                DetailDialog.open(_site.getShell(), false, Messages.SaveInLdapSecureAction_StatusDialog_Title,
                                  appliedMods.size() + Messages.SaveInLdapSecureAction_StatusDialog_Text, summary);
                setEnabled(false);

            } catch (final AlarmTreeModificationException e) {
                for (final ITreeModificationItem item : _ldapModifications) {
                    notAppliedMods.add("\n-" + item.getDescription()); //$NON-NLS-1$
                }
                DetailDialog.open(_site.getShell(),
                                  true,
                                  Messages.SaveInLdapSecureAction_StatusDialog_Title,
                                  Messages.SaveInLdapSecureAction_Error_Text,
                                  NLS.bind(Messages.SaveInLdapSecureAction_10, appliedMods) + "\n"
                                          + NLS.bind(Messages.SaveInLdapSecureAction_11, failedMod) + "\n"
                                          + NLS.bind(Messages.SaveInLdapSecureAction_12,
                                                     notAppliedMods));
            } catch (final NoSuchElementException e) {
                LOG.error("Removal of first element in LDAP modification queue failed - empty queue?"); //$NON-NLS-1$
            }
        }
    }
}
