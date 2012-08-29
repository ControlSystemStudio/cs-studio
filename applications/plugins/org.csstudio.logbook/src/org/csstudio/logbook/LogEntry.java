package org.csstudio.logbook;

import java.io.File;
import java.io.InputStream;
import java.util.Collection;
import java.util.Date;

/**
 * @author shroffk
 *
 */
public interface LogEntry {
	
	/**
	 * @return
	 */
	public Object getId();
	
	/**
	 * @return
	 */
	public String getText();
	
	/**
	 * @return
	 */
	public String getOwner();
	
	/**
	 * @return
	 */
	public Date getCreateDate();
	
	/**
	 * @return
	 */
	public Date getModifiedDate();
	
	/**
	 * @return the attached file or empty collection
	 */
	public Collection<Attachment> getAttachment();
	
	/**
	 * @return
	 */
	public Collection<Tag> getTags();
	
	/**
	 * @return
	 */
	public Collection<Logbook> getLogbooks();
	
	/**
	 * @return
	 */
	public Collection<Property> getProperties();
	

}
