package org.csstudio.saverestore;

/**
 *
 * <code>InvalidCommentException</code> describes an error when the provided comment (when storing beamline set
 * or snapshot) is considered invalid.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public class InvalidCommentException extends Exception {
    private static final long serialVersionUID = 8892164207644948393L;

    /**
     * Constructs a new exception.
     *
     * @param message the message describing the exception
     * @param cause the cause of the exception
     */
    public InvalidCommentException(String message, Throwable cause) {
        super(message,cause);
    }

    /**
     * Constructs a new exception.
     *
     * @param message the message describing the exception
     */
    public InvalidCommentException(String message) {
        super(message);
    }

}
