package org.csstudio.apputil.macros;

/**Infinite loop detected. 
 * @author Xihui Chen
 *
 */
public class InfiniteLoopException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 569430280936384743L;

	public InfiniteLoopException() {
	}

	public InfiniteLoopException(String message) {
		super(message);
	}

	public InfiniteLoopException(Throwable cause) {
		super(cause);
	}

	public InfiniteLoopException(String message, Throwable cause) {
		super(message, cause);
	}

}
