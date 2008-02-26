/* 
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron, 
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
 package AAPI;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
//
// Answer about HierarchySkeleton from AAPI-server class
//
public class AnswerHierarchySkeleton
{
  private final static boolean DEBUG=false;
  private final static int maxNumOfBranch=5;
  private final static int maxLength=128;
  private int numberOfBranch; 
  private int[] depth;
  private String[] label;
  
  public  int getNumberOfBranch()   {return numberOfBranch;}
  public  int[] getDepth()   {return depth;}
  public  String[] getLabel()   {return label;}

  public  AnswerHierarchySkeleton analyzeAnswer (byte[] answer, int command) {
    if (answer==null) { 
      System.err.println("AAPI analyzeAnswer: null answer");
      return null;   
    }

    DataInputStream readStream = new DataInputStream(new ByteArrayInputStream(answer));
    try { 
      int cmd = readStream.readInt();
      if (command != cmd) {
	System.err.println("AAPI analyzeAnswer: returnCommandTag=" +command+ " != requestCommandTag="+cmd);
	return null;   
      }
      int err  = readStream.readInt();
      int ver  = readStream.readInt();
      numberOfBranch = readStream.readInt();
      if((numberOfBranch<0)&&(numberOfBranch>maxNumOfBranch)) {
	System.err.println("analyzeAnswer: strange numberOfBranch="+numberOfBranch);
	return null; 
      }

      if(DEBUG) System.out.println("numberOfBranch="+numberOfBranch);
      depth = new int[maxNumOfBranch];
      label = new String[maxNumOfBranch];
      for(int i=0;i<numberOfBranch;i++) {
	depth[i]=readStream.readInt();
	if(DEBUG) System.out.println("depth[i]="+depth[i]);
	char[] labelStr = new char[maxLength];
	int count=0;
	for(int j=0;j<maxLength;j++) {
	  if ( (labelStr[j] = (char) readStream.readByte())=='\0') {
	    if(DEBUG) System.out.println("j="+j);
	    labelStr[j]='|';
	    count++;
	    if (count > depth[i] -1) break;
	  } else if(DEBUG) System.out.println("A="+labelStr[j]);

	}

	label[i]=new String(labelStr);
	if(DEBUG) System.out.println("label="+label[i]);
      }   
    } catch (Exception e) { 
      System.err.println("AnswerHierarchySkeleton: AAPI-server read buffer error "+e);
      return null;
    }
    return this;    
  }
  
} //Eo class AnswerHierarchySkeleton
