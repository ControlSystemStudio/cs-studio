package org.csstudio.archive.reader.channelarchiver.file;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.List;

import org.csstudio.archive.reader.ValueIterator;
import org.csstudio.archive.vtype.ArchiveVEnum;
import org.csstudio.archive.vtype.ArchiveVNumber;
import org.csstudio.archive.vtype.ArchiveVNumberArray;
import org.csstudio.archive.vtype.ArchiveVString;
import org.csstudio.archive.vtype.ArchiveVType;
import org.diirt.vtype.AlarmSeverity;
import org.diirt.vtype.Display;
import org.diirt.vtype.VType;

//TODO: imported package gov.aps.jca, better to require bundle?
import gov.aps.jca.dbr.Status;

/**
 * Obtains channel archiver samples from channel archiver
 * data files, and translates them to ArchiveVTypes.
 * <p>Note: Does not currently support data in multiple sub-archives.
 * @author Amanda Carpenter
 *
 */
public class ArchiveFileSampleReader implements ValueIterator
{
	private final Instant iteratorStop;
	private final ArchiveFileBuffer buffer;
	private DataHeader header;
	private ArchiveVType next; //sample that will be returned by nextSample(), or else null
	
	public ArchiveFileSampleReader(Instant iteratorStart, Instant iteratorStop,
			File file, long offset) throws IOException
	{
		this.iteratorStop = iteratorStop;
		this.buffer = new ArchiveFileBuffer(file);
		buffer.offset(offset);
		header = DataHeader.readDataHeader(buffer, new CtrlInfoReader(0));
		//possible sanity check: is start between header's start and stop times?
		binarySearchSamples(iteratorStart);
	}
	
	//Searches for samples in the buffer, starting from its current offset, using the information in
	//this.header.
	//Finds sample with closest timestamp to 'time', either at or below.
	//Sets this.next = found sample, or null if not found, and sets this.header.numSamples = number of samples
		//after current buffer position.
	private void binarySearchSamples(Instant time)
	{
		ArchiveVType sample = null;
		int size = header.dbrType.getSize(header.dbrCount);
		long initOffset;
		try
		{
			initOffset = buffer.offset();
		}
		catch (IOException e)
		{
			next = null;
			header.numSamples = 0;
			return;
		}
		long minOffset = initOffset;
		long maxOffset = minOffset + (header.numSamples-1) * size;
		long midOffset;
		do
		{
			// need to make sure midOffset > minOffset unless minOffset == maxOffset
			midOffset = minOffset + (maxOffset - minOffset)/(2*size) * size;
			try
			{
				buffer.offset(midOffset);
				sample = getSample(header.dbrType, header.dbrCount, header.info, buffer);
			}
			catch (IOException e)
			{
				next = null;
				header.numSamples = 0;
			}
			int compare = sample.getTimestamp().compareTo(time);
			if (compare == 0)
			{
				break;
			}
			else if (compare < 0) //sample time < 'time'
			{
				if (minOffset == midOffset)
				{	//'time' is after min sample's time; it might be at or above max sample's time
					try
					{
						ArchiveVType maxSample = getSample(header.dbrType, header.dbrCount, header.info, buffer);
						if (maxSample.getTimestamp().compareTo(time) <= 0)
						{
							sample = maxSample;
						}
						else
						{
							buffer.offset(buffer.offset() - size);
						}
						break;
					}
					catch (IOException e)
					{
						next = null;
						header.numSamples = 0;
						break;
					}
				}
				minOffset = midOffset;
			}
			else
			{
				maxOffset = midOffset - size;
			}
		} while (minOffset <= maxOffset);
		next = sample;
		header.numSamples -= (midOffset - initOffset)/size + 1;
	}
	
	private boolean hasNextHeader()
	{
		return header.nextOffset != 0 && header.nextTime.compareTo(iteratorStop) <= 0;
	}
	
	private DataHeader nextHeader() throws IOException
	{
		if (!buffer.getFile().equals(header.nextFile))
		{
			buffer.close();
			buffer.setFile(header.nextFile);
		}
		buffer.offset(header.nextOffset);
		return DataHeader.readDataHeader(buffer, header.info);
	}
	
    private ArchiveVType nextSample() throws IOException
    {
		if (header.numSamples == 0)
		{
			if (!hasNextHeader()) return null;
			nextHeader();
		}
		ArchiveVType sample = getSample(header.dbrType, header.dbrCount, header.info, buffer);
		header.numSamples--;
		if (sample.getTimestamp().compareTo(iteratorStop) <= 0)
			return sample;
		else
			return null;
    }

	@Override
    public boolean hasNext()
    {
		return next != null;
    }

	@Override
    public VType next() throws IOException
    {
		VType ret = next;
		try
		{
			next = nextSample();
		}
		catch (IOException e)
		{
			next = null;
		}
		return ret;
    }
    
	@Override
    public void close()
    {
    	try
    	{
			buffer.close();
		}
    	catch (IOException e)
    	{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
	
	//a sort-of-struct for holding data header info
	static class DataHeader
	{
		public final File nextFile;
		public final long nextOffset;
		public final Instant nextTime; //start time for next file
		
		//public final Instant startTime;
		//public final Instant endTime;

		public final CtrlInfoReader info;
		public final DbrType dbrType; //dbr_time_xxx type of data
		public final short dbrCount; //count of data (i.e. number of dbr_xxx_t values per dbr_time_xxx sample)
		protected long numSamples; //number of dbr_time_xxx samples in the buffer which follows
		
		private DataHeader(File nextFile, long nextOffset, Instant nextTime, CtrlInfoReader info,
				DbrType dbrType, short dbrCount, long numSamples)
		{
			this.nextFile = nextFile;
			this.nextOffset = nextOffset;
			this.nextTime = nextTime;
			
			//this.startTime = startTime;
			//this.endTime = endTime;

			this.info = info;
			this.dbrType = dbrType;
			this.dbrCount = dbrCount;
			this.numSamples = numSamples;
		}
		
		public String toString()
		{
			return String.format("next=%s @ 0x%X, type %s[%d], %d samples", nextFile.getName(), nextOffset, dbrType.toString(), dbrCount, numSamples);
		}
		
		//assumes buffer is already opened and positioned
		public static DataHeader readDataHeader(ArchiveFileBuffer buffer, CtrlInfoReader info) throws IOException
		{
			buffer.skip(4);
			byte nameBytes [] = new byte [40];
			// first part of data file header:
			//	4 bytes directory_offset (skipped)
			//	"		next_offset (offset of next entry in its file)
			//	"		prev_offset (offset of previous entry in its file)
			//	"		cur_offset (used by FileAllocator writing file)
			//	"		num_samples (number of samples in the buffer)
			//	"		ctrl_info_offset (offset in this file of control info header (units, limits, etc.))
			//	"		buff_size (bytes allocated for this entry, including header)
			//	"		buff_free (number of un-used, allocated bytes for this header)
			//  2 bytes DbrType (type of data stored in buffer)
			//	2 bytes	DbrCount (count of values for each buffer element, i.e. 1 for scalar types, >1 for array types)
			long nextOffset = buffer.getUnsignedInt();
			buffer.skip(8);
			long numSamples = buffer.getUnsignedInt();
			long ctrlInfoOffset = buffer.getUnsignedInt();
			// compute amount of data in this data file entry: (bytes allocated) - (bytes free) - (bytes in header)
			long buffDataSize = buffer.getUnsignedInt() - buffer.getUnsignedInt() - 152;
			short dbrTypeCode = buffer.getShort();
			short dbrCount = buffer.getShort();
			
			
			if (!info.isOffset(ctrlInfoOffset))
				info = new CtrlInfoReader(ctrlInfoOffset);
			DbrType dbrType = DbrType.forValue(dbrTypeCode);
			assert (12 + dbrType.padding + (dbrCount - 1) * dbrType.valueSize + dbrType.getValuePad(dbrCount)) *
							numSamples == buffDataSize :
								String.format("Anticipated size of type %s (%d) with count %d does not match size of data",
										dbrType.toString(), dbrTypeCode, dbrCount);
			// last part of data file header:
			//	4 bytes padding (used to align the period)
			//	8 bytes (double) period
			//	8 bytes (epicsTimeStamp) begin_time
			//	"						next_file_time
			//	"						end_time
			//	char [40] prev_file
			//	char [40] next_file
			buffer.skip(20);
			Instant nextTime = buffer.getEpicsTime();
			buffer.skip(48);
			buffer.get(nameBytes);
			String nextFilename = nextOffset != 0 ? new String(nameBytes).split("\0", 2)[0] : "*";
			File nextFile = new File(buffer.getFile().getParentFile(), nextFilename);
			
			return new DataHeader(nextFile, nextOffset, nextTime, info, dbrType, dbrCount, numSamples);
		}
	}

	/**
	 * Get all samples associated with a channel name, given the data file and offset of its first entry.
	 * @param file File (DataFile name)
	 * @param offset Offset (of data header)
	 * @param dataParams an array of at least two; on return, dataParams[0] and dataParams[1] contain
	 * 		the DbrType and DbrCount of the data, respectively, as shorts
	 * @return A list of all samples associated with the channel, in bytes
	 * @throws IOException
	 * @throws {@link ArrayIndexOutOfBoundsException} if dataParams.length < 2
	 */
	public static void readDataFileEntries(List<ArchiveVType> dst, File dataFile, long offset) throws IOException
	{
		ArchiveFileBuffer buffer = new ArchiveFileBuffer(dataFile);
		CtrlInfoReader ctrlInfo = new CtrlInfoReader(0);

		// prepare to read header
		buffer.offset(offset);
		do {
			DataHeader header = DataHeader.readDataHeader(buffer, ctrlInfo);
			ArchiveFileSampleReader.getSamples(dst, header, buffer);
			//System.out.print("Have " + ret.size() + " samples\n   "); //for debug

			// Is there a next entry?
			if (header.nextOffset == 0)
			{
				return;
			}
			// Prepare to get the next entry
			if (!dataFile.equals(header.nextFile))
			{	//close the file and open the next one
				buffer.close();
				dataFile = new File(dataFile.getParent(), header.nextFile.getName());
				buffer.setFile(dataFile);
			}
			buffer.offset(header.nextOffset);
		} while (true);
	}
	
	private static void getSamples(List<ArchiveVType> dst, DataHeader header, ArchiveFileBuffer buff) throws IOException
	{
		getSamples(dst, header.dbrType, header.dbrCount, header.numSamples, header.info, buff);
	}

	public static void getSamples(List<ArchiveVType> dst, short dbrType, short dbrCount, long numSamples,
			CtrlInfoReader info, ArchiveFileBuffer dataBuff) throws IOException
	{
		getSamples(dst, DbrType.forValue(dbrType), dbrCount, numSamples, info, dataBuff);
	}
	
	private static void getSamples(List<ArchiveVType> dst, DbrType dbrType, short dbrCount, long numSamples,
			CtrlInfoReader info, ArchiveFileBuffer dataBuff) throws IOException
	{
		while (numSamples-- > 0)
		{
			dst.add(getSample(dbrType, dbrCount, info, dataBuff));
		}
	}

	private static ArchiveVType getSample(DbrType dbrType, short dbrCount,
			CtrlInfoReader info, ArchiveFileBuffer dataBuff) throws IOException
	{
		short statusCode = dataBuff.getShort();
		short severity = dataBuff.getShort();
		Instant timestamp = dataBuff.getEpicsTime();
		dataBuff.skip(dbrType.padding);
		AlarmSeverity sev = getSeverity(severity);
		String stat = getStatus(statusCode);
		Display display = info.getDisplay(dataBuff);
		ArchiveVType sample = null;
		switch (dbrType)
		{
			case DBR_TIME_STRING:
				assert dbrCount == 1 : "String type DBR value must be scalar (count = 1).";
				dbrCount = 40; //read as a string of 40 chars
			case DBR_TIME_CHAR:
				byte valueBytes [] = new byte [dbrCount];
				dataBuff.get(valueBytes);
				dataBuff.skip(dbrType.getValuePad(dbrCount));
				String strValue = new String(valueBytes).split("\0", 2)[0];
				sample = new ArchiveVString(timestamp, sev, stat, strValue);
				break;
			case DBR_TIME_ENUM:
				assert dbrCount == 1 : "Enum type DBR value must be scalar (count = 1).";
				List<String> labels = info.getLabels(dataBuff);
				int index = dataBuff.getShort(); 
				sample = new ArchiveVEnum(timestamp, sev, stat, labels, index);
				break;
			case DBR_TIME_FLOAT:
				if (dbrCount == 1)
				{
					float value = dataBuff.getFloat();
					sample = new ArchiveVNumber(timestamp, sev, stat, display, value);
				}
				else
				{
					double value [] = new double [dbrCount];
					for (int i = 0; i < dbrCount; ++i)
						value[i] = dataBuff.getFloat();
					dataBuff.skip(dbrType.getValuePad(dbrCount));
					sample = new ArchiveVNumberArray(timestamp, sev, stat, display, value);
				}
				break;
			case DBR_TIME_DOUBLE:
				if (dbrCount == 1)
				{
					double value = dataBuff.getDouble();
					sample = new ArchiveVNumber(timestamp, sev, stat, display, value);
				}
				else
				{
					double value [] = new double [dbrCount];
					for (int i = 0; i < dbrCount; ++i)
						value[i] = dataBuff.getDouble();
					dataBuff.skip(dbrType.getValuePad(dbrCount));
					sample = new ArchiveVNumberArray(timestamp, sev, stat, display, value);
				}
				break;
			case DBR_TIME_SHORT: //==DBR_TIME_INT
				if (dbrCount == 1)
				{
					short value = dataBuff.getShort();
					sample = new ArchiveVNumber(timestamp, sev, stat, display, value);
					dataBuff.skip(dbrType.getValuePad(dbrCount));
				}
				else
				{
					int value [] = new int [dbrCount];
					for (int i = 0; i < dbrCount; ++i)
						value[i] = dataBuff.getShort();
					dataBuff.skip(dbrType.getValuePad(dbrCount));
					sample = new ArchiveVNumberArray(timestamp, sev, stat, display, value);
				}
				break;
			case DBR_TIME_LONG:
				if (dbrCount == 1)
				{
					int value = dataBuff.getInt();
					sample = new ArchiveVNumber(timestamp, sev, stat, display, value);
				}
				else
				{
					int value [] = new int [dbrCount];
					for (int i = 0; i < dbrCount; ++i)
						value[i] = dataBuff.getInt();
					dataBuff.skip(dbrType.getValuePad(dbrCount));
					sample = new ArchiveVNumberArray(timestamp, sev, stat, display, value);
				}
				break;
		} //end switch(type)
		return sample;
	}

	//Dbr types (defines how data is arranged in file)
	private enum DbrType
	{
		DBR_TIME_STRING(14, 0, 40),
		DBR_TIME_SHORT(15, 2, 2), //==DBR_TIME_INT
		DBR_TIME_FLOAT(16, 0, 4),
		DBR_TIME_ENUM(17, 2, 2),
		DBR_TIME_CHAR(18, 3, 1),
		DBR_TIME_LONG(19, 0, 4),
		DBR_TIME_DOUBLE(20, 4, 8);

		public final int typeCode;
		public final int padding;
		public final int valueSize;
		private DbrType(int type, int padding, int valueSize)
		{
			this.typeCode = type;
			this.padding = padding;
			this.valueSize = valueSize;
		}
		
		public static DbrType forValue(int typeCode)
		{
			return DbrType.values()[typeCode-14];
		}
		
		//DBR types, when stored in files, are padded for alignment;
		//that is, so that their size is a multiple of 8. This includes
		//both the struct as defined (which only includes the first value)
		//and the "true" data structure (which includes an array of values
		//of arbitrary size).
		public int getValuePad(int count)
		{
			int remainder = getUnpaddedSize(count) % 8;
			return remainder != 0 ? 8 - remainder : 0;
		}

		public int getSize(int count)
		{
			int size = getUnpaddedSize(count);
			if (size % 8 != 0)
				size += 8 - size%8;
			return size;
		}
		
		private int getUnpaddedSize(int count)
		{
			return 12 + padding + valueSize * count;
		}
	}
	
	private static AlarmSeverity getSeverity(short severity)
	{
		if ((severity & 0x0F00) != 0) //special archiver values
			return AlarmSeverity.NONE;
		AlarmSeverity severities [] = AlarmSeverity.values();
		if (severity < severities.length && severity >= 0)
			return severities[severity];
		return AlarmSeverity.NONE;
	}
	
	private static String getStatus(short statusCode)
	{
        String statusText;
        try
        {
            final Status status = Status.forValue(statusCode);
            //statusText = status.toString();
            statusText = status.getName();
        }
        catch (Exception e)
        {
            statusText = "<" + statusCode + ">";
        }
        return statusText;
    }
}