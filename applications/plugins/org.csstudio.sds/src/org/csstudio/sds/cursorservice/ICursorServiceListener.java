package org.csstudio.sds.cursorservice;

/**
 * The interface for the listeners called by the {@link CursorService}. 
 * @author Kai Meyer
 */
public interface ICursorServiceListener {
	
	/**
	 * Cursors defined by the preference page have changed.
	 */
	void cursorChanged();

}
