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
