/*
 * This software is Copyright by the Board of Trustees of Michigan
 * State University (c) Copyright 2016.
 *
 * Contact Information:
 *   Facility for Rare Isotope Beam
 *   Michigan State University
 *   East Lansing, MI 48824-1321
 *   http://frib.msu.edu
 */
package org.csstudio.saverestore.masar;

public class MasarException extends Exception {

    private static final long serialVersionUID = 6135827328000670561L;


    /**
     * Constructs a new exception.
     *
     * @param message the message describing the exception
     * @param cause the cause of the exception
     */
    public MasarException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new exception.
     *
     * @param message the message describing the exception
     */
    public MasarException(String message) {
        super(message);
    }

    /**
     * Constructs a new exception.
     *
     * @param cause the cause of the exception
     */
    public MasarException(Throwable cause) {
        super(cause);
    }
}
