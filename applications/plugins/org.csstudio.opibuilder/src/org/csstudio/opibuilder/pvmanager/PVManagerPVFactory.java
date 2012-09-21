package org.csstudio.opibuilder.pvmanager;

import org.csstudio.utility.pv.IPVFactory;

/**The factory to create a {@link PVManagerPV}.
 * @author Xihui Chen
 *
 */
public class PVManagerPVFactory implements IPVFactory {
	
	private PVManagerPVFactory(){
		
	}
	
	public synchronized static PVManagerPVFactory getInstance(){
		return new PVManagerPVFactory();
	}

	@Override
	public PVManagerPV createPV(String name) throws Exception {
		return new PVManagerPV(name, false, 50);
	}
	
	public PVManagerPV createPV(String name, boolean bufferAllValues, int updateDuration){
		return new PVManagerPV(name, bufferAllValues, updateDuration);
	}

}
