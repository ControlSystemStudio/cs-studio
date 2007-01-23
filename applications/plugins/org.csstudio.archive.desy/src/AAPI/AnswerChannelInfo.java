package AAPI;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
//
// Answer about ChannelInfo from AAPI-server class
//
public class AnswerChannelInfo
{
    private int fromTime; 
    private int fromUTime;
    private int    toTime; 
    private int    toUTime;
    
    
    public void setFromTime (int tm)    {fromTime =tm;} 
    public void setFromUTime(int tm)    {fromUTime=tm;} 
    public void setToTime   (int tm)    {toTime   =tm;} 
    public void setToUTime  (int tm)    {toUTime  =tm;} 

    public  int getFromTime()   {return fromTime;}
    public  int getFromUTime()  {return fromUTime;}
    public  int getToTime()     {return toTime;}
    public  int getToUTime()    {return toUTime;}
          
    //
    // Analyzing of byteArray package coming from AAPI-server
    // and extracting all data from that
    //
    
    public AnswerChannelInfo analyzeAnswer (byte[] answer,int command) {
        if (answer==null) { 
         System.err.println("AAPI analyzeAnswer: null answer");
         return null;   
        }
        DataInputStream readStream = new DataInputStream(new 
            ByteArrayInputStream(answer));
        try { 
            int cmd = readStream.readInt();
            if (command != cmd) {
                 System.err.println("AAPI analyzeAnswer: returnCommandTag=" +cmd+ " != requestCommandTag="+cmd);
                 return null;   
            }
            int err  = readStream.readInt();
            int ver  = readStream.readInt();
            fromTime = readStream.readInt();
            fromUTime= readStream.readInt();
            toTime   = readStream.readInt();
            toUTime  = readStream.readInt();
            
        } catch (Exception e) { 
            System.err.println("AAPI-server read buffer error "+e);
            return null;
        }
        return this;    
    }
}