
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
 *
 */

package org.csstudio.websuite.dao;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.LinkedList;

import org.csstudio.websuite.WebSuiteActivator;
import org.csstudio.websuite.dataModel.BasicMessage;
import org.csstudio.websuite.internal.PreferenceConstants;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.XMLOutputter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Data access object that provides the ChannelMessage in different formats
 * Html (debugging only) and Xml are currently supported.
 * 
 * @author ababic, Markus Moeller
 */
public class ChannelMessagesProvider
{
    private static final Logger LOG = LoggerFactory.getLogger(ChannelMessagesProvider.class);
    
    /**  */
    private static ChannelMessagesProvider instance = null;
	
    /**  */
    private LinkedList<String> displayParameters = null;
	
    /**  */
    private final RecordDataReceiver recordDataReceiver;
    
    /** */
	public static final String PLUGIN_ID = "org.csstudio.websuite";
	
	/**
	 * It sets the columns that will be included in the response.
	 * For all valid columns check the configuration file.
	 * 
	 * @param displayParameters
	 */
	public void setDisplayParameters(LinkedList<String> displayParameters)
	{
		this.displayParameters = displayParameters;
	}
	
	/**
	 * Private constructor - singleton
	 * 
	 * @param displayParameters
	 */
	private ChannelMessagesProvider(LinkedList<String> displayParameters)
	{
	    recordDataReceiver = RecordDataReceiver.getInstance();
	    
		if(displayParameters != null)
		{
			setDisplayParameters(displayParameters);
		}
		else
		{
			setDisplayParameters(getDefaultDisplayParameters());
		}
	}
	
	/**
	 * Returns the current settings for display parameters (the column that are shown in the resposne)
	 * 
	 * @return
	 */
	private LinkedList<String> getDefaultDisplayParameters()
	{
	    IPreferencesService preferences = Platform.getPreferencesService();
		LinkedList<String> output = new LinkedList<String>();
		int numberOfColumns = preferences.getInt(WebSuiteActivator.PLUGIN_ID, PreferenceConstants.CHANNEL_COLUMN_0, 0, null);
		
		for(int i = 1;i <= numberOfColumns;i++)
		{
			output.add(preferences.getString(WebSuiteActivator.PLUGIN_ID, "channelColumn_" + i, "", null));
		}
		
		return output;
	}
	
	/**
	 * Method to create an instance.
	 * DisplayParameters will be retrieved from the configuration file (default)
	 */
	public static void createInstance()
	{
		if(instance == null)
		{
			instance = new ChannelMessagesProvider(null);
		}
	}
	
	/**
	 * Method to create an instance.
	 * 
	 * @param displayParameters If null default display parameters from configuration file will be used
	 */
	public static void createInstance(LinkedList<String> displayParameters)
	{
		if(instance == null)
		{
			instance = new ChannelMessagesProvider(displayParameters);
		}
	}
	
	/**
	 * Returns an instance of the DAO
	 * 
	 * @return ChannelMessagesProvider(singleton)
	 */
	public static ChannelMessagesProvider getInstance()
	{
		return instance;
	}

	/**
	 * Returns the channel message in HTML format (for the servlet)
	 * 
	 * @param parameter Channel name
	 * @return HTML file
	 */
	public String getChannelMessagesAsHtml(String parameter)
	{
		StringBuffer output = new StringBuffer();
		String value;
		String nextAttr;
		
		if(parameter == null)
		{
			parameter = "";
		}
		
		BasicMessage basicMessage = getMessageForChannel(parameter);
		
		output.append("<html>");
		output.append("<body>");
		output.append("<table border='1'>");
		output.append("<tr>");
		output.append("<th>");
		output.append(parameter);
		output.append("</th>");
        output.append("</tr>");
		
		if(basicMessage != null)
		{
			Iterator<String> iterList = displayParameters.iterator();
			while(iterList.hasNext())
			{
                output.append("<tr>");
				output.append("<td>");
				nextAttr = iterList.next();
				value = basicMessage.getHashMap().get(nextAttr);
				if(value != null){
					output.append(value);
				}else{
					output.append("");
				}
				output.append("</td>");
                output.append("</tr>");
			}
		}
		
		//output.append("</tr>");
		output.append("</table>");
		output.append("</body>");
		output.append("</html>");
		
		return output.toString();
	}

	/**
	 * Retrives the message from the channel
	 * SHOULD BE REPLACED WITH THE REAL PROVIDER
	 * 
	 * @param parameter Channal name
	 * @return BasicMessage from the channel
	 */
	private BasicMessage getMessageForChannel(String parameter)
	{
		return recordDataReceiver.getRecordData(parameter);
	}

	/**
	 * Writes the XML file in the writer (for the servlet)
	 * 
	 * @param out Writer to write in
	 * @param parameterList List of all parameters you want to display. If null default display parameters will be used.
	 */
	public void getChannelMessagesAsXml(PrintWriter out, String parameter)
	{
        IPreferencesService preferences = Platform.getPreferencesService();
		XMLOutputter outputter = new XMLOutputter();

		Element channelMessage = new Element("channelmessage");
		Document output = new Document(channelMessage);

		if(parameter==null)
		{
			parameter = "";
		}
		
		BasicMessage basicMessage = getMessageForChannel(parameter);

		String nextAttr = null;
		Element variable;
		variable = new Element("tag");
		variable.setAttribute("key", preferences.getString(WebSuiteActivator.PLUGIN_ID, "channelColumnName", "", null));
		variable.setAttribute("value", parameter);
		channelMessage.addContent(variable);
		
		if(basicMessage != null)
		{
			Iterator<String> iterList = displayParameters.iterator();
			while (iterList.hasNext())
			{
				nextAttr = iterList.next();
				String value = basicMessage.getHashMap().get(nextAttr);
				if(value != null)
				{
					variable= new Element("tag");
					variable.setAttribute("key",nextAttr);
					variable.setAttribute("value",value);
					channelMessage.addContent(variable);
				}
			}
		}
		
		try
		{
			outputter.output(output, out);
		}
		catch(IOException e)
		{
			LOG.error("Cannot write to buffer", e);
		}
	}
}
