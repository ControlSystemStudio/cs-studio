
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
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Arrays;

import org.apache.log4j.Logger;

/**
 * TODO (mmoeller) : 
 * 
 * @author mmoeller
 * @version 
 * @since 06.12.2010
 */
public class AapiIOHandler {
    
	/** The logger for this class */
	private Logger logger;

	/** Socket object for the server connection */
    private Socket socket;

    /** Name of host */
    private String host;

    /** Port number */
    private int port;
    
    /**
     * 
     * @param host
     * @param port
     */
    public AapiIOHandler(String host, int port) {
        
		logger = AapiClient.getLogger();
        this.socket = null;
        this.host = host;
        this.port = port;
    }

    /**
     * 
     * @return
     */
	public String getHost() {
		return host;
	}

	/**
	 * 
	 * @param host
	 */
	public void setHost(String host) {
		this.host = host;
	}

	/**
	 * 
	 * @return
	 */
	public int getPort() {
		return port;
	}

	/**
	 * 
	 * @param port
	 */
	public void setPort(int port) {
		this.port = port;
	}

    /**
     * TCP/IP connction
     * 
     * @return
     */
    private boolean connect() {
        
    	try {
            socket = new Socket(host, port);
            return true;
        } catch(Exception e) {
            logger.error("AAPI-server connection error: " + e.getMessage());
            socket = null;
            return false;
        }
    }
    
    /**
     * TCP/IP data sending
     * 
     * @return
     */
    private boolean write(byte[] data) {
        
    	try {
            OutputStream to_server = socket.getOutputStream();
            to_server.write(data);
            
            return true;
        } catch(Exception e) {
        	logger.error("AAPI-server send error: " + e.getMessage());
            return false;
        }
    }

    /**
     * TCP/IP data receiving
     * 
     * @return
     */
    private byte[] receive() {
        
    	byte[] data;
        
        try {
            
        	byte[] firstLength = new byte[4];
            InputStream in = socket.getInputStream();
            
            int err;
            if((err = in.read(firstLength)) <= 0) {
                logger.error("Read Socket Error");
                return null;
            }

            DataInputStream readStream = new DataInputStream( new ByteArrayInputStream(firstLength));
            int num = readStream.readInt();
            if(num <= 0) {
            	logger.error("AAPI packet length is negative: " + num);
                return null;
            }

            // Exclude header
            num -= 4;
            
            data = new byte[num];
            int count = 0;
            
            // 8K per buff
            int availableBytes = 8 * 1024;
            
            byte [] lastByte = new byte [availableBytes];

            int needLen=num;
            while(needLen > 0) {
                
            	if((err = in.read(lastByte)) <= 0) {
            		logger.error("Read Socket Error");
                    return null;
                }

                if(err > availableBytes) {
                	logger.error("Read Socket Error overread");
                    return null;
                }
              
                needLen -= err;
                for(int i = 0;i < err;i++) {
                    data[count++] = lastByte[i];
                }
            }

            in.close();
            
            if(count != num) {
            	logger.warn("Warn: ReadSocket incomplete? " + "SZ = " + count + ", NM = " + num);
            }
        } catch(Exception e) {
        	logger.error("AAPI-server received error: " + e.getMessage());
            return null;
        }
        
        return data;
    }

    /**
     * AAPI first 4 bytes is header. Create it and try open TCP connection:
     * 
     * @return
     */
    public byte[] createConnectionAndHeader(int cmd, int len) {
        
        byte[] result = null;
        
        if(connect()) {
            try {
                ByteArrayOutputStream bout = new ByteArrayOutputStream();
                DataOutputStream dout = new DataOutputStream(bout);
                dout.writeInt(AAPI.HEADER_LENGTH + len);
                dout.writeInt(cmd);
                dout.writeInt(AAPI.ERROR_TAG);
                dout.writeInt(AAPI.AAPI_VERSION);
                
                result = bout.toByteArray();
            }catch(IOException ioe) {
            	logger.error("createConnectionAndHeader(): " + ioe.getMessage());
            }
        }

        return result;
    }

    /**
     * Typical client-server request/answer function:
     */
    public byte[] sendReceivedPacket(byte[] header, byte[] data) {
        
        if(!write(concatenate(header, data))) {
            return null;
        }
        
        byte[] rawAnswer;
        if((rawAnswer = receive()) == null) {
            return null;
        }
        
        return analyzeReturnHeader(rawAnswer);
    }
    
    /**
     * Preparing byteArray package from String for sending over TCP/IP
     * 
     * @return
     */
    public byte[] buildPacketFromString(String str, int cmd) {
        
    	try {
            
    		ByteArrayOutputStream bout = new ByteArrayOutputStream();
            DataOutputStream dout = new DataOutputStream(bout);
            
            // From AAPI-Web:
            // write length of name first
            //dout.writeInt(str.length());
            
            // and name as string second
            char[] strArr = str.toCharArray();
            
            for(int i = 0; i < strArr.length; i++) {
                dout.writeByte(strArr[i]);
            }
            
            dout.writeChar('\0');
            
            // return the underlying byte array
            return bout.toByteArray();
        } catch(IOException ioe) {
            logger.error("AAPI-client send error: " + ioe.getMessage());
            return null;
        }
    }

    /**
     * AAPI first 4 bytes is header. Analyze it!
     * 
     * @return
     */
    private byte[] analyzeReturnHeader(byte[] rawAnswer) {
        
    	try {
            
    		DataInputStream readStream = new DataInputStream(new ByteArrayInputStream(rawAnswer));
            
    		@SuppressWarnings("unused")
            int cmd = readStream.readInt();
            
            int err = readStream.readInt();
            
            @SuppressWarnings("unused")
            int ver = readStream.readInt();
            
            if(err != 0) {
            	
            	String error = new String(Arrays.copyOfRange(rawAnswer, 12, rawAnswer.length));
            	error = error.trim();
                logger.error(error);
                return null;
            }
            
            return rawAnswer;
        } catch(IOException ioe) {
            logger.error("analyzeReturnHeader(): " + ioe.getMessage());
            return null;
        }
    }

    /**
     * a + b for byteArrays
     */
    private byte[] concatenate(byte[] a, byte[] b) {
         
        if(a == null) return null;
        if(b == null) return a;
        byte[] ret = new byte[a.length + b.length];
        int i, j;
         
        for (i = 0; i < a.length; i++) ret[i] = a[i];
        for (j = 0; j < b.length; j++) ret[i + j] = b[j];
         
        return ret;
    }
}
