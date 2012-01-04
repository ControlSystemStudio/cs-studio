

package edu.msu.nscl.olog.api;


import javax.activation.DataHandler;

/**
 *
 * @author Eric Berryman
 * @deprecated
 */
@Deprecated public class Attachment {
	private final String fileName;
        private final String Uri;
        private final DataHandler fileContent;

	Attachment(XmlAttachment xml) {
		this.fileName = xml.getFileName();
		this.Uri = xml.getUri();
                this.fileContent = xml.getFileContent();
	}

	public String getFileName() {
		return fileName;
	}

	public String getUri() {
		return Uri;
	}

        public DataHandler getFileContent() {
                return fileContent;
        }

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((Uri == null) ? 0 : Uri.hashCode());
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
		if (Uri == null) {
			if (other.Uri != null)
				return false;
		} else if (!Uri.equals(other.Uri))
			return false;
		return true;
	}


}
