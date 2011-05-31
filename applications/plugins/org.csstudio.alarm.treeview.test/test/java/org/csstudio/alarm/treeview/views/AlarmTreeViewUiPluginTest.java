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
package org.csstudio.alarm.treeview.views;

import static org.csstudio.utility.ldap.treeconfiguration.LdapEpicsAlarmcfgConfiguration.FACILITY;
import static org.csstudio.utility.ldap.treeconfiguration.LdapEpicsAlarmcfgConfiguration.UNIT;
import static org.csstudio.utility.ldap.treeconfiguration.LdapEpicsAlarmcfgConfiguration.VIRTUAL_ROOT;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.naming.InvalidNameException;
import javax.naming.NamingException;
import javax.naming.ldap.LdapName;

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.csstudio.alarm.service.AlarmServiceActivator;
import org.csstudio.alarm.service.declaration.AlarmPreference;
import org.csstudio.alarm.treeview.model.IAlarmSubtreeNode;
import org.csstudio.alarm.treeview.model.IAlarmTreeNode;
import org.csstudio.alarm.treeview.views.AlarmTreeView;
import org.csstudio.alarm.treeview.views.actions.AcknowledgeSecureAction;
import org.csstudio.alarm.treeview.views.actions.CreateComponentAction;
import org.csstudio.alarm.treeview.views.actions.CreateRecordAction;
import org.csstudio.alarm.treeview.views.actions.CssStripChartAction;
import org.csstudio.alarm.treeview.views.actions.DeleteNodeAction;
import org.csstudio.alarm.treeview.views.actions.RenameAction;
import org.csstudio.alarm.treeview.views.actions.RunCssAlarmDisplayAction;
import org.csstudio.alarm.treeview.views.actions.RunCssDisplayAction;
import org.csstudio.alarm.treeview.views.actions.SaveAsXmlAction;
import org.csstudio.alarm.treeview.views.actions.ShowHelpGuidanceAction;
import org.csstudio.alarm.treeview.views.actions.ShowHelpPageAction;
import org.csstudio.alarm.treeview.AlarmTreePlugin;
import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.utility.ldap.LdapTestHelper;
import org.csstudio.utility.ldap.LdapTestTreeBuilder;
import org.csstudio.utility.ldap.service.ILdapService;
import org.csstudio.utility.ldap.service.LdapServiceException;
import org.csstudio.utility.ldap.service.util.LdapUtils;
import org.csstudio.utility.treemodel.CreateContentModelException;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.ObjectPluginAction;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.common.collect.ImmutableSet;

/**
 * AlarmTreeView Test.
 *
 * @author bknerr
 * @author $Author$
 * @version $Revision$
 * @since 24.06.2010
 */
public class AlarmTreeViewUiPluginTest {

    private static final Logger LOG =
        CentralLogger.getInstance().getLogger(AlarmTreeViewUiPluginTest.class);

    private static AlarmTreeView VIEW;
    private static IWorkbenchPage ACTIVE_PAGE;
    private static ILdapService LDAP_SERVICE;
    private static Random RANDOM = new Random(System.currentTimeMillis());
    private static String EFAN_NAME = "Test" + String.valueOf(Math.abs(RANDOM.nextInt())) + "Efan1";

    private static Set<Class<? extends IAction>> COMMON_CONTEXT_MENU_ACTIONS =
        ImmutableSet.<Class<? extends IAction>>builder().add(AcknowledgeSecureAction.class)
                                                        .add(RunCssAlarmDisplayAction.class)
                                                        .add(RunCssDisplayAction.class)
                                                        .add(CssStripChartAction.class)
                                                        .add(ShowHelpGuidanceAction.class)
                                                        .add(ShowHelpPageAction.class)
                                                        .add(DeleteNodeAction.class)
                                                        .build();
    @SuppressWarnings("restriction")
    private static Set<Class<? extends IAction>> RECORD_CONTEXT_MENU_ACTIONS =
        ImmutableSet.<Class<? extends IAction>>builder().addAll(COMMON_CONTEXT_MENU_ACTIONS)
                                                       .add(RenameAction.class)
                                                       .add(ObjectPluginAction.class) // An action supplied via plugin extensions
                                                       .build();
    private static Set<Class<? extends IAction>> COMPONENT_CONTEXT_MENU_ACTIONS =
        ImmutableSet.<Class<? extends IAction>>builder().addAll(COMMON_CONTEXT_MENU_ACTIONS)
                                                       .add(CreateRecordAction.class)
                                                       .add(CreateComponentAction.class)
                                                       .add(RenameAction.class)
                                                       .build();
    private static Set<Class<? extends IAction>> FACILITY_CONTEXT_MENU_ACTIONS =
        ImmutableSet.<Class<? extends IAction>>builder().addAll(COMMON_CONTEXT_MENU_ACTIONS)
                                                       .add(CreateRecordAction.class)
                                                       .add(CreateComponentAction.class)
                                                       .add(SaveAsXmlAction.class)
                                                       .build();


    @BeforeClass
    public static void setUpViewAndService() throws PartInitException, InterruptedException {

        // Set the LDAP service to the LDAP test instance
        LDAP_SERVICE = AlarmTreePlugin.getDefault().getLdapService();
        LDAP_SERVICE.reInitializeLdapConnection(LdapTestHelper.LDAP_TEST_PREFS);

        // Set up the dynamic test alarm tree
        LdapTestTreeBuilder.createLdapEpicsAlarmcfgTestTree(LDAP_SERVICE, EFAN_NAME);

        // Set up the just created test facility name in the preferences
        setUpAlarmTreeViewPreferences(EFAN_NAME);

        final IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        ACTIVE_PAGE = window.getActivePage();
        Assert.assertNotNull(ACTIVE_PAGE);

        VIEW = (AlarmTreeView) ACTIVE_PAGE.showView(AlarmTreeView.getID());
        waitForJobs(); // wait for the jobs to terminate (retrieval of data from LDAP and via JMS)
        delay(5000); // wait for the tree to be displayed otherwise the actions can't be run
    }

    /**
     * Wait until all background tasks are complete.
     * @throws InterruptedException
     */
    private static void waitForJobs() throws InterruptedException {
       while (!Job.getJobManager().isIdle()) {
           delay(1000);
       }
    }
    /**
     * Process UI input but do not return for the
     * specified time interval.
     *
     * @param waitTimeMillis the number of milliseconds
     */
    private static void delay(final long waitTimeMillis) {
        final Display display = Display.getCurrent();

        // If this is the UI thread, then process input.
        if (display != null) {
            final long endTimeMillis = System.currentTimeMillis() + waitTimeMillis;
            while (System.currentTimeMillis() < endTimeMillis) {
                if (!display.readAndDispatch()) {
                    display.sleep();
                }
            }
            display.update();

        } else {
            try { // Otherwise, perform a simple sleep.
                Thread.sleep(waitTimeMillis);
            } catch (final InterruptedException e) {
                // Ignored.
                LOG.debug("Ignored interrupted exception from sleep thread.");
            }
        }
    }


    private static void setUpAlarmTreeViewPreferences(@Nonnull final String testValue) {
        final IEclipsePreferences prefs = new DefaultScope().getNode(AlarmServiceActivator.PLUGIN_ID);
        prefs.put(AlarmPreference.ALARMSERVICE_FACILITIES.getKeyAsString(), testValue);
    }


    @Test
    public void testViewAndRootNodePresent() throws InterruptedException {

        Assert.assertNotNull(VIEW.getRootNode());
    }

    /**
     * Tests the rename action and modifies LDAP test data of first component.
     *
     * @throws InterruptedException
     */
    // CHECKSTYLE OFF: MethodLength
    @Test
    public void testRenameAndSaveInLdapAction() throws InterruptedException {
        final TreeViewer viewer = VIEW.getViewer();

        // Trigger rename action on selection
        viewer.setSelection(new StructuredSelection(new Object[] {getComponentNode()}), true);
        final Action renameAction = VIEW.getRenameAction();
        Display.getCurrent().asyncExec(new Runnable() {
            @Override
            public void run() {
                renameAction.run();
            }
        });
        // Get access to the newly opened input dialog and set the name value field reflectively
        Display.getCurrent().asyncExec(new Runnable() {
            @Override
            public void run() {
                final Dialog dialog = findDialog();
                Assert.assertTrue(dialog instanceof InputDialog);
                try {
                    final Field value = dialog.getClass().getDeclaredField("value");
                    value.setAccessible(true);
                    value.set(dialog, "EcomRenamed");
                } catch (final Throwable e) {
                    Assert.fail("Private field of input dialog could not be accessed for testing.");
                    return;
                }
                dialog.close();
            }
        });
        delay(1000); // wait for the window to be closed
        // Persist the value in LDAP
        final Action saveInLdapAction = VIEW.getSaveInLdapAction();
        Display.getCurrent().asyncExec(new Runnable() {
            @Override
            public void run() {
                saveInLdapAction.run();
            }
        });
        Display.getCurrent().asyncExec(new Runnable() {
            @Override
            public void run() {
                final Dialog dialog = findDialog();
                Assert.assertTrue(dialog instanceof MessageDialog);
                dialog.close();
            }
        });
        waitForJobs(); // wait for LDAP write established
        delay(5000);
        final IAlarmSubtreeNode efan = (IAlarmSubtreeNode) getFacilityNode();
        try {
            final IAlarmTreeNode child = efan.getChild("EcomRenamed");
            Assert.assertNotNull(LDAP_SERVICE.lookup(child.getLdapName()));
        } catch (final NamingException e) {
            Assert.fail("Rename action had not been persisted.\n" + e.getMessage());
        }
    }
    // CHECKSTYLE ON: MethodLength

    @Nonnull
    public Dialog findDialog() {
        final Shell shell = VIEW.getSite().getShell();
        final Shell[] children = shell.getShells();
        Assert.assertEquals(1, children.length);
        final Object data = children[0].getData();
        Assert.assertTrue(data instanceof Dialog);
        return (Dialog) data;
    }

    @Test
    public void testContextMenuActionsPresentForFacilityNode() throws InterruptedException {
        testContextMenuActions(FACILITY_CONTEXT_MENU_ACTIONS, getFacilityNode());
    }

    @Test
    public void testContextMenuActionsPresentForComponentNode() throws InterruptedException {
        testContextMenuActions(COMPONENT_CONTEXT_MENU_ACTIONS, getComponentNode());
    }

    @Test
    public void testContextMenuActionsPresentForRecordNode() throws InterruptedException {
        testContextMenuActions(RECORD_CONTEXT_MENU_ACTIONS, getRecordNode());
    }

    private void testContextMenuActions(@Nonnull final Set<Class<? extends IAction>> expActions,
                                        @Nonnull final IAlarmTreeNode node) {
        final TreeViewer viewer = VIEW.getViewer();

        viewer.setSelection(new StructuredSelection(new Object[] {node}), true);

        final Menu menu = viewer.getTree().getMenu();
        // notify the listeners (the anonymous listener that builds the entries of the context menu)
        menu.notifyListeners(SWT.Show, new Event());

        final Set<Class<? extends IAction>> menuActions = createExistingActionsSet(menu);

        // check whether the sets' sizes are equal
        Assert.assertEquals(expActions.size(), menuActions.size());
        // remove any content that is not equal
        menuActions.retainAll(expActions);
        // and check the sizes again
        Assert.assertEquals(expActions.size(), menuActions.size());

    }

    @Nonnull
    private IAlarmTreeNode getFacilityNode() {
        //set a new selection on the root node
        final IAlarmSubtreeNode efan = (IAlarmSubtreeNode) VIEW.getRootNode().getChild(EFAN_NAME);
        Assert.assertNotNull(efan);
        return efan;
    }

    @Nonnull
    private IAlarmTreeNode getComponentNode() {
        final IAlarmTreeNode efan = getFacilityNode();
        IAlarmTreeNode ecom = ((IAlarmSubtreeNode) efan).getChild("TestEcom3");
        if (ecom == null) {
            ecom = ((IAlarmSubtreeNode) efan).getChild("EcomRenamed");
        }
        Assert.assertNotNull(ecom);
        return ecom;
    }
    @Nonnull
    private IAlarmTreeNode getRecordNode() {
        final IAlarmTreeNode efan = getFacilityNode();
        final IAlarmTreeNode eren = ((IAlarmSubtreeNode) efan).getChild("TestEren1");
        Assert.assertNotNull(eren);
        return eren;
    }



    /**
     * Creates a set of action class types that are contained in the context menu of the tree
     * @param menu
     * @return
     */
    @Nonnull
    private Set<Class<? extends IAction>> createExistingActionsSet(@Nonnull final Menu menu) {
        final MenuItem[] items = menu.getItems();
        final Set<Class<? extends IAction>> menuActions = new HashSet<Class<? extends IAction>>();
        for (final MenuItem menuItem : items) {
            final Object data = menuItem.getData();
            if (data instanceof ActionContributionItem) {
                final IAction action = ((ActionContributionItem) data).getAction();
                menuActions.add(action.getClass());
            }
        }
        return menuActions;
    }


    @AfterClass
    public static void closeView() throws InterruptedException {

        waitForJobs();

        // Unset the just created test facility name in the preferences
        setUpAlarmTreeViewPreferences(AlarmPreference.ALARMSERVICE_FACILITIES.getDefaultAsString());

        ACTIVE_PAGE.hideView(VIEW);

        final LdapName name =
            LdapUtils.createLdapName(FACILITY.getNodeTypeName(), EFAN_NAME,
                                     UNIT.getNodeTypeName(), UNIT.getUnitTypeValue());
        try {
            Assert.assertTrue(LDAP_SERVICE.removeComponent(VIRTUAL_ROOT, name));
        } catch (final InvalidNameException e) {
            Assert.fail("Unexpected exception:\n" + e.getMessage());
        } catch (final CreateContentModelException e) {
            Assert.fail("Content model could not be created:\n" + e.getMessage());
        } catch (LdapServiceException e) {
            Assert.fail("LDAP service exception:\n" + e.getMessage());
        }
        try {
            LDAP_SERVICE.lookup(name);
        } catch (final NamingException e) {
            Assert.assertEquals("[LDAP: error code 32 - No Such Object]", e.getMessage());
            return;
        } catch (final Exception e) {
            Assert.fail("Unknown exception");
        }
        Assert.fail("NamingException for 'error code 32' not thrown!");
    }
}
