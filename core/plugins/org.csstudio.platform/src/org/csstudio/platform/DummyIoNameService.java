package org.csstudio.platform;

/**
 * Dummy implementation of {@link IoNameService}.
 * 
 * @author Sven Wende
 * 
 */
// FIXME: swende: Provide a real implementation
public class DummyIoNameService implements IoNameService {

	/**
	 *{@inheritDoc}
	 */
	public String getIoName(String key, String field) {
		return "ioxyz123."+key+"."+(field!=null?field:"xxx");
	}

}
