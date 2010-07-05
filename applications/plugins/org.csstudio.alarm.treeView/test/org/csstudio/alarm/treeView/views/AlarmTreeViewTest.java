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
package org.csstudio.alarm.treeView.views;

import junit.framework.Assert;

import org.csstudio.alarm.service.declaration.AlarmPreference;
import org.csstudio.alarm.treeView.model.IAlarmSubtreeNode;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * AlarmTreeView Test.
 *
 * @author bknerr
 * @author $Author$
 * @version $Revision$
 * @since 24.06.2010
 */
public class AlarmTreeViewTest {
    private static final String TEST_FACILITY = AlarmPreference.ALARMSERVICE_FACILITIES.getDefaultAsString();
    private static AlarmTreeView VIEW;

    @BeforeClass
    public static void openView() throws PartInitException {

        final IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        final IWorkbenchPage activePage = window.getActivePage();

        Assert.assertNotNull(activePage);

        activePage.showView(AlarmTreeView.getID());
        final IViewPart view = activePage.findView(AlarmTreeView.getID());

        Assert.assertTrue(view instanceof AlarmTreeView);
        VIEW = (AlarmTreeView) view;
    }

    @Test
    public void testView() {
        final IAlarmSubtreeNode node = VIEW.getRootNode();
        Assert.assertNotNull(node);

       // Assert.fail("LAUNCH CONFIG TEST FOR HUDSON");

    }

}
