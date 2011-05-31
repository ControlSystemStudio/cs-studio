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

import java.net.MalformedURLException;
import java.net.URL;

import javax.annotation.Nonnull;

import org.apache.log4j.Logger;
import org.csstudio.alarm.treeview.model.IAlarmTreeNode;
import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.utility.ldap.treeconfiguration.EpicsAlarmcfgTreeNodeAttribute;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWebBrowser;

/**
 * Show help page action.
 *
 * @author bknerr
 * @author $Author$
 * @version $Revision$
 * @since 14.06.2010
 */
public final class ShowHelpPageAction extends Action {
    private static final Logger LOG =
        CentralLogger.getInstance().getLogger(ShowHelpPageAction.class);
    private final TreeViewer _viewer;

    /**
     * Constructor.
     * @param viewer
     */
    ShowHelpPageAction(@Nonnull final TreeViewer viewer) {
        _viewer = viewer;
    }

    @Override
    public void run() {
        final IStructuredSelection selection = (IStructuredSelection) _viewer.getSelection();
        final Object selected = selection.getFirstElement();
        if (selected instanceof IAlarmTreeNode) {
            final IAlarmTreeNode node = (IAlarmTreeNode) selected;
            URL helpPage;
            try {
                helpPage = new URL(node.getInheritedPropertyWithUrlProtocol(EpicsAlarmcfgTreeNodeAttribute.HELP_PAGE));
            } catch (final MalformedURLException e1) {
                LOG.warn("URL property of node " + node.getName() + " was malformed.");
                helpPage = null;
            }
            if (helpPage != null) {
                try {
                    // Note: we have to pass a browser id here to work
                    // around a bug in eclipse. The method documentation
                    // says that createBrowser accepts null but it will
                    // throw a NullPointerException.
                    // See https://bugs.eclipse.org/bugs/show_bug.cgi?id=194988
                    final IWebBrowser browser =
                        PlatformUI.getWorkbench().getBrowserSupport().createBrowser("workaround");
                    browser.openURL(helpPage);
                } catch (final PartInitException e) {
                    LOG.error("Failed to initialize workbench browser.", e);
                }
            }
        }
    }
}
