package org.csstudio.archive.desy.aapi;

import org.csstudio.archive.ArchiveValues;
import org.csstudio.platform.data.INumericMetaData;
import org.csstudio.platform.data.ISeverity;
import org.csstudio.platform.data.ITimestamp;
import org.csstudio.platform.data.IValue;
import org.csstudio.platform.data.TimestampFactory;
import org.csstudio.platform.data.ValueFactory;

import AAPI.AAPI;
import AAPI.AnswerData;
import AAPI.RequestData;


/** Handles the "archiver.values" request and its results.
 *  @author Albert Kagarmanov
 */
public class ValuesRequest implements ClientRequest
{
	public void read(){};
	private ArchiveServer server;
	private int key;
	private String channels[];
	private ITimestamp start, end;
	private int how;
    private Object parms[];
	
	// Possible 'type's for the values.
	private static final int TYPE_STRING = 0;
	private static final int TYPE_ENUM = 1;
	private static final int TYPE_INT = 2;
	private static final int TYPE_DOUBLE = 3;
	
	// The result of the query
	private ArchiveValues archived_samples[];
	
	/** Constructor for new value request.
	 *  <p>
	 *  Details regarding the meaning of 'count' for the different 'how'
	 *  values are in the XML-RPC description in the ChannelArchiver manual.
	 *  
	 *  @param key Archive key
	 *  @param channels Vector of channel names
	 *  @param start Start time for retrieval
	 *  @param end  End time for retrieval
	 *  @param count How many values to get
	 *  @param how How to retrieve
	 */
	public ValuesRequest(ArchiveServer server,
			int key, String channels[],
			ITimestamp start, ITimestamp end,
            int how, Object parms[])
	{
		this.server = server;
		this.key = key;
		this.channels = channels;
		this.start = start;
		this.end = end;
		this.how = how;
        this.parms = parms;
	}

	/** @see org.csstudio.archive.channelarchiver.ClientRequest#read() */
	public int read(AAPI aapi)  throws Exception
	{
		int error = 0;
		 RequestData input = new RequestData();
		 
        input.setFrom( (int) this.start.seconds());
        input.setU_from((int) this.start.nanoseconds());
        input.setTo((int) this.end.seconds());
        input.setU_to((int) this.end.nanoseconds());
        if (parms.length == 1  &&  parms[0] instanceof Integer)
            input.setNum(((Integer)parms[0]).intValue());
    
        input.setConversionTag(this.how );
        /*
         * This is temporary solution for SPLINE<->AVERAGE permutate
         *  Need discuss with Kay how to implement it in common way
         *  Albert 13 April 2007 
         
        if(this.how ==4) input.setConversionTag(1 );
        if(this.how ==1) input.setConversionTag(4 );
        change back to June 2007
        */
        input.setConvers_param(AAPI.DEADBAND_PARAM); // DESY specific
    
        int num_returned_channels = this.channels.length;
        archived_samples = new ArchiveValues[num_returned_channels];
        
        for (int COUNT=0;COUNT< this.channels.length;COUNT++) 
        {
        	int pvCount=1;
            String[] strArray = new String[pvCount];
            strArray[0]=new String(this.channels[COUNT]);
            input.setPV_size(pvCount);
            input.setPV(strArray);            	    
	        AnswerData answerClass=aapi.getData(input);
	        error = Math.max(answerClass.getError(), error);
	        if (answerClass == null) {
	            System.out.println("AAPI client:bad getData command");
	            throw new Exception("AAPI getData call failed");
	        }   
			int type = TYPE_DOUBLE; //  TODO answerClass.getType(); AAPI return only Double
			int count = 1; // do not use WF answerClass.getCount();
			int num_samples=answerClass.getCount();
			IValue samples[]= new IValue[num_samples];
			INumericMetaData meta = ValueFactory.createNumericMetaData(
                                                answerClass.getDisplayLow(),
                            					answerClass.getDisplayHigh(),
                                                answerClass.getLowWarning(),
                                                answerClass.getHighWarning(),
                                                answerClass.getLowAlarm(),
                            					answerClass.getHighAlarm(),
                            					answerClass.getPrecision(),
                                                answerClass.getEgu());	
			for (int si=0; si<num_samples; si++) {
				long secs = answerClass.getTime()[si];
				long nano = answerClass.getUtime()[si];
				ITimestamp time = TimestampFactory.createTimestamp(secs, nano);
				int stat = answerClass.getStatus()[si];
				int sevr = answerClass.getStatus()[si];
				if((stat<0)||(stat>AAPI.alarmStatusString.length-1)) stat=AAPI.alarmStatusString.length-1;
				if((sevr<0)||(sevr>AAPI.severityList.length -1))     sevr=AAPI.severityList.length -1;

				//Changed 23.1.07
				//	Severity sevClass= new SeverityImpl(AAPI.alarmStatusString[sevr],false,false);
				ISeverity sevClass= new SeverityImpl(AAPI.alarmStatusString[sevr],true,true);
			
				double values[] = new double[count]; // count=1
			    for (int vi=0; vi<count; ++vi) values[vi] = answerClass.getData()[si];
				samples[si] = ValueFactory.createDoubleValue(time,
                                sevClass,
                                AAPI.alarmStatusString[stat],
                                meta,
                                IValue.Quality.Original,
                                values);
			}
			archived_samples[COUNT] =
				new ArchiveValues(server, strArray[0], samples );
			
			
        } // end of foreach COUNT=0 ... channels.length 
        return error;
	}

	/** @return Returns one <code>ArchivedValues</code> per channel. */
	public ArchiveValues[] getArchivedSamples()
	{
		return archived_samples;
	}	
}
