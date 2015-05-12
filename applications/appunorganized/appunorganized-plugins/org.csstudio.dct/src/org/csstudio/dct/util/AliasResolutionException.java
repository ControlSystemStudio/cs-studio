package org.csstudio.dct.util;

/**
 * Signals that an attempt to resolve the aliases in a name has failed. Will be
 * thrown by {@link AliasResolutionUtil}.
 *
 * @author Sven Wende
 *
 */
public class AliasResolutionException extends Exception {

    private static final long serialVersionUID = 8823855724651109333L;

    /**
     * Constructor.
     *
     * @param message
     *            the error message
     */
    public AliasResolutionException(String message) {
        super(message);
    }

}
