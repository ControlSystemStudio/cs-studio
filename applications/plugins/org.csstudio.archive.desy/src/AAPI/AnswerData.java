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
// Answer from AAPI-server class
//
public class AnswerData
{
    private final static String undefEgu = " ";
    private int count;
    private int error;
    private int type;
    private double[] data;
    private int[] time; 
    private int[] u_time;
    private int[] status;
    private int precision; 
    private double displayHigh;
    private double displayLow;
    private double highAlarm;
    private double highWarning;
    private double lowWarning;
    private double lowAlarm;
    private String egu;
   
    
    public void setCount(int Count)        {count=Count;} 
    public void setData (double[] Data)    {data=Data;} 
    public void setTime(int[] Time)        {time=Time;} 
    public void setUtime(int[] Utime)      {u_time=Utime;} 
    public void setStatus(int[] Status)    {status=Status;}
    public void setPrecision(int Prec)     {precision=Prec;} 
    public void setDisplayHigh(double High){displayHigh=High;} 
    public void setDisplayLow(double Low)  {displayLow=Low;}
    public void setHighAlarm(double High)  {highAlarm=High;} 
    public void setHighWarning(double High){highWarning=High;} 
    public void setLowAlarm(double Low)    {lowAlarm=Low;} 
    public void setLowWarning(double Low)  {lowWarning=Low;} 
    public void setEgu(String Egu)         {egu=Egu;} 
    
        
    public  int      getCount()      {return (count);} 
    public  int      getType()       {return (type);}
    public  int      getError()      {return (error);} 
    public  double[] getData()       {return (data);} 
    public  int[]    getTime()       {return (time);} 
    public  int[]    getUtime()      {return (u_time);} 
    public  int[]    getStatus()     {return (status);}
    public  int   getPrecision()  {return (precision);} 
    public  double   getDisplayHigh(){return (displayHigh);} 
    public  double   getDisplayLow() {return (displayLow);}
    public  double   getHighAlarm()  {return (highAlarm);} 
    public  double   getHighWarning(){return (highWarning);} 
    public  double   getLowAlarm()   {return (lowAlarm);} 
    public  double   getLowWarning() {return (lowWarning);} 
    public  String   getEgu()        {return (egu);} 
    
    public boolean debug=false;
    //
    // Analyzing of byteArray package coming from AAPI-server
    // and extracting all data from that
    //
    
    public AnswerData analyzeAnswer (byte[] answer,int command) {
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
            int err = readStream.readInt();
            int ver = readStream.readInt();
            int PV_size = readStream.readInt();
            for(int i=0;i<PV_size;i++) {
                error = readStream.readInt();
                type = readStream.readInt();
                count = readStream.readInt();
                if (count < 0) { 
                    System.err.println("AAPI analyzeAnswer: negative array size");
                    return null;   
                }
                data=new double[count];
                time   =new int[count];
                u_time =new int[count];
                status =new int[count];
                for(int j=0;j<count;j++) {
                    time[j]  =readStream.readInt();
                    u_time[j]=readStream.readInt(); 
                    status[j]=readStream.readInt();
                    data[j]  =readStream.readDouble();
                    status[j]=0;// AAPI curently return old VAX-VMS-style status
                }
                
                precision  =readStream.readInt();
                displayHigh=readStream.readDouble();                
                displayLow =readStream.readDouble();                   
                highAlarm  =readStream.readDouble();                                
                highWarning=readStream.readDouble();
                lowAlarm   =readStream.readDouble();                                
                lowWarning =readStream.readDouble();
                int eguLen =readStream.readInt();
                if (debug) System.out.println("eguLen="+eguLen);
                eguLen--; //Looks like one more symbol here
                if ( eguLen < -1) { 
                    System.err.println("AAPI analyzeAnswer: negative array size");
                    return null;   
                } else if ((eguLen == -1)|| (eguLen == 0) ) egu= new String(undefEgu);
                else {
                    char[] eguAsArray = new char[eguLen];
                    for (int j=0;j<eguLen;j++) { // Looks like one more symbol here
                      eguAsArray[j]= (char) readStream.readByte();
                    }
                    egu=new String(eguAsArray);
                }
            }
        } catch (Exception e) { 
            System.err.println("AAPI-server read buffer error "+e);
            return null;
        }
        return this;    
    }
}