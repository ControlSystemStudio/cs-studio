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
package org.csstudio.sds.ui.internal.actions;

import java.util.HashMap;
import java.util.Map;

import org.csstudio.csdata.ProcessVariable;
import org.csstudio.sds.ui.internal.preferences.DefaultDisplayPreference;
import org.csstudio.sds.ui.runmode.RunModeService;
import org.csstudio.ui.util.AdapterUtil;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.handlers.HandlerUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Action to open a process variable in the default CSS display. The default
 * display is configured in the preferences.
 * 
 * @author Joerg Rathlev
 */
public class OpenPvInDefaultDisplayAction extends AbstractHandler {

	private static final Logger LOG = LoggerFactory
			.getLogger(OpenPvInDefaultDisplayAction.class);

	public Object execute(ExecutionEvent event) throws ExecutionException {
		IPath displayPath = DefaultDisplayPreference.DEFAULT_DISPLAY_PATH
				.getValue();
		boolean openAsShell = DefaultDisplayPreference.OPEN_AS_SHELL.getValue();
		String alias = DefaultDisplayPreference.DEFAULT_DISPLAY_ALIAS
				.getValue();
		RunModeService runner = RunModeService.getInstance();

		final ISelection selection = HandlerUtil.getActiveMenuSelection(event);
		final ProcessVariable[] pvs = AdapterUtil.convert(selection,
				ProcessVariable.class);
		for (ProcessVariable pv : pvs) {
			Map<String, String> aliases = new HashMap<String, String>();
			String pvname = pv.getName();
			aliases.put(alias, pvname);
			LOG.debug("Opening display " + displayPath + " with alias " + alias
					+ "=" + pvname);
			if (openAsShell) {
				runner.openDisplayShellInRunMode(displayPath, aliases);
			} else {
				runner.openDisplayViewInRunMode(displayPath, aliases);
			}
		}
		return null;
	}
}
