package org.csstudio.simplepv.pvmanager;

import java.util.concurrent.Executor;

import org.csstudio.simplepv.AbstractPVFactory;
import org.csstudio.simplepv.ExceptionHandler;
import org.csstudio.simplepv.IPV;

public class PVManagerPVFactory extends AbstractPVFactory {

	@Override
	public IPV createPV(String name, boolean readOnly, int maxUpdateRate, boolean bufferAllValues,
			Executor notificationThread, ExceptionHandler exceptionHandler) {
		// TODO Auto-generated method stub
		return null;
	}

	
}
