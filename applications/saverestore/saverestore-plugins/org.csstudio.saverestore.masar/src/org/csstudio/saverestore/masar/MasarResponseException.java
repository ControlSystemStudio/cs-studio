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

/**
 *
 * <code>MasarResponseException</code> is an exception describing an error, which was triggered by the service.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public class MasarResponseException extends MasarException {

    private static final long serialVersionUID = -2518211588109658758L;

    /**
     * Constructs a new exception with the given message.
     *
     * @param message the exception message
     */
    public MasarResponseException(String message) {
        super(message);
    }
}
