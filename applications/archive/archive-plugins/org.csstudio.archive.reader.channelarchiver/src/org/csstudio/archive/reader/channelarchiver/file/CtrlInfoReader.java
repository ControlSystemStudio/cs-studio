package org.csstudio.archive.reader.channelarchiver.file;

import java.io.IOException;

import org.diirt.vtype.AlarmSeverity;
import org.diirt.vtype.Display;
import org.diirt.vtype.ValueFactory;

public class CtrlInfoReader
{
	private final long offset;
	//todo: reference to info
	
	public CtrlInfoReader(long offset)
	{
		this.offset = offset;
	}

	public void read(ArchiveFileBuffer buffer) throws IOException
	{
		//todo: if info is init'd, return
		long oldOffset = buffer.offset();
		buffer.offset(offset);
		short size = buffer.getShort();
		short type = buffer.getShort();
		switch(type)
		{	//TODO: implement
			case 0: //invalid
				break;
			case 1: //Numeric ?
				;
			case 2: //Enum ?
				break;
		}
		buffer.offset(oldOffset);
		//todo: return info?
	}
	
	public Display getDisplay()
	{
		//TODO: implement
		return ValueFactory.displayNone();
	}
	
	public AlarmSeverity getSeverity(short severity)
	{
		return AlarmSeverity.NONE;
	}
	
	public String getStatus(short status)
	{
		return "";
	}
	
	/*// NumericInfo is the CtrlInfo for numeric values.
	// So far, the structure's layout matches the binary
	// layout (except for the byte order of the individual elements),
	// so this strcuture must not be changed!
	class NumericInfo
	{
	public:
	    float    disp_high; // high display range
	    float    disp_low;  // low display range
	    float    low_warn;
	    float    low_alarm;
	    float    high_warn;
	    float    high_alarm;
	    int32_t  prec;      // display precision
	    char     units[1];  // actually as long as needed,
	};

	// Similar to NumericInfo, this is for enumerated channels
	class EnumeratedInfo
	{
	public:
	    int16_t num_states;     // state_strings holds num_states strings
	    int16_t pad;        // one after the other, separated by '\0'
	    char    state_strings[1];
	};

	// A glorified union of NumericInfo and EnumeratedInfo.
	//
	// type == CtrlInfo::Type
	// Info::size includes the "size" and "type" field.
	// The original archiver read/wrote "Info" that way,
	// but didn't properly initialize it:
	// size excluded size/type and was then rounded up by 8 bytes... ?!
	class CtrlInfoData
	{
	public:
	    uint16_t  size;
	    uint16_t  type;
	    union
	    {
	        NumericInfo     analog;
	        EnumeratedInfo  index;
	    }         value;
	    // class will be as long as necessary
	    // to hold the units or all the state_strings
	};*/

	
	public boolean isOffset(long offset)
	{
		return offset == this.offset;
	}
}
