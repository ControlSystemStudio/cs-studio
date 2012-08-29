/**
 * 
 */
package org.csstudio.logbook;

/**
 * @author shroffk
 * 
 */
public interface Attachment {

	public String getFileName();

	public String getContentType();

	public Boolean getThumbnail();

	public Long getFileSize();

}
