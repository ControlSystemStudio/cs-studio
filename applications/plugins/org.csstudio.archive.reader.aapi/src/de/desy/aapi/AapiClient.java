/* 
 * Copyright (c) 2010 Stiftung Deutsches Elektronen-Synchrotron, 
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

package de.desy.aapi;

import java.io.File;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.varia.NullAppender;

/**
 * This is the main class for the AAPI protocol.
 * 
 * TODO:
 * 
 * @author Albert Kagarmanov
 * @author Markus Moeller
 * @version Archive Protocol V2.4
 * @since 06.12.2010
 */
public class AapiClient {
    
	/** 
	 * Keyword used in System properties to configure AAPI logging. 
	 * Valie values are:
	 * <ul>
	 * <li>true - AAPI will call basic log4j configuration, which enables logging to console.</li>
	 * <li>false - AAPI will not configure logging to console.</li>
	 * </ul>
	 * 
	 * By default true is assumed and logging will be configured.
	 * 
	 * <p>
	 * Regardless of this setting an application can configure own appenders for AAPI logging.
	 * See {@link #getLogger()} for details.
	 * </p>  
	 *  
	 * @see #getLogger()
	 */
    private static final String LIB_LOGGING = "aapi.logging";
    
    /** Keyword used in System properties to set the AAPI logging configuration file. */
    private static final String LOG_PROPERTY_FILE = "aapi.logging.file";
        
    /** The logger for the AAPI library */
    private static Logger logger = null;
    
    /** Implements the AAPI commands */
    private AapiCommandHandler commandHandler;
        
    /**
     * 
     */
    public AapiClient(String host, int port) {
    	this.commandHandler = new AapiCommandHandler(host, port);
    }

	/**
	 * Return logger, which is parent for all AAPI plug loggers.
	 * 
	 * <p>
	 * AAPI loggers collect and distribute messages, which are intended for general plublic.
	 * E.g. application which is not interested in internal structure, but wants to display progress when some channel 
	 * was connected or some user initiated action failed.
	 * </p>
	 * 
	 * <p>
	 * Parent AAPI logger name is 'AAPI'.
	 * </p>
	 * 
	 * <p>
	 * Default configuration of appenders is controlled with System parameter {@link AapiClient#LIB_LOGGING}.
	 * </p>
	 *    
	 * @return parent logger for all AAPI plug loggers.
	 * 
	 * @see #LIB_LOGGING
	 */
	public static final Logger getLogger() {
		
		if (logger == null) {
			logger = Logger.getLogger("AAPI");
			
			boolean log = Boolean.parseBoolean(System.getProperty(LIB_LOGGING, Boolean.TRUE.toString()));
			
			if(log) {
				
				String fileName = System.getProperty(LOG_PROPERTY_FILE, null);
				if(fileName != null) {
					
					File file = new File(fileName);
					if(file.exists()) {
						PropertyConfigurator.configure(fileName);
					} else {
						BasicConfigurator.configure();
					}
				} else {
					// No file name is given.
					BasicConfigurator.configure();
				}
			} else {
				// supresses log4j warning about nonconfigured logging  
				logger.addAppender(new NullAppender());
			}			
		}
		
		return logger;
	}
	
    /**
     * 
     * @return
     */
    public String getDescription() {
        return AAPI.DESCRIPTION ;
    }
    
    /**
     *  Get AAPI-server Requested Types string aliases (storing localy)
     *
     * @return
     */
    public String[] getRequestedTypeList() {
        return AAPI.REQUESTED_TYPE_LIST ;
    }

    /**
     * get AAPI-server Severity string aliases (storing localy)
     * 
     * @return
     */
    public String getSeverityList(int i) {
        
        if(( i < 0) || ( i > AAPI.SEVERITY_LIST.length - 1)) {
            return AAPI.SEVERITY_LIST[AAPI.SEVERITY_LIST.length - 1] ;
        }
        
        return AAPI.SEVERITY_LIST[i];
    }
    
    /**
     * 
     * @return
     */
    public int getMaxEpicsSeverity() {
        return AAPI.SEVERITY_LIST.length;
    }
    
    /**
     * 
     * @return
     */
    public int getMaxEpicsStatus() {
        return AAPI.ALARM_STATUS_STRING.length;
    }


    /**
     * Get AAPI-server Status string aliases (storing localy)
     * 
     * @return
     */
    public String getStatusList(int i) {
        
        if(( i < 0) || ( i > AAPI.ALARM_STATUS_STRING.length - 1)) {
            return AAPI.ALARM_STATUS_STRING[AAPI.ALARM_STATUS_STRING.length - 1] ;
        }
        
        return AAPI.ALARM_STATUS_STRING[i];
    }
    
    /**
     * 
     * @param port
     */
    public void setPort(int port) {
        commandHandler.setPort(port);
    }

    /**
     * 
     * @param port
     */
    public void setPort(String port) {
        
        if(port != null) {
        	
        	try {
        		int p = Integer.parseInt(port);
        		setPort(p);
        	} catch(NumberFormatException nfe) {
        		logger.warn("setPort(String): [*** NumberFormatException ***]: " + nfe.getMessage());
        	}
        }
    }

    /**
     * 
     * @return
     */
    public int getPort() {
        return commandHandler.getPort();
    }
    
    /**
     * 
     * @return
     */
    public String getPortAsString() {
        return new Integer(getPort()).toString();
    }

    /**
     * 
     * @param host
     */
    public void setHost(String host) {
    
        if(host != null) {
            commandHandler.setHost(host);
        }
    }

    /**
     * 
     * @return
     */
    public String getHost() {
        return commandHandler.getHost();
    }
    
    /**
     * 
     * @return
     */
    public int getVersion() {
    	return commandHandler.getVersion();
    }
    
    /**
     * 
     * @param in
     * @return
     */
    public AnswerData getData(RequestData in) {
    	return commandHandler.getData(in);
    }
    
    /**
     * 
     * @param chName
     * @return
     */
    public AnswerChannelInfo getChannelInfo(String chName) {
    	return commandHandler.getChannelInfo(chName);
    }
    
    /**
     * 
     * @return
     */
    public String[] getChannelList() {
    	return commandHandler.getChannelList();
    }
    
    /**
     * 
     * @param node
     * @return
     */
    public String[] getChannelListHierarchy(String node) {
    	return commandHandler.getChannelListHierarchy(node);
    }
    
    /**
     * 
     * @return
     */
    public String[] getAlgoritmsList() {
    	return commandHandler.getAlgoritmsList();
    }
    
    /**
     * 
     * @param regExp
     * @return
     */
    public String[] getRegExpChannelList(String regExp) {
    	return commandHandler.getRegExpChannelList(regExp);
    }
    
    /**
     * 
     * @return
     */
    public AnswerHierarchySkeleton getHierarchySkeleton() {
    	return commandHandler.getHierarchySkeleton();
    }
}
