package org.csstudio.saverestore;

/**
 *
 * <code>DataProviderException</code> is the common exception thrown by the data provider.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public class DataProviderException extends Exception{

    private static final long serialVersionUID = -238780177979145997L;

    /**
     * Constructs a new exception.
     *
     * @param message the message describing the exception
     * @param cause the cause of the exception
     */
    public DataProviderException(String message, Throwable cause) {
        super(message,cause);
    }

    /**
     * Constructs a new exception.
     *
     * @param message the message describing the exception
     */
    public DataProviderException(String message) {
        super(message);
    }

    /**
     * Constructs a new exception.
     *
     * @param cause the cause of the exception
     */
    public DataProviderException(Throwable cause) {
        super(cause);
    }
}
