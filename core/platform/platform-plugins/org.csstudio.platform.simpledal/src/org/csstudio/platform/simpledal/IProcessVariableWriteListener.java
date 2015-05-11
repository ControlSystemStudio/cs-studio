package org.csstudio.platform.simpledal;

/**
 * Call-back interface that can be used in asynchronous write operations.
 *
 * @author Sven Wende
 *
 */
public interface IProcessVariableWriteListener {
    /**
     * This method is called when a value was successfully written.
     */
    void success();

    /**
     * This method is called when an error occured during the write attempt.
     *
     * @param error optional exception
     */
    void error(Exception error);
}
