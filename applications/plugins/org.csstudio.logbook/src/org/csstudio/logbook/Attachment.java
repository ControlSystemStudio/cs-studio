/**
 * 
 */
package org.csstudio.logbook;

import java.io.InputStream;

/**
 * 
 * @author shroffk
 * 
 */
public interface Attachment {

	public InputStream getInputStream();
	
	public String getFileName();

	public String getContentType();

	public Boolean getThumbnail();

	public Long getFileSize();

}
