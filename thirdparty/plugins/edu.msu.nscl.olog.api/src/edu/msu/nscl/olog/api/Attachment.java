

package edu.msu.nscl.olog.api;

/**
 *
 * @author Eric Berryman
 */
public class Attachment {
	private final String fileName;
        private final String contentType;
        private final Boolean thumbnail;
        private final Long fileSize;

	Attachment(XmlAttachment xml) {
		this.fileName = xml.getFileName();
		this.contentType = xml.getContentType();
                this.thumbnail = xml.getThumbnail();
                this.fileSize = xml.getFileSize();
	}

	public String getFileName() {
		return fileName;
	}

	public String getContentType() {
		return contentType;
	}

        public Boolean getThumbnail() {
                return thumbnail;
        }
        
        public Long getFileSize() {
                return fileSize;
        }

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((fileName == null) ? 0 : fileName.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Attachment))
			return false;
		Attachment other = (Attachment) obj;
		if (fileName == null) {
			if (other.fileName != null)
				return false;
		} else if (!fileName.equals(other.fileName))
			return false;
		return true;
	}


}
