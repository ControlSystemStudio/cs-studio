package org.csstudio.logbook;

/** Interface to a logbook
 *  @author nypaver
 *  @author Kay Kasemir
 */
public interface ILogbook
{
	/** Add new entry to the logbook.
	 *  @param title Title
	 *  @param text Text of the entry. Plain ASCII.
	 *  @param image_file_name Name of image file or <code>null</code>.
	 *         Has to contain the full path to the image. Exact path format
	 *         depends on the operating system.
	 *         File types that the logbook support (GIF, JPG) depend on
	 *         implementation.
	 *  @throws Exception on error
	 */
   void createEntry(String title, String text, String image_file_name) 
        throws Exception;

	/** Close the logbook. Should be called when done to reclaim resources. */
	void close();
}
