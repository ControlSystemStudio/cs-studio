package org.csstudio.archive.reader.channelarchiver.file;

import java.io.IOException;
import java.time.Instant;
import java.util.List;

import org.csstudio.archive.vtype.ArchiveVNumber;
import org.csstudio.archive.vtype.ArchiveVNumberArray;
import org.csstudio.archive.vtype.ArchiveVType;
import org.diirt.vtype.AlarmSeverity;
import org.diirt.vtype.Display;
import org.diirt.vtype.VType;

//VType: relevant classes: ArchiveVNumber, ArchiveVnumberArray, ArchiveVEnum, ArchiveVString
public class ArchiveFileSampleReader
{
	public static void getSamples(List<ArchiveVType> dst, short dbrType, short dbrCount, long numSamples,
			CtrlInfoReader info, ArchiveFileBuffer dataBuff) throws IOException
	{
		
		DbrType type = DbrType.getDbrType(dbrType);
		short status = dataBuff.getShort();
		short severity = dataBuff.getShort();
		long secsPastEpoch = dataBuff.getUnsignedInt();
		long nanos = dataBuff.getUnsignedInt();
		Instant timestamp = Instant.ofEpochSecond(secsPastEpoch, nanos);
			//TODO: is java epoch same as data writer epoch? (seems java's is -20 to data writer)
		dataBuff.skip(type.padding);
		switch (type)
		{
			case DBR_TIME_STRING: break;
			case DBR_TIME_SHORT: //==DBR_TIME_INT
				while (numSamples-- > 0)
					dst.add(createShortVType(dataBuff, dbrCount, status, severity, timestamp, info));
				break;
			case DBR_TIME_FLOAT:
				while (numSamples-- > 0)
					dst.add(createFloatVType(dataBuff, dbrCount, status, severity, timestamp, info));
				break;
			case DBR_TIME_ENUM:
			case DBR_TIME_CHAR: break;
			case DBR_TIME_LONG:
				while (numSamples-- > 0)
					dst.add(createLongVType(dataBuff, dbrCount, status, severity, timestamp, info));
				break;
			case DBR_TIME_DOUBLE:
				while (numSamples-- > 0)
					dst.add(createDoubleVType(dataBuff, dbrCount, status, severity, timestamp, info));
				break;
			//TODO: other types
		}
		return;
	}

	//Dbr types (defines how data is written to file)
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
		
		public static DbrType getDbrType(int type)
		{
			return DbrType.values()[type-14];
		}
	}
	
	//TODO: refactor: a few createNumberVType methods, with various ways of reading data
	protected static ArchiveVType createDoubleVType(ArchiveFileBuffer dataBuff, short count, short status, short severity,
			Instant timestamp, CtrlInfoReader info) throws IOException
	{
		if (count == 1)
		{
			double value = dataBuff.getDouble();

			AlarmSeverity sev = info.getSeverity(severity);
			String stat = info.getStatus(status);
			Display display = info.getDisplay();
			
			return new ArchiveVNumber(timestamp, sev, stat, display, value);
		}
		else
		{
			double value [] = new double [count];
			for (int i = 0; i < count; ++i)
				value[i] = dataBuff.getDouble();

			AlarmSeverity sev = info.getSeverity(severity);
			String stat = info.getStatus(status);
			Display display = info.getDisplay();
			
			return new ArchiveVNumberArray(timestamp, sev, stat, display, value);
		}
	}
	
	protected static ArchiveVType createFloatVType(ArchiveFileBuffer dataBuff, short count, short status, short severity,
			Instant timestamp, CtrlInfoReader info) throws IOException
	{
		if (count == 1)
		{
			float value = dataBuff.getFloat();

			AlarmSeverity sev = info.getSeverity(severity);
			String stat = info.getStatus(status);
			Display display = info.getDisplay();
			
			return new ArchiveVNumber(timestamp, sev, stat, display, value);
		}
		else
		{
			double value [] = new double [count];
			for (int i = 0; i < count; ++i)
				value[i] = dataBuff.getFloat();

			AlarmSeverity sev = info.getSeverity(severity);
			String stat = info.getStatus(status);
			Display display = info.getDisplay();
			
			return new ArchiveVNumberArray(timestamp, sev, stat, display, value);
		}
	}
	
	protected static ArchiveVType createLongVType(ArchiveFileBuffer dataBuff, short count, short status, short severity,
			Instant timestamp, CtrlInfoReader info) throws IOException
	{
		if (count == 1)
		{
			int value = dataBuff.getInt();

			AlarmSeverity sev = info.getSeverity(severity);
			String stat = info.getStatus(status);
			Display display = info.getDisplay();
			
			return new ArchiveVNumber(timestamp, sev, stat, display, value);
		}
		else
		{
			int value [] = new int [count];
			for (int i = 0; i < count; ++i)
				value[i] = dataBuff.getInt();

			AlarmSeverity sev = info.getSeverity(severity);
			String stat = info.getStatus(status);
			Display display = info.getDisplay();
			
			return new ArchiveVNumberArray(timestamp, sev, stat, display, value);
		}
	}

	protected static ArchiveVType createShortVType(ArchiveFileBuffer dataBuff, short count, short status, short severity,
			Instant timestamp, CtrlInfoReader info) throws IOException
	{
		if (count == 1)
		{
			short value = dataBuff.getShort();

			AlarmSeverity sev = info.getSeverity(severity);
			String stat = info.getStatus(status);
			Display display = info.getDisplay();
			
			return new ArchiveVNumber(timestamp, sev, stat, display, value);
		}
		else
		{
			int value [] = new int [count];
			for (int i = 0; i < count; ++i)
				value[i] = dataBuff.getShort();

			AlarmSeverity sev = info.getSeverity(severity);
			String stat = info.getStatus(status);
			Display display = info.getDisplay();
			
			return new ArchiveVNumberArray(timestamp, sev, stat, display, value);
		}
	}

}