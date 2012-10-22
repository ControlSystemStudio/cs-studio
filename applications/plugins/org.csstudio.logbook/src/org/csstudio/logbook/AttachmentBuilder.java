/**
 * 
 */
package org.csstudio.logbook;

import java.io.InputStream;

/**
 * 
 * A builder for a default implementation of the Attachment interface.
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
	 * @return
	 */
	public static AttachmentBuilder attachment(Attachment attachment) {
		AttachmentBuilder attachmentBuilder = new AttachmentBuilder(
				attachment.getFileName());
		attachmentBuilder.contentType = attachment.getContentType();
		attachmentBuilder.thumbnail = attachment.getThumbnail();
		attachmentBuilder.fileSize = attachment.getFileSize();
		return attachmentBuilder;
	}

	/**
	 * Set contentType
	 * 
	 * @param contentType
	 * @return
	 */
	public AttachmentBuilder contentType(String contentType) {
		this.contentType = contentType;
		return this;
	}

	public AttachmentBuilder fileSize(long fileSize) {
		this.fileSize = fileSize;
		return this;
	}

	public AttachmentBuilder thumbnail(boolean thumbnail) {
		this.thumbnail = thumbnail;
		return this;
	}
	
	public AttachmentBuilder inputStream(InputStream inputStream){
		this.inputStream = inputStream;
		return this;
	}

	/**
	 * Build an object implementing the Logbook.
	 * 
	 * @return
	 */
	Attachment build() {
		return new AttachmentImpl(inputStream, fileName, contentType, thumbnail, fileSize);
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
		private InputStream inputStream;

		public AttachmentImpl(InputStream inputStream, String fileName, String contentType,
				Boolean thumbnail, Long fileSize) {
			super();
			this.inputStream = inputStream;
			this.fileName = fileName;
			this.contentType = contentType;
			this.thumbnail = thumbnail;
			this.fileSize = fileSize;
		}		

		@Override
		public InputStream getInputStream() {
			return inputStream;
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

}
