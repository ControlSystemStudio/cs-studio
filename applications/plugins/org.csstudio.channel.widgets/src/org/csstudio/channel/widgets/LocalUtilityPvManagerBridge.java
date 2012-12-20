package org.csstudio.channel.widgets;

import static org.epics.pvmanager.ExpressionLanguage.channel;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.csstudio.utility.pv.PVFactory;
import org.epics.pvmanager.PVManager;
import org.epics.pvmanager.PVWriter;

public class LocalUtilityPvManagerBridge {
	
	private PVWriter<Object> selectionWriter;
	private org.csstudio.utility.pv.PV selectionUtilityPv;
	
	public LocalUtilityPvManagerBridge(String pvName) {
		selectionWriter = PVManager.write(channel(pvName)).async();
		try {
			selectionUtilityPv = PVFactory.createPV(pvName);
			selectionUtilityPv.start();
		} catch (Exception ex) {
			// Do nothing
		}
		
		write("");
	}
	
	private static Executor executor = Executors.newSingleThreadExecutor();
	
	public void write(final Object obj) {
		if (selectionWriter != null) {
			selectionWriter.write(obj);
		}
		
		final org.csstudio.utility.pv.PV copy = selectionUtilityPv;
		if (copy != null) {
			executor.execute(new Runnable() {
				
				@Override
				public void run() {
					try {
						copy.setValue(obj);
					} catch (Exception e) {
					}
				}
			});
		}
	}
	
	
	public void close() {
		if (selectionWriter != null) {
			selectionWriter.close();
			selectionWriter = null;
		}
		
		if (selectionUtilityPv != null) {
			selectionUtilityPv.stop();
			selectionUtilityPv = null;
		}
	}

}
