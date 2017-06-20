package org.csstudio.archive.reader.channelarchiver.file;

import java.io.IOException;
import java.time.Instant;

import org.csstudio.archive.vtype.ArchiveVNumber;
import org.csstudio.archive.vtype.ArchiveVNumberArray;
import org.csstudio.archive.vtype.ArchiveVType;
import org.diirt.vtype.AlarmSeverity;
import org.diirt.vtype.Display;
import org.diirt.vtype.VType;

//VType: relevant classes: ArchiveVNumber, ArchiveVnumberArray, ArchiveVEnum, ArchiveVString
public abstract class ArchiveFileSampleReader
{
	private final ArchiveVType value;
	
	protected ArchiveFileSampleReader(DbrType type, short dbrCount, CtrlInfoReader info, ArchiveFileBuffer dataBuff) throws IOException
	{
		short status = dataBuff.getShort();
		short severity = dataBuff.getShort();
		long secsPastEpoch = dataBuff.getUnsignedInt();
		long nanos = dataBuff.getUnsignedInt();
		Instant timestamp = Instant.ofEpochSecond(secsPastEpoch, nanos); //TODO: is java epoch same as data writer epoch? (seems java's is -20 to data writer)
		dataBuff.skip(type.padding);
		value = createVType(dataBuff, dbrCount, status, severity, timestamp, info);
	}
	
	protected abstract ArchiveVType createVType(ArchiveFileBuffer dataBuff, short count, short status, short severity, Instant timestamp, CtrlInfoReader info) throws IOException;
	
	public ArchiveVType getValue()
	{
		return value;
	}
	
	public static ArchiveVType getSample(short dbrType, short dbrCount,
			CtrlInfoReader info, ArchiveFileBuffer dataBuff) throws IOException
	{
		
		DbrType type = DbrType.getDbrType(dbrType);
		switch (type)
		{
			case DBR_TIME_DOUBLE:
			{
				return new ArchiveFileDoubleReader(dbrCount, info, dataBuff).getValue();
			}
			//TODO: other types
		}
		return null;
	}

	//Dbr types (defines how data is written to file)
	enum DbrType
	{
		DBR_TIME_STRING(14, 0, 40),
		DBR_TIME_SHORT(15, 2, 2),
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
}

class ArchiveFileDoubleReader extends ArchiveFileSampleReader
{
	protected ArchiveFileDoubleReader(short dbrCount, CtrlInfoReader info, ArchiveFileBuffer dataBuff) throws IOException
	{
		super(DbrType.DBR_TIME_DOUBLE, dbrCount, info, dataBuff);
	}

	@Override
	protected ArchiveVType createVType(ArchiveFileBuffer dataBuff, short count, short status, short severity,
			Instant timestamp, CtrlInfoReader info) throws IOException
	{
		if (count == 1)
		{
			double value = dataBuff.getDouble();
			 //TODO: init using info
			AlarmSeverity sev = null;
			String stat = "";
			Display display = null;
			
			return new ArchiveVNumber(timestamp, sev, stat, display, value);
		}
		else
		{
			double value [] = new double [count];
			for (int i = 0; i < count; ++i)
				value[i] = dataBuff.getDouble();
			//TODO: init using info
			AlarmSeverity sev = null;
			String stat = "";
			Display display = null;
			
			return new ArchiveVNumberArray(timestamp, sev, stat, display, value);
		}
	}
}
