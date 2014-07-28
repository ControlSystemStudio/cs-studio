package org.csstudio.archive.reader.fastarchiver.exceptions;

/**
 * Thrown when a data fetch from the archiver failed for some reason.
 * 
 * @author Friederike Johlinger
 *
 */
public class FADataNotAvailableException extends Exception {
	
	private static final long serialVersionUID = 1L;
	
	public FADataNotAvailableException(String message){
		super(message);
	}
	

}
