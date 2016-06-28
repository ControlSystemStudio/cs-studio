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

import org.epics.pvaccess.client.ChannelRPCRequester;
import org.epics.pvdata.pv.PVStructure;

/**
 *
 * <code>RPCRequester</code> is a channel roc requester, which provides the methods required by the masar client when
 * communicating to the service. These include waiting for connection to be established, making a request, and
 * destroying the connection.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public interface RPCRequester extends ChannelRPCRequester {

    /**
     * Blocks until connected. The implementor might choose to block only for a specific amount of time and if waiting
     * is terminated an {@link InterruptedException} may be thrown.
     *
     * @return true if channel is connected or false if not
     * @throws InterruptedException if the waiting was terminated abruptly
     */
    boolean waitUntilConnected() throws InterruptedException;

    /**
     * Makes a request to the service and returns the response the service sent to the given request. Implementation can
     * be done in a way to wait for the response for a given amount of time and if waiting is interrupted an
     * {@link InterruptedException} may be thrown.
     *
     * @param requestData the request input data
     * @return the response from the server
     * @throws InterruptedException if the waiting was terminated abruptly
     * @throws MasarException in case of an error
     */
    PVStructure request(PVStructure requestData) throws InterruptedException, MasarException;

    /**
     * Destroys this channel requester.
     */
    void destroy();

    /**
     * Returns true if the channel is connected or false otherwise.
     *
     * @return true if connected or false otherwise
     */
    boolean isConnected();
}
