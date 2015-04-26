
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

import java.util.Date;

import org.apache.log4j.Logger;

/**
 * @author mmoeller
 *
 */
public class AapiCommandHandler {
	
	/** The logger for this class */
	private Logger logger;
	
	/** Class that handles the network IO */
	private AapiIOHandler ioHandler;
	
	/** Provides methods to convert the received data to the appropriate type */
	private AapiDataInterpreter dataInterpreter;
	
    /**
	 * The one and only constructor
	 * 
	 * @param host
	 * @param port
	 */
	public AapiCommandHandler(String host, int port) {
		
		logger = AapiClient.getLogger();
		ioHandler = new AapiIOHandler(host, port);
		dataInterpreter = new AapiDataInterpreter();
	}

    /**
     * 
     * @return
     */
	public String getHost() {
		return ioHandler.getHost();
	}

	/**
	 * 
	 * @param host
	 */
	public void setHost(String host) {
		ioHandler.setHost(host);
	}

	/**
	 * 
	 * @return
	 */
	public int getPort() {
		return ioHandler.getPort();
	}

	/**
	 * 
	 * @param port
	 */
	public void setPort(int port) {
		ioHandler.setPort(port);
	}

	/**
     * 
     * @return
     */
    public int getVersion() {
        
        int cmd = AapiCommand.VERSION_CMD.getCommandNumber();
        
        byte[] header;
        byte[] answer;

        //parameters length in bytes
        int len = 0;
        
        if((header = ioHandler.createConnectionAndHeader(cmd, len)) == null) return -1;
        if((answer = ioHandler.sendReceivedPacket(header, null)) == null) return -1;
        
        return dataInterpreter.interpretAnswerAsVersion(answer);
    }

    /**
     * Get control-system (i.e. EPICS) data from AAPI-server
     * this is our main call.
     * 
     * @return
     */
    public AnswerData getData(RequestData in) {
        
        int cmd = AapiCommand.DATA_REQUEST_CMD.getCommandNumber();

        byte[] header;
        byte[] answer;
        byte[] data;

        logger.debug("AAPI from = " + new Date((long)((long)in.getFromTime()) * 1000));
        logger.debug("AAPI to   = " + new Date((long)((long)in.getToTime()) * 1000));

        //parameters length in bytes
        int len = in.calculateLength();

        if((header = ioHandler.createConnectionAndHeader(cmd, len)) == null) {
            return null;
        }
        
        AnswerData result = null;
        
        try {
			
        	data = in.buildPacketFromData(cmd);
	        
        	answer = ioHandler.sendReceivedPacket(header, data);
	        if(answer != null) {
	        	result = dataInterpreter.interpretAnswerAsData(answer, cmd);
	        }
		} catch (AapiException aapie) {
			logger.error(aapie.getMessage());
			result = null;
		}
        
        return result;
    }

    /**
     *  Get ChannelInfo about channel (not really used)
     *
     * @return
     */
    public AnswerChannelInfo getChannelInfo(String chName) {
        
        int cmd = AapiCommand.CHANNEL_INFO_CMD.getCommandNumber();
        
        byte[] header;
        byte[] answer;
        
        int len = chName.length(); //parameters length in bytes

        // From AAPI-Web:
        // if ((header = createConnectionAndHeader(cmd, len)) == null)
        if ((header = ioHandler.createConnectionAndHeader(cmd, len + 1)) == null) {
            return null;
        }
        
        if ((answer = ioHandler.sendReceivedPacket(header, ioHandler.buildPacketFromString(chName, cmd))) == null) {
          return null;
        }
        
        AnswerChannelInfo ret = dataInterpreter.interpretAnswerAsChannelInfo(answer, cmd);
        
        return ret;
    }

    /**
     * Get ChannelList It's huge! (use it carefully)!!
     * In a future it's better to use next getHierarchyChannelList
     * 
     * @return
     */
    public String[] getChannelList() {
        
        int cmd = AapiCommand.CHANNEL_LIST_CMD.getCommandNumber();
        
        byte[] header;
        byte[] answer;
        
        //parameters length in bytes
        int len = 0;
        
        if((header = ioHandler.createConnectionAndHeader(cmd, len)) == null) {
            return null;
        }
        
        if((answer = ioHandler.sendReceivedPacket(header, null)) == null) {
            return null;
        }
        
        return dataInterpreter.interpretAnswerAsString(answer);
    }
    
    /**
     * Get ChannelListHierarchy
     * 
     * @return
     */
    public String[] getChannelListHierarchy(String node) {
        
        int cmd = AapiCommand.HIERARCHY_CHANNEL_LIST_CMD.getCommandNumber();

        byte[] header;
        byte[] answer;
        
        //parameters length in bytes
        int len = node.length();
        
        if((header = ioHandler.createConnectionAndHeader(cmd, len)) == null) {
            return null;
        }
        
        if((answer = ioHandler.sendReceivedPacket(header, ioHandler.buildPacketFromString(node,cmd))) == null) {
            return null;
        }
        
        return dataInterpreter.interpretAnswerAsString(answer);
    }
    
    /**
     * getAlgoritmsList. Do not forget that also
     * full description should return, see doc. in www-kryo.desy.de
     * 
     * @return
     */
    public String[] getAlgoritmsList() {
        
        int cmd = AapiCommand.FILTER_LIST_CMD.getCommandNumber();

        byte[] header;
        byte[] answer;
        
        //parameters length in bytes
        int len = 0;
        
        if((header = ioHandler.createConnectionAndHeader(cmd, len)) == null) {
            return null;
        }
        
        if((answer = ioHandler.sendReceivedPacket(header, null)) == null) {
            return null;
        }
        
        return dataInterpreter.interpretAnswerAsDoubleString(answer);
    }

    /**
     * 
     * added by nejc.kosnik@cosylab.com
     * 
     * @return
     */
    public String[] getRegExpChannelList(String regExp) {
        
        int cmd = AapiCommand.REGEXP_LIST_CMD.getCommandNumber();
        
        byte[] header;
        byte[] answer;

        //parameters length in bytes
        int len = regExp.length();
        
        logger.debug("getRegExpChannelList.reg = " + regExp);

        // From AAPI-Web:
        // if ((header = createConnectionAndHeader(cmd, len+1)) == null)

        if((header = ioHandler.createConnectionAndHeader(cmd, len + 1)) == null) {
            return null;
        }
        
        if((answer = ioHandler.sendReceivedPacket(header, ioHandler.buildPacketFromString(regExp, cmd))) == null) {
            return null;
        }
        
        return dataInterpreter.interpretAnswerAsString(answer);
    }

    /**
     * 
     * @return
     */
    public AnswerHierarchySkeleton getHierarchySkeleton() {
    	
        int cmd = AapiCommand.HIERARCHY_SKELETON_CMD.getCommandNumber();
        
        byte[] header;
        byte[] answer;
        
        // Parameters length in bytes
        int len = 0;
        
        if((header = ioHandler.createConnectionAndHeader(cmd, len)) == null) return null;
        if((answer = ioHandler.sendReceivedPacket(header, null)) == null) return null;
        
        return dataInterpreter.interpretAnswerAsHierarchySkeleton(answer,cmd);
    }    
}
