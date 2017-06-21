package org.csstudio.archive.reader.channelarchiver.file;

import java.io.IOException;
import java.time.Instant;
import java.util.List;

import org.csstudio.archive.vtype.ArchiveVEnum;
import org.csstudio.archive.vtype.ArchiveVNumber;
import org.csstudio.archive.vtype.ArchiveVNumberArray;
import org.csstudio.archive.vtype.ArchiveVString;
import org.csstudio.archive.vtype.ArchiveVType;
import org.diirt.vtype.AlarmSeverity;
import org.diirt.vtype.Display;

//TODO: imported package gov.aps.jca, better to require bundle?
import gov.aps.jca.dbr.Status;

//VType: relevant classes: ArchiveVNumber, ArchiveVnumberArray, ArchiveVEnum, ArchiveVString
public class ArchiveFileSampleReader
{
	public static void getSamples(List<ArchiveVType> dst, short dbrType, short dbrCount, long numSamples,
			CtrlInfoReader info, ArchiveFileBuffer dataBuff) throws IOException
	{
		DbrType type = DbrType.forValue(dbrType);
		while (numSamples-- > 0)
		{
			short statusCode = dataBuff.getShort();
			short severity = dataBuff.getShort();
			long secsPastEpoch = dataBuff.getUnsignedInt();
			long nanos = dataBuff.getUnsignedInt();
			Instant timestamp = Instant.ofEpochSecond(secsPastEpoch, nanos);
				//TODO: is java epoch same as data writer epoch? (seems java's is -20 to data writer)
			dataBuff.skip(type.padding);
			AlarmSeverity sev = getSeverity(severity);
			String stat = getStatus(statusCode);
			Display display = info.getDisplay(dataBuff);
			ArchiveVType sample = null;
			switch (type)
			{
				case DBR_TIME_STRING:
					assert dbrCount == 1 : "String type DBR value must be scalar (count = 1).";
					dbrCount = 40; //read as a string of 40 chars
				case DBR_TIME_CHAR:
					byte valueBytes [] = new byte [dbrCount];
					dataBuff.get(valueBytes);
					dataBuff.skip(type.getValuePad(dbrCount));
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
						dataBuff.skip(type.getValuePad(dbrCount));
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
						dataBuff.skip(type.getValuePad(dbrCount));
						sample = new ArchiveVNumberArray(timestamp, sev, stat, display, value);
					}
					break;
				case DBR_TIME_SHORT: //==DBR_TIME_INT
					if (dbrCount == 1)
					{
						short value = dataBuff.getShort();
						sample = new ArchiveVNumber(timestamp, sev, stat, display, value);
						dataBuff.skip(type.getValuePad(dbrCount));
					}
					else
					{
						int value [] = new int [dbrCount];
						for (int i = 0; i < dbrCount; ++i)
							value[i] = dataBuff.getShort();
						dataBuff.skip(type.getValuePad(dbrCount));
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
						dataBuff.skip(type.getValuePad(dbrCount));
						sample = new ArchiveVNumberArray(timestamp, sev, stat, display, value);
					}
					break;
			} //end switch(type)
			dst.add(sample);
		} //end while(numSamples-- > 0)
		return;
	}

	//Dbr types (defines how data is arranged in file)
	enum DbrType
	{
		DBR_TIME_STRING(14, 0, 40),
		DBR_TIME_SHORT(15, 2, 2), //==DBR_TIME_INT
		DBR_TIME_FLOAT(16, 0, 4),
		DBR_TIME_ENUM(17, 2, 2),
		DBR_TIME_CHAR(18, 3, 1),
		DBR_TIME_LONG(19, 0, 4),
		DBR_TIME_DOUBLE(20, 4, 8);

		public final int type;
		public final int padding;
		public final int valueSize;
		private DbrType(int type, int padding, int valueSize)
		{
			this.type = type;
			this.padding = padding;
			this.valueSize = valueSize;
		}
		
		public static DbrType forValue(int type)
		{
			return DbrType.values()[type-14];
		}
		
		//DBR types, when stored in files, are padded for alignment;
		//that is, so that their size is a multiple of 8. This includes
		//both the struct as defined (which only includes the first value)
		//and the "true" data structure (which includes an array of values
		//of arbitrary size).
		public int getValuePad(int count)
		{
			int remainder = (12 + padding + valueSize * count) % 8;
			return remainder != 0 ? 8 - remainder : 0;
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