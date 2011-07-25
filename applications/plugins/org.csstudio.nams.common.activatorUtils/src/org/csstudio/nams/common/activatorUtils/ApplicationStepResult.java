
/**
 * 
 */

package org.csstudio.nams.common.activatorUtils;

public enum ApplicationStepResult {
	/**
	 * Application completes all it tasks and wishes to go done now.
	 */
	DONE,

	/**
	 * Application request the Eclipse-application-restart mechanism.
	 */
	RESTART,

	/**
	 * Application request the Eclipse-application-relaunch mechanism.
	 */
	RELAUNCH,

	/**
	 * Application wish to normally continue working.
	 */
	CONTINUE,

	/**
	 * Application wishes to get reconfigured.
	 */
	RECONFIGURE
}