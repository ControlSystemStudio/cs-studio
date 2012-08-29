/**
 * 
 */
package org.csstudio.logbook;

/**
 * 
 * A builder for a default implementation of the Attachment interface.
 * 
 * @author shroffk
 * 
 */
public interface Attachment {

	public String getFileName();

	public String getContentType();

	public Boolean getThumbnail();

	public Long getFileSize();

}
