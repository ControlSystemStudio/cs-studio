package org.csstudio.saverestore;

/**
 *
 * <code>UnsupportedActionException</code> indicates that an action, which is not supported or allowed has been
 * requested from the data provider. An example is when a snapshot is being tagged, but the tagging is not supported.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public class UnsupportedActionException extends DataProviderException {

    private static final long serialVersionUID = -8185072842564160433L;

    /**
     * Constructs a new exception.
     *
     * @param message the message describing the exception
     */
    public UnsupportedActionException(String message) {
        super(message);
    }
}
