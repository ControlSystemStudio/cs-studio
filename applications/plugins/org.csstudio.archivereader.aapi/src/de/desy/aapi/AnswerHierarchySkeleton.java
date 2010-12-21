
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

package de.desy.aapi;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;

/**
 * @author Albert Kagarmanov
 * @author Markus Moeller
 *
 */
public class AnswerHierarchySkeleton {
    
    private final static int MAX_NUMBER_OF_BRANCH = 5;
    private final static int MAX_LENGTH = 128;
    
    /** Number of branch */
    private int numberOfBranch; 
    
    /** */
    private int[] depth;
    
    /** */
    private String[] label;
    
    /**
     * 
     * @return
     */
    public int getNumberOfBranch() {
    	return numberOfBranch;
    }
    
    /**
     * 
     * @return
     */
    public int[] getDepth() {
    	return depth;
    }
    
    /**
     * 
     * @return
     */
    public String[] getLabel() {
    	return label;
    }

    /**
     * 
     * @param answer
     * @param command
     * @return
     * @throws AapiException
     */
    public AnswerHierarchySkeleton analyzeAnswer(byte[] answer, int command) throws AapiException {
        
    	if(answer == null) { 
            throw new AapiException("AAPI analyzeAnswer: null answer");
        }

        DataInputStream readStream = new DataInputStream(new ByteArrayInputStream(answer));
        try { 
            
        	int cmd = readStream.readInt();
            if(command != cmd) {
            	throw new AapiException("analyzeAnswer(): returnCommandTag = " + cmd + " != requestCommandTag = " + command);
            }
            
            @SuppressWarnings("unused")
            int err  = readStream.readInt();
            
            @SuppressWarnings("unused")
            int ver  = readStream.readInt();

            numberOfBranch = readStream.readInt();
            if((numberOfBranch < 0) && (numberOfBranch > MAX_NUMBER_OF_BRANCH)) {
            	throw new AapiException("analyzeAnswer(): strange numberOfBranch = " + numberOfBranch);
            }

            depth = new int[MAX_NUMBER_OF_BRANCH];
            label = new String[MAX_NUMBER_OF_BRANCH];

            for(int i=0;i<numberOfBranch;i++) {
                
            	depth[i]=readStream.readInt();
                
                char[] labelStr = new char[MAX_LENGTH];
                int count = 0;
                for(int j = 0;j < MAX_LENGTH;j++) {
                    
                	if((labelStr[j] = (char)readStream.readByte()) == '\0') {
                        labelStr[j] = '|';
                        count++;
                        if(count > depth[i] - 1) break;
                    }
                }

                label[i] = new String(labelStr);
            }   
      } catch(Exception e) { 
    	  throw new AapiException("AnswerHierarchySkeleton: AAPI-server read buffer error: " + e.getMessage());
      }
      
      return this;    
    }
}
