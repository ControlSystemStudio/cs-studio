package org.csstudio.platform.internal.simpledal.local;

import org.csstudio.platform.CSSPlatformInfo;
import org.csstudio.platform.ExecutionService;
import org.csstudio.platform.internal.simpledal.ProcessVariableConnectionService;
import org.eclipse.core.runtime.Platform;

public class Environment {
	public static String getApplicationId() {
		return CSSPlatformInfo.getInstance().getApplicationId();
	}

	public static String getHostId() {
		return CSSPlatformInfo.getInstance().getHostId();
	}

	public static String getQualifiedHostname() {
		return CSSPlatformInfo.getInstance().getQualifiedHostname();
	}

	public static String getUserId() {
		return CSSPlatformInfo.getInstance().getUserId();
	}
	
	public static Integer getNumberOfActiveConnectors() {
		return ProcessVariableConnectionService.getInstance().getConnectorCount();
	}
	
	public static Long getMaxMemory() {
		return Runtime.getRuntime().maxMemory();		
	}
	
	public static Long getFreeMemory() {
		return Runtime.getRuntime().freeMemory();		
	}
	
	public static Long getTotalMemory() {
		return Runtime.getRuntime().totalMemory();
	}
	
	public static long getThreadCount() {
		return ExecutionService.getInstance().getThreadCounter().getCount();
	}
	
	public static long getSystemTime() {
		return System.currentTimeMillis();
	}
}
