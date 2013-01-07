/**
 * 
 */
package org.csstudio.logbook;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 
 * A builder for a default implementation of the {@link Attachment} interface.
 * 
 * @author shroffk
 * 
 */
public class AttachmentBuilder {

    // required
    private InputStream inputStream;
    private String fileName;
    private String contentType;
    private Boolean thumbnail;
    private Long fileSize;

    private byte[] byteArray;

    private AttachmentBuilder(String fileName) {
	this.fileName = fileName;
    }

    /**
     * Create a Builder for Attachment with the name _name_
     * 
     * @param name
     */
    public static AttachmentBuilder attachment(String fileName) {
	AttachmentBuilder attachmentBuilder = new AttachmentBuilder(fileName);
	return attachmentBuilder;
    }

    /**
     * Create a Builder object with parameters initialized with the same values
     * as the given Attachment object
     * 
     * @param Attachment
     * @return AttachmentBuilder
     */
    public static AttachmentBuilder attachment(Attachment attachment) {
	AttachmentBuilder attachmentBuilder = new AttachmentBuilder(
		attachment.getFileName());
	attachmentBuilder.contentType = attachment.getContentType();
	attachmentBuilder.thumbnail = attachment.getThumbnail();
	attachmentBuilder.fileSize = attachment.getFileSize();
	try {
	    attachmentBuilder.byteArray = read2byteArray(attachment
		    .getInputStream());
	} catch (IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	return attachmentBuilder;
    }

    /**
     * Set contentType
     * 
     * @param contentType
     * @return AttachmentBuilder
     */
    public AttachmentBuilder contentType(String contentType) {
	this.contentType = contentType;
	return this;
    }

    /**
     * 
     * @param fileSize
     * @return
     */
    public AttachmentBuilder fileSize(long fileSize) {
	this.fileSize = fileSize;
	return this;
    }

    /**
     * 
     * @param thumbnail
     * @return AttachmentBuilder
     */
    public AttachmentBuilder thumbnail(boolean thumbnail) {
	this.thumbnail = thumbnail;
	return this;
    }

    /**
     * 
     * @param inputStream - InputStream to the 
     * @return AttachmentBuilder
     */
    public AttachmentBuilder inputStream(InputStream inputStream) {
	try {
	    this.byteArray = read2byteArray(inputStream);
	} catch (IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	return this;
    }

    /**
     * Build an object implementing the {@link Attachment}.
     * 
     * @return Attachment - an immutable instance of an {@link Attachment}
     * @throws IOException
     */
    Attachment build() throws IOException {
	return new AttachmentImpl(new ByteArrayInputStream(byteArray), fileName, contentType,
		thumbnail, fileSize);
    }

    /**
     * A Default implementation of the Attachment interface
     * 
     * @author shroffk
     * 
     */
    private class AttachmentImpl implements Attachment {

	private String fileName;
	private String contentType;
	private Boolean thumbnail;
	private Long fileSize;
	private byte[] byteArray;

	public AttachmentImpl(InputStream inputStream, String fileName,
		String contentType, Boolean thumbnail, Long fileSize)
		throws IOException {
	    super();
	    byteArray = read2byteArray(inputStream);
	    this.fileName = fileName;
	    this.contentType = contentType;
	    this.thumbnail = thumbnail;
	    this.fileSize = fileSize;
	}

	@Override
	public InputStream getInputStream() {
	    return new ByteArrayInputStream(byteArray);
	}

	@Override
	public String getFileName() {
	    return fileName;
	}

	@Override
	public String getContentType() {
	    return contentType;
	}

	@Override
	public Boolean getThumbnail() {
	    return thumbnail;
	}

	@Override
	public Long getFileSize() {
	    return fileSize;
	}

    }

    private static byte[] read2byteArray(InputStream input) throws IOException {
	byte[] buffer = new byte[8192];
	int bytesRead;
	ByteArrayOutputStream output = new ByteArrayOutputStream();
	while ((bytesRead = input.read(buffer)) != -1) {
	    output.write(buffer, 0, bytesRead);
	}
	return output.toByteArray();
    }

}
