package org.csstudio.diag.interconnectionServer.server;
/* 
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchroton, 
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS. 
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED 
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND 
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE 
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, 
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR 
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE 
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR 
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE. 
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS, 
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION, 
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS 
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY 
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */

import java.util.Hashtable;

/**
 * List of possible 'known' tags in alarm messages
 * 
 * @author Matthias Clausen
 *
 */
public class TagList {
	
	public static TagList tagListInstance = null;
	private Hashtable<String,TagProperties>	tagList	= null;
	private Hashtable<String,Integer>	messageTypes	= null;
	private Hashtable<String,Integer>	replyTypes	= null;
	
	public static final int ALARM_MESSAGE = 1;
	public static final int ALARM_STATUS_MESSAGE = 2;
	public static final int SYSTEM_LOG_MESSAGE = 3;
	public static final int APPLICATION_LOG_MESSAGE = 4;
	public static final int EVENT_MESSAGE = 5;
	public static final int STATUS_MESSAGE = 6;
	public static final int BEACON_MESSAGE = 7;
	public static final int BEACON_MESSAGE_SELECTED = 8;
	public static final int BEACON_MESSAGE_NOT_SELECTED = 9;
	public static final int UNKNOWN_MESSAGE = 10;
	public static final int PUT_LOG_MESSAGE = 11;
	public static final int TEST_COMMAND = 12;
	
	public static final int REPLY_TYPE_DONE = 1;
	public static final int REPLY_TYPE_OK = 2;
	public static final int REPLY_TYPE_ERROR = 3;
	public static final int REPLY_TYPE_CMD_UNKNOWN = 4;
	public static final int REPLY_TYPE_CMD_MISSING = 5;
	public static final int REPLY_TYPE_REFUSED = 6;
	public static final int REPLY_TYPE_SELECTED = 7;
	public static final int REPLY_TYPE_NOT_SELECTED = 8;
	
	
	public TagList () {
		//
		// initialize hash table
		//
		tagList  = new Hashtable<String,TagProperties>();
		messageTypes = new Hashtable<String,Integer>();
		replyTypes = new Hashtable<String,Integer>();

		fillTagList();
		fillMessageTypes();
		fillReplyTypes();
	}
	
	public static TagList getInstance() {
		//
		// get an instance of our sigleton
		//
		if ( tagListInstance == null) {
			synchronized (TagList.class) {
				if (tagListInstance == null) {
					tagListInstance = new TagList();
				}
			}
		}
		return tagListInstance;
	}
	
	private void fillMessageTypes() {
		
		messageTypes.put("alarm", 				ALARM_MESSAGE);
		messageTypes.put("alarmStatus", 		ALARM_STATUS_MESSAGE);
		messageTypes.put("sysLog", 				SYSTEM_LOG_MESSAGE);
		messageTypes.put("sysMsg", 				SYSTEM_LOG_MESSAGE);
		messageTypes.put("appLog", 				APPLICATION_LOG_MESSAGE);
		messageTypes.put("event", 				EVENT_MESSAGE);
		messageTypes.put("status", 				STATUS_MESSAGE);
		messageTypes.put("beacon", 				BEACON_MESSAGE);
		messageTypes.put("beaconSelected", 		BEACON_MESSAGE_SELECTED);
		messageTypes.put("beaconNotSelected", 	BEACON_MESSAGE_NOT_SELECTED);
		messageTypes.put("unknown", 			UNKNOWN_MESSAGE);
		messageTypes.put("putLog", 				PUT_LOG_MESSAGE);
		messageTypes.put("TCom", 				TEST_COMMAND);
	}
	
	private void fillReplyTypes() {
			
		replyTypes.put("done", 			REPLY_TYPE_DONE);
		replyTypes.put("ok", 			REPLY_TYPE_ERROR);
		replyTypes.put("error", 		REPLY_TYPE_ERROR);
		replyTypes.put("cmdUnknown", 	REPLY_TYPE_CMD_UNKNOWN);
		replyTypes.put("cmdMissing", 	REPLY_TYPE_CMD_MISSING);
		replyTypes.put("refused", 		REPLY_TYPE_REFUSED);
		replyTypes.put("selected", 		REPLY_TYPE_SELECTED);
		replyTypes.put("notSelected", 	REPLY_TYPE_NOT_SELECTED);
	}
	
	private void fillTagList() {
		//
		// get tags and tag properties
		//
		TagProperties tagProperties = new TagProperties( false, "NONE");  	// (isCommand , command String)
		tagProperties.setIsCommand( true);
		tagProperties.setIsSequentialCommand( true); // to not start independent thread
		tagProperties.setTagType ( PreferenceProperties.TAG_TYPE_LOG_SERVER_REPLY);
		tagList.put( PreferenceProperties.TAG_LOG_SERVER_REPLY, tagProperties);
		
		tagProperties = new TagProperties( );  	// (isCommand , command String)
		tagProperties.setIsCommand( true);
		tagProperties.setIsSequentialCommand( true); // to not start independent thread
		tagProperties.setTagType ( PreferenceProperties.TAG_TYPE_IS_ID);
		tagList.put( PreferenceProperties.TAG_IS_ID, tagProperties);
		
		tagProperties = new TagProperties( );  	// (isCommand , command String)
		tagProperties.setIsCommand( true);
		tagProperties.setIsSequentialCommand( true); // to not start independent thread
		tagProperties.setTagType ( PreferenceProperties.TAG_TYPE_IS_TYPE);
		tagList.put( PreferenceProperties.TAG_IS_TYPE, tagProperties);

		tagProperties = new TagProperties( );  	// (isCommand , command String)
		tagProperties.setIsCommand( true);
		tagProperties.setIsSequentialCommand( true); // to not start independent thread
		tagProperties.setTagType ( PreferenceProperties.TAG_TYPE_IS_REPLY);
		tagList.put( PreferenceProperties.TAG_IS_REPLY, tagProperties);
		
	}
	
	public int getTagType( String attribute) {
		//
		// find attribute in tag list and return
		//
		TagProperties theseProperties = null;
		if ( tagList.containsKey( attribute)) {
			theseProperties = (TagProperties)tagList.get( attribute);
		} else {
			return -1;
		}
		return theseProperties.getTagType();
	}
	
	public boolean isCommand( String attribute) {
		//
		// find attribute in tag list and return
		//
		TagProperties theseProperties = null;
		if ( tagList.containsKey( attribute)) {
			theseProperties = (TagProperties)tagList.get( attribute);
		} else {
			return false;
		}
		return theseProperties.getIsCommand();
	}
	
	public boolean isSequentialCommand( String attribute) {
		//
		// find attribute in tag list and return
		//
		TagProperties theseProperties = null;
		if ( tagList.containsKey( attribute)) {
			theseProperties = (TagProperties)tagList.get( attribute);
		} else {
			return false;
		}
		return theseProperties.getIsSequentialCommand();
	}
	
	public TagProperties getTagProperties( String attribute) {
		//
		// find attribute in tag list and return
		//
		TagProperties theseProperties = null;
		if ( (attribute != null) && tagList.containsKey( attribute)) {
			theseProperties = (TagProperties)tagList.get( attribute);
		} else {
			return null;
		}
		return theseProperties;
	}
	
	public int getMessageType ( String messageTypeString) {
		int messageType = UNKNOWN_MESSAGE;
		Integer semiInt = null;
		if ( ( messageTypeString != null) && messageTypes.containsKey(messageTypeString)) {
			semiInt = (Integer)messageTypes.get( messageTypeString);
			messageType = (int)semiInt;
		}
		return messageType;
	}
	
	public int getReplyType ( String messageTypeString) {
		int replyType = REPLY_TYPE_CMD_UNKNOWN;
		Integer semiInt = null;
		if ( ( messageTypeString != null) && replyTypes.containsKey(messageTypeString)) {
			semiInt = (Integer)replyTypes.get( messageTypeString);
			replyType = (int)semiInt;
		}
		return replyType;
	}
	
	
	public class TagProperties {
		boolean isCommand = false;
		boolean isSequentialCommand = false;
		String commandString = "NONE";
		int tagType = -1;
		
		public TagProperties ( ) {

		}
		
		public TagProperties ( boolean isCommand, String command) {
			this.isCommand = isCommand;
			this.commandString = command;
		}
		
		public void setIsCommand ( boolean command) {
			this.isCommand = command;
		}
		
		public boolean getIsCommand ( ) {
			return this.isCommand;
		}
		
		public void setIsSequentialCommand ( boolean command) {
			this.isSequentialCommand = command;
		}
		
		public boolean getIsSequentialCommand ( ) {
			return this.isSequentialCommand;
		}
		
		public void setTagType ( int type) {
			this.tagType = type;
		}
		
		public int getTagType ( ) {
			return this.tagType;
		}
	}

}
