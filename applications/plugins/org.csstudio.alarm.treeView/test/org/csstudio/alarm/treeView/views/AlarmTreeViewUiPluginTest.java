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

import static org.csstudio.utility.ldap.treeconfiguration.LdapEpicsAlarmcfgConfiguration.COMPONENT;
import static org.csstudio.utility.ldap.treeconfiguration.LdapEpicsAlarmcfgConfiguration.FACILITY;
import static org.csstudio.utility.ldap.treeconfiguration.LdapEpicsAlarmcfgConfiguration.RECORD;
import static org.csstudio.utility.ldap.treeconfiguration.LdapEpicsAlarmcfgConfiguration.UNIT;
import static org.csstudio.utility.ldap.treeconfiguration.LdapEpicsAlarmcfgConfiguration.VIRTUAL_ROOT;
import static org.csstudio.utility.ldap.treeconfiguration.LdapFieldsAndAttributes.ATTR_FIELD_OBJECT_CLASS;
import static org.csstudio.utility.ldap.treeconfiguration.LdapFieldsAndAttributes.ATTR_VAL_COM_OBJECT_CLASS;
import static org.csstudio.utility.ldap.treeconfiguration.LdapFieldsAndAttributes.ATTR_VAL_FAC_OBJECT_CLASS;
import static org.csstudio.utility.ldap.treeconfiguration.LdapFieldsAndAttributes.ATTR_VAL_REC_OBJECT_CLASS;

import java.util.Random;
import java.util.concurrent.Delayed;

import javax.annotation.Nonnull;
import javax.naming.InvalidNameException;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttributes;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.csstudio.alarm.service.AlarmServiceActivator;
import org.csstudio.alarm.service.declaration.AlarmPreference;
import org.csstudio.alarm.treeView.AlarmTreePlugin;
import org.csstudio.alarm.treeView.model.IAlarmSubtreeNode;
import org.csstudio.alarm.treeView.model.IAlarmTreeNode;
import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.utility.ldap.LdapTestHelper;
import org.csstudio.utility.ldap.service.ILdapService;
import org.csstudio.utility.ldap.treeconfiguration.EpicsAlarmcfgTreeNodeAttribute;
import org.csstudio.utility.ldap.utils.LdapUtils;
import org.csstudio.utility.treemodel.CreateContentModelException;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.internal.win32.OS;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.junit.AfterClass;
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
public class AlarmTreeViewUiPluginTest {

    private static final Logger LOG =
        CentralLogger.getInstance().getLogger(AlarmTreeViewUiPluginTest.class);

    private static AlarmTreeView VIEW;
    private static IWorkbenchPage ACTIVE_PAGE;
    private static ILdapService LDAP_SERVICE;
    private static Random RANDOM = new Random(System.currentTimeMillis());
    private static String EFAN_NAME = "Test" + String.valueOf(Math.abs(RANDOM.nextInt())) + "Efan1";

    private static Attributes EFAN_ATTRS = new BasicAttributes();
    private static Attributes ECOM_ATTRS = new BasicAttributes();
    private static Attributes EREN_ATTRS = new BasicAttributes();

    private static final String ATTR_TEST_CONTENT = "TestContent";

    static {
        EFAN_ATTRS.put(ATTR_FIELD_OBJECT_CLASS, ATTR_VAL_FAC_OBJECT_CLASS);
        ECOM_ATTRS.put(ATTR_FIELD_OBJECT_CLASS, ATTR_VAL_COM_OBJECT_CLASS);
        EREN_ATTRS.put(ATTR_FIELD_OBJECT_CLASS, ATTR_VAL_REC_OBJECT_CLASS);

        for (final String attrID : EpicsAlarmcfgTreeNodeAttribute.getLdapAttributes()) {
            ECOM_ATTRS.put(attrID, ATTR_TEST_CONTENT);
            EREN_ATTRS.put(attrID, ATTR_TEST_CONTENT);
        }
    }


    @BeforeClass
    public static void setUpViewAndService() throws PartInitException, InterruptedException {

        // Set the LDAP service to the LDAP test instance
        LDAP_SERVICE = AlarmTreePlugin.getDefault().getLdapService();
        LDAP_SERVICE.reInitializeLdapConnection(LdapTestHelper.LDAP_TEST_PREFS);

        // Set up the dynamic test alarm tree
        setUpCreateComponents();

        // Set up the just created test facility name in the preferences
        setUpAlarmTreeViewPreferences(EFAN_NAME);

        final IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        ACTIVE_PAGE = window.getActivePage();
        Assert.assertNotNull(ACTIVE_PAGE);


        waitForJobs();
        VIEW = (AlarmTreeView) ACTIVE_PAGE.showView(AlarmTreeView.getID());
        waitForJobs();
        delay(5000);
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


    private static void setUpCreateComponents() {
        try {
            final LdapName name =
                LdapUtils.createLdapName(FACILITY.getNodeTypeName(), EFAN_NAME,
                                         UNIT.getNodeTypeName(), UNIT.getUnitTypeValue());
            Assert.assertTrue(LDAP_SERVICE.createComponent(name, EFAN_ATTRS));

            name.add(new Rdn(RECORD.getNodeTypeName(), "TestEren1"));
            Assert.assertTrue(LDAP_SERVICE.createComponent(name, EREN_ATTRS));

            name.remove(name.size() - 1);
            name.add(new Rdn(COMPONENT.getNodeTypeName(), "TestEcom1"));
            Assert.assertTrue(LDAP_SERVICE.createComponent(name, ECOM_ATTRS));

            name.add(new Rdn(RECORD.getNodeTypeName(), "TestEren2"));
            Assert.assertTrue(LDAP_SERVICE.createComponent(name, EREN_ATTRS));

            name.remove(name.size() - 1);
            name.add(new Rdn(COMPONENT.getNodeTypeName(), "TestEcom2"));
            Assert.assertTrue(LDAP_SERVICE.createComponent(name, ECOM_ATTRS));

            name.add(new Rdn(RECORD.getNodeTypeName(), "TestEren3"));
            Assert.assertTrue(LDAP_SERVICE.createComponent(name, EREN_ATTRS));

            name.remove(name.size() - 1);
            name.add(new Rdn(RECORD.getNodeTypeName(), "TestEren4"));
            Assert.assertTrue(LDAP_SERVICE.createComponent(name, EREN_ATTRS));


            final LdapName name2 =
                LdapUtils.createLdapName(FACILITY.getNodeTypeName(), EFAN_NAME,
                                         UNIT.getNodeTypeName(), UNIT.getUnitTypeValue());

            name2.add(new Rdn(COMPONENT.getNodeTypeName(), "TestEcom3"));
            Assert.assertTrue(LDAP_SERVICE.createComponent(name2, ECOM_ATTRS));

            name2.add(new Rdn(COMPONENT.getNodeTypeName(), "TestEcom4"));
            Assert.assertTrue(LDAP_SERVICE.createComponent(name2, ECOM_ATTRS));

            name2.add(new Rdn(RECORD.getNodeTypeName(), "TestEren5"));
            Assert.assertTrue(LDAP_SERVICE.createComponent(name2, EREN_ATTRS));

            name2.remove(name2.size() - 1);
            name2.add(new Rdn(RECORD.getNodeTypeName(), "TestEren6"));
            Assert.assertTrue(LDAP_SERVICE.createComponent(name2, EREN_ATTRS));

        } catch (final InvalidNameException e) {
            Assert.fail("LDAP name composition failed.");
        }
    }


    @Test
    public void testView() throws InterruptedException {

        final IAlarmSubtreeNode node = VIEW.getRootNode();
        Assert.assertNotNull(node);
//        final IAlarmTreeNode child = node.getChild(EFAN_NAME);
//        Assert.assertNotNull(child);
    }

    @Test
    public void testRenameAction() {
        final Menu menu = VIEW.getViewer().getTree().getMenu();
        Listener[] listeners = menu.getListeners(SWT.Show);
        for (final Listener listener : listeners) {
            System.out.println("HUHU");
            listener.handleEvent(new Event());
        }
        listeners = menu.getListeners(SWT.BUTTON3);
        for (final Listener listener : listeners) {
            System.out.println("HUHU");
        }

        for (final MenuItem item : menu.getItems()) {
            System.out.println("HAHA");
        }
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
