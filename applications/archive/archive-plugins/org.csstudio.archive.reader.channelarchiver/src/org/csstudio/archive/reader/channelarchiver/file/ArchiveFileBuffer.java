package org.csstudio.archive.reader.channelarchiver.file;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class ArchiveFileBuffer implements AutoCloseable
{
	private final ByteBuffer buffer;
	private FileChannel file;
	
	public ArchiveFileBuffer(FileChannel file)
	{
		this.buffer = ByteBuffer.allocateDirect(1024); //TODO: what size? Bigger means less fetching, but too big means memory runs out
		setFile(file);
	}
	
	public void setFile(FileChannel file)
	{
		this.file = file;
		buffer.position(0).limit(0);
	}
	
	public void prepareGet(int numBytes) throws IOException
	{
		if (buffer.remaining() < numBytes)
		{
			buffer.compact();
			file.read(buffer);
			buffer.limit(buffer.position()); //use limit to mark extent of read
			buffer.position(0);
		}
	}
	
	public void get(byte dst []) throws IOException
	{
		prepareGet(dst.length);
		buffer.get(dst);
	}
	
	public long getUnsignedInt() throws IOException
	{
		prepareGet(4);
		return Integer.toUnsignedLong(buffer.getInt());
	}
	
	public short getShort() throws IOException
	{
		prepareGet(2);
		return buffer.getShort();
	}

	public float getFloat() throws IOException
	{
		prepareGet(4);
		return buffer.getFloat();
	}

	public double getDouble() throws IOException
	{
		prepareGet(8);
		return buffer.getDouble();
	}
	
	public byte get() throws IOException
	{
		if (!buffer.hasRemaining())
		{
			buffer.clear();
			buffer.limit(file.read(buffer));
		}
		return buffer.get();
	}
	
	public void skip(int numBytes) throws IOException
	{
		int numAlready = buffer.remaining();
		while (numBytes > numAlready)
		{
			numBytes -= numAlready;
			buffer.clear();
			numAlready = file.read(buffer);
			buffer.limit(numAlready);
			buffer.position(0);
		}
		buffer.position(buffer.position() + numBytes);
	}
	
	public void offset(long offset) throws IOException
	{
 		if (offset < 0 || offset > file.size())
		{
			//throw new RuntimeException("Offset is invalid.") ?
			return;
		}
		//check if buffer contains the data
		//(Buffer always represents a contiguous portion of the file's contents)
		long buffer_start_offset = file.position() - buffer.limit();
		boolean doesNotContain = buffer_start_offset > offset || file.position() < offset;
		if (doesNotContain)
		{
			file.position(offset);
			buffer.clear();
			buffer.limit(file.read(buffer));
			buffer.position(0);
		}
		else
		{
			buffer.position((int)(offset - buffer_start_offset));
		}
	}
	
	public int getInt() throws IOException
	{
		prepareGet(4);
		return buffer.getInt();
	}

	long offset() throws IOException
	{
		return file.position() - buffer.limit() + buffer.position();
	}

	public int remaining()
	{
		return buffer.remaining();
	}
	
	@Override
	public void close() throws IOException
	{
		file.close();
	}
}