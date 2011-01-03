
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

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.log4j.Logger;

/**
 * The class contains methods for interpreting the received data from the server.
 * @author mmoeller
 *
 */
public class AapiDataInterpreter {

	/** The logger for this class */
	private Logger logger;

	/**
	 * The standard constructor just creates the logger.
	 */
	public AapiDataInterpreter() {
		logger = AapiClient.getLogger();
	}
	
	/**
     * 
     * @param array
     * @return
     */
    public String toString(char[] array) {
        
    	String cur = new String(array).trim();
        int zIdx = cur.indexOf(0);
        
        if(zIdx >= 0) {
            cur=cur.substring(0, zIdx);
        }
        
        return cur;
    }

    /**
     * If answer is int (version only) recovery it from bytes
     * 
     * @return
     */
    public int interpretAnswerAsVersion(byte[] data) {
        
    	int version;
    	
    	try {
            
    		DataInputStream readStream = new DataInputStream(new ByteArrayInputStream(data));
            
            for(int i = 0; i < AAPI.HEADER_SIZE - 1; i++) {
            	//Skip header
                readStream.readInt();
            }
            
            version = readStream.readInt();
        } catch(IOException ioe) {
            logger.error("interpretAnswerAsVersion(): " + ioe.getMessage());
            version = -1;
        }
        
        return version;
    }

    /**
     * If answer is DataStructure (get_Data only) recovery it from bytes
     */
    public AnswerData interpretAnswerAsData(byte[] data, int cmd) {
        
    	try {
            
    		DataInputStream readStream = new DataInputStream(new ByteArrayInputStream(data));
            for(int i = 0; i < AAPI.HEADER_SIZE - 1; i++) {
                //Skip header
                readStream.readInt();
            }
            
            AnswerData answerClass = new AnswerData();
            if ((answerClass.analyzeAnswer(data, cmd)) == null) return null;
            
            logger.debug(answerClass.toString());
            
            return answerClass;
        }
        catch(IOException ioe)
        {
            logger.error("interpretAnswerAsData(): " + ioe.getMessage());
            return null;
        }
    }

    /**
     * 
     * @param data
     * @param cmd
     * @return
     */
    public AnswerChannelInfo interpretAnswerAsChannelInfo(byte[] data, int cmd) {
        
    	AnswerChannelInfo answerClass = null;
    	
    	try {
            
    		DataInputStream readStream = new DataInputStream(new ByteArrayInputStream(data));
            for (int i = 0; i < AAPI.HEADER_SIZE - 1; i++) {
                //Skip header
                readStream.readInt();
            }
            
            answerClass = new AnswerChannelInfo();
            answerClass = answerClass.analyzeAnswer(data, cmd);
        } catch(IOException ioe) {
            logger.error("interpretAnswerAsChannelInfo(): " + ioe.getMessage());
            answerClass = null;
        } catch(AapiException aapie) {
        	logger.error("interpretAnswerAsChannelInfo(): " + aapie.getMessage());
        	answerClass = null;
		}
        
        return answerClass;
    }

    /**
     * If answer is String[] (getChannelList, getHierarchy) recovery it from
     * bytes
     */
    public String[] interpretAnswerAsString(byte[] data) {
        
    	try {
            
    		DataInputStream readStream = new DataInputStream(new ByteArrayInputStream(data));
            for (int i = 0; i < AAPI.HEADER_SIZE - 1; i++) {
                //Skip header
                readStream.readInt();
            }
            
            int num = readStream.readInt(); // # of strings
            if(num <= 0) {
                logger.error("interpretAnswerAsString(): Number of strings <= 0");
                return null;
            }
            
            ArrayList<String> ret = new ArrayList<String>();
            char[] answerAsArray = new char[AAPI.MAX_NAME_LENGTH];
            int j;
            for(int i = 0; i < num; i++) {
                
            	for (j = 0; j < AAPI.MAX_NAME_LENGTH; j++) {
                    
            		if((answerAsArray[j] = (char)readStream.readByte()) == '\0') {
                        break;
                    }
                    
                    if(answerAsArray[j] == '\n') {
                        
                    	// record with unexpected
                        // carridgeReturn in the end
                        answerAsArray[j] = '\0';
                        readStream.readByte();
                        break;
                    }
                }
                
                if(j > (AAPI.MAX_NAME_LENGTH - 2)) {
                    logger.error(i + "-th name is unbelieveable long = " + new String(answerAsArray));
                    return null;
                }
                
                if(!(answerAsArray[0]==(char)0 || answerAsArray[1]==(char)0)) {
                    ret.add(toString(answerAsArray));
                }
            }
            
            String[] retArr = new String[ret.size()];
            ret.toArray(retArr);
            
            return retArr;
        } catch(IOException ioe) {
            logger.error("interpretAnswerAsString(): " + ioe.getMessage());
            return null;
        }
    }

    /**
     * If answer is String[]+String[] (getAlgorithmsList) recovery it from bytes
     */
    public String[] interpretAnswerAsDoubleString(byte[] data) {
        
        try {
            DataInputStream readStream = new DataInputStream(new ByteArrayInputStream(data));
            
            for (int i = 0; i < AAPI.HEADER_SIZE - 1; i++) {
                //Skip header
                readStream.readInt();
            }
            
            int num = readStream.readInt(); // # of strings
            if (num <= 0) {
                logger.error("interpretAnswerAsDoubleString(): Number of strings <= 0");
                return null;
            }
            
            num *= 2; // Double it
            String[] ret = new String[num];
            char[] answerAsArray = new char[AAPI.MAX_NAME_LENGTH];
            int j;
            for(int i = 0; i < num; i++) {
                
                for(j = 0; j < AAPI.MAX_NAME_LENGTH; j++) {
                    if ((answerAsArray[j] = (char)readStream.readByte()) == '\0') {
                        break;
                    }
                }
                
                if(j > (AAPI.MAX_NAME_LENGTH - 2)) {
                    System.err.println(i + "-th name is unbelieveable long =" + new String(answerAsArray));
                    return null;
                }
                
                ret[i] = this.toString(answerAsArray);
            }
            
            return ret;
        } catch(IOException ioe) {
            logger.error("interpretAnswerAsDoubleString(): " + ioe.getMessage());
            return null;
        }
    }

    /**
     * If answer is ChannelInfoStructure (get_ChannelInfo only) recovery it from
     * bytes
     */
    public AnswerHierarchySkeleton interpretAnswerAsHierarchySkeleton(byte[] data, int cmd) {
        
    	AnswerHierarchySkeleton answerClass = null;
    	
        try {
        	
            DataInputStream readStream = new DataInputStream(new ByteArrayInputStream(data));
            
            for(int i = 0; i < AAPI.HEADER_SIZE - 1; i++) {
                //Skip header
                readStream.readInt();
            }
                
            answerClass = new AnswerHierarchySkeleton();
            answerClass = answerClass.analyzeAnswer(data, cmd);
        } catch(IOException ioe) {
        	logger.error("interpretAnswerAsHierarchySkeleton(): " + ioe.getMessage());
        	answerClass = null;
        } catch (AapiException aapie) {
        	logger.error("interpretAnswerAsHierarchySkeleton(): " + aapie.getMessage());
        	answerClass = null;
		}
        
        return answerClass;
    }
}
