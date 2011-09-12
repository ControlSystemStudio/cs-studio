
/*
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchrotron,
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
 */

package org.csstudio.ams.alarmchainmanager;

import java.sql.Connection;

import org.csstudio.ams.Log;
import org.csstudio.ams.dbAccess.AmsConnectionFactory;
import org.csstudio.platform.ui.AbstractCssUiPlugin;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.osgi.framework.BundleContext;

public class AlarmChainManagerPlugin extends AbstractCssUiPlugin {

	/** The plug-in ID */
	public static final String PLUGIN_ID = "org.csstudio.ams.alarmchainmanager";

	/** The shared instance */
	private static AlarmChainManagerPlugin plugin;

	/** Connection object for the application database */
    private static Connection con = null;

    /** */
    private IWorkbenchWindow window = null;

    /** */
    private Display display = null;

    /**
	 * The constructor
	 */
	public AlarmChainManagerPlugin() {
	    plugin = this;
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static AlarmChainManagerPlugin getDefault() {
		return plugin;
	}

	public Shell getShell() {
	    
	    if(display != null) {
	        return display.getActiveShell();
	    }
	    
        return null;
	}

    public static Connection getConnection() {
        
        if(con == null) {
            try {
                con = AmsConnectionFactory.getApplicationDB();
            } catch(final Exception ex) {
                Log.log(Log.FATAL, ex);
            }
        }

        return con;
    }

    @Override
    protected void doStart(final BundleContext context) throws Exception {
        final IWorkbench workbench = PlatformUI.getWorkbench();
        window = workbench.getWorkbenchWindows()[0];
        display = window.getShell().getDisplay();
    }

    @Override
    protected void doStop(final BundleContext context) throws Exception {
        plugin = null;
    }

    @Override
    public String getPluginId() {
        return PLUGIN_ID;
    }
}
