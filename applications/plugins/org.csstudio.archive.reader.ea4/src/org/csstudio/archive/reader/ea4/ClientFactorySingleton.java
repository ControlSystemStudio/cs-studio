package org.csstudio.archive.reader.ea4;

public class ClientFactorySingleton {
	
	private static ClientFactorySingleton theInstance;
	
	protected ClientFactorySingleton() {
		
		// Start the pvAccess client side.
    	org.epics.pvaccess.ClientFactory.start();
 
	}
	
	public static ClientFactorySingleton getInstance(){
		
		if(theInstance == null){
			theInstance = new ClientFactorySingleton();
		}
		return theInstance;
	}

}
