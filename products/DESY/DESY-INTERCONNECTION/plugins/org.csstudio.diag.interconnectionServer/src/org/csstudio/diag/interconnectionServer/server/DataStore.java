package org.csstudio.diag.interconnectionServer.server;

import org.csstudio.platform.logging.CentralLogger;

public class DataStore {
	
	private static DataStore dataStoreInstance = null;
	private int maxMessageNumber = 50;
	private String[] messages = null;
	private int[] messageIds = null;
	private int actualIndex = 0; 
	
	
	
	private DataStore () {
		
		messages = new String[maxMessageNumber];
		messageIds = new int[maxMessageNumber];
		for ( int index = 0; index < maxMessageNumber; index++) {
			messages[index] = "";
			messageIds[index] = 0;
		}
	}
	
	public static DataStore getInstance() {
		//
		// get an instance of our singleton
		//
		if ( dataStoreInstance == null) {
			synchronized (DataStore.class) {
				if (dataStoreInstance == null) {
					dataStoreInstance = new DataStore();
				}
			}
		}
		return dataStoreInstance;
	}
	
	private void incrementIndex () {
			
		if ( actualIndex < (maxMessageNumber-1)) {
			actualIndex++;
		} else {
			actualIndex = 0;
		}
	}

	/**
	 * Store message ID and Message text
	 * @param messageId
	 * @param messageText
	 */
	synchronized public void storeData ( String messageId, String messageText, String hostName){
		int intMessageId = 0;
		
		intMessageId = Integer.parseInt(messageId);
		// try to find existing ID's
		for ( int index=0; index < maxMessageNumber; index++) {
			if ( intMessageId == messageIds[index]) {
				// found duplicates
				System.out.println("DUPLICATE Message from [" + hostName + "]:");
				System.out.println(" Old: ID:" + intMessageId + " Message " + messages[index]);
				System.out.println(" New: ID:" + intMessageId + " Message " + messageText);
				CentralLogger.getInstance().warn(this, "DUPLICATED Message from [" + hostName + "] OLD: " + messages[index] + " NEW: " + messageText);
			}
		}
		// Store message
		messages[actualIndex] = messageText;
		messageIds[actualIndex] = intMessageId;
		incrementIndex();
	}

}
