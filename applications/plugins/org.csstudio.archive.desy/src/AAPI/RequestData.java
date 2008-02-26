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
