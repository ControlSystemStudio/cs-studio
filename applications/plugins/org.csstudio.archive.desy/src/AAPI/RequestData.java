package AAPI;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

//
// Parametric data for sending to AAPI-server class
//

public class RequestData
{
    private final static int errorTag = 0;
    private final static int headerSize = 9;
    private final static int byteSize = 4;   
    private  int from; 
    private  int u_from;
    private  int to;
    private  int u_to; 
    private  int num;
    private  int conversionTag;
    private  double convers_param;
    private  int PV_size;
    private  String[] PV;

    public void setFrom(int From)      {from=From;}
    public void setU_from(int UFrom)   {u_from=UFrom;}
    public void setTo(int To)          {to=To;}
    public void setU_to(int UTo)       {u_to=UTo;}
    public void setNum(int Num)        {num=Num;}
    public void setPV_size(int Size)   {PV_size=Size;}
    public void setPV(String[] StrArr) {PV=StrArr;}
    public void setConversionTag(int Tag)     {conversionTag=Tag;}
    public void setConvers_param(double Param){convers_param=Param;}
    
    public int getFrom()    {return(from);}
    public int getU_from()  {return(u_from);}
    public int getTo()      {return(to);}
    public int getU_to()    {return(u_to);}
    public int getNum()     {return(num);}
    public int getPV_size() {return(PV_size);}
    public String[] getPV() {return(PV);}
    public int getConversionTag()   {return(conversionTag);}
    public double getConvers_param(){return(convers_param);}

    //
    // Preparing byteArray package for sending over TCP/IP 
    //

    public  byte[] buildPacketFromData(int cmd) {
    try {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream(bout);
        dout.writeInt(from);
        dout.writeInt(u_from);
        dout.writeInt(to);
        dout.writeInt(u_to);
        dout.writeInt(num);
        dout.writeInt(conversionTag);
        dout.writeDouble(convers_param);
        dout.writeInt(PV.length);
   
        for(int i=0; i<PV.length;i++) {
            char[] strArr=PV[i].toCharArray();
            for(int j=0;j<strArr.length;j++) dout.writeByte(strArr[j]);          
            dout.writeChar('\0');
        }

        // return the underlying byte array
        return bout.toByteArray();

    } catch (IOException e) {
        System.err.println("AAPI-server send error"+e);
        return null;
   }
   }

   //
   // AAPI header length claculation
   //

   public  int lenCalculate() {
        int len= headerSize*byteSize;
        for(int i=0; i<PV.length;i++) len += ( (PV[i]).length() +1);
        return len;
   }
} // eof class
