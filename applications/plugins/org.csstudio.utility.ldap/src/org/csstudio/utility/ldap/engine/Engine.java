package org.csstudio.utility.ldap.engine;

public class Engine extends Thread {

	private static Engine thisEngine = null;
	/**
	 * @param args
	 */
	public void run () {
		
		
	}
	
	private Engine() {
    	// absicherung
    }
    
    public static Engine getInstance() {
		//
		// get an instance of our sigleton
		//
		if ( thisEngine == null) {
			synchronized (Engine.class) {
				if (thisEngine == null) {
					thisEngine = new Engine();
				}
			}
		}
		return thisEngine;
	}

}
