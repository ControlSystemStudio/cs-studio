
/* 
 * Copyright (c) 2009 Stiftung Deutsches Elektronen-Synchrotron, 
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

package org.csstudio.websuite.dao;

import java.io.IOException;
import java.io.Writer;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import org.csstudio.websuite.WebSuiteActivator;
import org.csstudio.websuite.dataModel.AlarmMessage;
import org.csstudio.websuite.dataModel.AlarmMessageList;
import org.csstudio.websuite.dataModel.BasicMessage;
import org.csstudio.websuite.internal.PreferenceConstants;
import org.csstudio.websuite.jmsconnection.JmsMessageReceiver;
import org.csstudio.websuite.utils.BasicMessageComparator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.XMLOutputter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Data access object that provides the AlarmMessageList in different formats
 * Html (debugging only) and Xml are currently supported.
 *
 * @author ababic, Markus Moeller
 */
public class AlarmMessageListProvider {
   
    private static final Logger LOG = LoggerFactory.getLogger(AlarmMessageListProvider.class);
    
    /** This instance */
	private static AlarmMessageListProvider instance = null;
	
	/** Display parameters */
	private LinkedList<String> displayParameters = null;

	/** The list containg all received messages */
	private AlarmMessageList messageList = null;
	
	/** JMS message receiver / consumer */
	private JmsMessageReceiver jmsMessageReceiver = null;
	
	/** Comma separated lsit of all alarm topics */
	private final String alarmTopicList;
	
	/** Just the id of this plugin */
	public static final String PLUGIN_ID = "org.csstudio.alertviewer";

	/**
	 * It sets the columns that will be included in the response.
	 * For all valid columns check the configuration file.
	 * 
	 * @param params
	 */
	public void setDisplayParameters(LinkedList<String> params) {
		this.displayParameters = params;
	}

	
	/**
	 * Private constructor - singleton
	 * 
	 * @param defaultTopicSet
	 * @param params
	 */
	private AlarmMessageListProvider(String defaultTopicSet, LinkedList<String> params) {
	    
	    IPreferencesService preferences = Platform.getPreferencesService();
        
		messageList = new AlarmMessageList();
		jmsMessageReceiver = new JmsMessageReceiver(messageList);
		
		
		if(defaultTopicSet == null) {
			defaultTopicSet = preferences.getString(WebSuiteActivator.PLUGIN_ID,
			                                        PreferenceConstants.DEFAULT_TOPIC_SET,
			                                        "ALARM", null);
		}
		
		alarmTopicList = defaultTopicSet;
		
		jmsMessageReceiver.initializeJMSConnection(defaultTopicSet);
		if(params !=null){
			setDisplayParameters(params);
		}else{
			setDisplayParameters(getDefaultDisplayParameters());
		}
	}
	
	public void closeJms() {
	    if(jmsMessageReceiver != null) {
	        jmsMessageReceiver.stopJMSConnection();
	    }
	}
	
	/**
	 * Returns the current settings for display parameters (the column that are shown in the resposne)
	 * 
	 * @return
	 */
	private LinkedList<String> getDefaultDisplayParameters() {
	    
	    IPreferencesService preferences = Platform.getPreferencesService();
	    LinkedList<String> output = new LinkedList<String>();
		
	    int numberOfColumns= preferences.getInt(WebSuiteActivator.PLUGIN_ID, PreferenceConstants.COLUMN_0, 0, null);
		
		for(int i=1;i<=numberOfColumns;i++){
			output.add(preferences.getString(WebSuiteActivator.PLUGIN_ID, "column_" + i, "", null));
		}
		return output;
	}

	/**
	 * Method to create an instance.
	 * 
	 * @param defaultTopicSet If null defaultTopicSet from configuration file will be used
	 * @param displayParameters If null default display parameters from configuration file will be used
	 */
	public static void createInstance(String defaultTopicSet,LinkedList<String> displayParameters){
		if(instance == null){
			instance = new AlarmMessageListProvider(defaultTopicSet, displayParameters);
		}
	}
	
	/**
	 * Method to create an instance.
	 * 
	 * @param defaultTopicSet If null defaultTopicSet from configuration file will be used
	 */
	public static void createInstance(String defaultTopicSet){
		if(instance == null){
			instance = new AlarmMessageListProvider(defaultTopicSet, null);
		}
	}
	
	/**
	 * Method to create an instance.
	 * TopicSet and DisplayParameters will be retrieved from the configuration file (default)
	 */
	public static void createInstance(){
		if(instance == null){
			instance = new AlarmMessageListProvider(null, null);
		}
	}
	
	/**
	 * Method to create an instance.
	 * 
	 * @param displayParameters If null default display parameters from configuration file will be used
	 */
	public static void createInstance(LinkedList<String> displayParameters){
		if(instance == null){
			instance = new AlarmMessageListProvider(null, displayParameters);
		}
	}
	
	/**
	 * Returns an instance of the DAO
	 * 
	 * @return AlarmMessageListProvider(singleton)
	 */
	public static AlarmMessageListProvider getInstance(){
		return instance;
	}

	/**
	 * Returns the comma separated lsit of all alarm topics.
	 * 
	 * @return
	 */
	public String getAlarmTopicList() {
	    return alarmTopicList;
	}
	
	/**
	 * Returns the number of stored alarm messages.
	 * 
	 * @return
	 */
	public int countMessages() {
	    return messageList.getSize();
	}
	
	/**
	 * Deletes messages.
	 */
	public void deleteMessages(long timestamp) {
	    //TODO: Implement method
	}
	
    /**
     * Deletes messages.
     */
    public void deleteMessages(String timestamp) {
	    //TODO: Implement method
    }

    /**
     * 
     */
    public void deleteAllMessages() {
        messageList.deleteAllMessages();
    }
    
    /**
	 * Returns the message list in HTML format (for the servlet)
	 * 
	 * @param propertyList List of all parameters you want to display. If null default display parameters will be used.
	 * @return HTML file
	 */
	public String getAlarmMessageListAsHtml(List<String> propertyList, List<String> topicList, String command) {
		
	    StringBuffer output = new StringBuffer();
		BasicMessage basicMessage = null;
		String[] temp = null;
		String color = null;
		String value = null;
		String nextAttr = null;
		String topicName = null;
		boolean listTopics = false;
		boolean showEntry = false;
		
		if(command != null) {
		    if(command.toLowerCase().compareTo("list") == 0) {
		        listTopics = true;
		    }
		}
		
		if(propertyList == null) {
			propertyList = displayParameters;
		}
		
		output.append("<html>\n");
		output.append(" <body>\n");
		output.append(" <table border=\"1\">\n");
		
		if(listTopics) {
		    
		    output.append("  <tr>\n");
		    output.append("   <th>Alarm-Topics</th>\n");
            output.append("   <td>" + alarmTopicList + "</td>\n");
            output.append("  </tr>\n");

            output.append(" </table>\n");
		    output.append(" </body>\n");
		    output.append("</html>\n");

		    return output.toString();
		}
		
		try {
		    
		    Vector<? extends BasicMessage> ml = messageList.getJMSMessageList();
		    Collections.sort(ml, new BasicMessageComparator());
		} catch(Exception e) {
		    // output.append("<tr>\n");
            // output.append("<td>FEHLER</td>");
            // output.append("</tr>");
		}
		
		Iterator<? extends BasicMessage> iter = messageList.getJMSMessageList().iterator();
		
		while(iter.hasNext()) {
		   
		    try {
    		    
		        basicMessage = iter.next();
    		    
    		    if(topicList != null) {
    		        
    		        topicName = basicMessage.getHashMap().get("TOPICNAME");
    		        temp = topicName.split("://");
    		        if(temp.length == 2) {
    		            showEntry = topicList.contains(temp[1]);
    		        } else {
    		            showEntry = false;
    		        }
    		    } else {
    		        showEntry = true;
    		    }
    		    
    		    if(showEntry == false) {
    		        continue;
    		    }
    		    
    			color = basicMessage.getHashMap().get("SEVERITY");
    			if("MAJOR".equals(color)) {
    				if(!((AlarmMessage)basicMessage).isOutdated()) {
    					output.append("  <tr BGCOLOR='red'>\n");
    				} else {
    					output.append("  <tr BGCOLOR='Maroon'>\n");
    				}
    			} else if("MINOR".equals(color)) {
    			    if(!((AlarmMessage)basicMessage).isOutdated()) {
    					output.append("  <tr BGCOLOR='yellow'>\n");
    				} else {
    					output.append("  <tr BGCOLOR='olive'>\n");
    				}
    			} else if("NO_ALARM".equals(color)) {
    				if(!((AlarmMessage)basicMessage).isOutdated()) {
    					output.append("  <tr BGCOLOR='lime'>\n");
    				} else {
    					output.append("  <tr BGCOLOR='green'>\n");
    				}
    			} else {
    				if(!((AlarmMessage)basicMessage).isOutdated()) {
    					output.append("  <tr BGCOLOR='magenta'>\n");
    				} else {
    					output.append("  <tr BGCOLOR='DarkMagenta'>\n");
    				}
    			}
    			
    			Iterator<String> iterList = propertyList.iterator();
    			
    			while(iterList.hasNext()) {
    				
    			    output.append("   <td>");
    				nextAttr = iterList.next();
    				
    				value = basicMessage.getHashMap().get(nextAttr);
    				if(value !=null) {
    					output.append(value);
    				} else {
    					output.append("N/A");
    				}
    				
    				output.append("</td>\n");
    			}
    			
    			output.append("  </tr>\n");
		    } catch(Exception e) {
		        output.append("  <tr>\n");
		        output.append("   <td>FEHLER</td>");
		        output.append("  </tr>");
		    }
		}
		
		output.append(" </table>\n");
		output.append(" </body>\n");
		output.append("</html>\n");
		
		return output.toString();
	}
	
	/**
	 * Writes the XML file in the writer (for the servlet)
	 * 
	 * @param out Writer to write in
	 * @param parameterList List of all parameters you want to display. If null default display parameters will be used.
	 */
	public void getAlarmMessageListAsXml(Writer out, List<String> propertyList, List<String> topicList,
	                                     String command) {
		
	    XMLOutputter outputter = new XMLOutputter();
		BasicMessage basicMessage = null;
		Element alarmList = null;
		Document output = null;
		String[] temp = null;
		String nextAttr = null;
		String topicName = null;
		boolean listTopics = false;
		boolean showEntry = false;

        if(command != null) {
            if(command.toLowerCase().compareTo("list") == 0) {
                listTopics = true;
            }
        }
        
        if(listTopics) {
            
            alarmList = new Element("topiclist");
            output = new Document(alarmList);
            
            Element topics = new Element("topics");
            Element parameter = new Element("tag");
            parameter.setAttribute("key", "TOPICLIST");
            parameter.setAttribute("value", alarmTopicList);
            topics.addContent(parameter);
            alarmList.addContent(topics);
            
            try {
                outputter.output(output, out);
            } catch(IOException e) {
                LOG.error("Cannot write to buffer: " + e.getMessage());
            }

            return;
        }
        
		if(propertyList == null) {
		    propertyList = displayParameters;
		}

		alarmList = new Element("alarmlist");
	    output = new Document(alarmList);

		try {
		    
		    Vector<? extends BasicMessage> ml = messageList.getJMSMessageList();
		    Collections.sort(ml, new BasicMessageComparator());
		} catch(Exception e) {/* Can be ignored */}

        Iterator<? extends BasicMessage> iter = messageList.getJMSMessageList().iterator();

		while(iter.hasNext()) {
		    
		    try
		    {
    			Element alarmMessage = new Element("alarm");
    			basicMessage = iter.next();
    			
                if (((AlarmMessage)basicMessage).isOutdated()) {
                    continue;
                }

    			// Only messages with severity MAJOR or MINOR should be returned
    			String severity = basicMessage.getHashMap().get("SEVERITY");
    			if(severity != null) {
    			    
    			    if((severity.compareToIgnoreCase("MAJOR") != 0)
    			            && (severity.compareToIgnoreCase("MINOR") != 0)) {
    			        break;
    			    }
    			} else {
    			    break;
    			}
    			
                if(topicList != null) {
                    
                    topicName = basicMessage.getHashMap().get("TOPICNAME");
                    temp = topicName.split("://");
                    if(temp.length == 2) {
                        showEntry = topicList.contains(temp[1]);
                    } else {
                        showEntry = false;
                    }
                } else {
                    showEntry = true;
                }
                
                if(showEntry == false) {
                    continue;
                }

    			Element parameter = new Element("tag");
    			parameter.setAttribute("key", "OUTDATED");
    			if(((AlarmMessage)basicMessage).isOutdated()) {
    				parameter.setAttribute("value", "TRUE");
    			} else {
    				parameter.setAttribute("value","FALSE");
    			}
    			
    			alarmMessage.addContent(parameter);
    			
    			Iterator<String> iterList = propertyList.iterator();
    			while(iterList.hasNext()) {
    				
    			    nextAttr = iterList.next();
    				String value = basicMessage.getHashMap().get(nextAttr);
    				if(value != null) {
    					parameter= new Element("tag");
    					parameter.setAttribute("key", nextAttr);
    					parameter.setAttribute("value", value);
    					alarmMessage.addContent(parameter);
    				}
    			}
    			
    			alarmList.addContent(alarmMessage);
		    } catch(Exception e) {
		    	// Can be ignored
		    }
		}
		
		try {
			outputter.output(output, out);
		} catch(IOException e) {
			LOG.error("Cannot write to buffer: " + e.getMessage());
		}
	}
}
