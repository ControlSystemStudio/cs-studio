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

import org.csstudio.platform.CSSPlatformInfo;
import org.csstudio.platform.libs.dcf.actions.IAction;

/**
 * Implements a management action to query platform information from a
 * running CSS instance.
 * <p>
 * Right now, sends the following information:
 * <ul>
 *   <li>The host name of the system this CSS instance runs on.</li>
 *   <li>The user name of the logged in user.</li>
 *   <li>The CSS application ID.</li>
 * </ul>
 * 
 * @author Jörg Rathlev
 */
public class GetPlatformInformationAction implements IAction {

	/**
	 * Runs this action.
	 * 
	 * @param param ignored.
	 * @return a string containing the platform information for
	 * this CSS instance.
	 */
	public Object run(Object param) {
		final CSSPlatformInfo platform = CSSPlatformInfo.getInstance();
		final String result = "Hostname: " + platform.getHostId() + "\n"
			+ "User: " + platform.getUserId() + "\n"
			+ "Application Id: " + platform.getApplicationId();
		return result;
	}

}
