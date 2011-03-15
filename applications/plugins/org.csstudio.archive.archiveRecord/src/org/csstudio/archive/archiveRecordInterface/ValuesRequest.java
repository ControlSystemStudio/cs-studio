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
 package org.csstudio.archive.archiveRecordInterface;

import org.csstudio.archive.ArchiveAccessException;
import org.csstudio.archive.ArchiveValues;
import org.csstudio.archive.archiveRecord.ArchiveRecord;
import org.csstudio.data.values.INumericMetaData;
import org.csstudio.data.values.ISeverity;
import org.csstudio.data.values.ITimestamp;
import org.csstudio.data.values.IValue;
import org.csstudio.data.values.TimestampFactory;
import org.csstudio.data.values.ValueFactory;
/** Handles the "archiver.values" request and its results.
 *  @author Albert Kagarmanov
 */
public class ValuesRequest implements ClientRequest
{
	private static final boolean debug = false;
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

	/** @see org.csstudio.archive.channelarchiver.ClientRequest#read() */
	public int read() throws ArchiveAccessException
	{
		int error = 0;
        final int num_returned_channels = this.channels.length;
        archived_samples = new ArchiveValues[num_returned_channels];
        for (int COUNT=0;COUNT< this.channels.length;COUNT++)
        {
        	final ArchiveRecord ar = new ArchiveRecord(this.channels[COUNT]);
    		int dim;
            try {
                dim = ar.getDimension();
                if (dim <= 0) {
                    if(debug) {
                        System.out.println("bad Dim");
                    }
                    error = -1;
                    dim = 0;
                    //throw new Exception("Expected 'Integer count' for " + this.channels[COUNT]);
                }
                if (dim > 0) {
                    ar.getAllFromCA();
                }
            } catch (final Exception e) {
                throw new ArchiveAccessException("Access to archive record failed.", e);
            }
			final int count = 1; // do not use WF answerClass.getCount();
			final int num_samples = dim;
			final IValue samples[]= new IValue[num_samples];
			final INumericMetaData meta = ValueFactory.createNumericMetaData(
					100,0, //DisplayHigh(),DisplayLow(),
					100,0, //High,LowAlarm(),
					100,0, //High,LowWarn(),
					2, " "//Precision(),Egu()
					);
			for (int si=0; si<num_samples; si++) {
				final long secs = ar.getTime()[si];
				final long nano = ar.getNsec()[si];
				final ITimestamp time = TimestampFactory.createTimestamp(secs, nano);

				if(debug) {
                    System.out.println("TIME=="+time.toString()); // FIXME : debug logging via syso? use a logger!
                }

				final ISeverity sevClass= new SeverityImpl("",true,true);
				final double values[] = new double[count]; // count=1
			    for (int vi=0; vi<count; ++vi) {
                    values[vi] = ar.getVal()[si];
                }
				samples[si] = ValueFactory.createDoubleValue(time, sevClass,"", meta,IValue.Quality.Original, values);
			}
			archived_samples[COUNT] =
				new ArchiveValues(server, this.channels[COUNT], samples );
        } // end of foreach COUNT=0 ... channels.length

        return error;
	}

	/** @return Returns one <code>ArchivedValues</code> per channel. */
	public ArchiveValues[] getArchivedSamples()
	{
		return archived_samples;
	}
}
