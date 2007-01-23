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
