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
 package org.csstudio.archive.desy.aapi;

import org.csstudio.archive.ArchiveAccessException;
import org.csstudio.archive.ArchiveValues;
import org.csstudio.data.values.INumericMetaData;
import org.csstudio.data.values.ISeverity;
import org.csstudio.data.values.ITimestamp;
import org.csstudio.data.values.IValue;
import org.csstudio.data.values.TimestampFactory;
import org.csstudio.data.values.ValueFactory;

import AAPI.AAPI;
import AAPI.AnswerData;
import AAPI.RequestData;


/** Handles the "archiver.values" request and its results.
 *  @author Albert Kagarmanov
 */
public class ValuesRequest implements ClientRequest
{
	private static final boolean debug = false;
	public void read(){};
	private final ArchiveServer server;
	private final int key;
	private final String channels[];
	private final ITimestamp start, end;
	private final int how;
    private final Object parms[];

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
	public ValuesRequest(final ArchiveServer server,
			final int key, final String channels[],
			final ITimestamp start, final ITimestamp end,
            final int how, final Object parms[])
	{
		this.server = server;
		this.key = key;
		this.channels = channels;
		this.start = start;
		this.end = end;
		this.how = how;
        this.parms = parms;
	}


	/**
	 * FIXME : documentation, split method in smaller logical parts with telling names
	 * @param aapi
	 * @return
	 * @throws ArchiveAccessException
	 */
	public int read(final AAPI aapi) throws ArchiveAccessException
	{
		int error = 0;
		 final RequestData input = new RequestData();

        input.setFrom( (int) this.start.seconds());
        input.setU_from((int) this.start.nanoseconds());
        input.setTo((int) this.end.seconds());
        input.setU_to((int) this.end.nanoseconds());
        if ((parms.length == 1)  &&  (parms[0] instanceof Integer)) {
            input.setNum(((Integer)parms[0]).intValue());
        } else if ((parms.length == 1)  &&  (parms[0] instanceof Double)) {
        	final double binSize= ((Double) parms[0]).doubleValue();
        	final ITimestamp from = TimestampFactory.createTimestamp(this.start.seconds(),this.start.nanoseconds());
        	final ITimestamp to   = TimestampFactory.createTimestamp(this.end.seconds(),  this.end.nanoseconds());
        	final int numOfBeans=  (int) ( (to.toDouble() - from.toDouble())/ binSize   ); ;
        	input.setNum(numOfBeans);
        } else {
            throw new ArchiveAccessException("read(AAPI aapi): bad parms");
        }

        if(debug) {
            System.out.println("AAPI conversionTag="+this.how);
        }
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

        final int num_returned_channels = this.channels.length;
        archived_samples = new ArchiveValues[num_returned_channels];

        for (int COUNT=0;COUNT< this.channels.length;COUNT++)
        {
        	final int pvCount=1;
            final String[] strArray = new String[pvCount];
            strArray[0]=new String(this.channels[COUNT]);
            input.setPV_size(pvCount);
            input.setPV(strArray);
	        final AnswerData answerClass=aapi.getData(input);

	        if (answerClass == null) {
	            System.out.println("AAPI client:bad getData command");
	            error = -1;
	            final IValue samples[]= new IValue[0];
	            archived_samples[COUNT] = new ArchiveValues(server, strArray[0], samples );
				return error;
	            //throw new Exception("AAPI getData call failed");
	        }
	        error = Math.max(answerClass.getError(), error);

			final int type = TYPE_DOUBLE; //  TODO answerClass.getType(); AAPI return only Double
			final int count = 1; // do not use WF answerClass.getCount();
			int num_samples=answerClass.getCount();
			if (this.how == AAPI.MIN_MAX_AVERAGE_METHOD) {
                num_samples /= 3;  // Triplet min/max/average
            }
			final IValue samples[]= new IValue[num_samples];
			final INumericMetaData meta = ValueFactory.createNumericMetaData(
                                                answerClass.getDisplayLow(),
                            					answerClass.getDisplayHigh(),
                                                answerClass.getLowWarning(),
                                                answerClass.getHighWarning(),
                                                answerClass.getLowAlarm(),
                            					answerClass.getHighAlarm(),
                            					answerClass.getPrecision(),
                                                answerClass.getEgu());

			if (this.how == AAPI.MIN_MAX_AVERAGE_METHOD) {		//'MIN_MAX_AVERAGE'
				for (int si=0; si<num_samples; si++) {
					final long secs = answerClass.getTime()[3*si+2];
					final long nano = answerClass.getUtime()[3*si+2];
					final ITimestamp time = TimestampFactory.createTimestamp(secs, nano);
					int stat = answerClass.getStatus()[3*si+2];
					int sevr = answerClass.getStatus()[3*si+2];
					if((stat<0)||(stat>AAPI.alarmStatusString.length-1)) {
                        stat=AAPI.alarmStatusString.length-1;
                    }
					if((sevr<0)||(sevr>AAPI.severityList.length -1)) {
                        sevr=AAPI.severityList.length -1;
                    }
					final ISeverity sevClass= new SeverityImpl(AAPI.alarmStatusString[sevr],true,true);

					final double values[] = new double[count]; // count=1
				    for (int vi=0; vi<count; ++vi) {
                        values[vi] = answerClass.getData()[3*si+2];
                    }
				    final double min = answerClass.getData()[3*si];
                    final double max = answerClass.getData()[3*si+1];
                    if ( max > min) {
                    	samples[si] = ValueFactory.createMinMaxDoubleValue(time,sevClass,
                    			AAPI.alarmStatusString[stat],meta,IValue.Quality.Interpolated,
                    			values, min, max);
                    }else if ( max == min) {
                    	samples[si] = ValueFactory.createDoubleValue(time, sevClass,
                    			AAPI.alarmStatusString[stat],meta,IValue.Quality.Original,
                    			values);
					} else {
						System.out.println("read(AAPI aapi): min >max ("+min+","+max+")");
						throw new ArchiveAccessException("read(AAPI aapi): min >max ("+min+","+max+")"  );
					}
				}
			}else { // not 'MIN_MAX_AVERAGE'
				for (int si=0; si<num_samples; si++) {
					final long secs = answerClass.getTime()[si];
					final long nano = answerClass.getUtime()[si];
					final ITimestamp time = TimestampFactory.createTimestamp(secs, nano);
					int stat = answerClass.getStatus()[si];
					int sevr = answerClass.getStatus()[si];
					if((stat<0)||(stat>AAPI.alarmStatusString.length-1)) {
                        stat=AAPI.alarmStatusString.length-1;
                    }
					if((sevr<0)||(sevr>AAPI.severityList.length -1)) {
                        sevr=AAPI.severityList.length -1;
                    }

					//Changed 23.1.07
					//	Severity sevClass= new SeverityImpl(AAPI.alarmStatusString[sevr],false,false);
					final ISeverity sevClass= new SeverityImpl(AAPI.alarmStatusString[sevr],true,true);

					final double values[] = new double[count]; // count=1
				    for (int vi=0; vi<count; ++vi) {
                        values[vi] = answerClass.getData()[si];
                    }
					samples[si] = ValueFactory.createDoubleValue(time,
	                                sevClass,
	                                AAPI.alarmStatusString[stat],
	                                meta,
	                                IValue.Quality.Original,
	                                values);
				}
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
