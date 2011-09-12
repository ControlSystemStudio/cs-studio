
/*
 * Copyright (c) 2010 Stiftung Deutsches Elektronen-Synchrotron,
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS.
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE.
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS,
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION,
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 *
 */

package org.csstudio.archive.sdds.server.io;

import javax.annotation.Nonnull;

/**
 * @author Markus Moeller
 *
 */
public class CommandHeader {

    /** Size of received data without the header */
    private int packetSize;

    /** The command */
    private int commandTag;

    /** Error number */
    private int error;

    /** AAPI version */
    private int aapiVersion;

    /**
     * Standard constructor that sets the attributes to default values.
     *
     */
    public CommandHeader() {
        this.packetSize = -1;
        this.commandTag = -1;
        this.error = 0;
        this.aapiVersion = -1;
    }

    /**
     * @param packetSize
     * @param commandTag
     * @param error
     */
    public CommandHeader(final int packetSize, final int commandTag, final int error, final int aapiVersion)
    {
        this.packetSize = packetSize;
        this.commandTag = commandTag;
        this.error = error;
        this.aapiVersion = aapiVersion;
    }

    public int getPacketSize() {
        return packetSize;
    }

    public void setPacketSize(final int packetSize) {
        this.packetSize = packetSize;
    }

    public int getCommandTag() {
        return commandTag;
    }

    public void setCommandTag(final int commandTag) {
        this.commandTag = commandTag;
    }

    public int getError() {
        return error;
    }

    public void setError(final int error) {
        this.error = error;
    }

    public int getAapiVersion() {
        return aapiVersion;
    }

    public void setAapiVersion(final int aapiVersion) {
        this.aapiVersion = aapiVersion;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    @Nonnull
    public String toString() {
        final String result = "CommandHeader{size=" + packetSize + ",command=" + commandTag + ",error=" + error + ",AAPI version=" + Integer.toHexString(aapiVersion) + "}";
        return result;
    }
}
