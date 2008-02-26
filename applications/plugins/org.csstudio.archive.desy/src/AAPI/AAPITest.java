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
//
//  main (example) method for when run as an application 
//
public class AAPITest {
    public static void main(String args[]) {
    int cmd;
    //
    //For test AAPI cmd uncomment one from next 6 lines:
    //
    
    //cmd=AAPI.VERSION_CMD;
    cmd=AAPI.DATA_REQUEST_CMD;
    //cmd=AAPI.CHANNEL_INFO_CMD;
    //cmd=AAPI.CHANNEL_LIST_CMD;
    //cmd=AAPI.HIERARCHY_CHANNEL_LIST_CMD;
    //cmd=AAPI.FILTER_LIST_CMD;
     
    AAPI aapiClient = new AAPI("krynfs.desy.de",4054); 
    switch (cmd) {
    // 1) Ask version
    case AAPI.VERSION_CMD :
        int version=aapiClient.getVersion();
        if(version <0) System.out.println("Wrong Version="+version);
        else System.out.println("Version="+version);
        break;   
     
    // 2) Ask data
    case AAPI.DATA_REQUEST_CMD :
        RequestData input = new RequestData();
        input.setFrom(1171480000); // Feb 2007
        input.setU_from(0);
//        input.setTo(input.getFrom() + 3600*24*10 );
        input.setTo(input.getFrom() + 3600*1 );
        input.setU_to(0);
        input.setNum(2);
    
        input.setConversionTag(AAPI.MIN_MAX_AVERAGE_METHOD); 	
        input.setConversionTag(AAPI.NO_FILTERING_METHOD); 
        input.setConvers_param(AAPI.DEADBAND_PARAM);
    
        int pvCount=1;
        String[] strArray = new String[pvCount];
        strArray[0]=new String("AL:K:HZ:S1:DI1_RBi1_bi");
        //strArray[0]=new String("P1N:BUSERR_calc");
        input.setPV_size(pvCount);
        input.setPV(strArray);
    
        AnswerData answerClass=aapiClient.getData(input);
        if (answerClass == null) {
            System.out.println("AAPI client:bad getData command");
            return;
        }   
        System.out.println("Name='"+input.getPV()[0]+"'");
        System.out.println("count="+answerClass.getCount());
        System.out.println("count="+answerClass.getError());
        
        if ( answerClass.getError()!= 0) {
            System.out.println( "AAPI client:Error in server side=" + 
            		answerClass.getError() );
            
            if( answerClass.getError() == AAPI.NO_FILTER_BIG ) {
            	System.out.println( "RAW_DATA -- Number of return points is VERY big!");
            	}            
            //return;
        }
        
        for(int j=0;j<answerClass.getCount();j++) {
            System.out.print("T U S D="+answerClass.getTime()[j]); 
            System.out.print("; "+answerClass.getUtime()[j]); 
            System.out.print("; "+answerClass.getStatus()[j]);
            System.out.print("; "+answerClass.getData()[j] + " \n"); 
        }
        System.out.println("prec=" + answerClass.getPrecision());
        System.out.println("MAX="  + answerClass.getDisplayHigh()); 
        System.out.println("MIN="  + answerClass.getDisplayLow());
        System.out.println("HIGH=" + answerClass.getHighAlarm());
        System.out.println("HIHI=" + answerClass.getHighWarning());
        System.out.println("LOW="  + answerClass.getLowAlarm());
        System.out.println("LOLO=" + answerClass.getLowWarning());
        System.out.println("EGU = "+ answerClass.getEgu());            
        break;
        
          
    // 3) Ask ChannelInfo (not really used now)
    case AAPI.CHANNEL_INFO_CMD :
        String PV=new String("AL:K:KV:A1:UL1L2TA1_ai");
        //String PV=new String("EPICS|");
        AnswerChannelInfo ans=aapiClient.getChannelInfo(PV);  
        if (ans == null) return;
        System.out.println("From="  + ans.getFromTime());
        System.out.println("UFrom=" + ans.getFromUTime());
        System.out.println("To="    + ans.getToTime());
        System.out.println("UTo="   + ans.getToUTime());
        break;
        
        
    // 4) Ask channelList (not really used)
    case AAPI.CHANNEL_LIST_CMD :
        String[] chList=aapiClient.getChannelList();  
        if(chList == null) return;
        System.out.println("Common number of channel is="+chList.length);
        for(int i=0; i<chList.length; i++) 
        System.out.println(i+"-th channelName is="+chList[i]);
        break;
  

    // 5) Ask channelListHierarchy
    case AAPI.HIERARCHY_CHANNEL_LIST_CMD :
        //uncomment one of them: 
        //String node="DOOCS|";
        //String node="TINE|";
        //String node="EPICS|";
        //String node="EPICS|MKK|";
        String node="EPICS|MKK|mkkPowStatA|"; 
         
        String[] leaves=aapiClient.getChannelListHierarchy(node);  
        if(leaves == null) return;
        System.out.println("Node is "+node+" Common number of leaves is="+leaves.length);
        for(int i=0; i<leaves.length; i++) 
        System.out.println(i+"-th channelName is="+leaves[i]);
        break;
        
         // 6) Ask algoritmsList (not really used)
    case AAPI.FILTER_LIST_CMD :
        String[] algorithmList=aapiClient.getAlgoritmsList();  
        if(algorithmList == null) return;
        int len=algorithmList.length /2 ; // first part is method name , 
                                         // second is full Description
        System.out.println("Common number of algorithms is="+len);
        for(int i=0; i<len; i++) 
        System.out.println(i+"-th algoName is="+algorithmList[i]);
        for(int i=len; i< len*2 ; i++) 
        System.out.println(i+"-th algoName full Description is=" +algorithmList[i]);   
        break;
        
    } // eof switch() 
    } // eof main()
 }    // eof class