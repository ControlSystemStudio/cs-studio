package org.csstudio.archive.reader.channelarchiver.file;

import java.io.IOException;

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
		buffer.offset(offset);
		//todo: get info
		//todo: return info?
	}
	
	public boolean isOffset(long offset)
	{
		return offset == this.offset;
	}
}
