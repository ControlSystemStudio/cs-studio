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
 package org.csstudio.utility.managementactions.actions;

import java.util.Map;

import org.csstudio.platform.libs.dcf.actions.IAction;
import org.csstudio.platform.logging.CentralLogger;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

/**
 * Implements the &quot;Send message&quot; management action. The
 * message received from the remote CSS instance is displayed to the
 * user by this action.
 * 
 * @author Anze Vodovnik, Joerg Rathlev
 */
public class ActionOpenMessage implements IAction {
	
	private final CentralLogger log = CentralLogger.getInstance();

	/**
	 * Runs this action.
	 * 
	 * @param param the parameter object received from the remote CSS
	 *        instance. This must be a {@code Map<String, String>} which
	 *        must contain the message to be displayed under the key
	 *        &quot;{@code Message}&quot;.
	 * @return {@code null}.
	 */
	public Object run(Object param) {
		if(!(param instanceof Map)) {
			log.warn(this, "Parameter object is not of type Map.");
			return null;
		}
		final Object msgObject = ((Map) param).get("Message");
		if (!(msgObject instanceof String)) {
			log.warn(this, "Parameters do not contain a message.");
			return null;
		}
		final String message = (String) msgObject;

		final Display d = Display.getDefault();
		d.asyncExec(new Runnable() {
			public void run() {
				MessageDialog.openInformation(d.getActiveShell(),
						"Management Message", message);
			}
		});
		return null;
	}

}
