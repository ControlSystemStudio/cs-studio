package org.csstudio.askap.pvmanager.ice;


public class IceConnectionPayload {
	
    private boolean isConnected = false;;

    public IceConnectionPayload() {
    	
    }
    
    public boolean isConnected() {
    	return isConnected;
    }
    
    public void setIsConnected(boolean isConnected) {
    	this.isConnected = isConnected;
    }

}
